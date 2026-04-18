# 8. Requirements

This chapter specifies the functional and non-functional requirements for the VendNet system using the **FURPS+** classification model (Functionality, Usability, Reliability, Performance, Supportability, plus constraints). Requirements are derived from the [System Overview](01_System_Overview.md), [Domain Model](02_Domain_Model.md), [Threat Model](04_Threat_Model.md), and [Abuse Cases](05_Abuse_Cases.md). Each requirement is numbered, testable, and traceable to its source.

> **Priority key (MoSCoW):** **Must** = mandatory for Phase 2 delivery · **Should** = expected · **Could** = if time permits · **Won't** = out of scope for this phase.

---

## 8.1 Functional Requirements

Functional requirements describe observable system behaviour grouped by bounded context and actor.

### 8.1.1 Identity & Access (User Aggregate)

| ID | Requirement | Priority | Actor(s) |
|----|-------------|----------|----------|
| FR-01 | The system shall allow users to register with a unique username (3–30 alphanumeric chars), a unique RFC 5322-compliant email, a full name, and a password | Must | All |
| FR-02 | The system shall authenticate users via `POST /auth/login` returning a signed JWT containing `userId`, `role`, and `exp` claims | Must | All |
| FR-03 | The system shall enforce Role-Based Access Control (RBAC) with exactly three roles: `CUSTOMER`, `OPERATOR`, `ADMINISTRATOR` | Must | All |
| FR-04 | The system shall allow Administrators to create, update, suspend, and reactivate user accounts | Must | Administrator |
| FR-05 | The system shall allow Administrators to change a user's role | Must | Administrator |
| FR-06 | The system shall allow users to view and update their own profile (full name, email) | Should | All |
| FR-07 | The system shall transition `AccountStatus` to `LOCKED` after 5 consecutive failed login attempts within 15 minutes; lockout shall last 30 minutes | Should | System |
| FR-08 | The system shall deny authentication for accounts with `AccountStatus` = `SUSPENDED` or `LOCKED` | Must | System |

### 8.1.2 Machine Management (VendingMachine Aggregate)

| ID | Requirement | Priority | Actor(s) |
|----|-------------|----------|----------|
| FR-09 | The system shall allow Administrators to register vending machines with a unique serial number, location (address + GPS coordinates), and initial slot configuration (1–50 slots) | Must | Administrator |
| FR-10 | The system shall allow Administrators to update machine location and operational parameters | Must | Administrator |
| FR-11 | The system shall allow Operators to view the machines assigned to them, including status and slot details | Must | Operator |
| FR-12 | The system shall accept mTLS-authenticated telemetry data (temperature, connectivity, power status) from vending machines and update `MachineStatus` and `LastTelemetryAt` | Must | Vending Machine |
| FR-13 | The system shall allow Operators to view machine telemetry history and diagnostic logs | Should | Operator |
| FR-14 | The system shall allow Operators to report machine issues and track their resolution status | Could | Operator |
| FR-15 | The system shall allow Administrators to assign products to specific machine slots via `2.3 Slot Configuration` | Must | Administrator |
| FR-16 | The system shall dispatch alerts to Operators and Administrators when telemetry anomalies are detected (e.g., temperature thresholds exceeded, connectivity loss) | Should | System |

### 8.1.3 Sales & Inventory (Product and Sale Aggregates)

| ID | Requirement | Priority | Actor(s) |
|----|-------------|----------|----------|
| FR-17 | The system shall allow Customers to browse the product catalog with names, descriptions, prices, categories, and availability | Must | Customer |
| FR-18 | The system shall allow Administrators to create, update, and deactivate products in the catalog | Must | Administrator |
| FR-19 | The system shall allow Administrators to update product pricing; price changes shall propagate to vending machines via mTLS | Must | Administrator |
| FR-20 | The system shall allow Operators to update stock quantities (`CurrentQuantity`) for machine slots after physical restocking | Must | Operator |
| FR-21 | The system shall process customer purchases: validate product availability, authorize payment via the external Payment Gateway, decrement stock, and persist an immutable `Sale` record | Must | Customer |
| FR-22 | The system shall accept on-site sales events from vending machines via mTLS and process them through the same sales pipeline | Must | Vending Machine |
| FR-23 | The system shall allow Customers to view their own purchase history | Should | Customer |
| FR-24 | The system shall capture a `UnitPrice` snapshot at sale time so that `TotalAmount = Quantity × UnitPrice` is immutable | Must | System |
| FR-25 | The system shall reject purchases when `PaymentInfo.status ≠ COMPLETED` | Must | System |

