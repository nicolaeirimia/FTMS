package com.FTMS.FTMS_app.shipment.application.service.impl;

import com.FTMS.FTMS_app.customer.application.service.CustomerService;
import com.FTMS.FTMS_app.customer.domain.model.Customer;
import com.FTMS.FTMS_app.fleet.application.service.FleetService;
import com.FTMS.FTMS_app.fleet.domain.model.Driver;
import com.FTMS.FTMS_app.fleet.domain.model.Vehicle;
import com.FTMS.FTMS_app.shipment.application.dto.CreateShipmentRequest;
import com.FTMS.FTMS_app.shipment.application.dto.DeliveryConfirmationDto;
import com.FTMS.FTMS_app.shipment.application.dto.ShipmentLocationDto;
import com.FTMS.FTMS_app.shipment.application.dto.CargoDto;
import com.FTMS.FTMS_app.shipment.application.service.ShipmentService;
import com.FTMS.FTMS_app.shipment.domain.model.*;
import com.FTMS.FTMS_app.shipment.domain.repository.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;

    // Injectarea serviciilor din celelalte module
    private final FleetService fleetService;
    private final CustomerService customerService;

    // Stocăm și prețul cursei (necesar pentru facturare)
    // Într-o aplicație reală, prețul ar fi calculat de un al 4-lea modul (Pricing)
    // Aici îl luăm din DTO.
    private double shipmentPrice;

    public ShipmentServiceImpl(ShipmentRepository shipmentRepository,
                               FleetService fleetService,
                               CustomerService customerService) {
        this.shipmentRepository = shipmentRepository;
        this.fleetService = fleetService;
        this.customerService = customerService;
    }

    @Override
    @Transactional
    public Shipment createShipment(CreateShipmentRequest request) {
        // 1. Validare cross-modul: Verifică clientul
        Customer customer = customerService.getCustomerById(request.getCustomerId());
        if (!customer.canPlaceNewShipment()) {
            throw new IllegalStateException("Customer " + customer.getCompanyName() + " is suspended and cannot place new shipments.");
        }

        // Verifică unicitatea referenceNumber
        shipmentRepository.findByReferenceNumber(request.getReferenceNumber())
                .ifPresent(s -> { throw new IllegalArgumentException("Shipment with reference number " + request.getReferenceNumber() + " already exists."); });

        // Salvează prețul pentru facturare
        this.shipmentPrice = request.getPrice();

        // 2. Mapare DTO -> Model
        ShipmentContactLocation pickup = mapToLocation(request.getPickupLocation());
        ShipmentContactLocation delivery = mapToLocation(request.getDeliveryLocation());
        CargoDetails cargo = mapToCargo(request.getCargoDetails());

        Shipment shipment = new Shipment(
                request.getReferenceNumber(),
                request.getCustomerId(),
                pickup,
                delivery,
                cargo,
                request.getPickupDateTime(),
                request.getRequestedDeliveryDateTime()
        );

        // 3. Salvare
        return shipmentRepository.save(shipment);
    }

    @Override
    @Transactional
    public Shipment assignShipment(Long shipmentId, Long driverId, Long vehicleId) {
        // 1. Găsește toate agregatele
        Shipment shipment = getShipmentById(shipmentId);
        Driver driver = fleetService.getDriverById(driverId);
        Vehicle vehicle = fleetService.getVehicleById(vehicleId);

        // 2. Validări de Business (Reguli)
        if (!shipment.canBeAssigned()) {
            throw new IllegalStateException("Shipment is already processed.");
        }
        if (!driver.isAvailable()) {
            throw new IllegalStateException("Driver " + driver.getName() + " is not available.");
        }
        if (!vehicle.isAvailable()) {
            throw new IllegalStateException("Vehicle " + vehicle.getRegistrationNumber() + " is not available.");
        }
        if (!driver.canDriveVehicle(vehicle)) {
            throw new IllegalArgumentException("Driver " + driver.getName() + " is not licensed for vehicle " + vehicle.getVehicleType());
        }

        CargoDetails cargo = shipment.getCargoDetails();
        if (!vehicle.getCapacity().isSufficient(cargo.getWeightKg(), cargo.getVolumeCubicMeters())) {
            throw new IllegalArgumentException("Vehicle capacity is not sufficient for this cargo.");
        }

        // 3. Orchestrează modificările (APELEAZĂ SERVICIILE)
        fleetService.assignDriver(driverId);
        fleetService.assignVehicle(vehicleId);

        // Apelează logica de domeniu din Shipment
        shipment.assign(driverId, vehicleId);

        return shipmentRepository.save(shipment);
    }

    @Override
    @Transactional
    public void cancelShipment(Long shipmentId) {
        Shipment shipment = getShipmentById(shipmentId);

        // Dacă resursele erau alocate, eliberează-le
        if (shipment.getAssignedDriverId() != null) {
            fleetService.releaseDriver(shipment.getAssignedDriverId());
        }
        if (shipment.getAssignedVehicleId() != null) {
            fleetService.releaseVehicle(shipment.getAssignedVehicleId());
        }

        // Apelează logica de domeniu
        shipment.cancel();
        shipmentRepository.save(shipment);
    }

    @Override
    @Transactional
    public Shipment updateShipmentStatus(Long shipmentId, ShipmentStatus newStatus) {
        Shipment shipment = getShipmentById(shipmentId);

        // O mașină de stări simplă
        switch (newStatus) {
            case PICKED_UP:
                shipment.markAsPickedUp();
                break;
            case IN_TRANSIT:
                shipment.markAsInTransit();
                break;
            default:
                throw new IllegalArgumentException("Status update to " + newStatus + " is not managed by this method.");
        }
        return shipmentRepository.save(shipment);
    }

    @Override
    @Transactional
    public Shipment confirmDelivery(Long shipmentId, DeliveryConfirmationDto dto) {
        // 1. Găsește cursa
        Shipment shipment = getShipmentById(shipmentId);

        // 2. Mapare DTO -> Model
        DeliveryConfirmation confirmation = new DeliveryConfirmation(
                shipment,
                dto.getActualDeliveryDateTime(),
                dto.getRecipientName(),
                dto.getRecipientSignature(),
                dto.getIssuesOrDamages(),
                dto.getPhotoDocumentationUrl()
        );

        // 3. Apelează logica de domeniu
        shipment.completeDelivery(confirmation);

        // 4. Eliberează resursele
        fleetService.releaseDriver(shipment.getAssignedDriverId());
        fleetService.releaseVehicle(shipment.getAssignedVehicleId());

        Shipment savedShipment = shipmentRepository.save(shipment);

        // 5. REGULA DE BUSINESS: Generează factura
        // (Prețul este stocat temporar în acest serviciu - vezi createShipment)
        customerService.generateInvoice(shipment.getCustomerId(), shipmentId, this.shipmentPrice);

        return savedShipment;
    }

    @Override
    @Transactional(readOnly = true)
    public Shipment getShipmentById(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment not found with id: " + id)); // Excepție custom
    }

    // --- Metode Utilitare Private (Mapare) ---

    private ShipmentContactLocation mapToLocation(ShipmentLocationDto dto) {
        return new ShipmentContactLocation(
                dto.getStreet(), dto.getCity(), dto.getZipCode(), dto.getCountry(),
                dto.getContactPerson(), dto.getContactPhone()
        );
    }

    private CargoDetails mapToCargo(CargoDto dto) {
        return new CargoDetails(
                dto.getDescription(), dto.getWeightKg(), dto.getVolumeCubicMeters(),
                dto.getSpecialHandlingRequirements(), dto.getAdditionalNotes()
        );
    }
}