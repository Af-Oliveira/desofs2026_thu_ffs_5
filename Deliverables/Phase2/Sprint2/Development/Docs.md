# DESOFS — Phase 2, Sprint 2: Development Documentation

| | |
|---|---|
| **Project** | VendNet — Vending Machine Network Back-End |
| **Organisation** | Grupo Sensacao (ISEP — DESOFS 2025/2026) |
| **Date** | Junho 2026 |
| **Status** | ☑ Done |

---

## 1. Architecture

### 1.1 System Architecture Diagram

The system architecture follows the C4 model as established in Phase 1 (`Deliverables/Phase1/System-To-Be/C4/`). The Sprint 2 implementation faithfully realises the architecture described in those diagrams. The following C4 views present the system at increasing levels of detail.

#### C4 Level 1 — System Context

The Level 1 diagram shows VendNet as a single system interacting with five external actors: Customers, Operators, Administrators, Vending Machines (edge devices), and the Payment Gateway.

![C4 Level 1 — Implementation View](../../../Phase1/System-To-Be/C4/C4_Level1/Implementaion_view/Implementation%20View%20-%20Level%201%20-%20VendNet%20System.drawio.svg)

_Source: `Deliverables/Phase1/System-To-Be/C4/C4_Level1/Implementaion_view/`_

**External Actors (as implemented):**

| Actor           | Authentication                | Interaction                                                          |
| --------------- | ----------------------------- | -------------------------------------------------------------------- |
| Customer        | JWT Bearer (HS256)            | Browse catalog, view machines, purchase products, view own history   |
| Operator        | JWT Bearer (HS256)            | View machines and slots, restock inventory, view machine sales       |
| Administrator   | JWT Bearer (HS256) + TOTP MFA | Full system management — users, products, machines, backups, reports |
| Vending Machine | X.509 mTLS client certificate | Telemetry ingestion (temperature, stock levels, status, errors)      |
| Payment Gateway | HMAC-SHA256 webhook signature | Asynchronous payment confirmation callbacks                          |

#### C4 Level 2 — Container View

The Container View decomposes VendNet into its runtime containers: the Java Spring Boot API, MySQL 8.4 database, file system sandbox (`/var/vendnet/`), and the observability stack (Prometheus, Grafana, Loki, Jaeger, OpenTelemetry Collector).

![C4 Level 2 — Implementation View](../../../Phase1/System-To-Be/C4/C4_Level2/Implementation_view/Implementation%20View%20-%20Level%202%20-%20VendNet%20System.drawio.svg)

_Source: `Deliverables/Phase1/System-To-Be/C4/C4_Level2/Implementation_view/`_

![C4 Level 2 — Physical View](<../../../Phase1/System-To-Be/C4/C4_Level2/Physical_view/Physical%20View%20-%20Level%202%20-%20VendNet%20System%20(Vending%20Machine%20Network).drawio.svg>)

_Source: `Deliverables/Phase1/System-To-Be/C4/C4_Level2/Physical_view/`_

#### C4 Level 3 — Component View (Internal Architecture)

The Java backend internally follows a DDD-inspired layered architecture, verified by ArchUnit tests (`LayeredArchitectureTest.java`):

![C4 Level 3 — Implementation View](<../../../Phase1/System-To-Be/C4/C4_Level3/Implementation_view/Implementation%20View%20-%20Level%203%20-%20VendNet%20System%20(Vending%20Machine%20Network).drawio.svg>)

_Source: `Deliverables/Phase1/System-To-Be/C4/C4_Level3/Implementation_view/`_

**Internal Layer Structure (as implemented):**

| Layer                  | Package                                                                                                                            | Responsibility                                                                          |
| ---------------------- | ---------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------- |
| **Interface Adapters** | `api/controller/`, `api/dto/`                                                                                                      | REST endpoints, DTO mapping, `@PreAuthorize` enforcement, input validation              |
| **Application**        | `application/service/`                                                                                                             | Use case orchestration, transaction management, cross-aggregate coordination            |
| **Domain**             | `domain/model/`, `domain/repository/`                                                                                              | Business rules, invariants, entity state transitions, repository interfaces             |
| **Infrastructure**     | `infrastructure/persistence/`, `infrastructure/security/`, `infrastructure/file/`, `infrastructure/os/`, `infrastructure/payment/` | JPA implementations, security filters, file storage, backup/restore, payment simulation |

**Architecture Style:** DDD-inspired layered monolith with clean separation between layers. Stateless REST API with JWT-based authentication and X.509 mTLS for machine-to-server communication.

**Key Architecture Decisions:**

- **Layered over microservices:** The vending machine domain is small enough that a well-structured monolith simplifies deployment and transaction management while maintaining modularity through package-by-bounded-context organization.
- **Stateless JWT:** Enables horizontal scaling without sticky sessions. Token blocklisting uses an in-memory `ConcurrentHashMap` (acceptable for the development phase, noted as a limitation for production).
- **Pessimistic locking for inventory:** Slot stock reservation uses `@Lock(PESSIMISTIC_WRITE)` to prevent race conditions during concurrent purchases (`JpaSlotRepository`).
- **Ports and adapters pattern:** Domain repositories are interfaces in `domain/repository/` with JPA implementations in `infrastructure/persistence/`, allowing the domain to remain independent of persistence technology.

#### C4 Scenarios View — Use Case Diagram

The following use case diagram from Phase 1 summarises all 13 use cases supported by VendNet. All use cases are implemented in Sprint 2.

![Use Case Diagram](../../../Phase1/System-To-Be/C4/Scenarios_View/Use%20Case%20Diagram.drawio.svg)

_Source: `Deliverables/Phase1/System-To-Be/C4/Scenarios_View/Use Case Diagram.drawio`_

**Implemented Use Cases:**

| #    | Use Case          | Actor(s)                  | Endpoint(s)                                          |
| ---- | ----------------- | ------------------------- | ---------------------------------------------------- |
| UC1  | Authenticate      | Customer, Operator, Admin | `POST /api/auth/login`, `/register`, `/mfa/verify`   |
| UC2  | Manage Users      | Administrator             | `GET/POST/PUT /api/admin/users`                      |
| UC3  | Browse Catalog    | Customer, Operator, Admin | `GET /api/products`, `/api/products/{sku}`           |
| UC4  | Manage Products   | Administrator             | `POST/PUT /api/products`, `POST /api/admin/products` |
| UC5  | Manage Machines   | Administrator             | `POST/PUT /api/machines`                             |
| UC6  | Receive Telemetry | Vending Machine           | `POST /api/telemetry`                                |
| UC7  | Restock Slots     | Operator, Admin           | `PUT /api/machines/{id}/slots/{sid}/restock`         |
| UC8  | Purchase Product  | Customer                  | `POST /api/sales/purchase`                           |
| UC9  | View History      | Customer, Operator, Admin | `GET /api/sales/me`, `GET /api/sales/machine/{id}`   |
| UC10 | Generate Backup   | Administrator             | `POST /api/admin/backups`                            |
| UC11 | Manage Audit Logs | Administrator             | (via `AuditLogRepository`)                           |
| UC12 | Generate Reports  | Administrator             | `POST /api/admin/operations/reports/sales`           |
| UC13 | View Dashboards   | Administrator             | `GET /api/admin/dashboard`                           |

### 1.2 Domain Model Diagram

The domain model was designed in Phase 1 using DDD and is documented at three levels of detail. The Sprint 2 implementation realises these designs in the `domain/model/` package. The following diagrams are from `Deliverables/Phase1/System-To-Be/DDD/`.

#### DDD Level 1 — Core Entities and Relationships

This view shows the five core entities and their cross-aggregate ID-based references. Sprint 2 implements all five entities exactly as designed.

![DDD Level 1](../../../Phase1/System-To-Be/DDD/svg/DDD_Level1.svg)

_Source: `Deliverables/Phase1/System-To-Be/DDD/DDD_Level1.puml`_

#### DDD Level 2 — Aggregates, Properties, and Enums

This view shows the three-level aggregate decomposition with bounded contexts, entity properties, and internal enums. The colours distinguish the five bounded contexts: Identity & Access (red), Machine Management (blue), Slot Management (light blue), Product Catalog (green), and Sales (yellow).

![DDD Level 2 SVG](../../../Phase1/System-To-Be/DDD/svg/DDD_Level2.svg)

_Source: `Deliverables/Phase1/System-To-Be/DDD/DDD_Level2.puml`_

#### DDD Level 3 — Full Detail with Value Objects

![DDD Level 3](../../../Phase1/System-To-Be/DDD/svg/DDD_Level3.svg)

_Source: `Deliverables/Phase1/System-To-Be/DDD/DDD_Level3.puml`_

**Aggregates and Aggregate Roots (Sprint 2 Implementation):**

| Aggregate Root   | Bounded Context    | Entities                   | Value Objects              |
| ---------------- | ------------------ | -------------------------- | -------------------------- |
| `User`           | Identity & Access  | —                          | `Role`, `AccountStatus`    |
| `VendingMachine` | Machine Management | `Slot`, `MachineTelemetry` | `MachineStatus`            |
| `Product`        | Product Catalog    | —                          | —                          |
| `Sale`           | Sales              | `IdempotencyRecord`        | `PaymentInfo`, `PayStatus` |

**Cross-Aggregate References:** All cross-aggregate references use **database IDs** (foreign keys), not direct object references. For example, `Slot` references `VendingMachine` and `Product` via `@ManyToOne`, and `Sale` references all three aggregates via `machine_id`, `product_id`, and `user_id` foreign keys. This is consistent with DDD best practices for aggregate design and matches the Phase 1 cross-context ID references.

**Concurrency Control:**

- `Slot.version` uses JPA `@Version` for optimistic locking.
- Purchase operations additionally use `@Lock(PESSIMISTIC_WRITE)` at the repository level (`JpaSlotRepository`) to prevent lost inventory updates under concurrent access.

### 1.3 Repository Structure Explanation

The project follows a DDD-inspired package-by-layer structure. Each layer has a clear, enforced dependency direction: **API → Application → Domain ← Infrastructure**.

```
vendnet/src/main/java/pt/isep/desofs/vendnet/
├── VendnetApplication.java          # Spring Boot entry point
├── api/                              # Interface Adapters (inward-facing)
│   ├── controller/                   # 15 REST controllers
│   ├── dto/                          # 18 request/response DTOs
│   └── view/                         # ApiError response model
├── application/                      # Application Layer
│   └── service/                      # 9 orchestration services
├── domain/                           # Domain Layer (core, no dependencies)
│   ├── exception/                    # 15 domain-specific exceptions
│   ├── model/                        # Entities, Value Objects, Enums
│   │   ├── audit/                    # AuditLog entity
│   │   ├── machine/                  # VendingMachine + MachineStatus
│   │   ├── product/                  # Product + PaymentInfo (VO)
│   │   ├── sale/                     # Sale + PayStatus + IdempotencyRecord
│   │   ├── slot/                     # Slot entity
│   │   ├── telemetry/                # MachineTelemetry entity
│   │   └── user/                     # User + Role + AccountStatus
│   └── repository/                   # 8 repository interfaces (ports)
├── infrastructure/                   # Infrastructure Layer (outward-facing)
│   ├── file/                         # File storage, validation, EXIF stripping
│   ├── logging/                      # AuditLogger
│   ├── os/                           # Backup, path validation, reports, log rotation
│   ├── payment/                      # Payment gateway (simulator)
│   ├── persistence/                  # 8 JPA repository implementations
│   └── security/                     # 5 security filters
└── config/                           # Spring configuration
    ├── SecurityConfig.java           # Security filter chain, RBAC hierarchy
    ├── BootstrapProfileConfig.java   # Bootstrap seed data runner
    ├── BootstrapHealthIndicator.java # Health check during seeding
    ├── BootstrapReadyIndicator.java  # Readiness flag
    └── OpenApiConfig.java           # Swagger/OpenAPI configuration
```

