# DESOFS — Phase 2, Sprint 2: Deliverables Index

|                  |                                            |
| ---------------- | ------------------------------------------ |
| **Project**      | VendNet — Vending Machine Network Back-End |
| **Organisation** | Grupo Sensacao (ISEP — DESOFS 2025/2026)   |
| **Date**         | Junho 2026                                 |
| **Status**       | ☑ Done                                     |

---

## Sprint 2 Overview

Sprint 2 delivers the complete VendNet system — a secure, observable, and operationally mature backend for managing a network of vending machines. The deliverables span seven chapters, each documented with evidence, screenshots, and reproducible commands.

| #   | Chapter         | Document                                                          | Purpose                                          |
| --- | --------------- | ----------------------------------------------------------------- | ------------------------------------------------ |
| 1   | **Demo Guide**  | [Demo.md](Demo/Demo.md)                                           | Reproducible live demonstration walkthrough      |
| 2   | **Pipeline**    | [Deep_Pipeline_Report.md](Pipeline/Deep_Pipeline_Report.md)       | CI/CD pipeline deep-dive technical report        |
| 3   | **Telemetry**   | [telemetry.md](Telemetry/telemetry.md)                            | Observability stack architecture and usage       |
| 4   | **Security**    | [security-implementation.md](Security/security-implementation.md) | Phase 1 plan vs. Phase 2 implementation evidence |
| 5   | **Testing**     | [Testing.md](Testing/Testing.md)                                  | Testing strategy: black-box, white-box, E2E      |
| 6   | **ASVS**        | [Threat_Model.md](ASVS/Threat_Model.md)                           | Updated threat model with Sprint 2 evidence      |
| 7   | **Development** | [Docs.md](Development/Docs.md)                                    | Architecture, DDD, database, and API docs        |

**Total:** 7 markdown documents · 15 screenshots · 7 CI/CD code snippets · 1 ASVS workbook (`.xlsx`)

---

## Quick Start

Every chapter is reproducible. Start the system and verify:

**Figure 0.1 — Starting the full VendNet environment:**

```bash
cd vendnet
make demo         # Full reset: build → MySQL reset → start all 8 services
```

> Single-command bootstrap: stops all running services, rebuilds the Docker image, drops and recreates the database, seeds test data (3 users, 8 products, 4 machines), and starts all 8 containers including the observability stack.

**Figure 0.2 — Verifying all services are healthy:**

```bash
make status
```

> Checks all Docker containers, probes 5 service URLs, and tests public API endpoints. Expected: all `✓ UP`.

```
  ════════════════════════════════════
    VendNet Service Status
  ════════════════════════════════════

  Containers
    ...mysql-1          Up 34 seconds (healthy)
    ...grafana-1        Up 13 seconds
    ...prometheus-1     Up 13 seconds
    ...jaeger-1         Up 29 seconds
    ...loki-1           Up 29 seconds
    ...

  Service URLs
    App            ✓ UP
    Swagger        ✓ UP
    Prometheus     ✓ UP
    Grafana        ✓ UP
    Jaeger         ✓ UP

  API
    GET  /api/health/ping      ✓ UP
    GET  /api/public/info       ✓ UP
```

> Evidence: 7 containers running. 5 service URLs return HTTP 200. Both public API endpoints confirmed UP.

**Figure 0.3 — Smoke test covering all major endpoint categories:**

```bash
make api-test
```

> Executes 10 HTTP requests: public endpoints, registration, JWT login, authenticated profile, claims, products, machines, and RBAC verification.

```
  ═══ VendNet API Smoke Test ═══

  [1] /api/health/ping              ✓ 200
  [2] /api/public/info              ✓ 200
  [3] POST /api/auth/register       ✓ 200
  [4] GET /api/auth/me              ✓ 200
  [5] GET /api/auth/claims          ✓ 200
  [6] GET /api/products             ✓ 200
  [7] GET /api/machines             ✓ 200
  [8] GET /api/admin/dashboard      ✓ 403 (RBAC working)
  [9] GET /actuator/health          ✓ 200
  [A] GET /swagger-ui               ✓ 200

✓ Done.
```

