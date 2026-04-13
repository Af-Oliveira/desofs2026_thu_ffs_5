#!/usr/bin/env python3
"""
Level 1 DFD — Focused View: Operations
(Telemetry & Monitoring, OS Operations)

Shows Processes 4.0 and 5.0 with their connected external entities,
data stores (Telemetry Store, Machine Store, Audit Log Store), and
the server file system with its sub-boundary.

Run:
    python dfd_level1_view_ops.py --dfd | dot -Tpng -o dfd_level1_view_ops.png
"""

from pytm import TM, Process, Datastore, Dataflow, Boundary, Actor, ExternalEntity

# ─── Threat Model ───────────────────────────────────────────────

tm = TM("VendNet — Level 1 — Operations View")
tm.description = (
    "Focused Level 1 view showing Telemetry & Monitoring (4.0) "
    "and OS Operations (5.0) with their data flows to external "
    "entities (Operator, Administrator, Vending Machine), "
    "database stores (Telemetry, Machine, Audit), and the server "
    "file system (backups, logs, reports)."
)
tm.isOrdered = True

# ─── Trust Boundaries ───────────────────────────────────────────

tb_internet = Boundary("TB1: Internet / Public Network")
tb_internet.description = (
    "All user traffic crosses this boundary via HTTPS (TLS 1.2+)."
)