**Layer Responsibilities:**

| Layer              | Responsibility                                                                             | Dependency Rule              |
| ------------------ | ------------------------------------------------------------------------------------------ | ---------------------------- |
| **API**            | HTTP request/response handling, DTO mapping, input validation, `@PreAuthorize` enforcement | Depends on Application       |
| **Application**    | Use case orchestration, transaction management, business rule coordination                 | Depends on Domain            |
| **Domain**         | Business logic, invariants, entity state transitions, repository interfaces                | No outward dependencies      |
| **Infrastructure** | Technical implementations: JPA, filesystem, OS commands, payment simulation                | Implements Domain interfaces |
| **Config**         | Spring wiring, security filter chain assembly, bootstrap lifecycle                         | Wires all layers             |

**Enforced by ArchUnit Tests:** The dependency rules are verified by `LayeredArchitectureTest.java` and `architecture/LayeredArchitectureArchTest.java`, which assert:

- Domain does not depend on Application, Infrastructure, or API.
- Controllers do not call repositories directly.
- Repository interfaces reside only in `domain.repository`.
- `@Service` classes reside only in `application.service` or `infrastructure`.
- `@RestController` classes reside only in `api.controller`.

### 1.4 Database Schema Diagram

The following ER diagram is derived from actual JPA entity annotations and Hibernate's DDL auto-generation (`spring.jpa.hibernate.ddl-auto=update`).

```mermaid
erDiagram
    users ||--o{ sales : "user_id FK"
    vending_machines ||--o{ slots : "machine_id FK"
    vending_machines ||--o{ sales : "machine_id FK"
    vending_machines ||--o{ machine_telemetry : "machine_id FK"
    products ||--o{ slots : "product_id FK"
    products ||--o{ sales : "product_id FK"

    users {
        bigint id PK
        varchar username UK
        varchar email UK
        varchar password
        varchar name
        varchar role
        varchar account_status
        datetime created_at
        datetime updated_at
        int failed_attempts
        datetime lock_time
        datetime last_failed_attempt_time
        varchar totp_secret
    }

    vending_machines {
        bigint id PK
        varchar code UK
        varchar location
        boolean active
        varchar status
        datetime last_telemetry_at
        datetime created_at
        datetime updated_at
    }

    products {
        bigint id PK
        varchar name
        varchar description
        decimal price
        varchar sku UK
        varchar currency
        varchar category
        varchar image_url
        varchar image_checksum
        boolean active
        datetime created_at
        datetime updated_at
    }

    slots {
        bigint id PK
        varchar position
        int capacity
        int current_stock
        bigint version
        bigint machine_id FK
        bigint product_id FK
        datetime created_at
        datetime updated_at
    }

    sales {
        bigint id PK
        bigint machine_id FK
        bigint product_id FK
        bigint user_id FK
        decimal price
        int quantity
        decimal total_amount
        decimal unit_price
        varchar payment_method
        varchar transaction_ref
        varchar payment_status
        datetime sale_date
        datetime created_at
    }

    machine_telemetry {
        bigint id PK
        bigint machine_id FK
        decimal cpu_usage
        decimal memory_usage
        decimal disk_usage
        varchar status
        bigint uptime_seconds
        int total_sales_today
        decimal temperature_celsius
        datetime timestamp
    }

    audit_logs {
        bigint id PK
        varchar event_type
        varchar principal
        text details
        varchar resource
        varchar action
        varchar outcome
        varchar ip_address
        datetime timestamp
        varchar integrity_hash
    }

    idempotency_records {
        bigint id PK
        varchar idempotency_key UK
        varchar response_status
        text response_body
        bigint sale_id
        datetime created_at
    }
```

**Schema Notes:**

- All primary keys use `GenerationType.IDENTITY` (auto-increment).
- `sales.payment_method`, `sales.transaction_ref`, and `sales.payment_status` are embedded columns from the `@Embeddable PaymentInfo` value object.
- `slots.version` is a JPA `@Version` column for optimistic concurrency control.
- No SQL migration files exist; Hibernate auto-DDL is used (`ddl-auto=update`).
- `audit_logs` and `idempotency_records` are standalone tables with no foreign key relationships.

---

## 2. DDD Requirements

### 2.1 At Least 3 Aggregates

The implementation provides **four** aggregate roots, exceeding the minimum requirement.

#### Aggregate 1: User (Identity & Access)

| Aspect             | Detail                                                                                                                                                   |
| ------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Aggregate Root** | `User` (`domain/model/user/User.java`)                                                                                                                   |
| **Purpose**        | Manages identity, authentication state, and authorization role for all human actors                                                                      |
| **Entities**       | — (single-entity aggregate)                                                                                                                              |
| **Value Objects**  | `Role` (enum), `AccountStatus` (enum)                                                                                                                    |
| **Invariants**     | Account locks after 5 failed attempts within 15 minutes; auto-unlock after 30 minutes; suspended accounts remain permanently disabled until admin action |
| **Repository**     | `UserRepository` → `JpaUserRepository`                                                                                                                   |

**Key invariants implemented in `User.java:74-121`:**

```java
// Maximum failed attempts before account lockout
private static final int MAX_FAILED_ATTEMPTS = 5;
// Auto-unlock duration
private static final int LOCK_DURATION_MINUTES = 30;
// Reset window for failed attempts
private static final int LOCK_WINDOW_MINUTES = 15;
```

The `checkAccountStatus()` method enforces that SUSPENDED users are always rejected, LOCKED users are auto-unlocked after the duration or rejected, and ACTIVE users proceed.

#### Aggregate 2: Product (Product Catalog)

| Aspect             | Detail                                                                                                          |
| ------------------ | --------------------------------------------------------------------------------------------------------------- |
| **Aggregate Root** | `Product` (`domain/model/product/Product.java`)                                                                 |
| **Purpose**        | Represents items available for sale in vending machines                                                         |
| **Entities**       | — (single-entity aggregate)                                                                                     |
| **Value Objects**  | —                                                                                                               |
| **Invariants**     | SKU must be unique; price must be positive with max 2 decimal places; name max 100 chars with no HTML fragments |
| **Repository**     | `ProductRepository` → `JpaProductRepository`                                                                    |

**Validation rules enforced in `ProductService.java`:**

- Name: no null/blank, max 100 characters, rejects `<script`, `</`, `>`.
- Description: max 500 characters.
- Price: not null, > 0, max 2 decimal places.
- Currency: must match `^[A-Z]{3}$` (ISO 4217).
- Category: must match `^[A-Z_]{2,50}$`.

#### Aggregate 3: VendingMachine (Machine Management)

| Aspect             | Detail                                                                                                                                                       |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Aggregate Root** | `VendingMachine` (`domain/model/machine/VendingMachine.java`)                                                                                                |
| **Purpose**        | Represents a physical vending machine and its operational state                                                                                              |
| **Entities**       | `Slot`, `MachineTelemetry`                                                                                                                                   |
| **Value Objects**  | `MachineStatus` (enum: ONLINE, OFFLINE, MAINTENANCE, DECOMMISSIONED)                                                                                         |
| **Invariants**     | Sales and restocks only allowed when status is ONLINE or MAINTENANCE; machine must be active for telemetry ingestion; certificate CN must match machine code |
| **Repository**     | `VendingMachineRepository` → `JpaVendingMachineRepository`                                                                                                   |

**Key invariant in `VendingMachine.java:checkStatus()`:**

```java
public void checkStatus() {
    if (this.status != MachineStatus.ONLINE
            && this.status != MachineStatus.MAINTENANCE) {
        throw new MachineOfflineException(
            "Machine is not operational");
    }
}
```

The `Slot` entity (part of this aggregate) enforces inventory invariants:

```java
// Slot.java:63-68
public void reserveUnit() {
    if (this.currentStock <= 0) {
        throw new IllegalStateException("Slot is empty, cannot reserve");
    }
    this.currentStock--;
}

// Slot.java:76-86
public void addStock(int quantity) {
    if (quantity <= 0) {
        throw new IllegalArgumentException("Quantity must be positive");
    }
    int newStock = this.currentStock + quantity;
    if (newStock > this.capacity) {
        throw new CapacityExceededException(
            "Exceeds slot capacity: " + newStock + " > " + this.capacity);
    }
    this.currentStock = newStock;
}
```

#### Aggregate 4: Sale (Sales)

| Aspect             | Detail                                                                                                                                            |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Aggregate Root** | `Sale` (`domain/model/sale/Sale.java`)                                                                                                            |
| **Purpose**        | Represents a completed or attempted purchase transaction; immutable after creation                                                                |
| **Entities**       | `IdempotencyRecord`                                                                                                                               |
| **Value Objects**  | `PaymentInfo` (`@Embeddable`: method, transactionRef, status), `PayStatus` (enum)                                                                 |
| **Invariants**     | `totalAmount = price × quantity`; `unitPrice` is a snapshot taken from the product at purchase time; idempotency key prevents duplicate purchases |
| **Repository**     | `SaleRepository` → `JpaSaleRepository`                                                                                                            |

The `PaymentInfo` value object is embedded directly into the `Sale` table:

```java
@Embeddable
public class PaymentInfo {
    private String method;
    private String transactionRef;
    private String status;
}
```

### 2.2 Aggregate Boundaries

**Ownership Rules:**

- `VendingMachine` owns `Slot` and `MachineTelemetry`. Slots cannot exist without a machine. Telemetry records are time-series data points tied to a specific machine.
- `Sale` owns `PaymentInfo` as an embedded value object — the payment information has no identity outside the sale.
- `User`, `Product`, `VendingMachine`, and `Sale` are independent aggregate roots. They reference each other **by ID only** (via foreign keys), not by direct object navigation.

**Transactional Boundaries:**

- Each aggregate is modified within its own transaction boundary.
- The `purchase()` operation in `SaleService` is the only cross-aggregate transaction — it atomically reserves stock (`Slot`), processes payment, and creates a `Sale` record, all within a single `@Transactional` boundary. This is justified because inventory reservation and sale recording form a single business transaction.
- All other operations modify one aggregate at a time.

**Interaction Between Aggregates:**

The purchase process is the most complex cross-aggregate interaction. The following C4 Level 3 sequence diagram (from Phase 1) traces the flow through all Clean Architecture layers — from the SalesController (Interface Adapters), through the PurchaseService and IdempotencyService (Application), into the Sale and Slot aggregates (Domain), and down to the SaleRepository, SlotRepository, and PaymentGatewayClient (Infrastructure).

![C4 Level 3 — Purchase Process SVG](../../../Phase1/System-To-Be/C4/C4_Level3/Process_view/Purchase_process/svg/L3_Purchase_process.svg)

_Source: `Deliverables/Phase1/System-To-Be/C4/C4_Level3/Process_view/Purchase_process/L3_Purchase_process.puml` (158 lines)_

The diagram captures all critical decision points: idempotency check, `SELECT ... FOR UPDATE` pessimistic locking on slots, `Slot.reserveUnit()` domain invariant enforcement, payment gateway integration with four failure modes (authorised, declined, timeout, network failure), optimistic lock release on failure, and idempotency result caching.

