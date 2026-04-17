#!/usr/bin/env python3
"""
Level 2 DFD — VendNet — Process 6.0 (Pricing & Configuration)

Run:
    python dfd_level2_p6_pricing.py --dfd | dot -Tpng -o dfd_level2_p6_pricing.png
"""

from pytm import TM, Process, Datastore, Dataflow, Boundary, Actor, ExternalEntity

tm = TM("Level 2 DFD — 6.0 Pricing & Configuration")
tm.description = ("Level 2 Data Flow Diagram for 6.0 Pricing & Configuration sub-system.")
tm.isOrdered = True

tb_internet = Boundary("TB1: Internet")
tb_external = Boundary("TB2: External Systems")
tb_backend = Boundary("TB3: Back-End")
tb_database = Boundary("TB4: Database")
tb_database.inBoundary = tb_backend

# External Entities
admin = Actor("Administrator")
admin.inBoundary = tb_internet

vending_machine = ExternalEntity("Vending Machine")
vending_machine.inBoundary = tb_external

# Sub-processes
p6_1 = Process("6.1 Price Management")
p6_1.inBoundary = tb_backend

p6_2 = Process("6.2 Machine Configurator")
p6_2.inBoundary = tb_backend

p6_3 = Process("6.3 Sync & Deployment")
p6_3.inBoundary = tb_backend

# Datastores
db_products = Datastore("Product Store")
db_products.inBoundary = tb_database

db_machines = Datastore("Machine Store")
db_machines.inBoundary = tb_database

# Dataflows
# Pricing
Dataflow(admin, p6_1, "Update Product Pricing")
Dataflow(p6_1, db_products, "Persist New Price")
Dataflow(db_products, p6_1, "Price Updated Ack")

# Config
Dataflow(admin, p6_2, "Update Operational Parameters (Temp targets, limits)")
Dataflow(p6_2, db_machines, "Persist Device Configuration")
Dataflow(db_machines, p6_2, "Config Saved Ack")

# Deployment
Dataflow(p6_1, p6_3, "Trigger Price Sync Event")
Dataflow(p6_2, p6_3, "Trigger Config Sync Event")

Dataflow(p6_3, db_machines, "Fetch Associated Machine List")
Dataflow(db_machines, p6_3, "Machine Endpoint Targets")
Dataflow(p6_3, db_products, "Fetch Latest Prices")
Dataflow(db_products, p6_3, "Price List")

Dataflow(vending_machine, p6_3, "Poll for Configuration/Prices (mTLS)")
Dataflow(p6_3, vending_machine, "Push Updated Config & Prices")

if __name__ == '__main__':
    tm.process()
