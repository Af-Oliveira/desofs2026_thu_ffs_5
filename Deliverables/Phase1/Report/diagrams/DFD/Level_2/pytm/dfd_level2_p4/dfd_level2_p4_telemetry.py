#!/usr/bin/env python3
"""
Level 2 DFD — VendNet — Process 4.0 (Telemetry & Monitoring)

Run:
    python dfd_level2_p4_telemetry.py --dfd | dot -Tpng -o dfd_level2_p4_telemetry.png
"""

from pytm import TM, Process, Datastore, Dataflow, Boundary, Actor, ExternalEntity

tm = TM("Level 2 DFD — 4.0 Telemetry & Monitoring")
tm.description = ("Level 2 Data Flow Diagram for 4.0 Telemetry & Monitoring sub-system.")
tm.isOrdered = True

tb_internet = Boundary("TB1: Internet")
tb_external = Boundary("TB2: External Systems")
tb_backend = Boundary("TB3: Back-End")
tb_database = Boundary("TB4: Database")
tb_database.inBoundary = tb_backend

# External Entities
vending_machine = ExternalEntity("Vending Machine")
vending_machine.inBoundary = tb_external

operator = Actor("Operator")
operator.inBoundary = tb_internet

admin = Actor("Administrator")
admin.inBoundary = tb_internet

# Sub-processes
p4_1 = Process("4.1 Telemetry Ingestion API")
p4_1.inBoundary = tb_backend

p4_2 = Process("4.2 System Health Aggregator")
p4_2.inBoundary = tb_backend

p4_3 = Process("4.3 Alert Dispatcher")
p4_3.inBoundary = tb_backend

# Datastores
db_telemetry = Datastore("Telemetry Store")
db_telemetry.inBoundary = tb_database

db_machines = Datastore("Machine Store")
db_machines.inBoundary = tb_database

# Dataflows
Dataflow(vending_machine, p4_1, "mTLS Telemetry Data (Temp, Power, Connectivity)")
Dataflow(p4_1, db_telemetry, "Insert Raw Telemetry Event")

Dataflow(p4_1, db_machines, "Update LastTelemetryAt & Status")
Dataflow(db_machines, p4_1, "Current Machine Config")

Dataflow(p4_1, p4_3, "Detect Critical Anomalies (e.g., Temp High)")
Dataflow(p4_3, operator, "Dispatch Alert Notification (Threshold Exceeded)")
Dataflow(p4_3, admin, "Dispatch System-Wide Alert")

Dataflow(operator, p4_2, "View Machine Dashboard Request")
Dataflow(admin, p4_2, "View Network Health Map")

Dataflow(p4_2, db_telemetry, "Fetch Aggregated Telemetry History")
Dataflow(db_telemetry, p4_2, "Average Temp/Uptime Records")
Dataflow(p4_2, db_machines, "Fetch Machine Current Status")
Dataflow(db_machines, p4_2, "Machine Status Data")

Dataflow(p4_2, operator, "Machine Health Dashboard")
Dataflow(p4_2, admin, "Global Network Dashboard")

if __name__ == '__main__':
    tm.process()