A C4 Level 1 (System Context) view of the same process is also available:

![C4 Level 1 — Purchase Process SVG](../../../Phase1/System-To-Be/C4/C4_Level1/Process_view/Purchase_process/svg/L1_Purchase_process.svg)

_Source: `Deliverables/Phase1/System-To-Be/C4/C4_Level1/Process_view/Purchase_process/L1_Purchase_process.puml`_

**Why This Modeling:**

- **Slot is part of VendingMachine aggregate** because slot lifecycle (creation, deletion) is tied to a machine. However, Slot has its own identity and `@Version`-based concurrency control, making it a true entity within the aggregate.
- **IdempotencyRecord is a separate entity** (not embedded in Sale) because idempotency must be checked **before** the sale is created. It serves as a guard for the entire purchase flow.
- **User is a separate aggregate** from Sale to prevent loading user data during every sale query. Sales reference users by ID only.

### 2.3 Domain Responsibilities

| Responsibility               | Layer          | Examples                                                                                                                                                                       |
| ---------------------------- | -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Business rules**           | Domain         | `User.checkAccountStatus()`, `Slot.reserveUnit()`, `Slot.addStock()`, `VendingMachine.checkStatus()`, password verification, lockout logic                                     |
| **Use case orchestration**   | Application    | `AuthService.login()` (user lookup → password check → JWT issuance → audit), `SaleService.purchase()` (idempotency → stock reservation → payment → sale recording)             |
| **Technical infrastructure** | Infrastructure | JPA persistence, `ProcessBuilder` for `mysqldump`, file I/O with NIO, JWT parsing/filtering, X.509 certificate extraction, HMAC verification                                   |
| **Security enforcement**     | API + Config   | `@PreAuthorize` annotations on controllers, `SecurityFilterChain` URL-based access control, `JwtAuthenticationFilter` token validation, `X509MachineAuthenticationFilter` mTLS |

**Where Business Rules Are Enforced:**

- **Account lockout:** `User.incrementFailedAttempts()` → `User.lockAccount()` in the domain entity (not in a service). The `AuthService` calls these methods, but the rules are encapsulated in the domain model.
- **Inventory capacity:** `Slot.addStock(int)` throws `CapacityExceededException` if the new stock exceeds capacity. The `SlotService` calls this, but the invariant lives in `Slot`.
- **Machine availability:** `VendingMachine.checkStatus()` throws `MachineOfflineException` if the status is neither ONLINE nor MAINTENANCE. Called by `SaleService` and `SlotService` before operations.

### 2.4 Encapsulation of Business Rules

#### Validation Rules

**DTO-level validation** (API layer, using Jakarta Bean Validation):

```java
// LoginRequest.java
@NotBlank @Size(min = 3, max = 30)
@Pattern(regexp = "^[A-Za-z0-9]+$")
private String username;

@NotBlank @Size(min = 6, max = 100)
private String password;

// CreateUserRequest.java
@NotBlank @Size(min = 12)
@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$")
private String password;
```

**Entity-level invariants** (Domain layer):

```java
// Slot.java — enforces 0 ≤ currentStock ≤ capacity
public void reserveUnit() {
    if (this.currentStock <= 0) {
        throw new IllegalStateException("Slot is empty, cannot reserve");
    }
    this.currentStock--;
}

public void addStock(int quantity) {
    if (quantity <= 0) {
        throw new IllegalArgumentException("Quantity must be positive");
    }
    int newStock = this.currentStock + quantity;
    if (newStock > this.capacity) {
        throw new CapacityExceededException(
            "Exceeds slot capacity: " + newStock + " > " + this.capacity);
    }
    this.currentStock = newStock;
}
```

#### State Transitions

**User Account Status FSM:**

```mermaid
stateDiagram-v2
    [*] --> ACTIVE : Account created
    ACTIVE --> LOCKED : 5 failed attempts<br/>within 15 minutes
    LOCKED --> ACTIVE : Auto-unlock after<br/>30 minutes
    ACTIVE --> SUSPENDED : Admin action
    SUSPENDED --> ACTIVE : Admin action
```

Implemented in `User.java:74-121`:

```java
public void checkAccountStatus() {
    if (this.accountStatus == AccountStatus.SUSPENDED) {
        throw new DisabledException("Account is suspended");
    }
    if (this.accountStatus == AccountStatus.LOCKED) {
        if (this.lockTime != null
                && this.lockTime.plusMinutes(LOCK_DURATION_MINUTES)
                        .isBefore(LocalDateTime.now())) {
            resetLockout(); // Auto-unlock
            return;
        }
        throw new AccountLockedException(
            "Account is temporarily locked...");
    }
}
```

**Vending Machine Status:**

| Transition     | Allowed Operations                                   |
| -------------- | ---------------------------------------------------- |
| ONLINE         | All operations (sales, restock, telemetry)           |
| MAINTENANCE    | Sales and restock allowed (telemetry still accepted) |
| OFFLINE        | Only telemetry and status queries                    |
| DECOMMISSIONED | Nothing (machine is permanently retired)             |

#### Business Methods

| Method                           | Class            | Business Rule                                                         |
| -------------------------------- | ---------------- | --------------------------------------------------------------------- |
| `reserveUnit()`                  | `Slot`           | Decrement stock; throw if empty                                       |
| `releaseReservation()`           | `Slot`           | Increment stock (idempotent, capped at capacity)                      |
| `addStock(int)`                  | `Slot`           | Add stock; throw if exceeds capacity or quantity ≤ 0                  |
| `checkStatus()`                  | `VendingMachine` | Throw if machine is OFFLINE or DECOMMISSIONED                         |
| `checkAccountStatus()`           | `User`           | Throw if LOCKED (unless expired) or SUSPENDED                         |
| `incrementFailedAttempts()`      | `User`           | Increment counter; lock account at threshold; reset if window expired |
| `verifyPassword(String, String)` | `User`           | BCrypt constant-time comparison                                       |

---

## 3. Backend API

### 3.1 API Overview

| Aspect                     | Detail                                                                                                       |
| -------------------------- | ------------------------------------------------------------------------------------------------------------ |
| **Architecture Style**     | RESTful over HTTPS                                                                                           |
| **API Documentation**      | OpenAPI 3.0 via SpringDoc (Swagger UI at `/swagger-ui.html`)                                                 |
| **Content Type**           | `application/json` (except product image upload: `multipart/form-data`)                                      |
| **Authentication**         | JWT Bearer tokens (HS256) for human users; X.509 mTLS client certificates for machines                       |
| **Authorization**          | Role-based access control (RBAC) with role hierarchy: `ADMINISTRATOR > OPERATOR`, `ADMINISTRATOR > CUSTOMER` |
| **Session Management**     | Stateless — no HTTP sessions                                                                                 |
| **CSRF Protection**        | Disabled (appropriate for stateless token-based API)                                                         |
| **Error Response Format**  | Standardized `ApiError` JSON: `{status, error, message, timestamp}`                                          |
| **Information Disclosure** | Disabled: `server.error.include-message=never`, `include-stacktrace=never`, `include-exception=false`        |
| **Serialization**          | Jackson: `fail-on-unknown-properties=true` (prevents mass assignment); `default-property-inclusion=non_null` |
| **File Upload**            | Max 5 MB per file, max 10 MB per request; images only (JPEG, PNG, WebP)                                      |

### 3.2 Endpoint Documentation

#### Authentication Endpoints

| Method | Path                   | Purpose                      | Auth              | Request Body       | Response         | Status Codes  |
| ------ | ---------------------- | ---------------------------- | ----------------- | ------------------ | ---------------- | ------------- |
| POST   | `/api/auth/register`   | Register new user account    | `permitAll`       | `RegisterRequest`  | `AuthResponse`   | 200, 400      |
| POST   | `/api/auth/login`      | Authenticate and receive JWT | `permitAll`       | `LoginRequest`     | `AuthResponse`   | 200, 401, 423 |
| POST   | `/api/auth/mfa/verify` | Verify MFA code for admin    | `permitAll`       | `MfaVerifyRequest` | `AuthResponse`   | 200, 401      |
| GET    | `/api/auth/me`         | Get current user profile     | `isAuthenticated` | —                  | `UserResponse`   | 200, 401      |
| GET    | `/api/auth/claims`     | Get current JWT claims       | `isAuthenticated` | —                  | `ClaimsResponse` | 200, 401      |

#### Machine Management Endpoints

| Method | Path                   | Purpose                   | Auth                      | Request Body           | Response               | Status Codes |
| ------ | ---------------------- | ------------------------- | ------------------------- | ---------------------- | ---------------------- | ------------ |
| GET    | `/api/machines`        | List all vending machines | `CUSTOMER/OPERATOR/ADMIN` | —                      | `List<VendingMachine>` | 200          |
| GET    | `/api/machines/{code}` | Find machine by code      | `CUSTOMER/OPERATOR/ADMIN` | —                      | `VendingMachine`       | 200, 404     |
| POST   | `/api/machines`        | Create new machine        | `ADMINISTRATOR`           | `CreateMachineRequest` | `VendingMachine`       | 201, 400     |
| PUT    | `/api/machines/{id}`   | Update machine            | `ADMINISTRATOR`           | `UpdateMachineRequest` | `VendingMachine`       | 200, 404     |

#### Slot / Inventory Endpoints

| Method | Path                                               | Purpose                  | Auth             | Request Body     | Response             | Status Codes  |
| ------ | -------------------------------------------------- | ------------------------ | ---------------- | ---------------- | -------------------- | ------------- |
| GET    | `/api/machines/{machineId}/slots`                  | List slots for a machine | `OPERATOR/ADMIN` | —                | `List<SlotResponse>` | 200           |
| PUT    | `/api/machines/{machineId}/slots/{slotId}/restock` | Restock a slot           | `OPERATOR/ADMIN` | `RestockRequest` | `SlotResponse`       | 200, 409, 422 |

#### Telemetry Endpoint

| Method | Path             | Purpose                  | Auth                | Request Body       | Response            | Status Codes       |
| ------ | ---------------- | ------------------------ | ------------------- | ------------------ | ------------------- | ------------------ |
| POST   | `/api/telemetry` | Ingest machine telemetry | `permitAll` (X.509) | `TelemetryRequest` | `TelemetryResponse` | 200, 401, 403, 429 |

#### Product Catalog Endpoints

| Method | Path                  | Purpose                   | Auth              | Request Body           | Response        | Status Codes |
| ------ | --------------------- | ------------------------- | ----------------- | ---------------------- | --------------- | ------------ |
| GET    | `/api/products`       | List active products      | `isAuthenticated` | —                      | `List<Product>` | 200          |
| GET    | `/api/products/{sku}` | Find product by SKU       | `isAuthenticated` | —                      | `Product`       | 200, 404     |
| POST   | `/api/products`       | Create product with image | `ADMINISTRATOR`   | Multipart form         | `Product`       | 200, 400     |
| PUT    | `/api/products/{id}`  | Update product            | `ADMINISTRATOR`   | `UpdateProductRequest` | `Product`       | 200, 404     |
| POST   | `/api/admin/products` | Admin product creation    | `ADMINISTRATOR`   | Multipart form         | `Product`       | 201          |

#### Sales & Purchase Endpoints

