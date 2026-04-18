#!/usr/bin/env python3
"""
Level 2 DFD — VendNet — Process 2.0 (Inventory Management)

Run:
    python dfd_level2_p2_inventory.py --dfd | dot -Tpng -o dfd_level2_p2_inventory.png
"""

from pytm import TM, Process, Datastore, Dataflow, Boundary, Actor

tm = TM("Level 2 DFD — 2.0 Inventory Management")
tm.description = ("Level 2 Data Flow Diagram for 2.0 Inventory Management sub-system.")
tm.isOrdered = True

tb_internet = Boundary("TB1: Internet")
tb_backend = Boundary("TB3: Back-End")
tb_database = Boundary("TB4: Database")
tb_database.inBoundary = tb_backend

# External Entities/Processes
operator = Actor("Operator")
operator.inBoundary = tb_internet

admin = Actor("Administrator")
admin.inBoundary = tb_internet

p3_sales = Process("3.0 Sales Processing")
p3_sales.inBoundary = tb_backend

# Sub-processes
p2_1 = Process("2.1 Catalog Management")
p2_1.inBoundary = tb_backend

p2_2 = Process("2.2 Stock Operations")
p2_2.inBoundary = tb_backend

p2_3 = Process("2.3 Slot Configuration")
p2_3.inBoundary = tb_backend

# Datastores
db_products = Datastore("Product Store")
db_products.inBoundary = tb_database

db_machines = Datastore("Machine Store (Slots)")
db_machines.inBoundary = tb_database

# Dataflows
Dataflow(admin, p2_1, "Create/Update Product")
Dataflow(p2_1, db_products, "Write Product Record")
Dataflow(db_products, p2_1, "Product Details")

Dataflow(operator, p2_2, "Restock Event (Quantities)")
Dataflow(p2_2, db_machines, "Update CurrentQuantity")

Dataflow(p3_sales, p2_2, "Decrement Stock Command")
Dataflow(p2_2, p3_sales, "Stock Decrement Ack")

Dataflow(admin, p2_3, "Assign Product to Slot")
Dataflow(p2_3, db_products, "Verify Product Exists")
Dataflow(db_products, p2_3, "Product Exists Confirmation")
Dataflow(p2_3, db_machines, "Update ProductId in Slot")
Dataflow(db_machines, p2_3, "Slot Config Saved")

if __name__ == '__main__':
    tm.process()
