#!/usr/bin/env python3
"""
Level 1 DFD — Focused View: Core Business
(Sales Processing, Inventory Management, Pricing & Configuration)

Shows Processes 2.0, 3.0, and 6.0 with their connected external
entities, inter-process flows, and granular data stores.

Run:
    python dfd_level1_view_core.py --dfd | dot -Tpng -o dfd_level1_view_core.png
"""

from pytm import TM, Process, Datastore, Dataflow, Boundary, Actor, ExternalEntity

# ─── Threat Model ───────────────────────────────────────────────

tm = TM("VendNet — Level 1 — Core Business View")
tm.description = (
    "Focused Level 1 view showing the core business processes: "
    "Sales Processing (3.0), Inventory Management (2.0), and "
    "Pricing & Configuration (6.0) with their data flows to "
    "external entities, inter-process interactions, and granular "
    "data store access (Product Store, Machine Store, Sales Store)."
)
tm.isOrdered = True

# ─── Trust Boundaries ───────────────────────────────────────────

tb_internet = Boundary("TB1: Internet / Public Network")
tb_internet.description = (
    "All user traffic crosses this boundary via HTTPS (TLS 1.2+)."
)

tb_external = Boundary("TB2: External Systems Network")
tb_external.description = (
    "Boundary for Vending Machines (mTLS) and Payment Gateway "
    "(HTTPS + API key + HMAC)."
)

tb_backend = Boundary("TB3: VendNet Back-End (Internal)")
tb_backend.description = (
    "Internal trust zone for application processes."
)

tb_database = Boundary("TB4: Database Zone")
tb_database.description = (
    "MySQL database zone with encrypted connections (MySQL/TLS)."
)
tb_database.inBoundary = tb_backend

# ─── External Entities ──────────────────────────────────────────

customer = Actor("Customer")
customer.description = (
    "End-user who browses the product catalog and makes purchases."
)
customer.inBoundary = tb_internet

operator = Actor("Operator")
operator.description = (
    "Field technician who restocks machines and reports issues."
)
operator.inBoundary = tb_internet

admin = Actor("Administrator")
admin.description = (
    "System manager who manages the product catalog, pricing, "
    "and machine configuration."
)
admin.inBoundary = tb_internet

vending_machine = ExternalEntity("Vending Machine")
vending_machine.description = (
    "Physical vending machine that reports on-site sales events "
    "and receives configuration/price updates."
)
vending_machine.inBoundary = tb_external

payment_gw = ExternalEntity("Payment Gateway")
payment_gw.description = (
    "External payment processor (e.g., Stripe). Handles payment "
    "authorization and settlement."
)
payment_gw.inBoundary = tb_external

# ─── Processes ──────────────────────────────────────────────────

p2 = Process("2.0 Inventory Management")
p2.description = (
    "Manages product catalog (CRUD), vending machine slot "
    "assignments, and stock levels. Operators update stock "
    "after physical restocking. Provides real-time stock "
    "data for sales validation."
)
p2.inBoundary = tb_backend

p3 = Process("3.0 Sales Processing")
p3.description = (
    "Orchestrates the sales transaction lifecycle: validates "
    "purchase requests, coordinates payment with the Payment "
    "Gateway, requests stock decrement from Inventory, and "
    "records the immutable Sale record."
)
p3.inBoundary = tb_backend

p6 = Process("6.0 Pricing & Configuration")
p6.description = (
    "Manages product pricing updates and machine configuration "
    "(slot assignments, operational parameters). Pushes changes "
    "to vending machines via mTLS."
)
p6.inBoundary = tb_backend

# ─── Data Stores ────────────────────────────────────────────────

db_products = Datastore("Product Store")
db_products.description = (
    "MySQL table(s): product catalog with names, descriptions, "
    "prices (amount + ISO 4217 currency), categories, and "
    "active/inactive status."
)
db_products.inBoundary = tb_database
db_products.isEncrypted = True
db_products.isSQL = True

db_machines = Datastore("Machine Store (Slots)")
db_machines.description = (
    "MySQL table(s): vending machine records with serial numbers, "
    "locations, status, and slot configuration (slot number, "
    "capacity, current quantity, assigned ProductId)."
)
db_machines.inBoundary = tb_database
db_machines.isEncrypted = True
db_machines.isSQL = True

