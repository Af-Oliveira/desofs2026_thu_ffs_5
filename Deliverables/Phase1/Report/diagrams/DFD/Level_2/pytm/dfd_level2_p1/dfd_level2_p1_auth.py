#!/usr/bin/env python3
"""
Level 2 DFD — VendNet — Process 1.0 (Authentication & Authorization)

Run:
    python dfd_level2_p1_auth.py --dfd | dot -Tpng -o dfd_level2_p1_auth.png
"""

from pytm import TM, Process, Datastore, Dataflow, Boundary, Actor

tm = TM("VendNet — Level 2 DFD — Auth")
tm.description = ("Level 2 DFD for 1.0 Authentication & Authorization.")
tm.isOrdered = True

tb_internet = Boundary("TB1: Internet")
tb_backend = Boundary("TB3: Back-End")
tb_database = Boundary("TB4: Database")
tb_database.inBoundary = tb_backend

# External Entities
customer = Actor("Customer")
customer.inBoundary = tb_internet
operator = Actor("Operator")
operator.inBoundary = tb_internet
admin = Actor("Administrator")
admin.inBoundary = tb_internet

# Level 2 Processes (Decomposition of 1.0)
p1_1 = Process("1.1 Credential Verification")
p1_1.inBoundary = tb_backend
p1_2 = Process("1.2 Token Generation & Management")
p1_2.inBoundary = tb_backend
p1_3 = Process("1.3 RBAC Enforcement")
p1_3.inBoundary = tb_backend

# Datastores
db_users = Datastore("User Store")
db_users.inBoundary = tb_database

# Dataflows
Dataflow(customer, p1_1, "Login Credentials")
Dataflow(operator, p1_1, "Login Credentials")
Dataflow(admin, p1_1, "Login Credentials")

Dataflow(p1_1, db_users, "Fetch hashed password & role")
Dataflow(db_users, p1_1, "Hashed Credentials & Status")

Dataflow(p1_1, p1_2, "Authenticated User ID")
Dataflow(p1_2, customer, "JWT Token")
Dataflow(p1_2, operator, "JWT Token")
Dataflow(p1_2, admin, "JWT Token")

Dataflow(customer, p1_3, "Request + JWT")
Dataflow(p1_3, p1_2, "Validate JWT Token")
Dataflow(p1_2, p1_3, "Token Validity & Role")

if __name__ == '__main__':
    tm.process()