> Evidence: All 10 checks pass. Test [8] is the key security indicator — an unprivileged user receives HTTP 403 on the admin endpoint, proving that `@PreAuthorize` RBAC is actively enforced at runtime.

---

## Chapter 1 — Demonstration Guide

|                 |                                                                                                                                                                                                                                                                                                                                                                                  |
| --------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Document**    | [Demo.md](Demo/Demo.md)                                                                                                                                                                                                                                                                                                                                                          |
| **Screenshots** | 6 — [swagger-ui](Demo/screenshots/demo-swagger-ui.png), [account-locked](Demo/screenshots/demo-account-locked.png), [rbac-enforcement](Demo/screenshots/demo-rbac-enforcement.png), [grafana-dashboard](Demo/screenshots/demo-grafana-dashboard.png), [jaeger-trace](Demo/screenshots/demo-jaeger-trace.png), [prometheus-metrics](Demo/screenshots/demo-prometheus-metrics.png) |

A step-by-step academic demonstration guide covering all 13 use cases. Structured as a 18-minute presentation script with exact commands, expected outputs, and verification criteria.

**Key demonstrations covered:**

- Full environment bootstrap (`make demo`)
- Service health verification (`make status`, `make ping`, `make urls`)
- Authentication across 3 roles (Customer, Operator, Administrator)
- RBAC enforcement (403 vs. 200 per role)
- Account lockout after 5 failed attempts (HTTP 423)
- Purchase flow with idempotency and server-side price resolution
- Grafana dashboard with 8 populated panels
- Jaeger trace waterfall view
- Loki structured log querying

**Figure 1.1 — RBAC enforcement across three roles:**

![RBAC Enforcement](Demo/screenshots/demo-rbac-enforcement.png)

> Customer → Admin endpoint = 403. Operator → Purchase = 403. Admin → Customer endpoint = 200 (role hierarchy).

**Figure 1.2 — Account lockout after brute-force attempt:**

![Account Lockout](Demo/screenshots/demo-account-locked.png)

> After 5 rapid failed login attempts within 15 minutes, a valid login returns HTTP 423. Auto-unlock after 30 minutes.

---

## Chapter 2 — CI/CD Pipeline

|                   |                                                                                                                                                                                                                                                                                                                                                                                                   |
| ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Document**      | [Deep_Pipeline_Report.md](Pipeline/Deep_Pipeline_Report.md)                                                                                                                                                                                                                                                                                                                                       |
| **Code snippets** | 7 — [01](Pipeline/code_snips/01_setup_context_metadata.sh), [02](Pipeline/code_snips/02_build_unit_tests.sh), [03](Pipeline/code_snips/03_jacoco_coverage_parser.py), [04](Pipeline/code_snips/04_sonarqube_quality_gate.sh), [05](Pipeline/code_snips/05_dast_zap_scan.sh), [06](Pipeline/code_snips/06_docker_build_push_trivy.sh), [07](Pipeline/code_snips/07_pipeline_summary_aggregator.sh) |
| **Screenshots**   | 2 — [github-actions-overview](Pipeline/screenshots/sprint2-github-actions-overview.png), [github-artifacts](Pipeline/screenshots/sprint2-github-artifacts.png)                                                                                                                                                                                                                                    |

A deep-dive technical report on the VendNet CI/CD Pipeline v2.0 — an enterprise-grade DevSecOps pipeline with 14 jobs across 13 stages, from source-code secret detection through Docker image publication and zero-downtime deployment. Includes a full section on the SonarQube Quality Gate with configuration files, quality gate rules, and coverage enforcement.

**Pipeline stages:**

