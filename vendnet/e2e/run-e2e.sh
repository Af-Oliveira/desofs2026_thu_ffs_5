#!/usr/bin/env bash
# =============================================================================
# VendNet — End-to-End Test Runner (Newman)
# =============================================================================
# Prerequisites:
#   1. npm install -g newman newman-reporter-htmlextra
#   2. The VendNet app must be running on http://localhost:8080
#      Or run via: make e2e  (handles start/stop automatically)
#
# Usage:
#   ./e2e/run-e2e.sh                   # Run all tests (checks if app is running)
#   ./e2e/run-e2e.sh --no-check        # Skip the app-is-running check
#   ./e2e/run-e2e.sh --folder "RBAC"   # Run specific folder
# =============================================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
COLLECTION="$SCRIPT_DIR/VendNet_E2E_Tests.postman_collection.json"
ENVIRONMENT="$SCRIPT_DIR/vendnet-local.postman_environment.json"
REPORT_DIR="$SCRIPT_DIR/reports"
APP_URL="${BASE_URL:-http://localhost:8080}"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
BOLD='\033[1m'
NC='\033[0m'

SKIP_CHECK=false
EXTRA_ARGS=()

for arg in "$@"; do
    case "$arg" in
        --no-check) SKIP_CHECK=true ;;
        *) EXTRA_ARGS+=("$arg") ;;
    esac
done

echo -e "${BOLD}${BLUE}"
echo "  ════════════════════════════════════════"
echo "    VendNet — E2E Test Suite (Newman)"
echo "  ════════════════════════════════════════"
echo -e "${NC}"

# Check if Newman is installed
if ! command -v newman &>/dev/null; then
    echo -e "${RED}✗ Newman is not installed.${NC}"
    echo "  Run: make e2e-install"
    exit 1
fi

# Check if the app is running (unless skipped)
if [ "$SKIP_CHECK" = false ]; then
    if ! curl -sf --max-time 3 "$APP_URL/api/health/ping" >/dev/null 2>&1 && \
       ! curl -sf --max-time 3 "$APP_URL/actuator/health" >/dev/null 2>&1; then
        echo -e "${YELLOW}⚠ App is not running at $APP_URL${NC}"
        echo "  Run: make e2e  (starts/stops app automatically)"
        exit 1
    fi
    echo -e "${GREEN}✓ App is running at $APP_URL${NC}"
fi

# Create report directory
mkdir -p "$REPORT_DIR"

echo ""
echo -e "${BOLD}Running E2E tests...${NC}"
echo ""

newman run "$COLLECTION" \
    --environment "$ENVIRONMENT" \
    --reporters cli,htmlextra \
    --reporter-htmlextra-export "$REPORT_DIR/e2e-report.html" \
    --color on \
    --timeout-request 10000 \
    ${EXTRA_ARGS:+"${EXTRA_ARGS[@]}"}

EXIT_CODE=$?

echo ""
if [ $EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}${BOLD}  ✓ All E2E tests passed.${NC}"
else
    echo -e "${RED}${BOLD}  ✗ Some E2E tests failed (exit code: $EXIT_CODE).${NC}"
fi

echo ""
echo -e "  Report: ${BLUE}$REPORT_DIR/e2e-report.html${NC}"
echo ""

exit $EXIT_CODE
