# VendNet Testing Overview


> **Project:** VendNet — Vending Machine Network<br>
> **Phase:** 2 Sprint 2 — Tests<br>
> **Date:** 2026-06-16

---

This document summarizes, in a simple and effective way, the testing strategy used in the project.
The goal is to ensure functional quality, security, and stability in every delivery.

## 1) Black-box testing

In **black-box** tests, we validate the API's external behavior without relying on internal code details.

- Focus: inputs, outputs, HTTP status codes, business rules, and security behavior.
- Concrete scenarios from `BlackBoxSecurityE2ETest`:
  - **AC-01:** `/api/admin/backups` only accepts `ADMIN` (401/403 for unauthenticated or lower roles).
  - **AC-02:** webhook HMAC validation rejects missing/invalid/tampered signatures (`/api/webhooks/payment`).
  - **AC-03:** purchase requests with client-injected price fields are rejected, and server-side price is enforced.
  - **AC-04:** forged JWTs (`alg:none`, malformed tokens) are rejected with 401.
  - **AC-07:** concurrent purchases are stress-tested to confirm stock consistency.
- Benefit: validates the platform as an external client would use and attack it.

## 2) White-box testing

In **white-box** tests, we test internal parts of the code (methods, classes, and internal rules).

- Focus: internal logic, validations, exceptions, and branch coverage.
- Concrete scenarios:
  - `SaleServiceTest` validates success flow, idempotency handling, out-of-stock behavior, and payment decline paths.
  - `JwtServiceTest` validates token generation/extraction, invalid token rejection, and `alg:none` blocking.
  - `BackupServiceImplTest` validates backup creation, sandbox path validation, rotation behavior, and invalid tool path handling.
  - Domain tests (`ProductTest`, `SlotTest`, `SaleTest`, `UserTest`) validate core model invariants and domain behavior.
- Benefit: catches logic regressions early and protects critical decision branches.

## 3) Integration testing

In **integration** tests, we verify whether multiple components work correctly together.

- Focus: controller + service + persistence + security + external dependencies.
- Concrete scenarios:
  - `RbacIntegrationTest` checks role permissions end-to-end (`CUSTOMER`, `OPERATOR`, `ADMINISTRATOR`) for admin, sales, and public endpoints.
  - `AuthControllerIntegrationTest` validates authentication flow with real Spring Security filters and persistence.
  - `ControllerIntegrationTests` validates endpoint wiring, authorization behavior, and error handling across layers.
  - `IastIntegrationTest` validates runtime security instrumentation paths under integration execution.
- In several scenarios, test profiles and tools such as Testcontainers/WireMock simulate realistic infrastructure.
- Benefit: reduces defects that only appear when components interact in real request flows.

## 4) Other relevant tests

In addition to black-box, white-box, and integration testing, the project also uses:

- **Abuse-case regression tests** (`AbuseCaseRegressionTest`): continuously validate AC-01 to AC-08 controls at API level.
- **Architecture tests** (`LayeredArchitectureArchTest`, `NamingConventionArchTest`, `SecurityAnnotationArchTest`): enforce layer boundaries and security annotation rules.
- **System/functional suites** (`SystemFunctionalTests`, `ControllerUnitTests` groups): validate endpoint contracts and cross-functional behavior.
- **Automated security testing in pipeline**:
  - SAST (`semgrep`, `spotbugs` + `findsecbugs`);
  - SCA (`dependency-check`);
  - IAST profile (`-P iast`);
  - DAST (ZAP job).

## 5) How tests are used in CI/CD

In the CI/CD pipeline (`.github/workflows/vendnet-ci-cd.yml`), tests run in stages:

1. **Unit tests** (fail fast);
2. **Integration tests** (only if unit tests pass);
3. **Security and quality analysis** (SAST/SCA/IAST/Sonar and remaining gates);
4. **Build/deploy** only after full validation.

Practical effect: code with regressions (functional, security, or quality) is blocked before deployment.

## 6) Conclusion

VendNet's testing strategy combines multiple testing levels and perspectives. Together, these practices help maintain high code quality and support the project's coverage goals.

Current evidence from the pipeline:

- **CI Quality Gate:** Passed;
- **SonarQube overall coverage:** 81.6% (>=80% required);
- **JaCoCo local branch coverage:** 74.91%;

The test suite includes:

- **Black-box** to validate observed behavior;
- **White-box** to validate internal logic;
- **Integration** to validate collaboration between components;
- **E2E + security** to confirm robustness in real scenarios.

Result: more reliable deliveries, lower regression risk, and better overall system quality.
