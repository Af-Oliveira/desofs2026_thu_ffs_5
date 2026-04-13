#!/usr/bin/env python3
"""
Level 0 DFD — VendNet (Vending Machine Network)

Generates a Data Flow Diagram (Level 0 / Context Diagram) using pytm.
Run:
    python dfd_level0.py --dfd | dot -Tpng -o dfd_level0.png
    python dfd_level0.py --dfd | dot -Tsvg -o dfd_level0.svg
"""

from pytm import TM, Server, Datastore, Dataflow, Boundary, Actor, ExternalEntity

# ─── Threat Model ───────────────────────────────────────────────
tm = TM("VendNet — Level 0 DFD")
tm.description = (
    "Level 0 Data Flow Diagram for VendNet, a centralized back-end "
    "management system for a geographically distributed Vending Machine Network. "
    "Shows all external entities, the main process, data stores, data flows, "
    "and trust boundaries."
)
tm.isOrdered = True

# ─── Trust Boundaries ───────────────────────────────────────────

# TB1: Internet / Public Network boundary
tb_internet = Boundary("TB1: Internet / Public Network")
tb_internet.description = (
    "Boundary between public internet users (Customers, Operators, "
    "Administrators accessing via HTTPS) and the VendNet back-end. "
    "All traffic must be encrypted (TLS 1.2+) and authenticated."
)

# TB2: External Systems boundary (Vending Machines, Payment Gateway)
tb_external_systems = Boundary("TB2: External Systems Network")
tb_external_systems.description = (
    "Boundary between external systems (physical Vending Machines "
    "and the Payment Gateway) and the VendNet back-end. "
    "Communication uses HTTPS with mutual TLS (mTLS) for machines "
    "and HTTPS + API keys for the Payment Gateway."
)

# TB3: Internal — Back-End boundary (API server, DB, File System)
tb_backend = Boundary("TB3: VendNet Back-End (Internal)")
tb_backend.description = (
    "Internal trust zone encompassing the VendNet API server, "
    "the relational database (MySQL), and the server file system. "
    "Access restricted to the application service account."
)

# TB4: Database boundary (within back-end)
tb_database = Boundary("TB4: Database Zone")
tb_database.description = (
    "Sub-boundary isolating the MySQL database. "
    "Only the application's connection pool can access the database; "
    "credentials stored in environment variables, not in code."
)
tb_database.inBoundary = tb_backend

# TB5: File System boundary (within back-end)
tb_filesystem = Boundary("TB5: File System Zone")
tb_filesystem.description = (
    "Sub-boundary for OS-level file operations on the server "
    "(backups, audit logs, report directories). "
    "Sandboxed to /var/vendnet/ with strict path validation."
)
tb_filesystem.inBoundary = tb_backend

# ─── External Entities ──────────────────────────────────────────

customer = Actor("Customer")
customer.description = (
    "End-user who purchases products via vending machines or the "
    "companion mobile/web app. Can view product catalog and own "
    "purchase history."
)
customer.inBoundary = tb_internet

operator = Actor("Operator")
operator.description = (
    "Field technician / maintenance staff responsible for restocking "
    "machines, accessing machine telemetry and logs, and reporting "
    "machine issues."
)
operator.inBoundary = tb_internet

admin = Actor("Administrator")
admin.description = (
    "System-wide manager with full access: manages user accounts, "
    "sets product pricing, configures system settings, generates "
    "reports, triggers backups, and manages security configuration."
)
admin.inBoundary = tb_internet

vending_machine = ExternalEntity("Vending Machine")
vending_machine.description = (
    "Physical vending machine at a remote location. Sends telemetry "
    "data (temperature, status), sales events, and stock levels to "
    "the back-end. Receives configuration and price updates."
)
vending_machine.inBoundary = tb_external_systems

payment_gateway = ExternalEntity("Payment Gateway")
payment_gateway.description = (
    "External third-party payment processor (e.g., Stripe). "
    "Handles card/mobile payment authorization and settlement."
)
payment_gateway.inBoundary = tb_external_systems

# ─── Main Process ───────────────────────────────────────────────

vendnet_backend = Server("0.0 VendNet Back-End")
vendnet_backend.description = (
    "Central process: REST API (Java/Spring Boot) that handles "
    "authentication, authorization, inventory management, sales "
    "processing, telemetry ingestion, pricing/config, and OS-level "
    "operations (backups, log rotation, report generation)."
)
vendnet_backend.inBoundary = tb_backend
vendnet_backend.port = 443
vendnet_backend.protocol = "HTTPS"
vendnet_backend.isEncrypted = True

# ─── Data Stores ────────────────────────────────────────────────

database = Datastore("MySQL Database")
database.description = (
    "Relational database (MySQL 8.4 LTS) storing users, products, "
    "vending machines, slots, sales, telemetry, and audit records. "
    "Encrypted at rest (TDE) and in transit (TLS)."
)
database.inBoundary = tb_database
database.isEncrypted = True
database.isSQL = True

file_system = Datastore("Server File System")
file_system.description = (
    "OS-level file storage under /var/vendnet/ for encrypted "
    "database backups, rotated audit logs, and generated report "
    "directory structures. Access sandboxed with strict path validation."
)
file_system.inBoundary = tb_filesystem

# ─── Data Flows ─────────────────────────────────────────────────

