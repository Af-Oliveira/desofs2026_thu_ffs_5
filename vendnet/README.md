# VendNet — CI/CD Pipeline

## Quick Reference

| Command | Description |
|---------|-------------|
| `make build` | Compile the project |
| `make test` | Run unit tests (H2 in-memory) |
| `make verify` | Build + test + coverage report |
| `make sast` | SpotBugs + FindSecBugs static analysis |
| `make sca` | OWASP Dependency-Check CVE scan |
| `make pipeline` | Full CI pipeline: clean + SAST + SCA + verify |
| `make archunit` | Run ArchUnit layered architecture tests |
| `make abuse-tests` | Run abuse case regression tests |
| `make integration-test` | Integration tests with Maven Failsafe |
| `./mvnw verify -Pe2e` (from `vendnet/`) | E2E + black-box HTTP tests (`*E2ETest`); preconditions & seeding: `e2e/README.md` |
| `make sbom` | Generate CycloneDX SBOM |
| `make enforcer` | Check Maven dependency version rules |
| `make docker-build` | Build Docker image (non-root, jre-alpine) |
| `make docker-scan` | Scan Docker image with Trivy |
| `make secret-scan` | Scan for secrets with Gitleaks |

## CI/CD Pipeline (GitHub Actions)

The full security pipeline is in `.github/workflows/ci-security.yml`:

```
Stage 1: Build & Unit Test   — compile, test (H2), JaCoCo coverage
Stage 2: SAST                — SpotBugs + FindSecBugs, Semgrep (SARIF → Security tab)
Stage 3: SCA                 — Maven Enforcer, OWASP Dependency-Check (fail on CVSS ≥7.0)
Stage 4: ArchUnit            — DDD layer isolation, @PreAuthorize check, @EnableMethodSecurity
Stage 5: Integration         — Abuse case regression tests (AC-01..AC-08), IAST taint tracking
Stage 6: SonarQube Cloud     — Quality gate: 0 Critical/Blocker, coverage ≥80%, duplication ≤3%
Stage 7: DAST (ZAP)          — Baseline + authenticated scan (main branch, nightly, or manual)
Stage 8: Artifact Scan       — Trivy JAR + Docker image scan (nightly or manual)
Stage 9: Secret Detection    — Gitleaks backup check
```

### Triggers

| Event | Pipeline |
|-------|----------|
| Push to `main`/`develop` | Stages 1–6 |
| PR to `main` | Stages 1–6 + PR annotations |
| Nightly (02:00 UTC) | Full pipeline (1–9) |
| `workflow_dispatch` | Any stage selectable |

### Required GitHub Secrets

| Secret | Purpose |
|--------|---------|
| `SONAR_TOKEN` | SonarQube Cloud authentication |
| `DOCKER_HUB_TOKEN` | Docker Hub registry push (optional) |

## Environment Variables (CI)

Set via `application-ci.properties`:

| Variable | Default (CI) |
|----------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/vendnet` |
| `SPRING_DATASOURCE_USERNAME` | `vendnet_user` |
| `SPRING_DATASOURCE_PASSWORD` | `vendnet_pass` |
| `APP_JWT_SECRET` | (set via `secrets.APP_JWT_SECRET`) |
| `APP_PAYMENT_WEBHOOK_SECRET` | (set via `secrets.PAYMENT_WEBHOOK_SECRET`) |

## Pre-Commit Hooks

Install with `pre-commit install`:

- **Gitleaks**: Blocks commits containing secrets (passwords, API keys, JWT secrets)

## Quality Gates

| Gate | Threshold | Tool |
|------|-----------|------|
| Coverage (domain) | ≥80% line | JaCoCo |
| Coverage (application) | ≥80% line | JaCoCo |
| Coverage (overall) | ≥60% line | JaCoCo |
| CVE severity | <7.0 (no High/Critical) | Dependency-Check |
| SpotBugs threshold | Low (catch everything) | SpotBugs/FindSecBugs |
| Semgrep severity | ERROR/WARNING fail build | Semgrep |
| SonarQube | 0 new Critical/Blocker | SonarQube Cloud |
| Trivy | 0 Critical/High CVEs | Trivy |
