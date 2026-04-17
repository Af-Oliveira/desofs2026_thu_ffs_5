#!/usr/bin/env python3
"""
Level 2 DFD — VendNet — Process 5.0 (OS Operations)

Run:
    python dfd_level2_p5_os_ops.py --dfd | dot -Tpng -o dfd_level2_p5_os_ops.png
"""

from pytm import TM, Process, Datastore, Dataflow, Boundary, Actor

tm = TM("Level 2 DFD — 5.0 OS Operations")
tm.description = ("Level 2 Data Flow Diagram for OS Operations (Backups, Logs, Reports).")
tm.isOrdered = True

tb_internet = Boundary("TB1: Internet")
tb_backend = Boundary("TB3: Back-End")
tb_fs = Boundary("TB5: File System")
tb_fs.inBoundary = tb_backend
tb_db = Boundary("TB4: Database")
tb_db.inBoundary = tb_backend

# External
admin = Actor("Administrator")
admin.inBoundary = tb_internet

# Level 2 Processes
p5_1 = Process("5.1 DB Backup Generator")
p5_1.inBoundary = tb_backend

p5_2 = Process("5.2 Audit Log Rotator")
p5_2.inBoundary = tb_backend

p5_3 = Process("5.3 Vendor Report Builder")
p5_3.inBoundary = tb_backend

# Datastores
db_all = Datastore("All DBs (MySQL)")
db_all.inBoundary = tb_db

ds_fs_backups = Datastore("/var/vendnet/backups")
ds_fs_backups.inBoundary = tb_fs

ds_fs_logs = Datastore("/var/vendnet/logs")
ds_fs_logs.inBoundary = tb_fs

ds_fs_reports = Datastore("/var/vendnet/reports")
ds_fs_reports.inBoundary = tb_fs

# Dataflows
# Backups
Dataflow(admin, p5_1, "Trigger Backup Command")
Dataflow(p5_1, db_all, "mysqldump command (auth)")
Dataflow(db_all, p5_1, "SQL Dump File Stream")
Dataflow(p5_1, ds_fs_backups, "Encrypted SQL Backup File")
Dataflow(p5_1, admin, "Backup Completion Status")

# Log Rotation
Dataflow(p5_2, db_all, "Fetch Audit Events (older than X)")
Dataflow(db_all, p5_2, "Audit Records")
Dataflow(p5_2, ds_fs_logs, "Compress/Archive Log File")
Dataflow(p5_2, db_all, "Delete Archived Events")

# Reports
Dataflow(admin, p5_3, "Request Vendor Report")
Dataflow(p5_3, db_all, "Fetch Aggregated Sale Data")
Dataflow(db_all, p5_3, "Sales Data Result Set")
Dataflow(p5_3, ds_fs_reports, "Write Directory/File Output")
Dataflow(ds_fs_reports, p5_3, "Report Read Stream")
Dataflow(p5_3, admin, "Report File Download")

if __name__ == '__main__':
    tm.process()
