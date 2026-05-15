# Phase 2 Sprint 1 — CI/CD Security Pipeline Implementation

## Pipeline Summary

The VendNet CI/CD pipeline implements a **shift-left security model** with 9 stages and 18 user stories mapped to Phase 1 artifacts.

## Implemented User Stories

| Story | Status | Description |
|-------|--------|-------------|
| US-PIPE-001 | Done | Build gate: compile + unit test on every push |
| US-PIPE-002 | Done | Maven Enforcer: pinned deps, ban version ranges |
| US-PIPE-003 | Done | ArchUnit: DDD layer isolation, @PreAuthorize, @EnableMethodSecurity |
| US-SAST-001 | Done | SpotBugs + FindSecBugs (Max effort, Low threshold) |
| US-SAST-002 | Done | Semgrep: Java/Spring/Secrets + custom rules for JWT, SQLi, PreAuthorize |
| US-SAST-003 | Done | SonarQube Cloud: quality gate (0 Critical, coverage ≥80%) |
| US-SCA-001 | Done | OWASP Dependency-Check: fail on CVSS ≥7.0 |
| US-SCA-002 | Done | CycloneDX SBOM generation during package phase |
| US-DAST-001 | Done | OWASP ZAP baseline scan against running app |
| US-DAST-002 | Done | ZAP authenticated scan with seed data script |
| US-IAST-001 | Done | Custom taint tracking filter + integration tests |
| US-SCAN-001 | Done | Trivy JAR filesystem scan for embedded CVEs |
| US-SCAN-002 | Done | Multi-stage Dockerfile (jdk→jre-alpine, non-root) + Trivy image scan |
| US-OWASP-001 | Done | Abuse case regression tests mapping to OWASP Top 10 |
| US-TEST-001 | Done | JaCoCo coverage gate: domain ≥80%, application ≥80%, overall ≥60% |
| US-TEST-002 | Done | All 8 abuse case regression tests (AC-01..AC-08) in CI |
| US-SECRET-001 | Done | Gitleaks pre-commit hook + CI backup scan |
| US-SECRET-002 | Done | GitHub Secrets for SONAR_TOKEN, workflow uses `${{ secrets.XXX }}` |

## Files Delivered

| File | Purpose |
|------|---------|
| `.github/workflows/ci-security.yml` | Full CI/CD pipeline (9 stages) |
| `.github/workflows/ci.yml` | Legacy pipeline (disabled, manual only) |
| `.zap/rules.tsv` | ZAP scan policy rules |
| `.zap/seed-data.sh` | ZAP authenticated scan seed data script |
| `vendnet/pom.xml` | Updated with enforcer, CycloneDX, JaCoCo check, SonarQube, profiles |
| `vendnet/Dockerfile` | Multi-stage hardened Docker image |
| `vendnet/.semgrep/vendnet-security.yml` | Custom Semgrep rules (8 rules) |
| `vendnet/.pre-commit-config.yaml` | Pre-commit hooks (Gitleaks) |
| `vendnet/.gitleaks.toml` | Gitleaks configuration |
| `vendnet/dependency-check-suppressions.xml` | OWASP DC suppressions (empty) |
| `vendnet/sonar-project.properties` | SonarQube project configuration |
| `vendnet/README.md` | CI/CD pipeline documentation |
| `vendnet/src/main/resources/application-ci.properties` | CI-specific Spring config |
| `vendnet/src/main/java/.../infrastructure/security/IastTaintTrackingFilter.java` | IAST runtime taint tracking |
| `vendnet/src/main/java/.../infrastructure/security/TaintAwareHttpServletResponseWrapper.java` | IAST response wrapper |
| `vendnet/src/test/java/.../LayeredArchitectureTest.java` | ArchUnit layered architecture tests (8 tests) |
| `vendnet/src/test/java/.../AbuseCaseRegressionTest.java` | Abuse case regression tests (14 tests) |
| `vendnet/src/test/java/.../IastIntegrationTest.java` | IAST taint detection tests (4 tests) |

## Traceability

| Story | Phase 1 SR | Phase 1 AC | Threat | ASVS |
|-------|-----------|-----------|--------|------|
| US-PIPE-003 | SR-06 | AC-04 | T-36 | V4.1.2 |
| US-SAST-001 | SR-09, SR-10 | AC-05 | T-39 | V5.2.1 |
| US-SAST-002 | SR-03, SR-04 | AC-04 | T-28, T-29 | V2.2.1 |
| US-SCA-001 | SR-32, SR-33 | — | — | V14.2.1 |
| US-DAST-001 | SR-17, SR-24, SR-26 | AC-03, AC-06 | T-39, T-46, T-58 | V5.1.1 |
| US-DAST-002 | SR-02, SR-06, SR-08 | AC-04 | T-11, T-36 | V4.1.1 |
| US-IAST-001 | SR-09, SR-26 | AC-01, AC-05, AC-06 | T-39, T-58, T-59 | V5.2.1 |
| US-SCAN-001 | SR-32, SR-33 | — | — | V14.2.1 |
| US-SCAN-002 | SR-17, NFR-15 | AC-01 | T-63 | V14.2.1 |
| US-TEST-002 | All SRs | AC-01..AC-08 | T-28,T-39,T-46,T-58,T-59 | V1.5.2 |
| US-SECRET-001 | SR-11, NFR-19 | — | T-70 | V5.3.5 |

## Test Results

- **42 tests pass**: 8 ArchUnit + 14 abuse case + 4 IAST + 16 existing integration tests
- **Zero failures, zero errors**
- Build validation passes: Java 17, no version ranges, dependency convergence