db_sales = Datastore("Sales Store")
db_sales.description = (
    "MySQL table(s): immutable sales transaction records with "
    "sale ID, machine ref, product ref, user ref, quantity, "
    "unit price snapshot, total amount, payment info, timestamp."
)
db_sales.inBoundary = tb_database
db_sales.isEncrypted = True
db_sales.isSQL = True

# ═══════════════════════════════════════════════════════════════
# DATA FLOWS — External Entity ↔ Process
# ═══════════════════════════════════════════════════════════════

# ── Customer Flows ─────────────────────────────────────────────

df_cust_catalog = Dataflow(customer, p2, "Catalog Queries (Browse Products)")
df_cust_catalog.protocol = "HTTPS"
df_cust_catalog.isEncrypted = True
df_cust_catalog.description = (
    "Customer requests product catalog listings. JWT-authenticated "
    "via 1.0 Auth before reaching Inventory."
)

df_catalog_cust = Dataflow(p2, customer, "Product Catalog (Names, Prices, Availability)")
df_catalog_cust.protocol = "HTTPS"
df_catalog_cust.isEncrypted = True

df_cust_purchase = Dataflow(customer, p3, "Purchase Request (Product, Machine, Payment)")
df_cust_purchase.protocol = "HTTPS"
df_cust_purchase.isEncrypted = True
df_cust_purchase.description = (
    "Customer submits a purchase request with product ID, machine "
    "ID, quantity, and tokenized payment method."
)

df_receipt = Dataflow(p3, customer, "Purchase Confirmation / Digital Receipt")
df_receipt.protocol = "HTTPS"
df_receipt.isEncrypted = True

# ── Operator Flows ─────────────────────────────────────────────

df_op_restock = Dataflow(operator, p2, "Restock Updates (Machine, Slot, Quantity)")
df_op_restock.protocol = "HTTPS"
df_op_restock.isEncrypted = True
df_op_restock.description = (
    "Operator submits stock restock data after physically "
    "servicing a vending machine."
)

df_op_status = Dataflow(p2, operator, "Stock Status / Issue Acknowledgement")
df_op_status.protocol = "HTTPS"
df_op_status.isEncrypted = True

# ── Administrator Flows ────────────────────────────────────────

df_admin_catalog = Dataflow(admin, p2, "Create/Update Product (Catalog CRUD)")
df_admin_catalog.protocol = "HTTPS"
df_admin_catalog.isEncrypted = True
df_admin_catalog.description = (
    "Admin creates new products, updates descriptions/categories, "
    "or deactivates products in the catalog."
)

df_admin_pricing = Dataflow(admin, p6, "Price Changes / Machine Config Updates")
df_admin_pricing.protocol = "HTTPS"
df_admin_pricing.isEncrypted = True
df_admin_pricing.description = (
    "Admin sets product prices and configures machine operational "
    "parameters (slot assignments, temperature targets)."
)

df_pricing_confirm = Dataflow(p6, admin, "Config & Pricing Confirmations")
df_pricing_confirm.protocol = "HTTPS"
df_pricing_confirm.isEncrypted = True

# ── Vending Machine Flows ──────────────────────────────────────

df_vm_sales = Dataflow(vending_machine, p3, "On-Site Sales Events (mTLS)")
df_vm_sales.protocol = "HTTPS"
df_vm_sales.isEncrypted = True
df_vm_sales.description = (
    "Vending machine reports completed on-site sale events "
    "(product, quantity, payment method, timestamp). "
    "Authenticated via mutual TLS (mTLS)."
)

df_vm_config = Dataflow(p6, vending_machine, "Config & Price Push (mTLS)")
df_vm_config.protocol = "HTTPS"
df_vm_config.isEncrypted = True
df_vm_config.description = (
    "Pushes updated pricing and configuration to vending "
    "machines via mTLS."
)

# ── Payment Gateway Flows ──────────────────────────────────────

df_pay_req = Dataflow(p3, payment_gw, "Payment Authorization Request")
df_pay_req.protocol = "HTTPS"
df_pay_req.isEncrypted = True
df_pay_req.description = (
    "Sends payment authorization request with amount, currency, "
    "and tokenized card data. No raw card data transmitted."
)