| Stage | Job                  | Purpose                                             |
| ----- | -------------------- | --------------------------------------------------- |
| 0     | `setup-context`      | Build metadata (version, branch, SHA)               |
| 1a    | `secret-detect`      | Gitleaks secret scanning                            |
| 1b    | `semgrep`            | SAST with Java + secrets rules                      |
| 2     | `build-unit-tests`   | Compile, unit tests, JaCoCo coverage                |
| 3     | `integration-tests`  | Testcontainers MySQL integration                    |
| 4     | `sast-spotbugs`      | SpotBugs + FindSecBugs bytecode analysis            |
| 5a    | `sca`                | OWASP Dependency Check + Maven Enforcer             |
| 5b    | `iast`               | Runtime taint analysis                              |
| 6     | `sonarqube`          | Quality gate (coverage ≥ 80%)                       |
| 7     | `dast-zap`           | OWASP ZAP baseline + authenticated API scan         |
| 8     | `docker-build-push`  | Docker build, Trivy scan, push to GHCR + Docker Hub |
| 9–10  | `deploy-dev/staging` | Blue/green zero-downtime deployment                 |
| 11    | `deploy-prod`        | Production deployment (manual gate)                 |
| 12    | `github-release`     | Release with JAR + SBOM                             |
| 13    | `pipeline-summary`   | Aggregated results report                           |

**Figure 2.1 — Complete pipeline execution on GitHub Actions:**

![GitHub Actions Pipeline Overview](Pipeline/screenshots/sprint2-github-actions-overview.png)

> All 14 jobs visible in the GitHub Actions UI, showing the stage progression from setup-context through pipeline-summary.

**Run locally:**

```bash
make pipeline            # Full CI: clean + SAST + SCA + verify
make ci-local-pipeline   # GitHub Actions via act (local)
```

---

## Chapter 3 — Telemetry & Observability

|                 |                                                                                                                                                                                                                 |
| --------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Document**    | [telemetry.md](Telemetry/telemetry.md)                                                                                                                                                                          |
| **Screenshots** | 3 — [grafana-dashboard](Telemetry/screenshots/demo-grafana-dashboard.png), [jaeger-trace](Telemetry/screenshots/demo-jaeger-trace.png), [prometheus-metrics](Telemetry/screenshots/demo-prometheus-metrics.png) |

Documents the full observability stack: metrics (Prometheus), logs (Loki + Promtail), and traces (Jaeger + OpenTelemetry Collector). Every signal is collected, stored, and visualised with zero manual instrumentation beyond Spring Boot Actuator, Micrometer, and OpenTelemetry.

**Key contents:**

- End-to-end telemetry architecture diagram (Mermaid)
- All 7 infrastructure component configurations (YAML, XML, properties)
- 8-panel Grafana VendNet Dashboard specification
- Jaeger trace waterfall structure
- Loki structured logging with correlation IDs
- DevOps operational procedures (startup, status, logs, reset)
- Security monitoring and audit log integrity

**Figure 3.1 — Grafana VendNet Dashboard with all 8 panels:**

![Grafana Dashboard](Telemetry/screenshots/demo-grafana-dashboard.png)

> JVM memory, CPU usage, HTTP request rate/latency, app status, active threads, max heap, and application logs — all in one auto-provisioned dashboard.

**Figure 3.2 — Jaeger distributed trace waterfall view:**

![Jaeger Trace](Telemetry/screenshots/demo-jaeger-trace.png)

> Full request path: JWT filter → controller → service → repository → SQL queries. Each span shows duration in microseconds.

---

## Chapter 4 — Security Implementation Evidence

|                 |                                                                                                                                  |
| --------------- | -------------------------------------------------------------------------------------------------------------------------------- |
| **Document**    | [security-implementation.md](Security/security-implementation.md)                                                                |
| **Screenshots** | 2 — [zap-baseline](Security/screenshots/security-zap-baseline.png), [asvs-tracker](Security/screenshots/asvs-level2-tracker.png) |

Traces every security control planned in Phase 1 (Reports 07, 09, and 10) to its actual implementation in the Sprint 2 codebase. Contains 21 verbatim Java code blocks from the source, each with an evidence caption explaining what threat it mitigates.

**Key evidence categories:**