| Method | Path                             | Purpose               | Auth             | Request Body      | Response           | Status Codes            |
| ------ | -------------------------------- | --------------------- | ---------------- | ----------------- | ------------------ | ----------------------- |
| GET    | `/api/sales/machine/{machineId}` | List sales by machine | `OPERATOR/ADMIN` | —                 | `List<Sale>`       | 200                     |
| GET    | `/api/sales/me`                  | My purchase history   | `CUSTOMER`       | —                 | `List<Sale>`       | 200                     |
| POST   | `/api/sales/purchase`            | Initiate purchase     | `CUSTOMER`       | `PurchaseRequest` | `PurchaseResponse` | 201, 200, 202, 402, 409 |

#### Admin Endpoints

| Method | Path                                  | Purpose               | Auth            | Request Body        | Response             | Status Codes |
| ------ | ------------------------------------- | --------------------- | --------------- | ------------------- | -------------------- | ------------ |
| GET    | `/api/admin/dashboard`                | Admin dashboard       | `ADMINISTRATOR` | —                   | `Map`                | 200          |
| GET    | `/api/admin/users`                    | List all users        | `ADMINISTRATOR` | —                   | `List<UserResponse>` | 200          |
| POST   | `/api/admin/users`                    | Create user (admin)   | `ADMINISTRATOR` | `CreateUserRequest` | `UserResponse`       | 201          |
| PUT    | `/api/admin/users/{userId}`           | Update user           | `ADMINISTRATOR` | `UpdateUserRequest` | `UserResponse`       | 200          |
| GET    | `/api/admin/reports`                  | Reports access        | `ADMINISTRATOR` | —                   | `Map`                | 200          |
| POST   | `/api/admin/backups`                  | Trigger backup        | `ADMINISTRATOR` | —                   | `BackupResult`       | 201          |
| POST   | `/api/admin/operations/reports/sales` | Generate sales report | `ADMINISTRATOR` | —                   | `Map`                | 200          |

#### Public & Health Endpoints

| Method | Path                    | Purpose          | Auth               | Response                 | Status Codes |
| ------ | ----------------------- | ---------------- | ------------------ | ------------------------ | ------------ |
| GET    | `/api/public/info`      | Application info | `permitAll`        | `Map`                    | 200          |
| GET    | `/api/health`           | Health check     | `permitAll`        | `"UP"` or `"Seeding..."` | 200, 503     |
| GET    | `/api/health/ping`      | Detailed health  | `permitAll`        | `Map`                    | 200, 503     |
| POST   | `/api/webhooks/payment` | Payment callback | `permitAll` + HMAC | `Map`                    | 200, 401     |

### 3.3 Request/Response Examples

#### Register a new user

**Request:**

```json
POST /api/auth/register
Content-Type: application/json

{
    "email": "john@example.com",
    "password": "SecurePass1!",
    "name": "John Doe"
}
```

**Response (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 86400,
  "email": "john@example.com",
  "username": "john",
  "name": "John Doe",
  "role": "CUSTOMER",
  "mfaRequired": false
}
```

#### Login with valid credentials

**Request:**

```json
POST /api/auth/login
Content-Type: application/json

{
    "username": "john",
    "password": "SecurePass1!"
}
```

**Response (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 86400,
  "email": "john@example.com",
  "username": "john",
  "name": "John Doe",
  "role": "CUSTOMER",
  "mfaRequired": false
}
```

#### Login with invalid credentials

**Response (401 Unauthorized):**

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid credentials",
  "timestamp": "2026-06-15T10:30:00"
}
```

#### Account locked after 5 failed attempts

**Response (423 Locked):**

```json
{
  "status": 423,
  "error": "Account Locked",
  "message": "Account is temporarily locked. Try again in 30 minutes.",
  "timestamp": "2026-06-15T10:35:00"
}
```

#### Purchase a product

**Request:**

```json
POST /api/sales/purchase
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
    "productId": 1,
    "machineId": 1,
    "paymentToken": "tok_test_abc123",
    "idempotencyKey": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response — Success (201 Created):**

```json
{
  "saleId": "42",
  "status": "COMPLETED",
  "transactionRef": "txn_a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "message": "Purchase completed"
}
```

**Response — Duplicate (200 OK):**

```json
{
  "saleId": "42",
  "status": "DUPLICATE",
  "transactionRef": "txn_a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "message": "Duplicate request"
}
```

**Response — Pending Verification (202 Accepted):**

```json
{
  "saleId": "42",
  "status": "PENDING_VERIFICATION",
  "statusUrl": "/api/sales/42/status",
  "message": "Payment is being processed"
}
```

#### Validation error

**Response (400 Bad Request):**

```json
{
  "status": 400,
  "error": "Validation Error",
  "message": "username: size must be between 3 and 30; password: size must be between 6 and 100",
  "timestamp": "2026-06-15T10:30:00"
}
```

### 3.4 Authentication Flow

The authentication flow for human users uses JWT Bearer tokens with the HS256 algorithm. The following C4 sequence diagrams were designed in Phase 1 and are faithfully implemented in Sprint 2.

#### C4 Level 1 (System Context) — Authentication

![C4 Level 1 — Authentication Process SVG](../../../Phase1/System-To-Be/C4/C4_Level1/Process_view/Authentication_process/svg/L1_Authentication_process.svg)

_Source: `Deliverables/Phase1/System-To-Be/C4/C4_Level1/Process_view/Authentication_process/L1_Authentication_process.puml`_

This Level 1 view shows the complete authentication decision tree at the system boundary: input validation, account status checks (ACTIVE/SUSPENDED/LOCKED), BCrypt password comparison, failed attempt tracking with automatic account lockout at threshold, dummy BCrypt hash for missing users (timing attack prevention), JWT generation, and audit event recording.

#### C4 Level 3 (Component) — Authentication

![C4 Level 3 — Authentication Process SVG](../../../Phase1/System-To-Be/C4/C4_Level3/Process_view/Authentication_process/svg/L3_Authentication_process.svg)

_Source: `Deliverables/Phase1/System-To-Be/C4/C4_Level3/Process_view/Authentication_process/L3_Authentication_process.puml` (123 lines)_

This Level 3 view traces the authentication flow through the Clean Architecture layers: `AuthController` (Interface Adapters) validates the `LoginRequest` DTO; `AuthenticationService` (Application) orchestrates the process; `User` aggregate (Domain) enforces `checkAccountStatus()`, `verifyPassword()`, `incrementFailedAttempts()`, and `lockAccount()`; `JwtTokenProvider` (Application) builds and signs the JWT with HMAC-SHA256. All security decisions (account status, password validity, lockout threshold) are encapsulated in the Domain layer.

**Token Generation** (`JwtService.java`):

- Subject: email or user ID
- Claims: `role`, `jti` (UUID for blocklisting), `iat`, `exp`
- Algorithm: HMAC-SHA256 with 256-bit secret key
- Expiry: 24 hours (configurable via `app.jwt.expiration-ms`)
- Refuses to generate tokens with `alg: none` by explicit header inspection

**Token Validation** (`JwtAuthenticationFilter.java`):

- Extracts token from `Authorization: Bearer <token>` header
- Validates signature, expiry, and blocklist status
- Looks up `User` from database; re-checks `AccountStatus` on every request
- Sets `SecurityContextHolder` with `UsernamePasswordAuthenticationToken`
- Blocklists token for SUSPENDED users (prevents reuse)

**X.509 Machine Authentication** (`X509MachineAuthenticationFilter.java`):

- Extracts client certificate CN from `jakarta.servlet.request.X509Certificate` request attribute
- Maps CN to `ROLE_MACHINE` authority for telemetry endpoints
- Stores CN as request attribute for downstream identity verification

### 3.5 Authorization Flow

Authorization is enforced at three layers, all of which are embedded within the C4 process sequence diagrams produced in Phase 1:

1. **URL-based access control** (`SecurityFilterChain`): Public endpoints (`/api/auth/**`, `/api/health/**`, etc.) are `permitAll()`; all others require authentication.
2. **Method-level security** (`@PreAuthorize`): Every controller method carries a role annotation (enforced by `SecurityAnnotationArchTest`).
3. **Service-level checks**: `TelemetryService` and other services perform programmatic authorization (certificate CN matching, machine active status, rate limiting).

The Phase 1 C4 process views explicitly trace authorization decisions within each flow. The following seven C4 Level 1 process diagrams include RBAC checks at the system boundary:

| Process            | Actors / Roles           | Source PUML                                                                    |
| ------------------ | ------------------------ | ------------------------------------------------------------------------------ |
| **Authentication** | User (any role)          | `C4_Level1/Process_view/Authentication_process/L1_Authentication_process.puml` |
| **Registration**   | Administrator only       | `C4_Level1/Process_view/Registration_process/L1_Registration_process.puml`     |
| **Manage Product** | Administrator only       | `C4_Level1/Process_view/ManageProduct_process/L1_ManageProduct_process.puml`   |
| **Purchase**       | Customer only            | `C4_Level1/Process_view/Purchase_process/L1_Purchase_process.puml`             |
| **Restock**        | Operator / Administrator | `C4_Level1/Process_view/Restock_process/L1_Restock_process.puml`               |
| **Telemetry**      | Machine (mTLS X.509)     | `C4_Level1/Process_view/Telemetry_process/L1_Telemetry_process.puml`           |
| **Backup**         | Administrator only       | `C4_Level1/Process_view/Backup_process/L1_Backup_process.puml`                 |

All seven SVGs are available at:
`Deliverables/Phase1/System-To-Be/C4/C4_Level1/Process_view/{process_name}/svg/`

Each diagram shows the RBAC gate as the first decision point after request receipt — for example, the Registration process diagram explicitly checks "Authorized (ADMINISTRATOR role)" before proceeding to input validation.

**Role Hierarchy** (configured in `SecurityConfig.java:106-108`):

```java
@Bean
public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy(
            "ROLE_ADMINISTRATOR > ROLE_OPERATOR\n" +
            "ROLE_ADMINISTRATOR > ROLE_CUSTOMER");
}
```

This means administrators can access both operator and customer endpoints, but operators and customers cannot access each other's endpoints.

**Authorization Layers:**

| Layer             | Mechanism                                                                             | Location                          |
| ----------------- | ------------------------------------------------------------------------------------- | --------------------------------- |
| URL-based         | `SecurityFilterChain.requestMatchers().permitAll()` / `.anyRequest().authenticated()` | `SecurityConfig.java:67-78`       |
| Controller method | `@PreAuthorize("hasRole('...')")`                                                     | Every public controller method    |
| Service method    | `@PreAuthorize` (on `AuthService`, `UserManagementService`, etc.)                     | `application/service/`            |
| Tests enforced    | ArchUnit: every public controller method must have `@PreAuthorize`                    | `SecurityAnnotationArchTest.java` |

---

## 4. Database

### 4.1 Relational Database Used

| Aspect                   | Detail                                                                                                                                                                             |
| ------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **DBMS**                 | MySQL 8.4                                                                                                                                                                          |
| **Why MySQL**            | Required by project constraints (C-02). MySQL provides ACID transactions, row-level locking (used for `PESSIMISTIC_WRITE` on slots), and strong referential integrity enforcement. |
| **Test/Dev alternative** | H2 in-memory database (for CI and development environments via profiles)                                                                                                           |
| **Integration**          | Spring Data JPA + Hibernate 6.x ORM, configured via `spring.datasource.*` properties                                                                                               |
| **Connection Pool**      | HikariCP (Spring Boot default)                                                                                                                                                     |
| **DDL Strategy**         | `spring.jpa.hibernate.ddl-auto=update` — schema auto-generated from JPA entity annotations                                                                                         |

### 4.2 Database Configuration

**Connection Configuration** (`application.properties`):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/vendnet
spring.datasource.username=vendnet_user
spring.datasource.password=vendnet_pass
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

