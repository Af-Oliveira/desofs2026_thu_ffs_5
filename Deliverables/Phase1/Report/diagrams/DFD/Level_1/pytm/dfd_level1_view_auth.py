#!/usr/bin/env python3
"""
Level 1 DFD — Focused View: Authentication & Authorization

Shows Process 1.0 with all connected actors, data stores (User Store,
Audit Log Store), and auth context propagation to downstream processes.

Run:
    python dfd_level1_view_auth.py --dfd | dot -Tpng -o dfd_level1_view_auth.png
"""

from pytm import TM, Process, Datastore, Dataflow, Boundary, Actor

# ─── Threat Model ───────────────────────────────────────────────

tm = TM("VendNet — Level 1 — Authentication & Authorization View")
tm.description = (
    "Focused Level 1 view showing the 1.0 Authentication & "
    "Authorization sub-process with detailed data flows to "
    "actors (Customer, Operator, Administrator), dedicated "
    "data stores (User Store, Audit Log Store), and auth "
    "context propagation to all downstream sub-processes."
)
tm.isOrdered = True

# ─── Trust Boundaries ───────────────────────────────────────────

tb_internet = Boundary("TB1: Internet / Public Network")
tb_internet.description = (
    "All user traffic crosses this boundary via HTTPS (TLS 1.2+). "
    "Rate limiting applied on authentication endpoints."
)

tb_backend = Boundary("TB3: VendNet Back-End (Internal)")
tb_backend.description = (
    "Internal trust zone. Inter-process communication occurs "
    "within the same JVM via in-process method calls."
)

tb_database = Boundary("TB4: Database Zone")
tb_database.description = (
    "MySQL database accessible only via the application's "
    "connection pool with least-privilege credentials."
)
tb_database.inBoundary = tb_backend

# ─── External Entities ──────────────────────────────────────────

customer = Actor("Customer")
customer.description = (
    "End-user who authenticates to browse the product catalog "
    "and make purchases."
)
customer.inBoundary = tb_internet

operator = Actor("Operator")
operator.description = (
    "Field technician who authenticates to manage machine "
    "stock and view telemetry data."
)
operator.inBoundary = tb_internet

admin = Actor("Administrator")
admin.description = (
    "System manager who authenticates for full system access "
    "including user account management."
)
admin.inBoundary = tb_internet

# ─── Processes ──────────────────────────────────────────────────

p1 = Process("1.0 Authentication & Authorization")
p1.description = (
    "Central authentication gateway. Verifies credentials against "
    "BCrypt-hashed passwords, issues JWT tokens with role claims, "
    "enforces RBAC for all requests, and manages user accounts "
    "(create, update, suspend/lock)."
)
p1.inBoundary = tb_backend

# Downstream processes (shown for auth context propagation)
p2 = Process("2.0 Inventory Management")
p2.inBoundary = tb_backend

p3 = Process("3.0 Sales Processing")
p3.inBoundary = tb_backend

p4 = Process("4.0 Telemetry & Monitoring")
p4.inBoundary = tb_backend

p5 = Process("5.0 OS Operations")
p5.inBoundary = tb_backend

p6 = Process("6.0 Pricing & Configuration")
p6.inBoundary = tb_backend

# ─── Data Stores ────────────────────────────────────────────────

db_users = Datastore("User Store")
db_users.description = (
    "MySQL table(s) storing user accounts: UserId, Username, "
    "Email, PasswordHash (BCrypt), Role (CUSTOMER/OPERATOR/"
    "ADMINISTRATOR), AccountStatus, FullName, CreatedAt."
)
db_users.inBoundary = tb_database
db_users.isEncrypted = True
db_users.isSQL = True

db_audit = Datastore("Audit Log Store")
db_audit.description = (
    "MySQL table(s) storing security audit records: login "
    "attempts (success/failure), account lockouts, role "
    "changes, and RBAC decisions."
)
db_audit.inBoundary = tb_database
db_audit.isEncrypted = True
db_audit.isSQL = True

# ═══════════════════════════════════════════════════════════════
# DATA FLOWS
# ═══════════════════════════════════════════════════════════════

