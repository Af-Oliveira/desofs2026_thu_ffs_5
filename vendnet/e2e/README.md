# E2E and black-box tests (issue #33)

End-to-end and black-box coverage as an **external HTTP client** (no access to internal beans in the test code).

## Backend REST demo order

For a university demo, show the material in this order so the teacher can see the trace from design to executable evidence:

1. Start with `Deliverables/Phase1/System-To-Be/C4/README.md`: explain that the submission uses C4 for abstraction levels and 4+1 for logical/process/development/physical/scenario views.
2. Open the Level 1 to Level 3 process views for the seven backend-critical flows: authentication, user management/registration, product image upload, purchase, restock, telemetry, and backup/report operations.
3. Show the Spring REST controllers in `src/main/java/.../api/controller`: this is the backend-only implementation boundary.
4. Run Newman with `make e2e` or `./e2e/run-e2e.sh`: this is the executable proof with assertions.
5. Import `VendNet_E2E_Tests.insomnia.json` in Insomnia for manual exploration. Run folders in numeric order from `00` to `06`.
6. Finish with the CI security evidence: unit/integration tests, Newman E2E, Semgrep, Dependency Check, Trivy, and ZAP/RBAC.

## C4 + 4+1 use-case coverage

The REST API covers the use cases that are meaningful for a backend-only deliverable. Observability dashboards and audit-log rotation exist as operational/security architecture concerns, not as a complete public REST workflow.

| C4 scenario / use case | Backend REST coverage | Newman / Insomnia folder |
| --- | --- | --- |
| UC1 Authenticate | Login for admin/operator/customer, auth claims, invalid/anonymous JWT checks | `00 Bootstrap And Authentication`, `06 RBAC And Public Documentation` |
| UC2 Manage Users | Admin lists, creates, and updates users; responses avoid password hashes | `04 Admin Product User And OS Operations` |
| UC3 Browse Product Catalog | Authenticated product list and SKU lookup | `01 Catalog And Machine Discovery` |
| UC4 Manage Products & Pricing | Admin price update and multipart secure image upload | `04 Admin Product User And OS Operations` |
| UC5 Manage Vending Machines | Authenticated machine lookup plus admin machine creation | `01 Catalog And Machine Discovery`, `04 Admin Product User And OS Operations` |
| UC6 Receive Machine Telemetry | Valid registered-machine telemetry and unknown-machine rejection | `05 Telemetry And Payment Webhook Security` |
| UC7 Restock Machine Slots | Operator restock, capacity rejection, customer forbidden | `03 Machine Restock Flow` |
| UC8 Purchase Product | Customer purchase, duplicate idempotency key, price tampering rejection | `02 Purchase Product Flow` |
| UC9 View Purchase History | Customer reads own sales history | `02 Purchase Product Flow` |
| UC10 Generate Encrypted Backup | Admin backup endpoint protected and reachable | `04 Admin Product User And OS Operations` |
| UC11 Manage Audit Logs | Implemented as internal audit/log rotation security service tests, not a user-facing REST endpoint | JUnit service/security tests |
| UC12 Generate Reports | Admin triggers sales report directory generation | `04 Admin Product User And OS Operations` |
| UC13 View Dashboards & Metrics | Exposed as operational metrics/docs endpoints for the backend demo; Grafana itself is outside REST API scope | `06 RBAC And Public Documentation` |

## Insomnia manual collection

`VendNet_E2E_Tests.insomnia.json` is ordered for a live demo and mirrors the Newman flow. Insomnia's project export/import format is JSON; YAML is used for OpenAPI specifications, not for this manual request collection.

- `00` logs in the three seeded roles and captures tokens where Insomnia response scripting is available.
- `01` discovers product and machine IDs used by later requests.
- `02` demonstrates purchase, idempotency, and mass-assignment/price tampering rejection.
- `03` demonstrates restock and RBAC enforcement.
- `04` demonstrates admin-only user, machine, product image upload, report, and backup operations.
- `05` demonstrates telemetry and signed payment webhook security.
- `06` demonstrates RBAC failures plus public OpenAPI/Swagger/metrics endpoints.