tb_external = Boundary("TB2: External Systems Network")
tb_external.description = (
    "Vending machines authenticate via mutual TLS (mTLS) with "
    "client certificates."
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

tb_fs = Boundary("TB5: File System Zone")
tb_fs.description = (
    "OS-level file operations sandboxed to /var/vendnet/ with "
    "strict path validation (whitelist patterns), directory "
    "permissions (700/750), and AES-256 encryption for backups."
)
tb_fs.inBoundary = tb_backend

# ─── External Entities ──────────────────────────────────────────

operator = Actor("Operator")
operator.description = (
    "Field technician who views machine telemetry dashboards "
    "and diagnostic logs."
)
operator.inBoundary = tb_internet

admin = Actor("Administrator")
admin.description = (
    "System manager who triggers backups, requests reports, "
    "reviews audit logs, and monitors network health."
)
admin.inBoundary = tb_internet

vending_machine = ExternalEntity("Vending Machine")
vending_machine.description = (
    "Physical vending machine that sends periodic telemetry "
    "data (status, temperature, connectivity) via mTLS."
)
vending_machine.inBoundary = tb_external

# ─── Processes ──────────────────────────────────────────────────

p4 = Process("4.0 Telemetry & Monitoring")
p4.description = (
    "Ingests telemetry data from vending machines, updates "
    "machine status (ONLINE/OFFLINE/MAINTENANCE), persists "
    "telemetry records, and provides monitoring dashboards "
    "for operators and administrators."
)
p4.inBoundary = tb_backend

p5 = Process("5.0 OS Operations")
p5.description = (
    "Executes OS-level server operations: AES-256 encrypted "
    "database backups via ProcessBuilder, audit log rotation "
    "(compress/archive/delete), and structured report directory "
    "generation under /var/vendnet/. All paths validated "
    "against whitelist patterns."
)
p5.inBoundary = tb_backend

# ─── Data Stores ────────────────────────────────────────────────

db_telemetry = Datastore("Telemetry Store")
db_telemetry.description = (
    "MySQL table(s): time-series telemetry data from vending "
    "machines — status readings, temperature, connectivity."
)
db_telemetry.inBoundary = tb_database
db_telemetry.isEncrypted = True
db_telemetry.isSQL = True

db_machines = Datastore("Machine Store")
db_machines.description = (
    "MySQL table(s): vending machine records — serial numbers, "
    "locations, status, last telemetry timestamp."
)
db_machines.inBoundary = tb_database
db_machines.isEncrypted = True
db_machines.isSQL = True

db_audit = Datastore("Audit Log Store")
db_audit.description = (
    "MySQL table(s): security audit records for rotation and "
    "admin review — login events, admin actions, OS operations."
)
db_audit.inBoundary = tb_database
db_audit.isEncrypted = True
db_audit.isSQL = True

db_all = Datastore("MySQL Database (Full Dump)")
db_all.description = (
    "The complete MySQL database, accessed by the backup "
    "process via mysqldump for full database export."
)
db_all.inBoundary = tb_database
db_all.isEncrypted = True
db_all.isSQL = True

fs = Datastore("Server File System (/var/vendnet/)")
fs.description = (
    "OS-level file storage: /backups/ for encrypted DB dumps, "
    "/logs/audit/ for rotated audit logs, /reports/ for "
    "generated report directory structures."
)
fs.inBoundary = tb_fs

# ═══════════════════════════════════════════════════════════════
# DATA FLOWS — External Entity ↔ Process
# ═══════════════════════════════════════════════════════════════

# ── Vending Machine → Telemetry ────────────────────────────────

df_vm_tel = Dataflow(vending_machine, p4, "Telemetry: Status, Temp, Connectivity (mTLS)")
df_vm_tel.protocol = "HTTPS"
df_vm_tel.isEncrypted = True
df_vm_tel.description = (
    "Vending machine sends periodic telemetry data authenticated "
    "via mutual TLS client certificate."
)

# ── Operator ↔ Telemetry ──────────────────────────────────────

df_op_dash = Dataflow(operator, p4, "Machine Dashboard / Log Requests")
df_op_dash.protocol = "HTTPS"
df_op_dash.isEncrypted = True

df_op_data = Dataflow(p4, operator, "Machine Health Data / Telemetry Logs")
df_op_data.protocol = "HTTPS"
df_op_data.isEncrypted = True

# ── Administrator ↔ Telemetry ─────────────────────────────────

df_admin_health = Dataflow(admin, p4, "Network Health Map Request")
df_admin_health.protocol = "HTTPS"
df_admin_health.isEncrypted = True

df_admin_dash = Dataflow(p4, admin, "Global Network Health Dashboard")
df_admin_dash.protocol = "HTTPS"
df_admin_dash.isEncrypted = True

# ── Administrator ↔ OS Operations ─────────────────────────────

df_admin_ops = Dataflow(admin, p5, "Trigger Backup / Request Report / View Audit Logs")
df_admin_ops.protocol = "HTTPS"
df_admin_ops.isEncrypted = True
df_admin_ops.description = (
    "Administrator triggers database backup, requests vendor "
    "report generation, or retrieves audit log data."
)

df_ops_admin = Dataflow(p5, admin, "Backup Status / Report Download / Audit Logs")
df_ops_admin.protocol = "HTTPS"
df_ops_admin.isEncrypted = True

# ═══════════════════════════════════════════════════════════════
# DATA FLOWS — Telemetry ↔ Data Stores
# ═══════════════════════════════════════════════════════════════

df_tel_write = Dataflow(p4, db_telemetry, "Insert Telemetry Record")
df_tel_write.protocol = "MySQL/TLS"
df_tel_write.isEncrypted = True

df_tel_read = Dataflow(db_telemetry, p4, "Telemetry History (Aggregated)")
df_tel_read.protocol = "MySQL/TLS"
df_tel_read.isEncrypted = True

df_tel_mach_w = Dataflow(p4, db_machines, "Update Machine Status & LastTelemetryAt")
df_tel_mach_w.protocol = "MySQL/TLS"
df_tel_mach_w.isEncrypted = True

df_tel_mach_r = Dataflow(db_machines, p4, "Machine Config & Current Status")
df_tel_mach_r.protocol = "MySQL/TLS"
df_tel_mach_r.isEncrypted = True

# ═══════════════════════════════════════════════════════════════
# DATA FLOWS — OS Operations ↔ Data Stores & File System
# ═══════════════════════════════════════════════════════════════

df_os_audit_rw = Dataflow(p5, db_audit, "Read/Write Audit Events")
df_os_audit_rw.protocol = "MySQL/TLS"
df_os_audit_rw.isEncrypted = True
df_os_audit_rw.description = (
    "Reads audit records for admin retrieval and rotation; "
    "writes OS operation audit events (backup, rotation)."
)

df_audit_os = Dataflow(db_audit, p5, "Audit Records for Rotation/Review")
df_audit_os.protocol = "MySQL/TLS"
df_audit_os.isEncrypted = True

df_os_dump = Dataflow(p5, db_all, "mysqldump Command (Authenticated)")
df_os_dump.protocol = "MySQL/TLS"
df_os_dump.isEncrypted = True

df_dump_os = Dataflow(db_all, p5, "SQL Dump Stream")
df_dump_os.protocol = "MySQL/TLS"
df_dump_os.isEncrypted = True

df_os_fs_write = Dataflow(p5, fs, "Encrypted Backups, Rotated Logs, Report Files")
df_os_fs_write.description = (
    "Writes AES-256 encrypted backup files to /backups/, "
    "compressed audit log archives to /logs/audit/, and "
    "report directory structures to /reports/."
)

df_fs_os_read = Dataflow(fs, p5, "Report Files for Admin Download")
df_fs_os_read.description = (
    "Reads generated report files and backup metadata for "
    "serving to administrators via the API."
)

# ─── Process ────────────────────────────────────────────────────

if __name__ == "__main__":
    tm.process()
