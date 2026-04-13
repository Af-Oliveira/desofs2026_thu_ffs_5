#!/usr/bin/env python3
"""
Level 1 DFD - VendNet (Master Overview)

Decomposes the Level 0 single process (0.0 VendNet Back-End) into
six sub-processes showing primary data flows between external entities,
processes, and consolidated data stores.

For detailed data store access and internal flows per functional area,
see the focused view diagrams:
  - dfd_level1_view_auth.py  (Authentication & Authorization)
  - dfd_level1_view_core.py  (Sales, Inventory, Pricing)
  - dfd_level1_view_ops.py   (Telemetry, OS Operations)

Run:
    python dfd_level1.py --dfd | dot -Tpng -o dfd_level1.png
"""

from pytm import (
    TM,
    Process,
    Datastore,
    Dataflow,
    Boundary,
    Actor,
    ExternalEntity,
)

# --- Threat Model -------------------------------------------------------

tm = TM("VendNet - Level 1 DFD (Master Overview)")
tm.description = (
    "Level 1 Data Flow Diagram - Master Overview. Decomposes the "
    "single Level 0 process into six sub-processes with primary "
    "data flows, consolidated data stores, and trust boundaries."
)
tm.isOrdered = True

# --- Trust Boundaries ----------------------------------------------------

tb_internet = Boundary("TB1: Internet / Public Network")
tb_internet.description = (
    "Boundary between public internet users (Customers, Operators, "
    "Administrators accessing via HTTPS) and the VendNet back-end. "
    "All traffic must be encrypted (TLS 1.2+) and authenticated."
)

tb_external = Boundary("TB2: External Systems Network")
tb_external.description = (
    "Boundary between external systems (Vending Machines via mTLS, "
    "Payment Gateway via HTTPS + API key) and the back-end."
)

tb_backend = Boundary("TB3: VendNet Back-End (Internal)")
tb_backend.description = (
    "Internal trust zone encompassing the VendNet API server, "
    "application processes, database, and file system."
)

tb_data = Boundary("TB4: Data Persistence Zone")
tb_data.description = (
    "Sub-boundary isolating the MySQL database and server file "
    "system. Only the application connection pool has access."
)
tb_data.inBoundary = tb_backend

# --- External Entities ---------------------------------------------------

customer = Actor("Customer")
customer.description = (
    "End-user who purchases products via vending machines or the "
    "companion app. Can browse catalog and view purchase history."
)
customer.inBoundary = tb_internet

operator = Actor("Operator")
operator.description = (
    "Field technician responsible for restocking machines, "
    "accessing telemetry/logs, and reporting issues."
)
operator.inBoundary = tb_internet

admin = Actor("Administrator")
admin.description = (
    "System-wide manager: manages users, pricing, configuration, "
    "reports, backups, and security."
)
admin.inBoundary = tb_internet

vending_machine = ExternalEntity("Vending Machine")
vending_machine.description = (
    "Physical vending machine. Sends telemetry, sales events, "
    "and stock levels. Receives config and price updates."
)
vending_machine.inBoundary = tb_external

payment_gw = ExternalEntity("Payment Gateway")
payment_gw.description = (
    "External payment processor (e.g., Stripe). Handles payment "
    "authorization and settlement."
)
payment_gw.inBoundary = tb_external

# --- Sub-Processes (Level 1) ---------------------------------------------

p1 = Process("1.0 Authentication & Authorization")
p1.description = (
    "Handles user login (credential verification), JWT token "
    "issuance and validation, RBAC enforcement, and user account "
    "management (create, update, suspend)."
)
p1.inBoundary = tb_backend

p2 = Process("2.0 Inventory Management")
p2.description = (
    "Manages product catalog (CRUD), machine slot assignments, "
    "and stock levels. Provides stock data for sales validation."
)
p2.inBoundary = tb_backend

