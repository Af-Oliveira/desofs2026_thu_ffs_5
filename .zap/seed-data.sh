#!/bin/bash
# =============================================================================
# ZAP Authenticated Scan — Seed Data Script
# =============================================================================
# Logs into the VendNet app (which must already be seeded via bootstrap profile)
# and exports JWT tokens for the 3 RBAC roles into .zap/tokens/.
#
# This script assumes the app is running with the 'bootstrap' Spring profile
# which pre-seeds: admin@vendnet.io / Admin@1234
#                   operator@vendnet.io / Operator@1234
#                   customer@vendnet.io / Customer@1234
#
# Usage: ./.zap/seed-data.sh [BASE_URL] [OUTPUT_DIR]
#   BASE_URL   — default: http://localhost:8080
#   OUTPUT_DIR — default: .zap/tokens
# =============================================================================

set -e

BASE_URL="${1:-http://localhost:8080}"
OUTPUT_DIR="${2:-.zap/tokens}"

mkdir -p "$OUTPUT_DIR"

echo "=== VendNet ZAP RBAC Token Acquisition ==="
echo "Target: $BASE_URL"
echo ""

login_and_export_token() {
    local email=$1
    local password=$2
    local name=$3
    local role=$4
    local output_file="$OUTPUT_DIR/$5"

    echo "--- $role ($email) --- "

    login_resp=$(curl -s -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d "{\"email\":\"$email\",\"password\":\"$password\"}" 2>/dev/null)

    token=$(echo "$login_resp" | python3 -c "import sys,json; print(json.load(sys.stdin).get('token',''))" 2>/dev/null || echo "")

    if [ -n "$token" ] && [ "$token" != "None" ]; then
        echo "$token" > "$output_file"
        echo "  Token obtained: $(echo $token | cut -c1-20)..."
    else
        echo "  ERROR: Could not obtain token for $email"
        echo "  Response: $login_resp"
        exit 1
    fi
}

login_and_export_token "admin@vendnet.io"     "Admin@1234"     "Admin"          "ADMINISTRATOR"  "admin.jwt"
login_and_export_token "operator@vendnet.io"  "Operator@1234"  "Operator"       "OPERATOR"       "operator.jwt"
login_and_export_token "customer@vendnet.io"  "Customer@1234"  "Customer"       "CUSTOMER"       "customer.jwt"

echo ""
echo "=== Tokens exported to: $OUTPUT_DIR/ ==="
ls -la "$OUTPUT_DIR/"
echo ""
echo "=== RBAC Endpoint Map ==="
echo "Role          | Endpoint              | Method | Expected"
echo "--------------+-----------------------+--------+----------"
echo "Customer      | /api/auth/me          | GET    | 200"
echo "Customer      | /api/products         | GET    | 200"
echo "Customer      | /api/machines         | GET    | 200"
echo "Customer      | /api/admin/dashboard  | GET    | 403"
echo "Operator      | /api/machines         | GET    | 200"
echo "Operator      | /api/machines/1/slots | GET    | 200"
echo "Operator      | /api/sales/machine/1  | GET    | 200"
echo "Operator      | /api/admin/dashboard  | GET    | 403"
echo "Administrator | /api/admin/dashboard  | GET    | 200"
echo "Administrator | /api/admin/users      | GET    | 200"
echo "Administrator | /api/admin/operations/backup | POST | 200/500"
echo ""
echo "=== ZAP Scan Commands ==="
echo "  # Baseline (public surface)"
echo "  docker run --rm --network host -v \$(pwd):/zap/wrk zaproxy/zap-stable zap-baseline.py \\"
echo "    -t http://localhost:8080 \\"
echo "    -c .zap/rules.tsv \\"
echo "    -r zap-baseline-report.html"
echo ""
echo "  # API scan with admin JWT (authenticated)"
echo "  docker run --rm --network host -v \$(pwd):/zap/wrk zaproxy/zap-stable zap-api-scan.py \\"
echo "    -t http://localhost:8080/v3/api-docs \\"
echo "    -f openapi \\"
echo "    -c .zap/rules.tsv \\"
echo "    -r zap-api-scan-report.html \\"
echo "    -z \"-config replacer.full_list[0].description=admin_auth \\"
echo "         -config replacer.full_list[0].enabled=true \\"
echo "         -config replacer.full_list[0].matchtype=REQ_HEADER \\"
echo "         -config replacer.full_list[0].matchstr=Authorization \\"
echo "         -config replacer.full_list[0].regex=false \\"
echo "         -config 'replacer.full_list[0].replacement=Bearer \$(cat .zap/tokens/admin.jwt)'\""