df_pay_result = Dataflow(payment_gw, p3, "Payment Result (Approved/Declined)")
df_pay_result.protocol = "HTTPS"
df_pay_result.isEncrypted = True
df_pay_result.description = (
    "Returns authorization result and transaction reference ID."
)

# ═══════════════════════════════════════════════════════════════
# DATA FLOWS — Inter-Process
# ═══════════════════════════════════════════════════════════════

df_stock_check = Dataflow(p3, p2, "Stock Availability Check / Decrement Command")
df_stock_check.description = (
    "Sales queries Inventory for product availability in the "
    "requested machine slot, then commands stock decrement "
    "after successful payment."
)

df_stock_confirm = Dataflow(p2, p3, "Stock Confirmed / Insufficient")
df_stock_confirm.description = (
    "Inventory confirms stock availability or rejects the "
    "request if insufficient quantity."
)

df_price_sync = Dataflow(p6, p2, "Updated Prices & Slot Config")
df_price_sync.description = (
    "Pricing & Configuration propagates updated product prices "
    "and slot assignments to Inventory Management so that "
    "catalog queries reflect current pricing."
)

# ═══════════════════════════════════════════════════════════════
# DATA FLOWS — Process ↔ Data Store
# ═══════════════════════════════════════════════════════════════

# ── Inventory ↔ Product Store ──────────────────────────────────

df_inv_prod_w = Dataflow(p2, db_products, "Read/Write Product Records")
df_inv_prod_w.protocol = "MySQL/TLS"
df_inv_prod_w.isEncrypted = True

df_inv_prod_r = Dataflow(db_products, p2, "Product Data (Price, Category, Status)")
df_inv_prod_r.protocol = "MySQL/TLS"
df_inv_prod_r.isEncrypted = True

# ── Inventory ↔ Machine Store ──────────────────────────────────

df_inv_mach_w = Dataflow(p2, db_machines, "Read/Write Machine Slots & Stock Levels")
df_inv_mach_w.protocol = "MySQL/TLS"
df_inv_mach_w.isEncrypted = True

df_inv_mach_r = Dataflow(db_machines, p2, "Slot Configuration & Current Quantities")
df_inv_mach_r.protocol = "MySQL/TLS"
df_inv_mach_r.isEncrypted = True

# ── Sales ↔ Sales Store ───────────────────────────────────────

df_sales_w = Dataflow(p3, db_sales, "Write Immutable Sale Record")
df_sales_w.protocol = "MySQL/TLS"
df_sales_w.isEncrypted = True
df_sales_w.description = (
    "Persists the immutable sale transaction record after "
    "successful payment and stock decrement."
)

df_sales_r = Dataflow(db_sales, p3, "Sale History Data")
df_sales_r.protocol = "MySQL/TLS"
df_sales_r.isEncrypted = True

# ── Sales → Product Store (price lookup) ───────────────────────

df_sales_price = Dataflow(p3, db_products, "Fetch Product Price at Sale Time")
df_sales_price.protocol = "MySQL/TLS"
df_sales_price.isEncrypted = True
df_sales_price.description = (
    "Looks up the current product price to include in the "
    "immutable sale record as a price snapshot."
)

# ── Pricing ↔ Product Store ───────────────────────────────────

df_price_prod_w = Dataflow(p6, db_products, "Update Product Prices")
df_price_prod_w.protocol = "MySQL/TLS"
df_price_prod_w.isEncrypted = True

df_price_prod_r = Dataflow(db_products, p6, "Current Price Data")
df_price_prod_r.protocol = "MySQL/TLS"
df_price_prod_r.isEncrypted = True

# ── Pricing ↔ Machine Store ───────────────────────────────────

df_price_mach_w = Dataflow(p6, db_machines, "Update Machine Config & Slot Assignments")
df_price_mach_w.protocol = "MySQL/TLS"
df_price_mach_w.isEncrypted = True

df_price_mach_r = Dataflow(db_machines, p6, "Current Machine Configuration")
df_price_mach_r.protocol = "MySQL/TLS"
df_price_mach_r.isEncrypted = True

# ─── Process ────────────────────────────────────────────────────

if __name__ == "__main__":
    tm.process()