### 8.1.4 OS Operations & Administration

| ID | Requirement | Priority | Actor(s) |
|----|-------------|----------|----------|
| FR-26 | The system shall generate AES-256 encrypted MySQL database backups on a daily schedule and on-demand via Administrator API request | Must | Administrator / System |
| FR-27 | The system shall rotate and compress audit log files daily, retaining them for 90 days before automatic deletion | Must | System |
| FR-28 | The system shall generate structured report directories under `/var/vendnet/reports/` and produce CSV/JSON report files on Administrator request | Should | Administrator |
| FR-29 | The system shall allow Administrators to download generated reports and view audit logs | Should | Administrator |
| FR-30 | The system shall record audit logs for all authentication attempts, authorization decisions, data modifications, and administrative actions | Must | System |
| FR-31 | The system shall provide Administrators with a global network health dashboard aggregating telemetry across all machines | Should | Administrator |

---

## 8.2 Non-Functional Requirements (FURPS+)

Non-functional requirements are organized according to the FURPS+ model.

### 8.2.1 Usability

| ID | Requirement | Justification |
|----|-------------|---------------|
| NFR-01 | The REST API shall follow OpenAPI 3.0 specification with auto-generated documentation (SpringDoc/Swagger UI) accessible at `/swagger-ui.html` | Reduces integration friction for front-end and third-party consumers |
| NFR-02 | All API error responses shall use a consistent JSON schema: `{ "status", "error", "message", "timestamp" }` with no internal details (stack traces, SQL errors) | Improves developer experience while preventing information disclosure (T-75) |
| NFR-03 | Paginated list endpoints shall support `page`, `size`, and `sort` query parameters with a default page size of 20 and a maximum of 100 | Ensures predictable API behaviour and prevents excessive data returns |

### 8.2.2 Reliability

| ID | Requirement | Justification |
|----|-------------|---------------|
| NFR-04 | The system shall achieve high uptime during operational hours | Vending operations require high availability for sales processing |
| NFR-05 | The system shall ensure ACID compliance for all database transactions with `READ_COMMITTED` isolation level or higher | Maintains data consistency and prevents race conditions (e.g., overselling — T-45) |
| NFR-06 | Sale transactions shall use pessimistic locking on the target slot row to prevent TOCTOU race conditions during stock decrement | Prevents overselling when concurrent purchases target the same slot (AC-07) |
| NFR-07 | The system shall implement circuit-breaker patterns for the Payment Gateway integration with fallback to queued retry | Prevents cascading failure when the external gateway is unavailable (T-27) |
| NFR-08 | Database backups shall be rotated automatically after 30 days; at least one valid backup shall exist at all times | Prevents disk exhaustion while ensuring point-in-time recovery capability |
| NFR-09 | The system shall validate backup file integrity via checksum verification after generation | Ensures backups are not corrupted and can be reliably restored |

### 8.2.3 Performance

| ID | Requirement | Justification |
|----|-------------|---------------|
| NFR-10 | API responses shall complete within 500 ms (p95) under normal load (≤ 100 concurrent users) | Ensures responsive user experience for vending operations |
| NFR-11 | The system shall use database connection pooling (HikariCP) with a maximum of 20 concurrent connections | Prevents connection exhaustion and resource starvation |
| NFR-12 | Backup operations shall execute asynchronously and shall not degrade API response times | Ensures system responsiveness during scheduled or on-demand backup operations |
| NFR-13 | Telemetry ingestion endpoints shall sustain ≥ 500 requests/second without data loss | Supports a fleet of machines reporting at 30-second intervals |
| NFR-14 | The system shall enforce request payload size limits (maximum 1 MB for standard endpoints, 10 MB for file uploads) | Prevents resource exhaustion attacks (T-92) |