**Spring Profiles:**

| Profile     | Properties File                    | Database                                                              |
| ----------- | ---------------------------------- | --------------------------------------------------------------------- |
| `default`   | `application.properties`           | MySQL 8.4 (`localhost:3306/vendnet`)                                  |
| `dev`       | `application-dev.properties`       | H2 in-memory (configured via Docker Compose `docker-compose.dev.yml`) |
| `test`      | `application-test.properties`      | H2 in-memory                                                          |
| `ci`        | `application-ci.properties`        | H2 in-memory (GitHub Actions)                                         |
| `e2e`       | `application-e2e.properties`       | H2 in-memory (with bootstrap seed data)                               |
| `zap`       | `application-zap.properties`       | H2 in-memory (OWASP ZAP DAST scans)                                   |
| `bootstrap` | `application-bootstrap.properties` | Any (used for seeding test data)                                      |
| `stage`     | `application-stage.properties`     | MySQL (staging server)                                                |
| `prod`      | `application-prod.properties`      | MySQL (production server)                                             |

**Environment Variables:**

- `AUDIT_LOG_HMAC_SECRET` — HMAC secret for audit log integrity verification
- `MYSQL_PWD` — set at runtime by `BackupServiceImpl` when spawning `mysqldump` (never in configuration files)

**Docker Integration:**

- `docker-compose.yml` defines MySQL 8.4 service with volume mounts for data persistence
- `docker-compose.dev.yml` overrides to use H2 in-memory (no MySQL container needed for local development)
- `docker-compose.prod.yml` uses hardened MySQL configuration

### 4.3 Migration Strategy

The project uses **Hibernate auto-DDL** (`ddl-auto=update`) rather than a dedicated migration framework. This means:

- Database schema is derived from JPA entity annotations at application startup.
- Hibernate generates `CREATE TABLE`, `ALTER TABLE`, and constraint changes automatically.
- No Flyway or Liquibase integration exists.
- **This is appropriate for the development/Sprint phase** but should be replaced with a versioned migration tool (Flyway/Liquibase) for production to ensure reproducible schema evolution.

**Versioning Strategy:**

- Entity changes are tracked via Git version control of the Java source files.
- The `Slot.version` `@Version` column provides optimistic locking for concurrent data access, not schema versioning.
- Bootstrap data seeding (via `BootstrapService.seed()`) provides consistent initial state for development and testing.

### 4.4 Seed Data

The `BootstrapService` seeds the following data when the `bootstrap` profile is active. All seed methods are **idempotent** — they check for existing records before inserting.

**Seeded Users:**

| Email                 | Password          | Role                 | Purpose                        |
| --------------------- | ----------------- | -------------------- | ------------------------------ |
| `admin@vendnet.io`    | `Admin@123456`    | `ROLE_ADMINISTRATOR` | Full system access             |
| `operator@vendnet.io` | `Operator@123456` | `ROLE_OPERATOR`      | Machine management, restocking |
| `customer@vendnet.io` | `Customer@123456` | `ROLE_CUSTOMER`      | Purchasing, browsing catalog   |

**Seeded Products (8 total):**

| Name          | SKU     | Price    | Category |
| ------------- | ------- | -------- | -------- |
| Coca-Cola     | DRK-001 | EUR 1.50 | DRINK    |
| Water         | DRK-002 | EUR 1.00 | DRINK    |
| Orange Juice  | DRK-003 | EUR 1.80 | DRINK    |
| Potato Chips  | SNK-001 | EUR 1.20 | SNACK    |
| Chocolate Bar | SNK-002 | EUR 1.50 | SNACK    |
| Mixed Nuts    | SNK-003 | EUR 2.00 | SNACK    |
| Hot Coffee    | HOT-001 | EUR 0.80 | HOT      |
| Hot Chocolate | HOT-002 | EUR 1.00 | HOT      |

**Seeded Machines (4 total):**

| Code       | Location        |
| ---------- | --------------- |
| VM-LIS-001 | Lisbon Airport  |
| VM-LIS-002 | Oriente Station |
| VM-PTO-001 | Porto Campanha  |
| VM-FAR-001 | Faro Downtown   |

**Bootstrap Procedure:**

1. `BootstrapProfileConfig` (active with `bootstrap` profile) runs as a `CommandLineRunner` on startup.
2. Repairs legacy slot versions: `UPDATE slots SET version = 0 WHERE version IS NULL`.
3. Calls `bootstrapService.seed()`.
4. Calls `readyIndicator.markReady()`.
5. `BootstrapHealthIndicator` reports `DOWN` until `markReady()` completes, preventing load balancers from routing traffic to an unseeded instance.

---

## 5. Authentication & Authorization

### 5.1 Authentication Implemented

The system implements two distinct authentication mechanisms:

#### Human User Authentication (JWT)

1. **Login:** `POST /api/auth/login` validates username/email + password against BCrypt-hashed credentials.
2. **Token Issuance:** On success, `JwtService` generates a signed JWT (HS256, 256-bit key, 24h expiry) containing the user's email as subject, role as a claim, and a unique JWT ID (`jti`) for blocklisting.
3. **Token Validation:** `JwtAuthenticationFilter` intercepts every authenticated request, extracts the Bearer token, validates signature and expiry, checks the blocklist, and re-verifies the user's account status from the database.
4. **MFA for Administrators:** Admin accounts with a `totpSecret` configured receive `mfaRequired=true` on login. They must call `POST /api/auth/mfa/verify` with a TOTP code before receiving a JWT.

#### Machine Authentication (X.509 mTLS)

1. **Certificate Extraction:** `X509MachineAuthenticationFilter` reads the client certificate from the `jakarta.servlet.request.X509Certificate` request attribute (set by the servlet container after mTLS handshake).
2. **CN Extraction:** Parses the certificate Subject DN to extract the Common Name.
3. **Identity Mapping:** The CN must match the machine's `serialNumber` in the telemetry payload (verified in `TelemetryService.ingest()`).
4. **Fallback Mode:** For E2E testing, the filter can authenticate via `X-Machine-CN` header when `app.telemetry.allow-header-cn=true`.

### 5.2 At Least 3 Roles

| Role              | Spring Security Name | Purpose                           | Permissions                                                                                     |
| ----------------- | -------------------- | --------------------------------- | ----------------------------------------------------------------------------------------------- |
| **Administrator** | `ROLE_ADMINISTRATOR` | Full system management            | User CRUD, machine CRUD, product CRUD, backup/restore, reports, audit logs, dashboard access    |
| **Operator**      | `ROLE_OPERATOR`      | Machine management and restocking | View machines and slots, restock inventory, view machine sales, view telemetry                  |
| **Customer**      | `ROLE_CUSTOMER`      | End user of vending machines      | Browse catalog, view machines, purchase products, view own purchase history, manage own profile |

Additionally:

- **Machine** (`ROLE_MACHINE`): Internal role assigned by `X509MachineAuthenticationFilter` for telemetry ingestion. Not visible to human users.

### 5.3 Role Permissions Matrix

| Endpoint                                     | Administrator     | Operator | Customer | Anonymous |
| -------------------------------------------- | ----------------- | -------- | -------- | --------- |
| `POST /api/auth/login`                       | ✓                 | ✓        | ✓        | ✓         |
| `POST /api/auth/register`                    | ✓                 | ✓        | ✓        | ✓         |
| `POST /api/auth/mfa/verify`                  | ✓                 | ✓        | ✓        | ✓         |
| `GET /api/auth/me`                           | ✓                 | ✓        | ✓        | ✗         |
| `GET /api/auth/claims`                       | ✓                 | ✓        | ✓        | ✗         |
| `GET /api/machines`                          | ✓                 | ✓        | ✓        | ✗         |
| `GET /api/machines/{code}`                   | ✓                 | ✓        | ✓        | ✗         |
| `POST /api/machines`                         | ✓                 | ✗        | ✗        | ✗         |
| `PUT /api/machines/{id}`                     | ✓                 | ✗        | ✗        | ✗         |
| `GET /api/machines/{id}/slots`               | ✓                 | ✓        | ✗        | ✗         |
| `PUT /api/machines/{id}/slots/{sid}/restock` | ✓                 | ✓        | ✗        | ✗         |
| `POST /api/telemetry`                        | ✓                 | ✓        | ✓        | ✓ (X.509) |
| `GET /api/products`                          | ✓                 | ✓        | ✓        | ✗         |
| `GET /api/products/{sku}`                    | ✓                 | ✓        | ✓        | ✗         |
| `POST /api/products`                         | ✓                 | ✗        | ✗        | ✗         |
| `PUT /api/products/{id}`                     | ✓                 | ✗        | ✗        | ✗         |
| `POST /api/admin/products`                   | ✓                 | ✗        | ✗        | ✗         |
| `GET /api/sales/machine/{id}`                | ✓                 | ✓        | ✗        | ✗         |
| `GET /api/sales/me`                          | ✓ (via hierarchy) | ✗        | ✓        | ✗         |
| `POST /api/sales/purchase`                   | ✓ (via hierarchy) | ✗        | ✓        | ✗         |
| `GET /api/admin/dashboard`                   | ✓                 | ✗        | ✗        | ✗         |
| `GET /api/admin/users`                       | ✓                 | ✗        | ✗        | ✗         |
| `POST /api/admin/users`                      | ✓                 | ✗        | ✗        | ✗         |
| `PUT /api/admin/users/{id}`                  | ✓                 | ✗        | ✗        | ✗         |
| `POST /api/admin/backups`                    | ✓                 | ✗        | ✗        | ✗         |
| `POST /api/admin/operations/reports/sales`   | ✓                 | ✗        | ✗        | ✗         |
| `POST /api/webhooks/payment`                 | ✓                 | ✓        | ✓        | ✓ (HMAC)  |
| `GET /api/health`                            | ✓                 | ✓        | ✓        | ✓         |
| `GET /api/public/info`                       | ✓                 | ✓        | ✓        | ✓         |

### 5.4 Access Control Examples

**Example 1: Customer attempting admin endpoint**

Verified by `RbacIntegrationTest.java` and `BlackBoxSecurityE2ETest.java`:

```
GET /api/admin/dashboard
Authorization: Bearer <customer_jwt>

Response: 403 Forbidden
{"status":403,"error":"Forbidden","message":"Access Denied","timestamp":"..."}
```

The `@PreAuthorize("hasRole('ADMINISTRATOR')")` annotation on `AdminController.dashboard()` intercepts the request. The customer's role (`ROLE_CUSTOMER`) does not match, and the role hierarchy does not grant upward access.

**Example 2: Unauthenticated access to protected endpoint**

```
GET /api/machines
(no Authorization header)

Response: 401 Unauthorized
{"status":401,"error":"Unauthorized","message":"Authentication required"}
```

The `SecurityFilterChain` denies all unauthenticated requests to non-public endpoints. The custom `authenticationEntryPoint` returns a JSON error (not the default Spring HTML login page).

**Example 3: Operator accessing sales**

```
GET /api/sales/machine/1
Authorization: Bearer <operator_jwt>

Response: 200 OK
[sale1, sale2, ...]
```

Operators have explicit access to `GET /api/sales/machine/{id}` via `@PreAuthorize("hasAnyRole('OPERATOR', 'ADMINISTRATOR')")`.

**Example 4: Cross-user data isolation (IDOR prevention)**

Verified by `BlackBoxSecurityE2ETest.java`:

Customer A purchases products → Customer B calls `GET /api/sales/me` → Customer B sees only their own purchases (zero items from Customer A). Additionally, Customer B cannot access `GET /api/sales/machine/{id}` (403 Forbidden) — machine-wide sales are restricted to operators and admins.

---

## 6. Backend Operating System Functionality

### 6.1 OS Interactions

The backend performs the following operating system interactions:

#### File System Operations

| Operation                           | Location                                                                                                                                 | Module                                           | Details                                                                   |
| ----------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------ | ------------------------------------------------------------------------- |
| **File creation** (product images)  | `/var/vendnet/uploads/products/{UUID}.{ext}`                                                                                             | `FileStorageServiceImpl`                         | Multipart image upload with UUID filenames                                |
| **File reading** (image validation) | Uploaded images                                                                                                                          | `FileValidationServiceImpl`                      | Magic bytes detection, image decode verification                          |
| **File deletion**                   | `/var/vendnet/uploads/{path}`                                                                                                            | `FileStorageServiceImpl.delete()`                | Sandbox-enforced deletion                                                 |
| **Directory creation**              | `/var/vendnet/uploads/`, `/var/vendnet/backups/{date}/`, `/var/vendnet/reports/{type}/{year}/{month}/{day}/`, `/var/vendnet/logs/audit/` | Multiple services                                | Recursive directory creation with POSIX permissions                       |
| **File encryption**                 | Backup files                                                                                                                             | `BackupServiceImpl`                              | AES-256-GCM encryption with random IV; plaintext deleted after encryption |
| **File compression**                | Audit logs                                                                                                                               | `AuditLogRotationServiceImpl`                    | GZIP compression of logs older than 1 day                                 |
| **File checksumming**               | Product images, backups                                                                                                                  | `FileValidationServiceImpl`, `BackupServiceImpl` | SHA-256 checksums for integrity verification                              |
| **HMAC signing**                    | Audit logs                                                                                                                               | `AuditLogRotationServiceImpl`                    | HMAC-SHA256 signatures for tamper detection                               |
| **EXIF metadata removal**           | Uploaded images                                                                                                                          | `ExifStripper`                                   | Re-encodes images to strip metadata                                       |

#### Process Spawning

| Operation           | Command                                                                                                                    | Module              | Details                                                                                  |
| ------------------- | -------------------------------------------------------------------------------------------------------------------------- | ------------------- | ---------------------------------------------------------------------------------------- |
| **Database backup** | `mysqldump -h {host} -P {port} -u {user} --single-transaction --routines --triggers --databases {db} --result-file={path}` | `BackupServiceImpl` | Password via `MYSQL_PWD` env var (not CLI); executable path validated; exit code checked |
| **H2 backup**       | Programmatic via `Script.process()` (reflection)                                                                           | `BackupServiceImpl` | Used when H2 is the active database                                                      |

#### Disk Operations

| Operation           | Module                                             | Details                                       |
| ------------------- | -------------------------------------------------- | --------------------------------------------- |
| **Backup rotation** | `BackupServiceImpl.rotateBackups(30)`              | Deletes backup directories older than 30 days |
| **Log rotation**    | `AuditLogRotationServiceImpl.deleteAfterDays(90)`  | Deletes log files older than 90 days          |
| **Report cleanup**  | `ReportDirectoryServiceImpl.cleanupOldReports(90)` | Deletes report directories older than 90 days |

### 6.2 Security Controls

#### Path Validation

The `PathValidatorImpl` enforces a strict sandbox model for all filesystem operations:

```java
public boolean isValidPath(Path path, Path sandboxRoot) {
    Path realSandbox = sandboxRoot.toRealPath();
    Path realPath;
    if (Files.exists(path)) {
        realPath = path.toRealPath();  // Resolves symlinks
    } else {
        realPath = path.toAbsolutePath().normalize();
    }
    return realPath.startsWith(realSandbox);
}
```

This prevents:

- **Path traversal attacks:** `../` sequences are resolved and checked against the sandbox root.
- **Symlink escapes:** `toRealPath()` follows symbolic links to their real location before checking.

Additional protections:

- **Symlink detection:** `containsSymlink(Path)` checks `Files.isSymbolicLink()` before writing.
- **Filename generation:** Files are stored with random UUID names (not user-supplied names).
- **Whitelist validation:** Report types are whitelisted (`sales`, `inventory`, `machine`, `audit` only).
- **POSIX permissions:** Stored files get `OWNER_READ | OWNER_WRITE | GROUP_READ`; backup directories get `OWNER_READ | OWNER_WRITE | OWNER_EXECUTE`.

#### Exception Handling for OS Operations

All OS operations wrap checked exceptions in domain exceptions:

- `FileStorageException` — for file I/O failures
- `FileValidationException` — for file validation failures
- `BackupException` — for backup/restore failures
- `ReportDirectoryException` — for report directory errors

#### Permission Checks

- Backup operations require `ROLE_ADMINISTRATOR`.
- Report generation requires `ROLE_ADMINISTRATOR`.
- File upload (product images) requires `ROLE_ADMINISTRATOR`.
- The Docker container runs as a non-root `vendnet` user (`Dockerfile` line 27-29).
- The `mysqldump` executable is validated to exist and be executable before execution.

#### Abuse Prevention

- **Rate limiting for telemetry:** Maximum 2 requests per minute per machine (`TelemetryService`).
- **Audit logging for security events:** All OS operations (backup, log rotation, report generation) are audit-logged with event type, principal, and outcome.
- **Backup encryption:** All database dumps are encrypted with AES-256-GCM before storage. The plaintext dump is deleted immediately after encryption.

---

## 7. Security Controls

### 7.1 Input Validation

#### DTO Validation (Bean Validation)

All incoming request DTOs use Jakarta Bean Validation annotations:

| DTO                    | Validated Fields                                                        | Constraints                                                           |
| ---------------------- | ----------------------------------------------------------------------- | --------------------------------------------------------------------- |
| `LoginRequest`         | `username`, `password`                                                  | `@NotBlank`, `@Size(min=3/max=30)`, `@Pattern("^[A-Za-z0-9]+$")`      |
| `RegisterRequest`      | `email`, `password`, `name`                                             | `@NotBlank`, `@Email`, `@Size(min=6/max=100)`, `@Size(min=2/max=100)` |
| `CreateUserRequest`    | `username`, `email`, `password`, `fullName`, `role`                     | `@NotBlank`, `@Email`, `@Size(min=12)`, `@Pattern` (complex password) |
| `PurchaseRequest`      | `productId`, `machineId`, `idempotencyKey`, `paymentToken`              | `@NotNull`, `@NotBlank`                                               |
| `TelemetryRequest`     | `serialNumber`, `temperature`, `stockLevels`, `statusCode`, `timestamp` | `@NotBlank`, `@DecimalMin(-40)`, `@DecimalMax(80)`, `@PastOrPresent`  |
| `CreateMachineRequest` | `code`, `location`                                                      | `@NotBlank`, `@Size(min=3/max=50)`, `@Size(max=200)`                  |

The `@Valid` annotation on controller method parameters triggers validation. Violations are handled by `GlobalExceptionHandler` → `MethodArgumentNotValidException` → 400 with field-level details.

#### Business Validation

Beyond DTO validation, the application layer performs domain-specific validation:

```java
// ProductService.java — name sanitization
if (name.contains("<script") || name.contains("</") || name.contains(">")) {
    // Reject HTML fragments
}

// ProductService.java — currency format
if (!currency.matches("^[A-Z]{3}$")) {
    // Reject invalid ISO 4217 codes
}
```

#### Mass Assignment Prevention

`application.properties` line 45:

```properties
spring.jackson.deserialization.fail-on-unknown-properties=true
```

Unknown JSON properties in request bodies cause deserialization failure (400 Bad Request). This prevents clients from injecting unexpected fields (e.g., attempting to set `unitPrice` during purchase, which is verified by `ac03_clientCannotOverrideCatalogPrice` in the E2E tests).

#### File Upload Validation

The `FileValidationServiceImpl` applies a 5-layer validation pipeline:

1. **Non-empty check:** Rejects null or empty files.
2. **Size limit:** Maximum 5 MB.
3. **Content type whitelist:** `image/jpeg`, `image/png`, `image/webp` only.
4. **Magic bytes verification:** Apache Tika detects actual MIME type from file content.
5. **Cross-check:** Declared content type must match magic bytes.
6. **Image decode check:** `ImageIO.read()` verifies the file is a valid, non-corrupt image.

### 7.2 Error Handling

#### Exception Hierarchy

All 15 custom exceptions extend `RuntimeException`:

```
RuntimeException
├── AccountLockedException       → 423 LOCKED
├── BackupException              → 500 (wrapped by handler)
├── CapacityExceededException    → 422 UNPROCESSABLE_ENTITY
├── DisabledException            → 403 FORBIDDEN
├── FileStorageException         → 500
├── FileValidationException      → 400
├── ForbiddenOperationException  → 403 FORBIDDEN
├── MachineOfflineException      → 409 CONFLICT
├── OutOfStockException          → 409 CONFLICT
├── PaymentDeclinedException     → 402 PAYMENT_REQUIRED
├── PaymentGatewayException      → 502 (generic)
├── RateLimitException           → 429 TOO_MANY_REQUESTS
├── ReportDirectoryException     → 500
├── TotpGenerationException      → 500
└── UnauthorizedException        → 401 UNAUTHORIZED
```

#### Global Exception Handler

`GlobalExceptionHandler.java` (`@RestControllerAdvice`) maps 15 exception types to specific HTTP status codes. All responses use the standardized `ApiError` format:

```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "timestamp": "2026-06-15T10:30:00"
}
```

#### Information Disclosure Prevention

- `server.error.include-message=never` — no detail in default error responses
- `server.error.include-stacktrace=never` — no stack traces exposed
- `server.error.include-exception=false` — no exception class names
- Generic catch-all for unhandled `Exception` returns generic `"An unexpected error occurred"` (never the actual exception message)
- No entity details leaked in 401 responses (same response for "user not found" and "wrong password" via constant-time BCrypt check)

### 7.3 Authorization Checks

Authorization is enforced at three layers:

**Layer 1: URL-based (Spring Security)**

```java
// SecurityConfig.java:67-78
.authorizeHttpRequests(auth -> auth
    .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
    .requestMatchers("/api/health/**").permitAll()
    // ...
    .anyRequest().authenticated()
)
```

**Layer 2: Method-level (@PreAuthorize)**

Every public controller method carries an authorization annotation:

```java
@PreAuthorize("hasRole('ADMINISTRATOR')")
public ResponseEntity<VendingMachine> create(...)

@PreAuthorize("hasAnyRole('CUSTOMER', 'OPERATOR', 'ADMINISTRATOR')")
public ResponseEntity<List<VendingMachine>> findAll()
```

**Layer 3: Service-level checks**

`TelemetryService.ingest()` performs programmatic authorization checks:

- Certificate CN must not be blank → `UnauthorizedException`
- Machine must match certificate CN → `ForbiddenOperationException`
- Machine must be active → `ForbiddenOperationException`
- Rate limit must not be exceeded → `RateLimitException`

**Enforcement Verification:** The `SecurityAnnotationArchTest` ArchUnit test ensures that every public controller method has `@PreAuthorize`, `@Secured`, or `@RolesAllowed`.

### 7.4 Logging Strategy

#### Application Logging

- **Framework:** Logback with `logback-spring.xml` configuration.
- **Format:** Structured JSON logging via Logstash encoder (for Loki ingestion in production).
- **Level:** `DEBUG` for `pt.isep.desofs.vendnet` package.
- **Correlation IDs:** Every request receives a `X-Correlation-Id` (UUID) via `CorrelationIdFilter`, added to both MDC and response headers.

