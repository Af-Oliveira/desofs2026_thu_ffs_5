# 8. Requirements

## 8.1 Functional Requirements

| ID    | Requirement                                                                                                             | Priority | Aggregate/Context |
|-------|-------------------------------------------------------------------------------------------------------------------------|----------|-------------------|
| FR-01 | The system shall allow Customers to browse the product catalog and view prices                                          | Must     | Inventory         |
| FR-02 | The system shall process customer purchases and generate Sale records                                                   | Must     | Sales             |
| FR-03 | The system shall allow Customers to view their purchase history and manage their profile                                | Should   | Customer          |
| FR-04 | The system shall allow Operators to view and update stock levels for assigned machines                                  | Must     | Inventory         |
| FR-05 | The system shall allow Operators to view machine telemetry data and logs                                                | Should   | Monitoring        |
| FR-06 | The system shall allow Operators to report machine issues and track their status                                        | Could    | Maintenance       |
| FR-07 | The system shall allow Operators to view the assigned machines                                                          | Must     | Monitoring        |
| FR-08 | The system shall allow Administrators to manage user accounts and roles                                                 | Must     | Administration    |
| FR-09 | The system shall allow Administrators to manage product pricing                                                         | Could    | Sales             |
| FR-10 | The system shall allow Administrators to configure system settings and generate operational reports                     | Should   | Administration    |
| FR-11 | The system shall allow Administrators to trigger on-demand database backups                                             | Could    | Infrastructure    |
| FR-12 | The system shall authenticate users using secure JWT-based authentication                                               | Must     | Security          |
| FR-13 | The system shall integrate with an external Payment Gateway to authorize and capture payments                           | Must     | Sales             |
| FR-14 | The system shall enforce role-based access control (RBAC) for all protected operations                                  | Must     | Security          |
| FR-15 | The system shall record audit logs for all authentication attempts, authorization decisions, and administrative actions | Must     | Security          |

## 8.2 Non-Functional Requirements

| ID     | Requirement                                                                                                                 | Category      | Justification                                                                            |
|--------|-----------------------------------------------------------------------------------------------------------------------------|---------------|------------------------------------------------------------------------------------------|
| NFR-01 | API responses shall complete within 500ms under normal load                                                                 | Performance   | Ensures responsive user experience for operations                                        |
| NFR-02 | The system shall achieve 99.5% uptime during operational hours                                                              | Availability  | Vending operations require high availability                                             |
| NFR-03 | The system shall use database connection pooling with max 20 concurrent connections (HikariCP)                              | Performance   | Prevents connection exhaustion and resource starvation                                   |
| NFR-04 | All API communications shall be encrypted using TLS 1.2                                                                     | Security      | Protects credentials and data in transit                                                 |
| NFR-05 | User passwords shall be securely hashed using BCrypt with work factor ≥ 12                                                  | Security      | Prevents password compromise; resists brute-force attacks                                |
| NFR-06 | The system shall enforce input validation and sanitization on all external inputs                                           | Security      | Prevents injection attacks (SQL injection)                                               |
| NFR-07 | The system shall log and monitor all authentication failures, authorization denials, and suspicious activities in real-time | Security      | Enables detection of credential stuffing, brute-force, and privilege escalation attempts |
| NFR-08 | The system shall ensure ACID compliance for all database transactions with `READ_COMMITTED` isolation level or higher       | Reliability   | Maintains data consistency and prevents race conditions (e.g., overselling)              |
| NFR-09 | The system shall support horizontal scaling via containerization (Docker) and orchestration (Docker Compose)                | Scalability   | Supports growth of vending machine network and concurrent user load                      |
| NFR-10 | Backup operations shall not impact API response times                                                                       | Performance   | Ensures system responsiveness during scheduled or on-demand backup operations            |
| NFR-11 | The system shall restrict file system access to designated directories only (`/var/vendnet/` for backups, logs, reports)    | Security      | Prevents path traversal attacks and unauthorized access to system files                  |
| NFR-12 | The system shall retain audit logs for a minimum of 90 days; logs older than 90 days shall be automatically deleted         | Compliance    | Satisfies GDPR Art. 32 (security measures) and PCI-DSS Req. 10.7 (log retention)         |
| NFR-13 | Backup files shall be encrypted using AES-256 before writing to persistent storage                                          | Security      | Protects backup confidentiality if storage is compromised                                |
| NFR-14 | Database backups shall be rotated automatically after 30 days                                                               | Availability  | Prevents disk exhaustion while ensuring point-in-time recovery capability                |
| NFR-15 | Vending machines shall authenticate to telemetry and sales endpoints using mutual TLS (mTLS) with client certificates       | Security      | Ensures only authorized machines can submit telemetry and sales data                     |

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
