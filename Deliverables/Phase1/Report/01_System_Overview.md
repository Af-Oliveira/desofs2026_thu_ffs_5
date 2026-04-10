# 1. System Overview

## 1.1 Purpose and Scope

**VendNet** is a centralized back-end management system for a geographically distributed **Vending Machine Network**. The system provides a REST API that enables three categories of users — Customers, Operators, and Administrators — to interact with and manage a fleet of vending machines spread across various locations.

### Business Context

Vending machine operators need a unified platform to:

- **Monitor** machine status, stock levels, and telemetry data in real time.
- **Process** sales transactions and integrate with external payment gateways.
- **Manage** product catalogs, pricing strategies, and machine assignments.
- **Generate** operational reports and maintain audit trails for compliance.
- **Automate** back-end operations such as database backups, audit-log rotation, and report directory management.

### Scope

The system scope covers:

| In Scope | 
|----------|
| REST API for all CRUD operations | 
| User authentication and role-based authorization | 
| Product catalog and pricing management | 
| Sales transaction processing with payment gateway integration | 
| Machine telemetry ingestion and monitoring | 
| OS-level operations (backups, log rotation, report directories) |
| Audit logging and security event tracking |

---

## 1.2 High-Level Architecture

The system follows a layered architecture based on **Domain-Driven Design (DDD)** principles, with clear separation between the API layer, application/service layer, domain layer, and infrastructure/persistence layer.

> For detailed C4 diagrams at multiple levels, see the [System Architecture (C4)](../System/C4/README.md) section.

### System Context Diagram (C4 Level 1)

```plantuml
@startuml
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml

title System Context Diagram — VendNet

Person(customer, "Customer", "Purchases products via\nmachine or companion app")
Person(operator, "Operator", "Restocks machines,\nextracts logs, reports issues")
Person(admin, "Administrator", "Manages network, users,\nprices, security, reports")

System(backend, "VendNet Back-End", "REST API (Java/Spring Boot)\n+ MySQL\n+ OS-level operations")

System_Ext(vendingMachine, "Vending Machine (Edge)", "Physical device: dispenses products,\ncollects telemetry, reports sales")
System_Ext(paymentGw, "Payment Gateway", "External payment processor\n(e.g., Stripe, PayPal)")

Rel(customer, vendingMachine, "Selects product, pays", "Physical interaction")
Rel(customer, backend, "Views catalog, purchase history", "HTTPS/JSON")
Rel(operator, backend, "Manages stock, views machine status", "HTTPS/JSON")
Rel(admin, backend, "Full system management", "HTTPS/JSON")
Rel(vendingMachine, backend, "Sends telemetry, sales events, stock levels", "HTTPS/JSON + mTLS")
Rel(backend, paymentGw, "Processes payments", "HTTPS/JSON")
@enduml
```

### Architecture Overview

| Layer | Responsibility | Key Technologies |
|-------|---------------|------------------|
| **API / Controller** | HTTP request handling, input validation, DTO mapping | Spring Boot REST Controllers |
| **Application / Service** | Use-case orchestration, transaction management | Spring Services |
| **Domain** | Business logic, aggregates, entities, value objects | Plain Java (POJO) |
| **Infrastructure** | Persistence, Database transactions,  external service integrations, OS operations | Spring Data JPA, MySQL, Java NIO (file ops) |

---

## 1.3 Technology Stack

| Component | Technology | Justification |
|-----------|-----------|---------------|
| **Language** | Java 17 (LTS) | Mature ecosystem, strong typing, excellent security libraries, long-term support |
| **Framework** | Spring Boot 3.x | Industry standard for REST APIs, built-in security (Spring Security), DI, auto-configuration |
| **Database** | MySqlSQL 8.4 (LTS) | Robust relational DB, ACID compliance, strong security features, not in-memory (satisfies constraint C1) |
| **ORM** | Spring Data JPA / Hibernate | Simplifies persistence layer, supports DDD aggregate mapping |
| **Authentication** | Spring Security + JWT (JSON Web Tokens) | Stateless authentication, industry standard, supports role-based access control |
| **Password Hashing** | BCrypt (via Spring Security) | Adaptive hashing, resistant to brute-force and rainbow table attacks |
| **API Documentation** | SpringDoc OpenAPI (Swagger) | Auto-generated API docs, supports security scheme documentation |
| **Build Tool** | Maven | Flexible, fast incremental builds, good dependency management |
| **Containerization** | Docker + Docker Compose | Reproducible environments, easy MySQL setup, CI/CD ready |
| **CI/CD** | GitHub Actions | Native integration with repository, supports SAST/DAST/SCA pipeline |
| **Testing** | JUnit 5 + Mockito + Testcontainers | Comprehensive unit/integration testing with real MySQL instances |

