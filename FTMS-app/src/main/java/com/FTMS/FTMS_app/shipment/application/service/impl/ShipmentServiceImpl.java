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
    private final FleetService fleetService;
    private final CustomerService customerService;

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
        Customer customer = customerService.getCustomerById(request.getCustomerId());
        if (!customer.canPlaceNewShipment()) {
            throw new IllegalStateException("Customer " + customer.getCompanyName() + " is suspended and cannot place new shipments.");
        }

        shipmentRepository.findByReferenceNumber(request.getReferenceNumber())
                .ifPresent(s -> { throw new IllegalArgumentException("Shipment with reference number " + request.getReferenceNumber() + " already exists."); });

        this.shipmentPrice = request.getPrice();


        ShipmentContactLocation pickup = mapToLocation(request.getPickupLocation());
        ShipmentContactLocation delivery = mapToLocation(request.getDeliveryLocation());
        CargoDetails cargo = mapToCargo(request.getCargoDetails());


        Shipment shipment = Shipment.builder()
                .referenceNumber(request.getReferenceNumber())
                .customerId(request.getCustomerId())
                .pickupLocation(pickup)
                .deliveryLocation(delivery)
                .cargoDetails(cargo)
                .pickupDateTime(request.getPickupDateTime())
                .requestedDeliveryDateTime(request.getRequestedDeliveryDateTime())
                .status(ShipmentStatus.PENDING)
                .build();
        // ---------------------------------------------------------------

        return shipmentRepository.save(shipment);
    }

    @Override
    @Transactional
    public Shipment assignShipment(Long shipmentId, Long driverId, Long vehicleId) {
        Shipment shipment = getShipmentById(shipmentId);
        Driver driver = fleetService.getDriverById(driverId);
        Vehicle vehicle = fleetService.getVehicleById(vehicleId);

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

        fleetService.assignDriver(driverId);
        fleetService.assignVehicle(vehicleId);
        shipment.assign(driverId, vehicleId);

        return shipmentRepository.save(shipment);
    }

    @Override
    @Transactional
    public Shipment cancelShipment(Long shipmentId) {
        Shipment shipment = getShipmentById(shipmentId);

        if (shipment.getAssignedDriverId() != null) {
            fleetService.releaseDriver(shipment.getAssignedDriverId());
        }
        if (shipment.getAssignedVehicleId() != null) {
            fleetService.releaseVehicle(shipment.getAssignedVehicleId());
        }

        shipment.cancel();
        return shipmentRepository.save(shipment);
    }

    @Override
    @Transactional
    public Shipment updateShipmentStatus(Long shipmentId, ShipmentStatus newStatus) {
        Shipment shipment = getShipmentById(shipmentId);

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
        Shipment shipment = getShipmentById(shipmentId);


        DeliveryConfirmation confirmation = DeliveryConfirmation.builder()
                .shipment(shipment)
                .actualDeliveryDateTime(dto.getActualDeliveryDateTime())
                .recipientName(dto.getRecipientName())
                .recipientSignature(dto.getRecipientSignature())
                .issuesOrDamages(dto.getIssuesOrDamages())
                .photoDocumentationUrl(dto.getPhotoDocumentationUrl())
                .build();


        shipment.completeDelivery(confirmation);

        fleetService.releaseDriver(shipment.getAssignedDriverId());
        fleetService.releaseVehicle(shipment.getAssignedVehicleId());

        Shipment savedShipment = shipmentRepository.save(shipment);

        customerService.generateInvoice(shipment.getCustomerId(), shipmentId, this.shipmentPrice);

        return savedShipment;
    }

    @Override
    @Transactional(readOnly = true)
    public Shipment getShipmentById(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment not found with id: " + id));
    }



    private ShipmentContactLocation mapToLocation(ShipmentLocationDto dto) {

        return new ShipmentContactLocation(
                dto.getStreet(), dto.getCity(), dto.getZipCode(), dto.getCountry(),
                dto.getContactPerson(), dto.getContactPhone()
        );
    }

    private CargoDetails mapToCargo(CargoDto dto) {

        return CargoDetails.builder()
                .description(dto.getDescription())
                .weightKg(dto.getWeightKg())
                .volumeCubicMeters(dto.getVolumeCubicMeters())
                .specialHandlingRequirements(dto.getSpecialHandlingRequirements())
                .additionalNotes(dto.getAdditionalNotes())
                .build();
    }
}