### 8.2.4 Supportability

| ID | Requirement | Justification |
|----|-------------|---------------|
| NFR-15 | The system shall be deployable via Docker containers orchestrated by Docker Compose | Enables reproducible environments and supports CI/CD pipelines |
| NFR-16 | The codebase shall follow a layered DDD architecture (Controller → Service → Domain → Infrastructure) with package-by-bounded-context organization | Ensures maintainability and domain encapsulation |
| NFR-17 | The system shall expose a health-check endpoint (`GET /actuator/health`) returning service and dependency status | Supports monitoring, load balancing, and automated restart policies |
| NFR-18 | The CI/CD pipeline shall include automated SAST (static analysis), SCA (dependency scanning), and unit/integration test execution on every pull request | Catches security and quality regressions before merge |
| NFR-19 | All configuration secrets (database credentials, JWT signing key, API keys) shall be injected via environment variables, never hardcoded in source | Prevents credential leakage in version control (T-70) |

### 8.2.5 Constraints (+)

| ID | Constraint | Source |
|----|-----------|--------|
| C-01 | The back-end shall be implemented in Java 17 (LTS) with Spring Boot 3.x | Project specification |
| C-02 | The database shall be a relational database (MySQL 8.4 LTS); in-memory databases are not allowed | Project specification §2 |
| C-03 | The Domain Model shall encompass at least three aggregates (User, VendingMachine, Product, Sale) | Project specification §2 |
| C-04 | The system shall implement authorization with at least three different roles | Project specification §2 |
| C-05 | The back-end shall execute OS-level operations (creating directories, reading/writing files) | Project specification §2 |
| C-06 | All project code and documentation shall be hosted in a Git-based web repository | Project specification §3 |

---

## 8.3 Security Requirements

### 8.3.1 Authentication & Access Control

| ID    | Requirement                                                                                                                    | Justification                                                                      | Threat Ref    | ASVS Ref |
|-------|--------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------|---------------|----------|
| SR-01 | The system shall require multi-factor authentication for Administrator accounts                                                | Prevents unauthorized access to privileged accounts                                | T-12, T-07    | V2.4.1   |
| SR-02 | The system shall implement role-based access control (RBAC) with exactly three roles: Customer, Operator, Administrator        | Each role has distinct permission                                                  | —             | V4.1.1   |
| SR-03 | The system shall reject JWT tokens with invalid or insecure signing algorithms                                                 | Prevents JWT signature bypass attack                                               | T-28, T-06    | V2.2.1   | 
| SR-04 | The system shall enforce a minimum JWT signing key entropy of 256 bits; The key shall be rotated every 90 days                 | Prevent HMAC brute-force attacks and mitigate JWT tampering post-recovery          | T-29, T-30    | V2.2.2   |
| SR-05 | The system shall validate user account status on every request and deny access if the account is suspended or revoked          | Prevents access using valid but disabled accounts                                  | T-35, T-64    | V2.3.1   |
| SR-06 | The system shall enforce authorization checks at both controller and service level                                             | Prevents privilege escalation via internal service calls                           | T-36          | V4.1.2   |
| SR-07 | The system shall enforce AccountStatus = LOCKED after 5 failed login attempts within 15 minutes; Lockout shall last 30 minutes | Mitigate credential stuffing and brute-force attacks                               | T-01, T-05    | V2.1.2   |
| SR-08 | The system shall ensure that users can only access their own data based on their authenticated identity from the JWT token     | Prevents unauthorized access to other users' data (IDOR and privilege escalation)  | T-04, T-48    | V4.2.1   | 