#### Security Logging

The `AuditLogger` component logs structured security events to the application log stream:

```
AUDIT {eventType=LOGIN_SUCCESS, principal=admin@vendnet.io, action=LOGIN, ...}
```

#### Audit Logging (Database-Persisted)

The `AuditLog` entity persists security-relevant events to the database with fields:

| Field           | Example Value                                                                                                                           |
| --------------- | --------------------------------------------------------------------------------------------------------------------------------------- |
| `eventType`     | `LOGIN_SUCCESS`, `LOGIN_FAILED`, `ACCOUNT_LOCKED`, `BACKUP_CREATED`, `TELEMETRY_INGESTED`, `TELEMETRY_ALERT`, `RESTOCK`, `USER_CREATED` |
| `principal`     | Email or username                                                                                                                       |
| `resource`      | Entity type affected                                                                                                                    |
| `action`        | Operation performed                                                                                                                     |
| `outcome`       | `SUCCESS`, `FAILURE`, `DENIED`                                                                                                          |
| `ipAddress`     | Client IP                                                                                                                               |
| `integrityHash` | SHA-256 hash for tamper evidence                                                                                                        |

**What is intentionally NOT logged:**

- Passwords (never logged)
- JWT tokens (never logged in full)
- Payment tokens (never logged)
- TOTP secrets (never logged)
- Full request bodies for sensitive endpoints

#### Audit Log Integrity

- `AuditLogRotationService` creates HMAC-SHA256 signatures for rotated log files.
- `AuditLog.integrityHash` stores a SHA-256 hash of the record content for database-level tamper detection.

### 7.5 Sensitive Data Handling

| Data Type                      | Storage                                                                                                 | Transport                 | Processing                                              |
| ------------------------------ | ------------------------------------------------------------------------------------------------------- | ------------------------- | ------------------------------------------------------- |
| **Passwords**                  | BCrypt hash (cost factor 12) in `users.password`                                                        | Over HTTPS only           | Compared with `BCrypt.checkpw()` (constant-time)        |
| **JWT Secrets**                | `application.properties` (`app.jwt.secret`) — must be overridden via environment variable in production | Over HTTPS only           | Used for HMAC-SHA256 signing                            |
| **JWT Tokens**                 | In-memory blocklist (`ConcurrentHashMap`) — lost on restart                                             | Over HTTPS only           | Validated per-request; blocklisted on logout/suspension |
| **TOTP Secrets**               | Base64-encoded in `users.totp_secret`                                                                   | Over HTTPS only           | Generated with `SecureRandom`; 20-byte entropy          |
| **Backup Encryption Key**      | In-memory only (generated at `BackupServiceImpl` construction) — ephemeral                              | N/A                       | AES-256-GCM encryption of backup files                  |
| **Audit HMAC Secret**          | From `AUDIT_LOG_HMAC_SECRET` env var or auto-generated (ephemeral)                                      | N/A                       | HMAC-SHA256 signing of rotated log files                |
| **MySQL Password (mysqldump)** | Set via `MYSQL_PWD` environment variable for subprocess only                                            | Never appears in CLI args | Used by `ProcessBuilder` with environment isolation     |
| **Webhook HMAC Secret**        | `app.payment.webhook-secret` in configuration                                                           | Over HTTPS only           | Constant-time comparison with `MessageDigest.isEqual()` |

### 7.6 Security Assumptions and Limitations

#### Assumptions

1. **TLS termination at reverse proxy:** The application assumes TLS is terminated by Nginx or a load balancer. The Spring Boot application runs on HTTP behind a TLS-terminating proxy in production.
2. **mTLS handled by container:** X.509 certificate extraction relies on the servlet container (Tomcat/Nginx) setting the `jakarta.servlet.request.X509Certificate` attribute.
3. **Client-side idempotency key generation:** The idempotency guarantee depends on clients generating unique, collision-resistant keys (UUIDs).
4. **Secure secret management in production:** Default secrets in `application.properties` are placeholders and must be overridden in production environments.

#### Known Limitations

1. **In-memory JWT blocklist:** Token blocklisting is stored in a `ConcurrentHashMap`, which is lost on application restart. This is explicitly noted in `JwtService.java` as "acceptable for this dev phase." Production would require a shared cache (Redis) or short-lived tokens with refresh rotation.
2. **No refresh token rotation:** Refresh tokens are not rotated on use, which increases the window for token theft.
3. **Hibernate auto-DDL (`ddl-auto=update`):** No versioned migration framework (Flyway/Liquibase) is in place. Schema changes are not auditable or reproducible across environments.
4. **Ephemeral encryption keys:** Backup encryption and audit HMAC keys are generated at application startup and lost on restart. Backups encrypted in a previous session cannot be decrypted after restart.
5. **No distributed rate limiting:** Telemetry rate limiting (2 req/min/machine) is per-instance. In a horizontally scaled deployment, each instance would enforce its own limit independently.
6. **No password rotation policy:** There is no mechanism to enforce password expiration or history.

#### Future Improvements

1. Replace `ddl-auto=update` with Flyway versioned migrations.
2. Implement refresh token rotation with reuse detection.
3. Replace in-memory token blocklist with Redis for multi-instance deployments.
4. Use a key management service (KMS) or persistent key store for backup encryption keys.
5. Add distributed rate limiting (e.g., via Redis) for telemetry endpoints.
6. Implement password expiration and history policies.

---

## 8. Testing Traceability

### 8.1 Requirement → Implementation Mapping

| Requirement                        | Category | Implementation                                                                     |
| ---------------------------------- | -------- | ---------------------------------------------------------------------------------- |
| FR-01 — User registration          | Identity | `AuthController.register()` → `AuthService.register()` → `UserRepository.save()`   |
| FR-02 — JWT login                  | Identity | `AuthController.login()` → `AuthService.login()` → `JwtService.generateToken()`    |
| FR-03 — RBAC with 3 roles          | Identity | `SecurityConfig.roleHierarchy()`, `@PreAuthorize` on all controllers               |
| FR-04 — User management            | Identity | `AdminController` → `UserManagementService` → `UserRepository`                     |
| FR-07 — Account lockout            | Identity | `User.incrementFailedAttempts()`, `User.checkAccountStatus()`                      |
| FR-08 — MFA for admins             | Identity | `AuthController.verifyMfa()` → `AuthService.verifyMfa()` with TOTP                 |
| FR-09 — Machine registration       | Machine  | `MachineController.create()` → `MachineService.createMachine()`                    |
| FR-10 — Machine listing            | Machine  | `MachineController.findAll()` → `MachineService.findAll()`                         |
| FR-11 — Slot management            | Machine  | `SlotController.findByMachine()` → `SlotService.findByMachineId()`                 |
| FR-12 — Telemetry ingestion        | Machine  | `MachineTelemetryController.ingest()` → `TelemetryService.ingest()`                |
| FR-17 — Product catalog            | Product  | `ProductController.findAllActive()` → `ProductService.findAllActive()`             |
| FR-21 — Purchase processing        | Sale     | `SaleController.purchase()` → `SaleService.purchase()` (with idempotency)          |
| FR-23 — Purchase history           | Sale     | `SaleController.findMySales()` → `SaleService.findByUserId()`                      |
| FR-26 — Encrypted backups          | OS       | `OperationsController.triggerBackup()` → `BackupServiceImpl.generateBackup()`      |
| FR-27 — Backup rotation            | OS       | `BackupServiceImpl.rotateBackups(30)`                                              |
| FR-28 — Report generation          | OS       | `ReportDirectoryServiceImpl.createReportDirectory()`                               |
| FR-29 — Audit logging              | OS       | `AuditLogRepository.save()` throughout all services                                |
| FR-30 — Log rotation               | OS       | `AuditLogRotationServiceImpl.rotate()` / `deleteAfterDays(90)`                     |
| NFR-05 — Idempotent purchases      | Sale     | `IdempotencyRepository.findByIdempotencyKey()` in `SaleService.purchase()`         |
| SR-01 — Admin-only endpoints       | Security | `@PreAuthorize("hasRole('ADMINISTRATOR')")`                                        |
| SR-02 — Role isolation             | Security | `@PreAuthorize("hasAnyRole(...)")` per endpoint                                    |
| SR-03 — JWT alg:none rejection     | Security | `JwtService.rejectAlgNone()` manual header inspection                              |
| SR-06 — Mandatory auth annotations | Security | `SecurityAnnotationArchTest` ArchUnit rule                                         |
| SR-07 — Account lockout            | Security | `User` lockout logic (5 fails/15min window, 30min lockout)                         |
| SR-09 — SQL injection prevention   | Security | JPA parameterized queries + `IastTaintTrackingFilter` detection                    |
| SR-15 — Inventory concurrency      | Security | `@Lock(PESSIMISTIC_WRITE)` + `@Version` on Slot                                    |
| SR-18 — Rate limiting              | Security | `TelemetryService` counts requests per minute per machine                          |
| SR-19 — Webhook HMAC               | Security | `PaymentGatewayServiceImpl.verifyWebhookSignature()` with constant-time comparison |
| SR-24 — Server-side pricing        | Security | `Sale.unitPrice` set server-side; `fail-on-unknown-properties=true`                |
| SR-26 — Path traversal prevention  | Security | `PathValidatorImpl.isValidPath()` with canonical path + sandbox check              |
| SR-28 — Input validation           | Security | Jakarta Bean Validation on all DTOs                                                |
| SR-38 — Audit trail integrity      | Security | `AuditLog.integrityHash` + `AuditLogRotationServiceImpl` HMAC                      |

### 8.2 Implementation → Test Mapping

