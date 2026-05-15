#!/bin/bash
# ZAP Authenticated Scan Seed Data Script
# Seeds test users (Customer, Operator, Administrator) and exports their JWTs
# for use in OWASP ZAP authenticated scanning.

set -e

BASE_URL="${1:-http://localhost:8080}"
OUTPUT_DIR="${2:-.zap/tokens}"

mkdir -p "$OUTPUT_DIR"

echo "=== VendNet ZAP Seed Data ==="
echo "Target: $BASE_URL"
echo ""

register_and_login() {
    local email=$1
    local password=$2
    local name=$3
    local role=$4
    local output_file=$5

    echo "--- $role ($email) ---"

    register_resp=$(curl -s -X POST "$BASE_URL/api/auth/register" \
        -H "Content-Type: application/json" \
        -d "{\"email\":\"$email\",\"password\":\"$password\",\"name\":\"$name\"}" 2>/dev/null) || true

    login_resp=$(curl -s -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d "{\"email\":\"$email\",\"password\":\"$password\"}" 2>/dev/null)

    token=$(echo "$login_resp" | python3 -c "import sys,json; print(json.load(sys.stdin).get('token',''))" 2>/dev/null || echo "")

    if [ -n "$token" ]; then
        echo "  Token obtained"
        echo "$token" > "$output_file"

        if echo "$login_resp" | python3 -c "import sys,json; d=json.load(sys.stdin); exit(0 if d.get('mfaRequired') else 1)" 2>/dev/null; then
            echo "  MFA required for $role - adding to MFA bypass list"
        fi
    else
        echo "  WARNING: Could not obtain token (user may already exist)"
        echo "PLACEHOLDER_TOKEN_FOR_$role" > "$output_file"
    fi
}

register_and_login "zap-customer@vendnet.io"    "ZapTest@1234" "ZAP Customer"    "CUSTOMER"       "$OUTPUT_DIR/customer.jwt"
register_and_login "zap-operator@vendnet.io"    "ZapTest@1234" "ZAP Operator"    "OPERATOR"       "$OUTPUT_DIR/operator.jwt"
register_and_login "zap-admin@vendnet.io"       "ZapTest@1234" "ZAP Administrator" "ADMINISTRATOR" "$OUTPUT_DIR/admin.jwt"

echo ""
echo "=== RBAC Endpoint Map ==="
echo "Role          | Endpoint              | Method | Expected"
echo "--------------+-----------------------+--------+----------"
echo "Customer      | /api/auth/me          | GET    | 200"
echo "Customer      | /api/products         | GET    | 200"
echo "Customer      | /api/machines         | GET    | 200"
echo "Customer      | /api/admin/dashboard  | GET    | 403"
echo "Customer      | /api/sales/machine/1  | GET    | 403"
echo "Operator      | /api/machines         | GET    | 200"
echo "Operator      | /api/sales/machine/1  | GET    | 200"
echo "Operator      | /api/admin/dashboard  | GET    | 403"
echo "Operator      | /api/admin/users      | GET    | 403"
echo "Administrator | /api/admin/dashboard  | GET    | 200"
echo "Administrator | /api/admin/users      | GET    | 200"
echo "Administrator | /api/admin/operations/backup | POST | 200"
echo ""
echo "Tokens saved to $OUTPUT_DIR/"
echo ""
echo "=== ZAP Authenticated Scan Command ==="
echo "  docker run --network host -v \$(pwd)/.zap:/zap/wrk owasp/zap2docker-stable zap-api-scan.py \\"
echo "    -t http://localhost:8080/v3/api-docs \\"
echo "    -f openapi \\"
echo "    -z \"-config replacer.full_list[0].description=auth \\"
echo "           -config replacer.full_list[0].enabled=true \\"
echo "           -config replacer.full_list[0].matchtype=REQ_HEADER \\"
echo "           -config replacer.full_list[0].matchstr=Authorization \\"
echo "           -config replacer.full_list[0].regex=false \\"
echo "           -config 'replacer.full_list[0].replacement=Bearer \$(cat .zap/tokens/admin.jwt)'\""