| Domain                       | Planned Controls                                      | Implemented                    | Key Source File                        |
| ---------------------------- | ----------------------------------------------------- | ------------------------------ | -------------------------------------- |
| Authentication — JWT         | HS256, BCrypt(12), lockout, MFA, dummy hash           | Fully implemented              | `User.java:74-121`                     |
| Authentication — mTLS        | X.509 certs + CRL/OCSP                                | Implemented (CRL/OCSP pending) | `X509MachineAuthenticationFilter.java` |
| Authorization — RBAC         | 3 roles, hierarchy, `@PreAuthorize`, ArchUnit CI      | Fully implemented              | `SecurityConfig.java:104-108`          |
| OS Operations Security       | ProcessBuilder array, path sandbox                    | Fully implemented              | `BackupServiceImpl.java:171-196`       |
| Input Validation             | 5-layer model (DTO → Jackson → JPA → Error → Payload) | Fully implemented              | `LoginRequest.java`, `Slot.java`       |
| Logging & Audit              | JSON, HMAC, correlation IDs                           | Core implemented               | `CorrelationIdFilter.java`             |
| alg:none rejection           | Explicit header inspection                            | Fully implemented              | `JwtService.java:142-158`              |
| Server-side price resolution | Ignore client-supplied price                          | Fully implemented              | `SaleService.java:70`                  |

**Figure 4.1 — OWASP ZAP DAST baseline scan results:**

![ZAP Baseline](Security/screenshots/security-zap-baseline.png)

> Dynamic Application Security Testing against the public API surface. Baseline scan executed via `make zap-full`.

**Figure 4.2 — ASVS Level 2 compliance tracker:**

![ASVS Tracker](Security/screenshots/asvs-level2-tracker.png)

> Filled workbook shows 41 of 46 security requirements implemented (89% coverage). Generated by `make asvs-tracker`.

**Selected code evidence — alg:none rejection in JwtService.java:**

```java
// JwtService.java:142-158
private void rejectAlgNone(String token) {
    String[] parts = token.split("\\.");
    if (parts.length < 2) return;
    try {
        byte[] headerBytes = Base64.getUrlDecoder().decode(parts[0]);
        String header = new String(headerBytes, StandardCharsets.UTF_8).toLowerCase();
        if (header.contains("\"alg\":\"none\"") || header.contains("\"alg\": \"none\"")) {
            throw new SecurityException("alg:none tokens are not permitted");
        }
    } catch (SecurityException e) {
        throw e;
    } catch (IllegalArgumentException ignored) {
        // header decode failure — treat as non-JWT, let parser reject it
    }
}
```

> This method inspects the raw JWT header **before** JJWT parsing — an independent guard layer that rejects `alg:none` tokens regardless of parser configuration.

---

## Chapter 5 — Testing Strategy

|              |                                  |
| ------------ | -------------------------------- |
| **Document** | [Testing.md](Testing/Testing.md) |

Summarises the project's testing strategy: black-box (external API behaviour), white-box (internal logic and coverage), and end-to-end (full user journeys via Newman/Postman). Covers all 8 abuse cases with specific test scenarios and expected outcomes.

**Testing categories:**

| Category         | Scope                                                    | Key Tests                                       | Make Target             |
| ---------------- | -------------------------------------------------------- | ----------------------------------------------- | ----------------------- |
| **Black-box**    | API behaviour, HTTP codes, security, business rules      | AC-01 through AC-07 abuse cases                 | `make e2e`              |
| **White-box**    | Internal logic, validations, exceptions, branch coverage | Domain entity invariants, service orchestration | `make test`             |
| **Architecture** | Layer isolation, `@PreAuthorize`, package conventions    | ArchUnit rules enforced at build time           | `make archunit`         |
| **Integration**  | Real MySQL via Testcontainers                            | Repository queries, transaction boundaries      | `make integration-test` |
| **Abuse Cases**  | Regression tests for all 8 abuse cases                   | AC-01 to AC-08 mapped from Phase 1 threat model | `make abuse-tests`      |
| **End-to-End**   | Full user journeys across all 13 use cases               | Newman/Postman collection, fully automated      | `make e2e`              |

**Run all tests:**