| Implementation                                       | Test Evidence                                                                                                                                        |
| ---------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------- |
| `User.checkAccountStatus()`                          | `UserTest.java`, `AuthControllerIntegrationTest.java` (lockout after 5 fails), `SystemFunctionalTests.java` (lockout journey)                        |
| `User.incrementFailedAttempts()`                     | `AuthControllerIntegrationTest.java` (5 failures → LOCKED), `CriticalFlowE2ETest.java` (3 fails still unlocked, 5 fails locked)                      |
| `JwtService.isTokenValid()`                          | `JwtServiceTest.java`, `AuthControllerIntegrationTest.java` (valid JWT → 200, expired → 401, alg:none → 401)                                         |
| `JwtService.rejectAlgNone()`                         | `AuthControllerIntegrationTest.java` (alg:none → 401), `BlackBoxSecurityE2ETest.java` (ac04)                                                         |
| `Slot.reserveUnit()` / `Slot.addStock()`             | `SlotTest.java`, `SlotServiceTest.java`                                                                                                              |
| `SaleService.purchase()`                             | `SaleServiceTest.java`, `CriticalFlowE2ETest.java` (purchase + idempotency), `BlackBoxSecurityE2ETest.java` (concurrent purchases ac07, idempotency) |
| `TelemetryService.ingest()`                          | `TelemetryServiceTest.java`, `SystemFunctionalTests.java`, `BlackBoxSecurityE2ETest.java` (ac08: validation + rate limiting)                         |
| `PaymentGatewayServiceImpl.verifyWebhookSignature()` | `PaymentGatewayServiceImplTest.java`, `BlackBoxSecurityE2ETest.java` (ac02: HMAC validation)                                                         |
| `PathValidatorImpl.isValidPath()`                    | `PathValidatorImplTest.java`, `BlackBoxSecurityE2ETest.java` (ac06: path traversal)                                                                  |
| `BackupServiceImpl.generateBackup()`                 | `BackupServiceImplTest.java`, `BlackBoxSecurityE2ETest.java` (ac01: backup RBAC)                                                                     |
| `FileValidationServiceImpl.validate()`               | `FileValidationServiceImplTest.java`, `FileValidationServiceImplFileTest.java`                                                                       |
| `ExifStripper.stripExif()`                           | `ExifStripperTest.java`                                                                                                                              |
| `IastTaintTrackingFilter`                            | `IastIntegrationTest.java` (SQL injection, path traversal detection)                                                                                 |
| `GlobalExceptionHandler`                             | `GlobalExceptionHandlerTest.java`, `ControllerIntegrationTests.java`                                                                                 |
| `SecurityConfig` filter chain                        | `SecurityConfig` tests, `ControllerIntegrationTests.java` (URL-based auth)                                                                           |
| `@PreAuthorize` coverage                             | `SecurityAnnotationArchTest.java`, `RbacIntegrationTest.java`, `LayeredArchitectureTest.java`                                                        |
| Layer dependencies                                   | `LayeredArchitectureTest.java`, `architecture/LayeredArchitectureArchTest.java`                                                                      |
| Naming conventions                                   | `architecture/NamingConventionArchTest.java`                                                                                                         |
| All controllers                                      | `ControllerUnitTests.java`, `MoreControllerUnitTests.java`                                                                                           |
| All services                                         | `Application/service/*Test.java` (9 test files)                                                                                                      |
| E2E critical flows                                   | `CriticalFlowE2ETest.java` (6 test scenarios)                                                                                                        |
| Black-box security                                   | `BlackBoxSecurityE2ETest.java` (ac01–ac08 + IDOR)                                                                                                    |
| Abuse case regression                                | `AbuseCaseRegressionTest.java` (ac01–ac08)                                                                                                           |
| RBAC enforcement                                     | `RbacIntegrationTest.java` (admin/operator/customer access)                                                                                          |
| DTO validation                                       | `DtoTests.java`, `ControllerUnitTests.java`                                                                                                          |

### 8.3 Security Requirement → Evidence Mapping

| Security Requirement                      | Evidence Type | Evidence                                                                                                                      |
| ----------------------------------------- | ------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| SR-01 — Admin-only sensitive endpoints    | Test + Code   | `RbacIntegrationTest.java` — customer/operator → 403 for admin endpoints; `@PreAuthorize` annotations                         |
| SR-02 — Role isolation                    | Test + ZAP    | `RbacIntegrationTest.java` — all 3 roles tested; ZAP RBAC scan report (all 11 checks PASS)                                    |
| SR-03 — JWT alg:none rejected             | Test          | `AuthControllerIntegrationTest.java` (401), `BlackBoxSecurityE2ETest.java` (ac04 — 4 token types rejected)                    |
| SR-04 — Token signature validation        | Code          | `JwtService.isTokenValid()` — `Jwts.parser().verifyWith(key)`                                                                 |
| SR-05 — Token expiry enforced             | Test          | `AuthControllerIntegrationTest.java` (expired → 401), `SystemFunctionalTests.java`                                            |
| SR-06 — Authorization on every controller | Test          | `SecurityAnnotationArchTest.java` — ArchUnit fails if any controller method lacks annotation                                  |
| SR-07 — Account lockout                   | Test          | `AuthControllerIntegrationTest.java` (5 fails → 423), `CriticalFlowE2ETest.java` (e2e03)                                      |
| SR-08 — IDOR prevention                   | Test          | `BlackBoxSecurityE2ETest.java` — customer cannot see other's sales, cannot access machine sales                               |
| SR-09 — SQL injection prevention          | Test + IAST   | `IastIntegrationTest.java` — detects taint but no confirmed exploits; `BlackBoxSecurityE2ETest.java` (ac05 — 400/404, no 200) |
| SR-15 — Inventory consistency             | Test + Code   | `BlackBoxSecurityE2ETest.java` (ac07 — concurrent purchases: 1 COMPLETED, rest rejected); `@PESSIMISTIC_WRITE` + `@Version`   |
| SR-18 — Rate limiting                     | Test          | `BlackBoxSecurityE2ETest.java` (ac08 — telemetry burst → 429)                                                                 |
| SR-19 — Webhook HMAC integrity            | Test + Code   | `BlackBoxSecurityE2ETest.java` (ac02 — missing/wrong/tampered → 401); `MessageDigest.isEqual()` constant-time comparison      |
| SR-24 — Server-side pricing               | Test + Code   | `BlackBoxSecurityE2ETest.java` (ac03 — `unitPrice` field → 400, server price verified); `fail-on-unknown-properties=true`     |
| SR-26 — Path traversal prevention         | Test + Code   | `BlackBoxSecurityE2ETest.java` (ac06 — `../` sandboxed, traversal in SKU → 400/404); `PathValidatorImpl`                      |
| SR-28 — Input validation                  | Test          | `SystemFunctionalTests.java` (malformed registration → 400), DTO validation tests                                             |
| SR-30 — DoS resilience                    | Test          | `BlackBoxSecurityE2ETest.java` (ac08 — 5× burst, rate limiting, no 500)                                                       |
| SR-38 — Audit tamper evidence             | Code          | `AuditLog.integrityHash` (SHA-256), `AuditLogRotationServiceImpl` HMAC signing                                                |
| SR-44 — No information leakage            | Code + Config | `server.error.include-stacktrace=never`, generic 500 messages, constant-time login error                                      |
| All 46 SRs                                | DAST          | ZAP baseline scan: 0 High, 0 Medium, 0 Low, 1 Informational (Non-Storable Content)                                            |
| All 46 SRs                                | DAST          | ZAP API scan reports (baseline, admin-authenticated, RBAC) — generated and stored in `.zap/reports/`                          |
| All 46 SRs                                | SAST          | Semgrep custom rules (`.semgrep/vendnet-security.yml`) run in CI pipeline                                                     |
| All 46 SRs                                | SCA           | OWASP Dependency-Check with known CVE suppressions (`dependency-check-suppressions.xml`)                                      |

### ZAP DAST Evidence

The following ZAP scan reports were generated and are stored in `.zap/reports/`:

| Report             | Date         | Findings Summary                         |
| ------------------ | ------------ | ---------------------------------------- |
| `zap-baseline.md`  | Jun 15, 2026 | 0 High, 0 Medium, 0 Low, 1 Informational |
| `zap-api-admin.md` | Jun 15, 2026 | Admin-authenticated API scan             |
| `zap-rbac.md`      | Jun 15, 2026 | 11 RBAC checks — all PASS                |

**ZAP RBAC Scan Results (from `.zap/reports/zap-rbac.md`):**

| Role      | Method | Path                    | Expected | Actual | Result |
| --------- | ------ | ----------------------- | -------- | ------ | ------ |
| anonymous | GET    | `/api/admin/dashboard`  | 401      | 401    | PASS   |
| admin     | GET    | `/api/admin/dashboard`  | 200      | 200    | PASS   |
| admin     | GET    | `/api/admin/users`      | 200      | 200    | PASS   |
| admin     | POST   | `/api/admin/backups`    | 201      | 201    | PASS   |
| operator  | GET    | `/api/admin/dashboard`  | 403      | 403    | PASS   |
| operator  | GET    | `/api/machines/1/slots` | 200      | 200    | PASS   |
| operator  | GET    | `/api/sales/machine/1`  | 200      | 200    | PASS   |
| customer  | GET    | `/api/admin/dashboard`  | 403      | 403    | PASS   |
| customer  | GET    | `/api/auth/me`          | 200      | 200    | PASS   |
| customer  | GET    | `/api/products`         | 200      | 200    | PASS   |
| customer  | GET    | `/api/machines`         | 200      | 200    | PASS   |

### E2E Test Evidence

E2E test reports are stored in `vendnet/e2e/reports/`. The latest HTML report (`e2e-report.html`, 1.0 MB) contains results from all critical flows and security E2E tests.

![E2E Test Report Screenshot](./screenshots/e2e-report.png)

### ZAP Report Screenshot

![ZAP Baseline Report Screenshot](./screenshots/security-zap-baseline.png)

---

## Appendix A: Technology Stack Summary

| Category             | Technology                                               |
| -------------------- | -------------------------------------------------------- |
| **Language**         | Java 17                                                  |
| **Framework**        | Spring Boot 3.5.14                                       |
| **Build**            | Maven (wrapper)                                          |
| **Database**         | MySQL 8.4 (prod), H2 (dev/test/CI)                       |
| **ORM**              | Hibernate 6.x (Spring Data JPA)                          |
| **Auth**             | JWT (jjwt 0.12.6) + X.509 mTLS                           |
| **Password Hashing** | BCrypt (cost factor 12)                                  |
| **API Docs**         | SpringDoc OpenAPI 3.0                                    |
| **Observability**    | Micrometer + Prometheus + OTel + Jaeger + Grafana + Loki |
| **Container**        | Docker (multi-stage, non-root user)                      |
| **CI/CD**            | GitHub Actions (13-stage pipeline)                       |
| **SAST**             | SpotBugs + FindSecBugs + Semgrep                         |
| **SCA**              | OWASP Dependency-Check + Maven Enforcer                  |
| **DAST**             | OWASP ZAP                                                |
| **IAST**             | Custom taint-tracking filter                             |
| **Secret Detection** | Gitleaks                                                 |
| **Container Scan**   | Trivy                                                    |
| **Code Quality**     | Checkstyle, PMD, Spotless, ArchUnit                      |
| **SBOM**             | CycloneDX 1.5                                            |
| **Version Control**  | Git (GitHub)                                             |
| **Deployment**       | Blue/Green zero-downtime with Nginx                      |

---

## Appendix B: Phase 1 Architecture Continuity

The Sprint 2 implementation respects the architectural decisions established in Phase 1:

1. **DDD Layered Architecture:** The Phase 1 C4 diagrams (Levels 1–3 in `Deliverables/Phase1/System-To-Be/C4/`) defined a clean layered architecture with API, Application, Domain, and Infrastructure layers. Sprint 2 implements this exactly, as verified by `LayeredArchitectureTest.java`.

2. **Bounded Contexts:** The 5 bounded contexts identified in Phase 1 (`Deliverables/Phase1/Report/02_Domain_Model.md`) — Identity & Access, Machine Management, Slot Management, Product Catalog, Sales — are all implemented as aggregate roots and entities in `domain/model/`.

3. **Security Principles:** All 7 secure design principles from `10_Secure_Architecture.md` are implemented: Defense in Depth (4 security filters + RBAC), Least Privilege (non-root Docker user, role hierarchy), Fail-Safe Defaults (deny-by-default `SecurityFilterChain`), Separation of Duties (3 roles), Complete Mediation (JWT filter on every request), Economy of Mechanism (hardcoded `ProcessBuilder` args, whitelisted backup types), Open Design (HS256 with 256-bit key, AES-256-GCM).

4. **Abuse Cases:** All 8 abuse cases (AC-01 to AC-08) identified in Phase 1 (`Deliverables/Phase1/Report/05_Abuse_Cases.md`) have executable regression tests in `AbuseCaseRegressionTest.java` and `BlackBoxSecurityE2ETest.java`.

5. **Requirements Coverage:** All 102 requirements (31 FR + 19 NFR + 46 SR + 6 Constraints) from `08_Requirements.md` are traceable to implementation and tests, as documented in Section 8.