The collection includes more than one request for flows that need multiple cases, especially purchase, restock, upload/admin operations, telemetry/webhooks, and RBAC. The multipart image example uses `e2e/assets/sample-product.png`.

Recommended fail-case demo requests:

| Endpoint / flow | Passing case | Rejection case(s) |
| --- | --- | --- |
| `/api/sales/purchase` | Customer purchase succeeds | Duplicate idempotency key, client-supplied `unitPrice`, client-supplied `price` |
| `/api/machines/{machineId}/slots/{slotId}/restock` | Operator restocks one unit | Excessive restock rejected, customer forbidden |
| `/api/machines/telemetry` | Registered machine telemetry accepted | Unknown machine rejected |
| `/api/webhooks/payment` | Valid signature accepted | Missing signature, tampered body |
| Protected admin routes | Admin access succeeds | Anonymous, invalid JWT, customer, and operator denial cases |

## Preconditions

| Requirement | Notes |
|-------------|--------|
| **JDK 17+** | Same as the main module. |
| **Maven** | Use `./mvnw` from the `vendnet/` directory. |
| **JUnit E2E** | **No Docker required.** Tests start Spring Boot on a random port with **H2** and the **`bootstrap`** profile so the database is seeded automatically. |
| **Newman / Postman** | Node + `newman` if you use `make e2e` or `e2e/run-e2e.sh` (see below). |

## Test data seeding (`bootstrap` profile)

When **`bootstrap`** is active, `BootstrapProfileConfig` runs `BootstrapService.seed()` once at startup. That fills:

- **Users (username / password):** `admin` / `Admin@123456`, `operator` / `Operator@123456`, `customer` / `Customer@123456`
- **Machines:** e.g. `VM-LIS-001`, `VM-LIS-002`, `VM-PTO-001`, `VM-FAR-001`
- **Products (SKU examples):** `DRK-001`, `DRK-002`, …
- **Slots, sample sales, telemetry** as defined in `BootstrapService`

The **Maven E2E** tests (`*E2ETest.java`) use `@ActiveProfiles({"test", "bootstrap"})`, so this seed runs **inside the test JVM** before HTTP calls.

## How to run

### 1) JUnit + Rest Assured (profile `e2e`)

From `vendnet/`:

```bash
./mvnw test-compile failsafe:integration-test -Pe2e
```

Or a full `verify` (runs default `verify` plugins **and** Failsafe with `*E2ETest.java`):

```bash
./mvnw verify -Pe2e
```

- **Unit tests** (`mvn test`) **exclude** `*E2ETest.java` so normal builds stay fast.
- Expect **14** tests total when the profile is active (critical flow + AC-01…AC-08 black-box scenarios and related checks).

### 2) Packaged JAR + Postman / Newman (H2, self-contained)

Uses `application-e2e.properties` (H2) and **`make e2e`** in `vendnet/` (starts the JAR, runs the collection, stops the app). See `Makefile` and `run-e2e.sh`.

### 3) Docker Compose + real MySQL

For a deployment-like run:

1. From repo root: `docker compose up -d mysql` (wait until healthy).
2. Run the **packaged JAR** (or `spring-boot:run`) with profiles **`dev`** and **`bootstrap`** and datasource pointing at that MySQL (see root `docker-compose.yml` and `application.properties`).
3. Point Newman’s `baseUrl` (or your HTTP client) at the running app (e.g. `http://localhost:8080`).

The **JUnit `e2e` profile** does **not** require this stack; it is the default path for CI-friendly black-box tests.

## Artefacts in this folder

| File | Role |
|------|------|
| `VendNet_E2E_Tests.postman_collection.json` | Newman collection (multi-role API checks). |
| `VendNet_E2E_Tests.insomnia.json` | Ordered Insomnia collection for manual C4-flow exploration. |
| `vendnet-local.postman_environment.json` | Local `baseUrl` and seed credentials for Postman/Newman. |
| `assets/sample-product.png` | Product image used by the multipart upload test/demo. |
| `run-e2e.sh` | Helper invoked by `make e2e`. |

Java sources for the Maven E2E suite live under `src/test/java/.../e2e/` (`*E2ETest.java`).