```bash
make test              # Unit tests (Surefire, H2)
make integration-test  # Integration tests (Failsafe, Testcontainers)
make archunit          # Architecture enforcement (ArchUnit)
make abuse-tests       # Abuse case regression tests
make e2e               # End-to-end (Newman, fully automatic)
```

---

## Chapter 6 — ASVS & Threat Model

|              |                                         |
| ------------ | --------------------------------------- |
| **Document** | [Threat_Model.md](ASVS/Threat_Model.md) |
| **Workbook** | [ASVS.xlsx](ASVS/ASVS.xlsx)             |

Updated Phase 1 threat model (STRIDE-per-element analysis over DFD Level 1) with Sprint 2 implementation columns added: **Status**, **Implementation** (file:line), and **Test Evidence** for all 116 threats. The `.xlsx` workbook provides the ASVS 5.0 Level 2 compliance tracker.

---

## Chapter 7 — Development Documentation

|                 |                                                                                                                             |
| --------------- | --------------------------------------------------------------------------------------------------------------------------- |
| **Document**    | [Docs.md](Development/Docs.md)                                                                                              |
| **Screenshots** | 2 — [e2e-report](Development/screenshots/e2e-report.png), [zap-baseline](Development/screenshots/security-zap-baseline.png) |

Comprehensive architectural documentation covering the C4 model (Level 1–3), domain model (DDD aggregates and value objects), database schema (Mermaid ER diagram), backend API (30+ endpoints with request/response examples), and authentication/authorisation flows with sequence diagrams.

**Key contents:**

- C4 Level 1–3 diagrams with external actor mappings
- 4 DDD aggregates: User, Product, VendingMachine, Sale
- 8 database tables with full column specifications
- 13 use cases mapped to endpoints with RBAC controls
- Authentication flow with JWT + X.509 mTLS
- Bootstrap seed data specification (3 users, 8 products, 4 machines)

---

## Evidence Summary

**Total deliverables:**

| Type                  | Count  |
| --------------------- | ------ |
| Markdown documents    | 7      |
| Screenshots (PNG)     | 15     |
| Code snippets (CI/CD) | 7      |
| ASVS workbook (XLSX)  | 1      |
| **Total files**       | **30** |

**Screenshots by chapter:**

| Chapter     | Count | Files                                                                                                         |
| ----------- | ----- | ------------------------------------------------------------------------------------------------------------- |
| Demo        | 6     | `swagger-ui`, `account-locked`, `rbac-enforcement`, `grafana-dashboard`, `jaeger-trace`, `prometheus-metrics` |
| Pipeline    | 2     | `github-actions-overview`, `github-artifacts`                                                                 |
| Telemetry   | 3     | `grafana-dashboard`, `jaeger-trace`, `prometheus-metrics`                                                     |
| Security    | 2     | `zap-baseline`, `asvs-tracker`                                                                                |
| Development | 2     | `e2e-report`, `zap-baseline`                                                                                  |
| Testing     | 0     | —                                                                                                             |
| ASVS        | 0     | —                                                                                                             |

**Key Make targets for reproducing all evidence:**

```bash
make demo              # Full environment bootstrap
make status            # Health verification
make api-test          # Smoke test (10 endpoints, RBAC verification)
make api-test-full     # Traffic generation for observability (55 requests)
make pipeline          # Full CI: clean + SAST + SCA + verify
make test              # Unit tests
make archunit          # Architecture enforcement tests
make abuse-tests       # Abuse case regression tests
make e2e               # End-to-end tests (Newman, fully automatic)
make zap-full          # OWASP ZAP DAST (baseline + authenticated API scan)
make grafana           # Open Grafana (http://localhost:3000, admin/admin)
make jaeger            # Open Jaeger (http://localhost:16686)
make loki-logs         # Query recent logs from Loki
make metrics           # View raw Prometheus metrics
make urls              # Print all service URLs
```

---

_Sprint 2 delivered by Grupo Sensacao — ISEP DESOFS 2025/2026. All commands are executable and reproducible. Screenshots captured from live `make demo` sessions._