### 8.3.2 Data Security

| ID | Requirement                                                                                                     | Justification                                                    | Threat Ref       | ASVS Ref |
|----|-----------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------|------------------|----------|
| SR-09 | The system shall use parameterised queries or ORM mechanisms for all database access                            | Prevents SQL injection attacks                                   | T-39, T-72, T-74 | V5.2.1   |
| SR-10 | All database queries in background jobs shall be reviewed and validated for secure parameter handling           | Prevents SQL injection in background processes                   | T-110            | V5.2.3   |
| SR-11 | Database credentials shall be stored securely using environment variables and not hardcoded in the application  | Prevents credential leakage and unauthorized database access     | T-70             | V5.3.5   |
| SR-12 | The database user shall have only the minimum required permissions to perform application operations            | Limits impact of SQL injection or credential compromise          | T-77             | V5.3.4   |
| SR-13 | Sensitive user data (e.g., personal information) shall be encrypted when stored in the database                 | Protects data confidentiality if the database is compromised     | T-70, T-71       | V6.2.1   |
| SR-14 | Backup files shall be encrypted before being stored on disk                                                     | Prevents unauthorized access to backup data                      | T-61, T-78       | V6.2.2   |
| SR-15 | The system shall ensure data consistency during transactions to prevent concurrent update issues                | Prevents errors such as overselling due to simultaneous requests | T-45             | V5.2.5   |
| SR-16 | API responses shall not include sensitive data and shall use dedicated response objects when needed             | Prevents exposure of sensitive information such as passwords     | T-43, T-100      | V5.1.4   |

### 8.3.3 Communication Security

| ID | Requirement                                                                                      | Justification                                            | Threat Ref         | ASVS Ref |
|----|--------------------------------------------------------------------------------------------------|----------------------------------------------------------|--------------------|----------|
| SR-17 | All API communications shall use HTTPS with TLS 1.2 or higher                                    | Prevents data interception and man-in-the-middle attacks | T-85, T-97         | V9.1.1 |
| SR-18 | Vending machines shall authenticate securely when sending telemetry and sales data               | Prevents unauthorized devices from accessing the system  | T-18, T-101, T-23  | V9.1.2 |
| SR-19 | Payment Gateway callbacks shall be authenticated using a secure signature mechanism              | Prevents forged requests and data tampering              | T-24, T-108, T-109 | V9.1.3 |
| SR-20 | Security keys used for external integrations shall be rotated periodically                       | Reduces risk of long-term key compromise                 | T-24, T-108        | V2.2.3 |
| SR-21 | The system shall verify the identity of external services before establishing secure connections | Prevents man-in-the-middle attacks                       | T-106              | V9.1.1 |
| SR-22 | Compromised devices shall be blocked from accessing the system                                   | Prevents misuse of stolen credentials or certificate     | T-18, T-101        | V9.1.2 |
| SR-23 | Prevents misuse of stolen credentials or certificate                                             | Enables detection of suspicious or malicious activity    | T-18, T-101        | V7.1.3 |

### 8.3.4 Input Validation & Data Handling

| ID | Requirement                                                                                         | Justification                                                  | Threat Ref        | ASVS Ref |
|----|-----------------------------------------------------------------------------------------------------|----------------------------------------------------------------|-------------------|----------|
| SR-24 | The system shall determine product prices on the server and reject any client-supplied price values | Prevents price manipulation attacks                            | T-46              | V5.1.1 |
| SR-25 | The system shall validate stock quantities to ensure they remain within valid limits before saving  | Prevents invalid stock values and system errors                | T-45              | V5.1.2 |
| SR-26 | The system shall validate report input values and restrict file operations to approved directories  | Prevents path traversal and unauthorized file access           | T-58, T-113, T-84 | V5.3.6 |
| SR-27 | The system shall ensure file operations do not follow unsafe file paths or links                    | Prevents unauthorized file access through system-level attacks | T-80              | V5.3.7 |
| SR-28 | The system shall validate all incoming data to ensure it matches expected formats and ranges        | Prevents malformed or malicious data from being processed      | T-56              | V5.2.4 |
| SR-29 | The system shall reject requests containing unexpected or unknown fields                            | Prevents mass-assignment vulnerabilities                       | T-43              | V5.1.4 |
| SR-30 | The system shall limit the maximum size of incoming requests                                        | Prevents resource exhaustion attacks                           | T-92              | V5.1.5 |
| SR-31 | The system shall enforce limits on paginated responses to avoid excessive data returns              | Prevents large responses that could impact system stability    | T-92              | V5.1.5 |

