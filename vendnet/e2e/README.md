# E2E and black-box tests (issue #33)

End-to-end and black-box coverage as an **external HTTP client** (no access to internal beans in the test code).

## Preconditions

| Requirement | Notes |
|-------------|--------|
| **JDK 17+** | Same as the main module. |
| **Maven** | Use `./mvnw` from the `vendnet/` directory. |
| **JUnit E2E** | **No Docker required.** Tests start Spring Boot on a random port with **H2** and the **`bootstrap`** profile so the database is seeded automatically. |
| **Newman / Postman** | Node + `newman` if you use `make e2e` or `e2e/run-e2e.sh` (see below). |

## Test data seeding (`bootstrap` profile)

When **`bootstrap`** is active, `BootstrapProfileConfig` runs `BootstrapService.seed()` once at startup. That fills:

- **Users (BCrypt passwords):** `admin@vendnet.io` / `Admin@1234`, `operator@vendnet.io` / `Operator@1234`, `customer@vendnet.io` / `Customer@1234`
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
| `vendnet-local.postman_environment.json` | Local `baseUrl` and seed credentials for Postman/Newman. |
| `run-e2e.sh` | Helper invoked by `make e2e`. |

Java sources for the Maven E2E suite live under `src/test/java/.../e2e/` (`*E2ETest.java`).
