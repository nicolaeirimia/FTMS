FTMS - Freight Transport Management System
Backend Architecture & Implementation Documentation
1. 📝 Prezentare Generală
FTMS este o aplicație web enterprise destinată gestionării logistice pentru o companie de transport marfă. Sistemul permite administrarea centralizată a flotei auto, a relațiilor cu clienții și a fluxului complet de livrare (de la crearea comenzii până la facturare).

Proiectul este construit ca un Monolit Modular, urmând principiile Domain-Driven Design (DDD) pentru a asigura scalabilitatea și separarea clară a responsabilităților.

2. 🏗️ Arhitectura Tehnică
2.1. Tech Stack
Limbaj: Java 21 (LTS)

Framework: Spring Boot 3.5.x

Bază de date: PostgreSQL 16

ORM: Spring Data JPA / Hibernate

Build Tool: Maven

Testing: JUnit 5, Mockito, Spring Boot Test

Utils: Lombok (pentru reducerea codului boilerplate)

2.2. Structura Modulară (Bounded Contexts)
Aplicația este împărțită în trei module funcționale distincte (pachete rădăcină), respectând arhitectura stratificată (Layered Architecture):

fleet (Fleet & Resource Management):

Gestionează resursele interne (Șoferi, Vehicule).

Responsabil de mentenanță și disponibilitate.

customer (Customer Management):

Gestionează profilurile clienților, contractele și aspectele financiare (Facturi).

shipment (Shipment Management):

Core Domain. Modulul orchestrator care leagă cererea (Clientul) de ofertă (Flota).

Fiecare modul are straturile interne:

domain: Entități, Value Objects, Repository Interfaces.

application: Servicii (Workflow), DTOs.

presentation: REST Controllers.

infrastructure: Implementări specifice (ex: SQL queries complexe).

3. 🧩 Modelul de Domeniu (Domain Model)
Implementarea persistenței utilizează facilități avansate JPA pentru a respecta regulile de business și integritatea datelor.

3.1. Entități Principale (Aggregates)
Vehicle: Rădăcina agregatului pentru flotă. Gestionează lista de MaintenanceRecord folosind CascadeType.ALL și orphanRemoval=true.

Customer: Gestionează lista de adrese de livrare folosind @ElementCollection (tabel secundar gestionat automat) și relația cu Contract.

Shipment: Entitatea centrală. Pentru a menține Decuplarea (Loose Coupling), aceasta nu are relații directe @OneToOne cu Driver sau Vehicle, ci stochează doar Referințe prin ID (assignedDriverId).

3.2. Obiecte Valoare (Value Objects)
Sunt folosite pentru a încapsula date care nu au identitate proprie, folosind @Embeddable:

VehicleCapacity: Încapsulează logica de validare (ex: isSufficient(weight, volume)).

ContactInfo & Address: Reutilizate în mai multe entități.

Soluționarea Coliziunilor: S-a folosit @AttributeOverrides pentru a permite utilizarea aceluiași VO de mai multe ori în aceeași entitate (ex: pickupLocation și deliveryLocation în Shipment).

3.3. Rich Enums (Polimorfism simplificat)
Logica de business nu este împrăștiată în if/else, ci încapsulată în Enums:

CustomerCategory: Conține logica de calcul a discount-ului (applyDiscount()).

PaymentTerms: Conține logica de calcul a scadenței (calculateDueDate()).

4. ⚙️ Logica de Business & Workflow
Sistemul implementează reguli complexe de validare și fluxuri tranzacționale.

4.1. Fluxul Principal (Shipment Workflow)
Implementat în ShipmentServiceImpl:

Creare: Se validează eligibilitatea clientului (ex: să nu fie SUSPENDED din cauza datoriilor).

Alocare (Assignment):

Se verifică disponibilitatea șoferului (driver.isAvailable()).

Se verifică compatibilitatea permisului (driver.canDriveVehicle()).

Se verifică capacitatea vehiculului (vehicle.getCapacity().isSufficient()).

Tranzacție Atomică: Dacă toate validările trec, statusurile se actualizează sincronizat (Shipment -> SCHEDULED, Driver -> ON_ROUTE, Vehicle -> IN_USE).

Livrare & Facturare:

La confirmarea livrării, resursele sunt eliberate automat (AVAILABLE).

Sistemul apelează CustomerService pentru a genera automat factura, aplicând reducerile contractuale.

5. 🧪 Strategia de Testare
Proiectul folosește o suită de teste automată robustă, împărțită pe 3 niveluri de complexitate:

Nivelul 1: Infrastructură & Persistență (Test1_EntityRepository)
Scop: Verifică integrarea cu PostgreSQL și mapările Hibernate.

Ce testează: CRUD operations, constrângeri unice (DataIntegrityViolationException), query-uri custom JPQL (ex: findHeavyShipments).

Tehnologie: @DataJpaTest / @SpringBootTest cu tranzacții.

Nivelul 2: Domain & Computation (Test2_ComputingServices)
Scop: Verifică logica matematică și regulile de business izolate.

Ce testează: Calculul facturilor (plăți parțiale/totale), eligibilitatea vehiculelor, aplicarea discount-urilor.

Tehnologie: JUnit 5 (Unit Tests simple, foarte rapide).

Nivelul 3: Workflow & Integration (Test3_WorkflowServices)
Scop: Verifică orchestrarea completă între module.

Ce testează: Scenariul "Happy Path" (Creare -> Alocare -> Confirmare) și scenarii de eroare ("Unhappy Path" - ex: alocarea unui șofer ocupat).

Tehnologie: @SpringBootTest, @Transactional, @Commit (pentru verificarea vizuală a datelor în DB).
