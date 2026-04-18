#!/usr/bin/env python3
"""
Level 2 DFD — VendNet — Process 3.0 (Sales Processing)

Run:
    python dfd_level2_p3_sales.py --dfd | dot -Tpng -o dfd_level2_p3_sales.png
"""

from pytm import TM, Process, Datastore, Dataflow, Boundary, Actor, ExternalEntity

tm = TM("Level 2 DFD — 3.0 Sales Processing")
tm.description = ("Level 2 Data Flow Diagram for the Sales Processing sub-system.")
tm.isOrdered = True

tb_internet = Boundary("TB1: Internet")
tb_external = Boundary("TB2: External Systems")
tb_backend = Boundary("TB3: Back-End")
tb_database = Boundary("TB4: Database")
tb_database.inBoundary = tb_backend

# External Entities
customer = Actor("Customer")
customer.inBoundary = tb_internet

payment_gw = ExternalEntity("Payment Gateway")
payment_gw.inBoundary = tb_external

# Other subsystem interactions (represented as processes or pseudo-actors)
p2_inventory = Process("2.0 Inventory Management")
p2_inventory.inBoundary = tb_backend

# Sub-processes
p3_1 = Process("3.1 Request Validation")
p3_1.inBoundary = tb_backend

p3_2 = Process("3.2 Payment Authorization")
p3_2.inBoundary = tb_backend

p3_3 = Process("3.3 Stock Decrement")
p3_3.inBoundary = tb_backend

p3_4 = Process("3.4 Finalize Immutable Sale")
p3_4.inBoundary = tb_backend

# Data Stores
db_sales = Datastore("Sales Store")
db_sales.inBoundary = tb_database

db_products = Datastore("Product Store")
db_products.inBoundary = tb_database

# Dataflows
Dataflow(customer, p3_1, "Purchase Request (Product ID, Payment Details)")
Dataflow(p3_1, db_products, "Fetch Product Price & Status")
Dataflow(db_products, p3_1, "Product Details (Price)")
Dataflow(p3_1, p3_2, "Validated Sale Context (Amount, Payment Details)")

Dataflow(p3_2, payment_gw, "Charge Request")
Dataflow(payment_gw, p3_2, "Payment Success/Failure")

Dataflow(p3_2, p3_3, "Payment Complete Event")
Dataflow(p3_3, p2_inventory, "Decrement Stock Command")
Dataflow(p2_inventory, p3_3, "Stock Decrement Confirmed")

Dataflow(p3_3, p3_4, "Execute Sale Record")
Dataflow(p3_4, db_sales, "Insert Immutable Sale Record")
Dataflow(db_sales, p3_4, "Sale ID Generated")

Dataflow(p3_4, customer, "Digital Receipt / Vending Dispense Command")

if __name__ == '__main__':
    tm.process()