### 8.3.5 Third-Party Components

| ID | Requirement                                                                                                    | Justification                                                     | Threat Ref | ASVS Ref |
|----|----------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------|------------|----------|
| SR-32 | The system shall scan all third-party dependencies for known security vulnerabilities during the build process | Prevents use of vulnerable libraries                              | —          | V14.2.1 |
| SR-33 | The system shall keep all frameworks and dependencies updated with the latest security patches                 | Reduces risk of exploiting known vulnerabilities                  | —          | V14.2.2 |
| SR-34 | The system shall use fixed versions of external libraries and test updates before deployment                   | Prevents instability and supply chain risks                       | —          | V14.1.1 |
| SR-35 | The system shall validate SSL certificates for all external secure connections                                 | Prevents man-in-the-middle attacks                                | —          | V9.1.1 |
| SR-36 | The system shall define timeouts of 5 seconds and connection timeouts of 3 seconds                             | Mitigates system delays caused by unresponsive external services  | —          | V11.2.2 |

### 8.3.6 Logging & Monitoring

| ID | Requirement                                                                           | Justification                                        | Threat Ref                          | ASVS Ref |
|----|---------------------------------------------------------------------------------------|------------------------------------------------------|-------------------------------------|----------|
| SR-37 | The system shall log all authentication events (login, failures, password reset, MFA) | Enables detection of suspicious login activity       | T-31, T-32                          | V7.1.3   |
| SR-38 | The system shall log all inventory changes (stock, price, configuration)              | Detects fraud and unauthorized modifications         | T-08, T-40, T-65                    | V7.1.1   |
| SR-39 | The system shall log all role and permission changes                                  | Detects privilege escalation and insider misuse      | T-13                                | V7.1.2   |
| SR-40 | The system shall log all payment transactions and their status                        | Supports auditing and dispute resolution             | T-47                                | V7.1.4   |
| SR-41 | The system shall protect audit logs against tampering                                 | Ensures integrity of forensic data                   | T-79                                | V7.3.4   |
| SR-42 | The system shall log administrative file operations                                   | Enables traceability of critical system actions      | T-60, T-81, T-58, T-84              | V7.1.3   |
| SR-43 | The system shall log database changes for audit and recovery purposes                 | Supports detection of unauthorized data changes      | T-73                                | V7.3.1   |
| SR-44 | The system shall generate alerts for suspicious or abnormal system activity           | Enables early detection of attacks                   | T-05, T-65, T-114, T-34, T-88, T-49 | V7.4.2   |
| SR-45 | The system shall prevent sensitive data from being stored in logs                     | Protects credentials and confidential information    | T-87, T-89                          | V7.1.5   |
| SR-46 | The system shall limit error details exposed in logs and responses                    | Prevents information disclosure via verbose messages | T-75                                | V7.4.1   |

---

## 8.4 Requirements Traceability Matrix

The following matrix maps each functional requirement to the FURPS+ non-functional requirements and security requirements that constrain its implementation, and traces security requirements back to their originating threats and abuse cases.

### 8.4.1 Functional → Non-Functional / Security Traceability