p3 = Process("3.0 Sales Processing")
p3.description = (
    "Orchestrates sales: validates purchases, coordinates payment "
    "with gateway, decrements stock, records immutable Sale."
)
p3.inBoundary = tb_backend

p4 = Process("4.0 Telemetry & Monitoring")
p4.description = (
    "Ingests telemetry from vending machines (status, temp, "
    "connectivity), updates machine status, provides dashboards."
)
p4.inBoundary = tb_backend

p5 = Process("5.0 OS Operations")
p5.description = (
    "Executes OS-level operations: AES-256 encrypted DB backups, "
    "audit log rotation, and report directory generation under "
    "/var/vendnet/."
)
p5.inBoundary = tb_backend

p6 = Process("6.0 Pricing & Configuration")
p6.description = (
    "Manages product pricing, machine configuration, and pushes "
    "config/price changes to vending machines."
)
p6.inBoundary = tb_backend

# --- Data Stores (Consolidated) ------------------------------------------

db = Datastore("MySQL Database")
db.description = (
    "MySQL 8.4 LTS storing all application data: users, products, "
    "machines/slots, sales, telemetry, and audit records. "
    "Encrypted at rest (TDE) and in transit (TLS)."
)
db.inBoundary = tb_data
db.isEncrypted = True
db.isSQL = True

fs = Datastore("Server File System (/var/vendnet/)")
fs.description = (
    "OS-level file storage for encrypted backups (/backups/), "
    "rotated audit logs (/logs/), and report directories "
    "(/reports/). Sandboxed with strict path validation."
)
fs.inBoundary = tb_data

# =========================================================================
# DATA FLOWS - External Entity <-> Process
# =========================================================================

# -- Customer Flows -------------------------------------------------------

df01 = Dataflow(customer, p1, "Login Credentials")
df01.protocol = "HTTPS"
df01.isEncrypted = True

df02 = Dataflow(p1, customer, "JWT Token")
df02.protocol = "HTTPS"
df02.isEncrypted = True

df03 = Dataflow(customer, p2, "Catalog Queries")
df03.protocol = "HTTPS"
df03.isEncrypted = True

df04 = Dataflow(p2, customer, "Product Catalog")
df04.protocol = "HTTPS"
df04.isEncrypted = True

df05 = Dataflow(customer, p3, "Purchase Request")
df05.protocol = "HTTPS"
df05.isEncrypted = True

df06 = Dataflow(p3, customer, "Purchase Confirmation")
df06.protocol = "HTTPS"
df06.isEncrypted = True

# -- Operator Flows -------------------------------------------------------

df07 = Dataflow(operator, p1, "Login Credentials")
df07.protocol = "HTTPS"
df07.isEncrypted = True

df08 = Dataflow(p1, operator, "JWT Token")
df08.protocol = "HTTPS"
df08.isEncrypted = True

df09 = Dataflow(operator, p2, "Restock Updates")
df09.protocol = "HTTPS"
df09.isEncrypted = True

df10 = Dataflow(p2, operator, "Stock Status")
df10.protocol = "HTTPS"
df10.isEncrypted = True

df11 = Dataflow(operator, p4, "Machine Log Requests")
df11.protocol = "HTTPS"
df11.isEncrypted = True

df12 = Dataflow(p4, operator, "Telemetry Data / Logs")
df12.protocol = "HTTPS"
df12.isEncrypted = True

# -- Administrator Flows --------------------------------------------------

df13 = Dataflow(admin, p1, "Credentials / User Management")
df13.protocol = "HTTPS"
df13.isEncrypted = True

df14 = Dataflow(p1, admin, "JWT Token / User Lists")
df14.protocol = "HTTPS"
df14.isEncrypted = True

df15 = Dataflow(admin, p6, "Price & Config Changes")
df15.protocol = "HTTPS"
df15.isEncrypted = True

df16 = Dataflow(p6, admin, "Config Confirmations")
df16.protocol = "HTTPS"
df16.isEncrypted = True

