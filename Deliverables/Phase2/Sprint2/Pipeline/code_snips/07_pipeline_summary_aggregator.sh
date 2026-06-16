#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# Stage 13 — Pipeline Summary (vendnet-ci-cd.yml, lines 1348-1473)
# Aggregates results from all pipeline jobs into a single developer-friendly
# GitHub Actions step summary with status icons and artifact links.
# ─────────────────────────────────────────────────────────────────────────────

icon() {
    case "$1" in
        success)   echo "✅" ;;
        failure)   echo "❌" ;;
        skipped)   echo "⏭️" ;;
        cancelled) echo "🚫" ;;
        *)         echo "❓" ;;
    esac
}

# Collect job statuses
SECRET_DETECT="${{ needs.secret-detect.result }}"
SEMGREP="${{ needs.semgrep.result }}"
BUILD_UNIT="${{ needs.build-unit-tests.result }}"
INTEGRATION="${{ needs.integration-tests.result }}"
SPOTBUGS="${{ needs.sast-spotbugs.result }}"
SCA="${{ needs.sca.result }}"
IAST="${{ needs.iast.result }}"
SONARQUBE="${{ needs.sonarqube.result }}"
DOCKER="${{ needs.docker-build-push.result }}"
DAST_ZAP="${{ needs.dast-zap.result }}"
DEPLOY_DEV="${{ needs.deploy-dev.result }}"
DEPLOY_STAGE="${{ needs.deploy-staging.result }}"
DEPLOY_PROD="${{ needs.deploy-prod.result }}"
RELEASE="${{ needs.github-release.result }}"

# Generate GitHub step summary with tables for:
# - Build Context (artifact, version, branch, commit, image tag)
# - Security Gates (Gitleaks, Semgrep, SpotBugs, SCA, IAST, DAST ZAP)
# - Quality Gates (Build & Unit Tests, Integration, SonarQube, Docker)
# - Deployments (DEV, STAGING, PROD with URLs)
# - GitHub Release (version, tag, link)