| Functional Req | Related NFRs | Related SRs | Abuse Cases |
|----------------|-------------|-------------|-------------|
| FR-01 (User registration) | NFR-16, NFR-19 | SR-05, SR-09, SR-28 | — |
| FR-02 (JWT authentication) | NFR-02 | SR-01, SR-03, SR-04, SR-07 | AC-04 |
| FR-03 (RBAC enforcement) | — | SR-02, SR-06, SR-08 | — |
| FR-07 (Account lockout) | — | SR-07, SR-37, SR-44 | — |
| FR-09 (Machine registration) | NFR-16 | SR-09, SR-28 | — |
| FR-12 (Telemetry ingestion) | NFR-13, NFR-14 | SR-18, SR-22, SR-30 | AC-08 |
| FR-17 (Catalog browsing) | NFR-03, NFR-10 | SR-31 | — |
| FR-19 (Pricing update) | — | SR-06, SR-24, SR-38 | AC-03 |
| FR-20 (Stock update) | NFR-05 | SR-06, SR-25, SR-38 | AC-07 |
| FR-21 (Purchase processing) | NFR-05, NFR-06, NFR-07, NFR-10 | SR-09, SR-15, SR-16, SR-24, SR-40 | AC-03, AC-05, AC-07 |
| FR-22 (On-site sales) | NFR-13 | SR-18, SR-19 | AC-02 |
| FR-26 (DB backups) | NFR-08, NFR-09, NFR-12 | SR-14, SR-26, SR-27, SR-42 | AC-01, AC-06 |
| FR-27 (Log rotation) | NFR-08 | SR-41, SR-42, SR-45 | — |
| FR-28 (Report generation) | NFR-12 | SR-26, SR-27, SR-42 | AC-06 |
| FR-30 (Audit logging) | — | SR-37, SR-38, SR-39, SR-40, SR-41, SR-43 | — |

### 8.4.2 Security Requirements → Threat / Abuse Case Traceability

| SR Range | Category | Threat IDs Covered | Abuse Cases |
|----------|----------|--------------------|-------------|
| SR-01 – SR-08 | Authentication & Access Control | T-01, T-04, T-05, T-06, T-07, T-12, T-28, T-29, T-30, T-35, T-36, T-48, T-64 | AC-04 |
| SR-09 – SR-16 | Data Security | T-39, T-43, T-45, T-46, T-61, T-70, T-71, T-72, T-74, T-77, T-78, T-100, T-110 | AC-05 |
| SR-17 – SR-23 | Communication Security | T-18, T-23, T-24, T-85, T-97, T-101, T-106, T-108, T-109 | AC-02 |
| SR-24 – SR-31 | Input Validation & Data Handling | T-43, T-45, T-46, T-56, T-58, T-80, T-84, T-92, T-113 | AC-01, AC-03, AC-06, AC-07 |
| SR-32 – SR-36 | Third-Party Components | — (best practice) | — |
| SR-37 – SR-46 | Logging & Monitoring | T-05, T-08, T-13, T-31, T-32, T-34, T-40, T-47, T-49, T-58, T-60, T-65, T-73, T-75, T-79, T-81, T-84, T-87, T-88, T-89, T-114 | — |

---

## 8.5 Requirements Coverage Summary

| FURPS+ Category | Count | ID Range |
|-----------------|-------|----------|
| **Functionality** (FR) | 31 | FR-01 – FR-31 |
| **Usability** (NFR) | 3 | NFR-01 – NFR-03 |
| **Reliability** (NFR) | 6 | NFR-04 – NFR-09 |
| **Performance** (NFR) | 5 | NFR-10 – NFR-14 |
| **Supportability** (NFR) | 5 | NFR-15 – NFR-19 |
| **Constraints** (+) | 6 | C-01 – C-06 |
| **Security** (SR) | 46 | SR-01 – SR-46 |
| **Total** | **102** | |

All 31 functional requirements are testable via unit, integration, or API tests. All 46 security requirements reference specific ASVS verification items and can be validated through the [Security Testing Plan](09_Security_Testing.md). The traceability matrix in 8.4 ensures bidirectional coverage between requirements, threats (Chapter 4), abuse cases (Chapter 5), and mitigations (Chapter 7).