df17 = Dataflow(admin, p5, "Backup & Report Requests")
df17.protocol = "HTTPS"
df17.isEncrypted = True

df18 = Dataflow(p5, admin, "Reports / Backup Status")
df18.protocol = "HTTPS"
df18.isEncrypted = True

# -- Vending Machine Flows ------------------------------------------------

df19 = Dataflow(vending_machine, p4, "Telemetry Data (mTLS)")
df19.protocol = "HTTPS"
df19.isEncrypted = True

df20 = Dataflow(vending_machine, p3, "Sales Events (mTLS)")
df20.protocol = "HTTPS"
df20.isEncrypted = True

df21 = Dataflow(p6, vending_machine, "Config & Price Push (mTLS)")
df21.protocol = "HTTPS"
df21.isEncrypted = True

# -- Payment Gateway Flows ------------------------------------------------

df22 = Dataflow(p3, payment_gw, "Payment Authorization Request")
df22.protocol = "HTTPS"
df22.isEncrypted = True

df23 = Dataflow(payment_gw, p3, "Payment Result")
df23.protocol = "HTTPS"
df23.isEncrypted = True

# =========================================================================
# DATA FLOWS - Inter-Process
# =========================================================================

df24 = Dataflow(p3, p2, "Stock Check / Decrement")
df24.description = (
    "Sales queries Inventory for stock availability and "
    "commands stock decrement after successful payment."
)

df25 = Dataflow(p2, p3, "Stock Confirmation")
df25.description = "Inventory confirms stock or rejects if insufficient."

df26 = Dataflow(p6, p2, "Updated Prices & Slot Config")
df26.description = (
    "Pricing propagates updated product prices and slot "
    "assignments to Inventory Management."
)

# =========================================================================
# DATA FLOWS - Process <-> Data Stores
# =========================================================================

df27 = Dataflow(p1, db, "Users, Audit Events")
df27.protocol = "MySQL/TLS"
df27.isEncrypted = True

df28 = Dataflow(db, p1, "Credentials, Roles")
df28.protocol = "MySQL/TLS"
df28.isEncrypted = True

df29 = Dataflow(p2, db, "Products, Slots")
df29.protocol = "MySQL/TLS"
df29.isEncrypted = True

df30 = Dataflow(db, p2, "Catalog, Stock Data")
df30.protocol = "MySQL/TLS"
df30.isEncrypted = True

df31 = Dataflow(p3, db, "Sale Records")
df31.protocol = "MySQL/TLS"
df31.isEncrypted = True

df32 = Dataflow(db, p3, "Product Prices")
df32.protocol = "MySQL/TLS"
df32.isEncrypted = True

df33 = Dataflow(p4, db, "Telemetry, Machine Status")
df33.protocol = "MySQL/TLS"
df33.isEncrypted = True

df34 = Dataflow(db, p4, "Machine Config")
df34.protocol = "MySQL/TLS"
df34.isEncrypted = True

df35 = Dataflow(p5, db, "Audit Events, DB Dump")
df35.protocol = "MySQL/TLS"
df35.isEncrypted = True

df36 = Dataflow(db, p5, "Audit Records")
df36.protocol = "MySQL/TLS"
df36.isEncrypted = True

df37 = Dataflow(p6, db, "Prices, Config")
df37.protocol = "MySQL/TLS"
df37.isEncrypted = True

df38 = Dataflow(db, p6, "Current Config")
df38.protocol = "MySQL/TLS"
df38.isEncrypted = True

# -- File System Flows ----------------------------------------------------

df39 = Dataflow(p5, fs, "Backups, Logs, Reports")
df39.description = (
    "Writes encrypted backup files, rotated audit logs, and "
    "report directory structures."
)

df40 = Dataflow(fs, p5, "File Contents")
df40.description = "Reads report files and backup metadata for admin."

# --- Process -------------------------------------------------------------

if __name__ == "__main__":
    tm.process()