# ── Customer Authentication ────────────────────────────────────

df_cust_login = Dataflow(customer, p1, "Login Credentials (username, password)")
df_cust_login.protocol = "HTTPS"
df_cust_login.isEncrypted = True
df_cust_login.description = (
    "Customer submits username and password over TLS for "
    "authentication."
)

df_cust_jwt = Dataflow(p1, customer, "JWT Access Token / Auth Error")
df_cust_jwt.protocol = "HTTPS"
df_cust_jwt.isEncrypted = True
df_cust_jwt.description = (
    "Returns JWT with CUSTOMER role claim on success, or "
    "generic error on failure (no user enumeration)."
)

# ── Operator Authentication ────────────────────────────────────

df_op_login = Dataflow(operator, p1, "Login Credentials (username, password)")
df_op_login.protocol = "HTTPS"
df_op_login.isEncrypted = True

df_op_jwt = Dataflow(p1, operator, "JWT Access Token (OPERATOR role)")
df_op_jwt.protocol = "HTTPS"
df_op_jwt.isEncrypted = True

# ── Administrator Authentication & User Management ─────────────

df_admin_auth = Dataflow(admin, p1, "Credentials / User Mgmt Commands")
df_admin_auth.protocol = "HTTPS"
df_admin_auth.isEncrypted = True
df_admin_auth.description = (
    "Admin authenticates and submits user management commands: "
    "create, update, suspend/lock accounts, change roles."
)

df_admin_resp = Dataflow(p1, admin, "JWT Token / User Lists & Status")
df_admin_resp.protocol = "HTTPS"
df_admin_resp.isEncrypted = True
df_admin_resp.description = (
    "Returns JWT with ADMINISTRATOR role on login, or user "
    "account listings and status on management queries."
)

# ── Data Store Flows ───────────────────────────────────────────

df_auth_write = Dataflow(p1, db_users, "Read/Write User Records")
df_auth_write.protocol = "MySQL/TLS"
df_auth_write.isEncrypted = True
df_auth_write.description = (
    "Reads hashed credentials and roles for verification; "
    "writes new user accounts and status changes."
)

df_auth_read = Dataflow(db_users, p1, "BCrypt Hash, Role, Account Status")
df_auth_read.protocol = "MySQL/TLS"
df_auth_read.isEncrypted = True
df_auth_read.description = (
    "Returns user record with BCrypt-hashed password, assigned "
    "role, and current account status for verification."
)

df_audit_write = Dataflow(p1, db_audit, "Auth Audit Events (Login, Lockout, Role Change)")
df_audit_write.protocol = "MySQL/TLS"
df_audit_write.isEncrypted = True
df_audit_write.description = (
    "Records all authentication events: successful logins, "
    "failed attempts, account lockouts, and role modifications."
)

# ── Auth Context Propagation ───────────────────────────────────

df_ctx_inv = Dataflow(p1, p2, "Authenticated User Context (ID, Role)")
df_ctx_inv.description = (
    "Passes verified user identity and role to Inventory "
    "Management for RBAC enforcement on catalog/stock operations."
)

df_ctx_sales = Dataflow(p1, p3, "Authenticated User Context (ID, Role)")
df_ctx_sales.description = (
    "Passes verified user identity and role to Sales Processing "
    "for purchase authorization."
)

df_ctx_tel = Dataflow(p1, p4, "Authenticated User Context (ID, Role)")
df_ctx_tel.description = (
    "Passes verified user identity and role to Telemetry "
    "for log access authorization."
)

df_ctx_os = Dataflow(p1, p5, "Authenticated User Context (ID, Role)")
df_ctx_os.description = (
    "Passes verified user identity and role to OS Operations "
    "for admin-only backup/report authorization."
)

df_ctx_pricing = Dataflow(p1, p6, "Authenticated User Context (ID, Role)")
df_ctx_pricing.description = (
    "Passes verified user identity and role to Pricing & "
    "Configuration for admin-only pricing authorization."
)

# ─── Process ────────────────────────────────────────────────────

if __name__ == "__main__":
    tm.process()
