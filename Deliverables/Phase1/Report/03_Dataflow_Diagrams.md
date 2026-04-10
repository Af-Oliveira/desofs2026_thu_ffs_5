# 3. Data Flow Diagrams

## 3.1 Level 0 — Context Diagram

<!-- TODO: Finalize diagram. Ensure trust boundaries are correct. -->

```mermaid
graph LR
    subgraph "Trust Boundary: External Users"
        Customer["👤 Customer"]
        Operator["🔧 Operator"]
        Admin["🛡️ Administrator"]
    end

    subgraph "Trust Boundary: External Systems"
        VM["🏭 Vending Machine"]
        PayGW["💳 Payment Gateway"]
    end

    subgraph "Trust Boundary: Back-End"
        Process0(("0.0\nVendNet\nBack-End"))
        DB[("Database\n(PostgreSQL)")]
        FS[/"File System\n(OS)"/]
    end

    Customer -- "Credentials, Purchase Requests,\nCatalog Queries" --> Process0
    Process0 -- "JWT Token, Catalog Data,\nPurchase Confirmation" --> Customer

    Operator -- "Stock Updates, Log Requests" --> Process0
    Process0 -- "Stock Status, Machine Logs" --> Operator

    Admin -- "Management Commands,\nConfig Changes" --> Process0
    Process0 -- "Reports, System Status,\nUser Lists" --> Admin

    VM -- "Telemetry, Sales Events,\nStock Levels" --> Process0
    Process0 -- "Config Updates,\nPrice Changes" --> VM

    Process0 -- "Payment Requests" --> PayGW
    PayGW -- "Payment Confirmations" --> Process0

    Process0 <--> DB
    Process0 --> FS
```

### Trust Boundaries Identified

| # | Trust Boundary | Description |
|---|----------------|-------------|
| TB1 | External Users ↔ Back-End | <!-- TODO: describe --> |
| TB2 | External Systems (VM, Payment GW) ↔ Back-End | <!-- TODO: describe --> |
| TB3 | Back-End Application ↔ Database | <!-- TODO: describe --> |
| TB4 | Back-End Application ↔ File System (OS) | <!-- TODO: describe --> |

---

## 3.2 Level 1 — Decomposed DFD

<!-- TODO: Finalize — this is a structural placeholder. -->

```mermaid
graph TB
    subgraph "Trust Boundary: External Users"
        Customer["👤 Customer"]
        Operator["🔧 Operator"]
        Admin["🛡️ Administrator"]
    end

    subgraph "Trust Boundary: External Systems"
        VM["🏭 Vending Machine"]
        PayGW["💳 Payment Gateway"]
    end

    subgraph "Trust Boundary: Back-End"
        P1(("1.0\nAuth &\nAuthz"))
        P2(("2.0\nInventory\nMgmt"))
        P3(("3.0\nSales\nProcessing"))
        P4(("4.0\nTelemetry &\nMonitoring"))
        P5(("5.0\nOS\nOperations"))
        P6(("6.0\nPricing &\nConfig"))

        DB_Users[("Users DB")]
        DB_Inventory[("Inventory DB")]
        DB_Sales[("Sales DB")]
        DB_Telemetry[("Telemetry DB")]
        FS[/"File System"/]
    end

    Customer -- "Credentials" --> P1
    P1 -- "JWT Token" --> Customer
    Customer -- "Purchase Request" --> P3
    P3 -- "Confirmation" --> Customer

    Operator -- "Stock Updates" --> P2
    P2 -- "Stock Status" --> Operator

    Admin -- "Management Commands" --> P6
    P6 -- "Config Status" --> Admin
    Admin -- "Report Requests" --> P5

    VM -- "Telemetry Data" --> P4
    VM -- "Sales Events" --> P3
    P6 -- "Price Updates" --> VM

    P3 -- "Payment Request" --> PayGW
    PayGW -- "Payment Result" --> P3

    P1 <--> DB_Users
    P2 <--> DB_Inventory
    P3 <--> DB_Sales
    P4 --> DB_Telemetry

    P5 --> FS
    P3 --> P5
```

### Level 1 Process Descriptions

| Process | Name | Description |
|---------|------|-------------|
| 1.0 | Authentication & Authorization | <!-- TODO --> |
| 2.0 | Inventory Management | <!-- TODO --> |
| 3.0 | Sales Processing | <!-- TODO --> |
| 4.0 | Telemetry & Monitoring | <!-- TODO --> |
| 5.0 | OS Operations | <!-- TODO --> |
| 6.0 | Pricing & Configuration | <!-- TODO --> |

---

## 3.3 Level 2 — Detailed Sub-Process DFDs

### 3.3.1 Process 3.0: Sales Processing — Level 2 Decomposition

<!-- TODO: Decompose into sub-processes such as:
  3.1 Validate Purchase Request
  3.2 Process Payment
  3.3 Update Stock
  3.4 Record Sale
-->

### 3.3.2 Process 5.0: OS Operations — Level 2 Decomposition

<!-- TODO: Decompose into sub-processes such as:
  5.1 Generate Encrypted Backup
  5.2 Rotate Audit Logs
  5.3 Generate Vendor Report Directory Structure
-->

### 3.3.3 Justification for Decomposition Decisions

| Process | Decomposed? | Justification |
|---------|-------------|---------------|
| 1.0 Auth | <!-- TODO --> | <!-- TODO --> |
| 2.0 Inventory | <!-- TODO --> | <!-- TODO --> |
| 3.0 Sales | Yes | <!-- TODO: e.g., involves multiple sub-steps: validation, payment, stock update, recording --> |
| 4.0 Telemetry | <!-- TODO --> | <!-- TODO --> |
| 5.0 OS Ops | Yes | <!-- TODO: e.g., file-system operations each have distinct security concerns --> |
| 6.0 Pricing | <!-- TODO --> | <!-- TODO --> |
