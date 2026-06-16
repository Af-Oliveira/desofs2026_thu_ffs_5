#!/usr/bin/env bash
set -u

BASE_URL="${BASE_URL:-http://localhost:8080}"
PASSES="${PASSES:-2}"
WEBHOOK_SECRET="${WEBHOOK_SECRET:-e2e-webhook-secret}"
ASSET_PATH="${ASSET_PATH:-vendnet/e2e/assets/sample-product.png}"

tmpdir="$(mktemp -d)"
trap 'rm -rf "$tmpdir"' EXIT

total=0
failed=0
run_id="$(date +%s)"

body_file() {
  printf "%s/%s.json" "$tmpdir" "$(printf "%s" "$1" | tr -c 'A-Za-z0-9' '_')"
}

json_value() {
  node - "$1" "$2" <<'NODE'
const fs = require('fs');
const file = process.argv[2];
const expr = process.argv[3];
const text = fs.readFileSync(file, 'utf8');
const data = text ? JSON.parse(text) : null;
const value = Function('data', `return (${expr});`)(data);
if (value === undefined || value === null || value === '') process.exit(2);
process.stdout.write(String(value));
NODE
}

hmac_sha256() {
  printf "%s" "$1" | openssl dgst -sha256 -hmac "$WEBHOOK_SECRET" -binary | xxd -p -c 256
}

expect_status() {
  local name="$1"
  local expected="$2"
  local actual="$3"
  local file="$4"
  total=$((total + 1))
  case ",$expected," in
    *,"$actual",*)
      printf "PASS %-64s HTTP %s\n" "$name" "$actual" >&2
      ;;
    *)
      failed=$((failed + 1))
      printf "FAIL %-64s expected %s got %s\n" "$name" "$expected" "$actual" >&2
      if [[ -f "$file" ]]; then
        sed -n '1,6p' "$file" >&2
      fi
      ;;
  esac
}

curl_json() {
  local name="$1"
  local expected="$2"
  local method="$3"
  local path="$4"
  local token="$5"
  local body="${6:-}"
  local extra_header="${7:-}"
  local out
  out="$(body_file "$name")"
  : > "$out"

  local args=(-sS -o "$out" -w "%{http_code}" -X "$method" "$BASE_URL$path" -H "Accept: application/json")
  if [[ -n "$token" ]]; then
    args+=(-H "Authorization: Bearer $token")
  fi
  if [[ -n "$extra_header" ]]; then
    args+=(-H "$extra_header")
  fi
  if [[ -n "$body" ]]; then
    args+=(-H "Content-Type: application/json" --data "$body")
  fi

  local status
  status="$(curl "${args[@]}")"
  expect_status "$name" "$expected" "$status" "$out"
  printf "%s" "$out"
}

curl_form_product() {
  local name="$1"
  local expected="$2"
  local token="$3"
  local sku="$4"
  local out
  out="$(body_file "$name")"
  : > "$out"

  local status
  status="$(
    curl -sS -o "$out" -w "%{http_code}" -X POST "$BASE_URL/api/admin/products" \
      -H "Accept: application/json" \
      -H "Authorization: Bearer $token" \
      -F "name=Curl Smoke Drink $sku" \
      -F "description=Multipart image product from curl smoke" \
      -F "price=2.25" \
      -F "currency=EUR" \
      -F "category=DRINK" \
      -F "sku=$sku" \
      -F "image=@$ASSET_PATH;type=image/png"
  )"
  expect_status "$name" "$expected" "$status" "$out"
  printf "%s" "$out"
}

curl_text() {
  local name="$1"
  local expected="$2"
  local path="$3"
  local out
  out="$(body_file "$name")"
  : > "$out"

  local status
  status="$(curl -sS -o "$out" -w "%{http_code}" -X GET "$BASE_URL$path" -H "Accept: text/plain")"
  expect_status "$name" "$expected" "$status" "$out"
  printf "%s" "$out"
}

require_value() {
  local label="$1"
  local value="$2"
  if [[ -z "$value" ]]; then
    printf "FAIL missing required value: %s\n" "$label"
    exit 1
  fi
}