---

## 1.4 Authorization Roles

The system implements **Role-Based Access Control (RBAC)** with three distinct roles, satisfying constraint C3.

| Role | Description | Key Permissions |
|------|-------------|-----------------|
| **Customer** | End-user who purchases products via vending machines or companion app | View product catalog; make purchases; view own purchase history; manage own profile |
| **Operator** | Field technician / maintenance staff responsible for machine upkeep | View/update machine stock levels; access machine telemetry and logs; report machine issues; view assigned machines |
| **Administrator** | System-wide manager with full access | All Operator permissions + manage user accounts and roles; set product pricing; configure system settings; generate and access reports; trigger backups; manage security configuration |

### RBAC Permission Matrix

| Permission | Customer | Operator | Administrator |
|------------|:--------:|:--------:|:-------------:|
| View product catalog | ✅ | ✅ | ✅ |
| Purchase product | ✅ | ❌ | ❌ |
| View own purchase history | ✅ | ❌ | ✅ |
| View machine stock levels | ❌ | ✅ | ✅ |
| Update machine stock | ❌ | ✅ | ✅ |
| Access machine telemetry/logs | ❌ | ✅ | ✅ |
| Report machine issues | ❌ | ✅ | ✅ |
| Manage user accounts | ❌ | ❌ | ✅ |
| Set product pricing | ❌ | ❌ | ✅ |
| Configure system settings | ❌ | ❌ | ✅ |
| Generate/access reports | ❌ | ❌ | ✅ |
| Trigger database backups | ❌ | ❌ | ✅ |
| View audit logs | ❌ | ❌ | ✅ |

---

## 1.5 OS-Level Functionality

The back-end executes several **operating system-level operations** on the server, satisfying constraint C4. All file-system operations are sandboxed to designated directories with strict path validation to prevent path-traversal attacks.

### 1.5.1 Database Backup Generation

| Aspect | Detail |
|--------|--------|
| **What** | Encrypted MySQL dump files |
| **Where** | `/var/vendnet/backups/{YYYY-MM-DD}/` |
| **When** | Scheduled daily (cron) + on-demand by Administrator |
| **How** | Java `ProcessBuilder` invokes `MySQL_Backup`, output piped through AES-256 encryption, written to time-stamped directory |
| **Security** | Backup directory created with `700` permissions; only the application service user has access; old backups rotated after 30 days |

### 1.5.2 Audit Log Rotation

| Aspect | Detail |
|--------|--------|
| **What** | Application audit logs (security events, data access, admin actions) |
| **Where** | `/var/vendnet/logs/audit/` |
| **When** | Daily rotation; compressed after 7 days; deleted after 90 days |
| **How** | Java NIO file operations: create new log file, compress old files (`GZIPOutputStream`), delete expired archives |
| **Security** | Log files created with `640` permissions; tamper detection via HMAC checksums appended to each log entry |

### 1.5.3 Vendor Report Directory Generation

| Aspect | Detail |
|--------|--------|
| **What** | Structured directory trees for operational reports (sales summaries, stock reports, maintenance logs) |
| **Where** | `/var/vendnet/reports/{report-type}/{YYYY}/{MM}/{DD}/` |
| **When** | On-demand when Administrator requests report generation via API |
| **How** | Java NIO `Files.createDirectories()` creates the nested structure; report files (CSV/JSON) written to the target directory |
| **Security** | Report names and paths are validated against a whitelist pattern (`^[a-zA-Z0-9_-]+$`); no user-supplied path components are used directly; directory permissions set to `750` |

### OS Operations Summary

```
/var/vendnet/
├── backups/
│   ├── 2026-04-09/
│   │   └── vendnet_backup_20260409_020000.sql.enc
│   └── 2026-04-10/
│       └── vendnet_backup_20260410_020000.sql.enc
├── logs/
│   └── audit/
│       ├── audit_2026-04-10.log          (current)
│       ├── audit_2026-04-09.log.gz       (compressed)
│       └── audit_2026-04-03.log.gz       (to be deleted at 90 days)
└── reports/
    ├── sales/
    │   └── 2026/04/10/
    │       └── daily_sales_summary.csv
    └── stock/
        └── 2026/04/10/
            └── stock_levels.json
```