# Customer <-> VendNet Back-End
customer_to_backend = Dataflow(customer, vendnet_backend, "Credentials, Purchase Requests, Catalog Queries")
customer_to_backend.protocol = "HTTPS"
customer_to_backend.isEncrypted = True
customer_to_backend.description = (
    "Customer sends login credentials (username/password), "
    "product catalog queries, and purchase requests over HTTPS."
)

backend_to_customer = Dataflow(vendnet_backend, customer, "JWT Token, Catalog Data, Purchase Confirmation")
backend_to_customer.protocol = "HTTPS"
backend_to_customer.isEncrypted = True
backend_to_customer.description = (
    "Back-end returns JWT authentication token, product catalog "
    "listings, and purchase confirmations/receipts to the Customer."
)

# Operator <-> VendNet Back-End
operator_to_backend = Dataflow(operator, vendnet_backend, "Stock Updates, Machine Log Requests, Issue Reports")
operator_to_backend.protocol = "HTTPS"
operator_to_backend.isEncrypted = True
operator_to_backend.description = (
    "Operator submits stock restock updates, requests machine "
    "telemetry/log data, and reports machine issues."
)

backend_to_operator = Dataflow(vendnet_backend, operator, "Stock Status, Machine Logs, Issue Acknowledgements")
backend_to_operator.protocol = "HTTPS"
backend_to_operator.isEncrypted = True
backend_to_operator.description = (
    "Back-end returns current stock levels, machine telemetry "
    "and log data, and acknowledgements for reported issues."
)

# Administrator <-> VendNet Back-End
admin_to_backend = Dataflow(admin, vendnet_backend, "Management Commands, Config Changes, Report Requests")
admin_to_backend.protocol = "HTTPS"
admin_to_backend.isEncrypted = True
admin_to_backend.description = (
    "Administrator sends user management commands (create/update/"
    "suspend), pricing changes, system configuration, backup "
    "triggers, and report generation requests."
)

backend_to_admin = Dataflow(vendnet_backend, admin, "Reports, System Status, User Lists, Audit Logs")
backend_to_admin.protocol = "HTTPS"
backend_to_admin.isEncrypted = True
backend_to_admin.description = (
    "Back-end returns generated reports (sales, stock), system "
    "status dashboards, user account listings, and audit log data."
)

# Vending Machine <-> VendNet Back-End
vm_to_backend = Dataflow(vending_machine, vendnet_backend, "Telemetry Data, Sales Events, Stock Levels")
vm_to_backend.protocol = "HTTPS"
vm_to_backend.isEncrypted = True
vm_to_backend.description = (
    "Vending machine sends periodic telemetry (status, temperature), "
    "real-time sales event notifications, and current stock levels. "
    "Authenticated via mutual TLS (mTLS)."
)

backend_to_vm = Dataflow(vendnet_backend, vending_machine, "Configuration Updates, Price Changes")
backend_to_vm.protocol = "HTTPS"
backend_to_vm.isEncrypted = True
backend_to_vm.description = (
    "Back-end pushes configuration updates (slot assignments, "
    "operational parameters) and price changes to vending machines."
)

# VendNet Back-End <-> Payment Gateway
backend_to_payment = Dataflow(vendnet_backend, payment_gateway, "Payment Authorization Requests")
backend_to_payment.protocol = "HTTPS"
backend_to_payment.isEncrypted = True
backend_to_payment.description = (
    "Back-end sends payment authorization requests containing "
    "transaction amount, currency, and tokenized card data to the "
    "external payment gateway."
)

payment_to_backend = Dataflow(payment_gateway, vendnet_backend, "Payment Confirmation / Rejection")
payment_to_backend.protocol = "HTTPS"
payment_to_backend.isEncrypted = True
payment_to_backend.description = (
    "Payment gateway returns authorization result (approved/declined), "
    "transaction reference ID, and settlement confirmation."
)

# VendNet Back-End <-> Database
backend_to_db = Dataflow(vendnet_backend, database, "SQL Queries (CRUD Operations)")
backend_to_db.protocol = "MySQL/TLS"
backend_to_db.isEncrypted = True
backend_to_db.description = (
    "Application issues SQL queries through JPA/Hibernate "
    "connection pool: INSERT/SELECT/UPDATE/DELETE for users, "
    "products, machines, sales, and telemetry records."
)

db_to_backend = Dataflow(database, vendnet_backend, "Query Results (Data Records)")
db_to_backend.protocol = "MySQL/TLS"
db_to_backend.isEncrypted = True
db_to_backend.description = (
    "Database returns result sets containing user records, "
    "product data, machine status, sales history, and telemetry "
    "data in response to application queries."
)

# VendNet Back-End -> File System
backend_to_fs = Dataflow(vendnet_backend, file_system, "Backup Files, Audit Logs, Report Directories")
backend_to_fs.description = (
    "Application writes encrypted database backup files to "
    "/var/vendnet/backups/, appends audit log entries to "
    "/var/vendnet/logs/audit/, and creates report directory "
    "structures under /var/vendnet/reports/. All paths validated "
    "against whitelist patterns to prevent path traversal."
)

fs_to_backend = Dataflow(file_system, vendnet_backend, "Backup Status, Log Contents, Report Files")
fs_to_backend.description = (
    "Application reads backup status metadata, retrieves audit "
    "log contents for admin review, and reads generated report "
    "files (CSV/JSON) for serving via the API."
)

# ─── Process ────────────────────────────────────────────────────

if __name__ == "__main__":
    tm.process()