for pass in $(seq 1 "$PASSES"); do
  printf "\n== Insomnia curl pass %s/%s ==\n" "$pass" "$PASSES"
  suffix="${run_id}${pass}"

  health_file="$(curl_json "00.01 Health ping [pass $pass]" "200" GET "/api/health/ping" "")"

  admin_login="$(curl_json "00.02 Admin login [pass $pass]" "200" POST "/api/auth/login" "" '{"username":"admin","password":"Admin@123456"}')"
  operator_login="$(curl_json "00.03 Operator login [pass $pass]" "200" POST "/api/auth/login" "" '{"username":"operator","password":"Operator@123456"}')"
  customer_login="$(curl_json "00.04 Customer login [pass $pass]" "200" POST "/api/auth/login" "" '{"username":"customer","password":"Customer@123456"}')"

  admin_token="$(json_value "$admin_login" 'data.accessToken || data.token')"
  operator_token="$(json_value "$operator_login" 'data.accessToken || data.token')"
  customer_token="$(json_value "$customer_login" 'data.accessToken || data.token')"
  require_value admin_token "$admin_token"
  require_value operator_token "$operator_token"
  require_value customer_token "$customer_token"

  curl_json "00.05 Register customer [pass $pass]" "200" POST "/api/auth/register" "" "{\"email\":\"curl-customer-${suffix}@vendnet.io\",\"password\":\"Customer@123456\",\"name\":\"Curl Customer ${pass}\"}" >/dev/null
  curl_json "00.06 Customer claims [pass $pass]" "200" GET "/api/auth/claims" "$customer_token" >/dev/null

  products_file="$(curl_json "01.01 List products [pass $pass]" "200" GET "/api/products" "$customer_token")"
  product_id="$(json_value "$products_file" '(Array.isArray(data) ? (data.find(p => p.sku === "DRK-002") || data[0] || {}).id : "")')"
  product_sku="$(json_value "$products_file" '(Array.isArray(data) ? (data.find(p => p.sku === "DRK-002") || data[0] || {}).sku : "")')"
  require_value product_id "$product_id"
  require_value product_sku "$product_sku"
  curl_json "01.02 Get product by SKU [pass $pass]" "200" GET "/api/products/$product_sku" "$customer_token" >/dev/null

  machines_file="$(curl_json "01.03 List machines [pass $pass]" "200" GET "/api/machines" "$customer_token")"
  machine_id="$(json_value "$machines_file" '(Array.isArray(data) ? (data.find(m => m.code === "VM-LIS-001") || data[0] || {}).id : "")')"
  machine_code="$(json_value "$machines_file" '(Array.isArray(data) ? (data.find(m => m.code === "VM-LIS-001") || data[0] || {}).code : "")')"
  require_value machine_id "$machine_id"
  require_value machine_code "$machine_code"
  curl_json "01.04 Get machine by code [pass $pass]" "200" GET "/api/machines/$machine_code" "$customer_token" >/dev/null

  purchase_body="{\"productId\":$product_id,\"machineId\":$machine_id,\"paymentToken\":\"tok_curl_valid\",\"idempotencyKey\":\"curl-purchase-${suffix}\"}"
  curl_json "02.01 Purchase succeeds [pass $pass]" "201" POST "/api/sales/purchase" "$customer_token" "$purchase_body" >/dev/null
  curl_json "02.02 Duplicate purchase is idempotent [pass $pass]" "200" POST "/api/sales/purchase" "$customer_token" "$purchase_body" >/dev/null
  curl_json "02.03 Unit price tampering rejected [pass $pass]" "400" POST "/api/sales/purchase" "$customer_token" "{\"productId\":$product_id,\"machineId\":$machine_id,\"paymentToken\":\"tok_price_attack\",\"idempotencyKey\":\"price-attack-${suffix}\",\"unitPrice\":0.01}" >/dev/null
  curl_json "02.04 Price tampering rejected [pass $pass]" "400" POST "/api/sales/purchase" "$customer_token" "{\"productId\":$product_id,\"machineId\":$machine_id,\"paymentToken\":\"tok_price_attack\",\"idempotencyKey\":\"price-attack-2-${suffix}\",\"price\":-1}" >/dev/null
  curl_json "02.05 Customer sales [pass $pass]" "200" GET "/api/sales/me" "$customer_token" >/dev/null

  slots_file="$(curl_json "03.01 List machine slots [pass $pass]" "200" GET "/api/machines/$machine_id/slots" "$operator_token")"
  slot_id="$(json_value "$slots_file" '(Array.isArray(data) ? (data.find(s => (s.currentStock ?? s.quantity ?? 0) < (s.capacity ?? 0)) || data[0] || {}).id : "")')"
  require_value slot_id "$slot_id"
  curl_json "03.02 Operator restocks slot [pass $pass]" "200" PUT "/api/machines/$machine_id/slots/$slot_id/restock" "$operator_token" '{"quantity":1}' >/dev/null
  curl_json "03.03 Excessive restock rejected [pass $pass]" "422" PUT "/api/machines/$machine_id/slots/$slot_id/restock" "$operator_token" '{"quantity":999}' >/dev/null
  curl_json "03.04 Customer cannot restock [pass $pass]" "403" PUT "/api/machines/$machine_id/slots/$slot_id/restock" "$customer_token" '{"quantity":1}' >/dev/null

  curl_json "04.01 Admin dashboard [pass $pass]" "200" GET "/api/admin/dashboard" "$admin_token" >/dev/null
  curl_json "04.02 Admin lists users [pass $pass]" "200" GET "/api/admin/users" "$admin_token" >/dev/null
  created_user_file="$(curl_json "04.03 Admin creates user [pass $pass]" "201" POST "/api/admin/users" "$admin_token" "{\"username\":\"curlop${suffix}\",\"email\":\"curlop${suffix}@vendnet.io\",\"password\":\"Operator@123456\",\"fullName\":\"Curl Operator ${pass}\",\"role\":\"ROLE_OPERATOR\"}")"
  created_user_id="$(json_value "$created_user_file" 'data.id')"
  require_value created_user_id "$created_user_id"
  curl_json "04.04 Admin updates user [pass $pass]" "200" PUT "/api/admin/users/$created_user_id" "$admin_token" '{"name":"Curl Operator Updated","role":"ROLE_OPERATOR","accountStatus":"ACTIVE"}' >/dev/null
  curl_json "04.05 Admin creates machine [pass $pass]" "201" POST "/api/machines" "$admin_token" "{\"code\":\"VM-CURL-${suffix}\",\"location\":\"Curl Smoke Location ${pass}\"}" >/dev/null
  curl_json "04.06 Admin updates product [pass $pass]" "200" PUT "/api/products/$product_id" "$admin_token" '{"name":"Water 500ml","description":"Natural mineral water, 500ml bottle","price":1.00,"active":true}' >/dev/null
  curl_form_product "04.07 Admin creates product with image [pass $pass]" "201" "$admin_token" "CURL-${suffix}" >/dev/null
  curl_json "04.08 Admin generates sales report [pass $pass]" "200" POST "/api/admin/operations/reports/sales" "$admin_token" "" >/dev/null
  curl_json "04.09 Admin triggers backup [pass $pass]" "201" POST "/api/admin/backups" "$admin_token" "" >/dev/null

  now_iso="$(date -u +"%Y-%m-%dT%H:%M:%S")"
  telemetry_body="{\"serialNumber\":\"VM-LIS-001\",\"temperature\":22.5,\"stockLevels\":{\"A1\":10,\"A2\":12,\"B1\":8},\"statusCode\":\"ONLINE\",\"errorCodes\":[],\"timestamp\":\"$now_iso\"}"
  curl_json "05.01 Valid telemetry [pass $pass]" "200" POST "/api/machines/telemetry" "" "$telemetry_body" "X-Machine-CN: VM-LIS-001" >/dev/null
  curl_json "05.02 Unknown machine telemetry rejected [pass $pass]" "403" POST "/api/machines/telemetry" "" "{\"serialNumber\":\"VM-UNKNOWN\",\"temperature\":22.5,\"stockLevels\":{\"A1\":10},\"statusCode\":\"ONLINE\",\"errorCodes\":[],\"timestamp\":\"$now_iso\"}" "X-Machine-CN: VM-UNKNOWN" >/dev/null

  webhook_body="{\"saleId\":\"NEWMAN-SALE\",\"status\":\"COMPLETED\"}"
  webhook_sig="$(hmac_sha256 "$webhook_body")"
  curl_json "05.03 Webhook missing signature rejected [pass $pass]" "401" POST "/api/webhooks/payment" "" "$webhook_body" >/dev/null
  curl_json "05.04 Webhook valid signature accepted [pass $pass]" "200" POST "/api/webhooks/payment" "" "$webhook_body" "X-Payment-Signature: $webhook_sig" >/dev/null
  curl_json "05.05 Webhook tampered body rejected [pass $pass]" "401" POST "/api/webhooks/payment" "" '{"saleId":"NEWMAN-SALE","status":"FAILED"}' "X-Payment-Signature: $webhook_sig" >/dev/null

  curl_json "06.01 Anonymous current user rejected [pass $pass]" "401" GET "/api/auth/me" "" >/dev/null
  curl_json "06.02 Invalid JWT rejected [pass $pass]" "401" GET "/api/products" "invalid.jwt.token" >/dev/null
  curl_json "06.03 Customer admin dashboard rejected [pass $pass]" "403" GET "/api/admin/dashboard" "$customer_token" >/dev/null
  curl_json "06.04 Operator admin dashboard rejected [pass $pass]" "403" GET "/api/admin/dashboard" "$operator_token" >/dev/null
  curl_json "06.05 Customer machine sales rejected [pass $pass]" "403" GET "/api/sales/machine/$machine_id" "$customer_token" >/dev/null
  curl_json "06.06 OpenAPI docs [pass $pass]" "200" GET "/v3/api-docs" "" >/dev/null
  curl_json "06.07 Swagger UI [pass $pass]" "200" GET "/swagger-ui/index.html" "" >/dev/null
  curl_text "06.08 Prometheus metrics [pass $pass]" "200" "/actuator/prometheus" >/dev/null
done

printf "\nCurl smoke complete: %s requests, %s failures\n" "$total" "$failed"
if [[ "$failed" -ne 0 ]]; then
  exit 1
fi
