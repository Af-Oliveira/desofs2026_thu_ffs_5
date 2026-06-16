#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# Stage 2 — Build & Unit Tests (vendnet-ci-cd.yml, lines 288-374)
# Compiles project, runs unit tests (surefire + ArchUnit), generates
# JaCoCo coverage report, packages runtime JAR.
# ─────────────────────────────────────────────────────────────────────────────

cd vendnet

# Compile + run unit tests (excluding integration, IAST, E2E tests)
./mvnw -B test -Dspring.profiles.active=test \
    -Dtest='!*IT,!*IntegrationTest,!*AbuseCaseTest,!*IastIntegrationTest,!*E2ETest'
# MAVEN_OPTS: -Xmx1024m -XX:+TieredCompilation -XX:TieredStopAtLevel=1

# Generate JaCoCo XML coverage report for SonarQube
./mvnw -B jacoco:report -DskipTests

# Package runtime JAR (skip tests, already run)
./mvnw -B package -DskipTests -q
