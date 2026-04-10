# 1. System Overview

## 1.1 Purpose and Scope
<!-- TODO: Describe the Vending Machine Network system purpose, business context, and scope -->

## 1.2 High-Level Architecture

<!-- TODO: Replace with final diagram -->
```plantuml
@startuml
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml

title System Context Diagram — Vending Machine Network

Person(customer, "Customer", "Purchases products via machine or app")
Person(operator, "Operator / Maintenance", "Restocks machines, extracts logs")
Person(admin, "Administrator", "Manages network, prices, accounts, security")

System(backend, "VendNet Back-End", "REST API + relational DB.\nManages inventory, pricing, reporting, maintenance.")
System_Ext(vendingMachine, "Vending Machine (Edge)", "Physical device: stock, dispense, telemetry")
System_Ext(paymentGw, "Payment Gateway", "External payment processor")

Rel(customer, vendingMachine, "Selects product, pays")
Rel(customer, backend, "Views catalog, history (app)")
Rel(operator, backend, "Checks stock, uploads logs")
Rel(admin, backend, "Full management")
Rel(vendingMachine, backend, "Sends telemetry, sales, stock levels")
Rel(backend, paymentGw, "Processes payments")
Rel(backend, backend, "OS ops: backups, log rotation, report dirs")
@enduml
```

## 1.3 Technology Stack
<!-- TODO: Justify chosen language/framework (e.g., Java/Spring Boot, C#/.NET, Python/FastAPI), DB (PostgreSQL), etc. -->

## 1.4 Authorization Roles

| Role | Description | Key Permissions |
|------|-------------|-----------------|
| Customer | End-user of the vending machine / mobile app | View catalog, purchase, view own history |
| Operator | Maintenance/field staff | View/update stock, access machine logs, report issues |
| Administrator | System-wide manager | All operator permissions + manage users, set prices, configure security, access reports |

## 1.5 OS-Level Functionality
<!-- TODO: Detail how the back-end fulfills constraint C4 -->
<!-- Examples: daily encrypted backup to /var/backups/vendnet/, rotating audit logs, generating /reports/{vendor}/{date}/ directories -->
