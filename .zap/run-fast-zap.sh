#!/usr/bin/env sh
set -eu

APP_URL="${APP_URL:-http://localhost:8080}"
REPORT_DIR="/zap/wrk/reports"
REPORT_REL_DIR="reports"
RULES_FILE="${RULES_FILE:-rules.tsv}"
SCAN_ZAP_PORT="${SCAN_ZAP_PORT:-9090}"
RBAC_ZAP_PORT="${RBAC_ZAP_PORT:-9091}"
ZAP_PROXY="http://127.0.0.1:${RBAC_ZAP_PORT}"

mkdir -p "${REPORT_DIR}"

zap-baseline.py \
  -t "${APP_URL}" \
  -c "${RULES_FILE}" \
  -r "${REPORT_REL_DIR}/zap-baseline.html" \
  -w "${REPORT_REL_DIR}/zap-baseline.md" \
  -x "${REPORT_REL_DIR}/zap-baseline.xml" \
  -z "-config proxy.port=${SCAN_ZAP_PORT}"

zap-api-scan.py \
  -t "${APP_URL}/v3/api-docs" \
  -f openapi \
  -c "${RULES_FILE}" \
  -r "${REPORT_REL_DIR}/zap-api-admin.html" \
  -w "${REPORT_REL_DIR}/zap-api-admin.md" \
  -x "${REPORT_REL_DIR}/zap-api-admin.xml" \
  -z "-config proxy.port=${SCAN_ZAP_PORT} \
      -config replacer.full_list[0].description=admin_auth \
      -config replacer.full_list[0].enabled=true \
      -config replacer.full_list[0].matchtype=REQ_HEADER \
      -config replacer.full_list[0].matchstr=Authorization \
      -config replacer.full_list[0].regex=false \
      -config replacer.full_list[0].replacement=Bearer ${ADMIN_TOKEN}"

zap.sh -daemon -host 127.0.0.1 -port "${RBAC_ZAP_PORT}" -dir /tmp/zap-rbac-home -config api.disablekey=true >/tmp/zap-rbac.log 2>&1 &
ZAP_PID="$!"
trap 'kill "${ZAP_PID}" 2>/dev/null || true' EXIT

i=0
while [ "${i}" -lt 30 ]; do
  if curl -fsS "${ZAP_PROXY}/JSON/core/view/version/" >/dev/null 2>&1; then
    break
  fi
  i=$((i + 1))
  sleep 1
done

if ! curl -fsS "${ZAP_PROXY}/JSON/core/view/version/" >/dev/null 2>&1; then
  echo "ZAP RBAC daemon did not become ready"
  cat /tmp/zap-rbac.log 2>/dev/null || true
  exit 1
fi

RBAC_REPORT="${REPORT_DIR}/zap-rbac.md"
printf '# ZAP RBAC Proxy Checks\n\n' > "${RBAC_REPORT}"
printf '| Role | Method | Path | Expected | Actual | Result |\n' >> "${RBAC_REPORT}"
printf '|------|--------|------|----------|--------|--------|\n' >> "${RBAC_REPORT}"

FAILED=0

check_rbac() {
  role="$1"
  token="$2"
  method="$3"
  path="$4"
  expected="$5"
  body="/tmp/zap-rbac-${role}.json"

  if [ "${token}" = "-" ]; then
    actual=$(curl -sS --noproxy "" -x "${ZAP_PROXY}" -o "${body}" -w "%{http_code}" \
      -X "${method}" "${APP_URL}${path}")
  else
    actual=$(curl -sS --noproxy "" -x "${ZAP_PROXY}" -o "${body}" -w "%{http_code}" \
      -X "${method}" "${APP_URL}${path}" \
      -H "Authorization: Bearer ${token}")
  fi

  if [ "${actual}" = "${expected}" ]; then
    result="PASS"
  else
    result="FAIL"
    FAILED=1
  fi

  printf '| %s | %s | `%s` | %s | %s | %s |\n' \
    "${role}" "${method}" "${path}" "${expected}" "${actual}" "${result}" >> "${RBAC_REPORT}"
  echo "RBAC ${role} ${method} ${path}: expected ${expected}, got ${actual} (${result})"
}

check_rbac "anonymous" "-"                 "GET"  "/api/admin/dashboard"  "401"
check_rbac "admin"     "${ADMIN_TOKEN}"    "GET"  "/api/admin/dashboard"  "200"
check_rbac "admin"     "${ADMIN_TOKEN}"    "GET"  "/api/admin/users"      "200"
check_rbac "admin"     "${ADMIN_TOKEN}"    "POST" "/api/admin/backups"    "201"
check_rbac "operator"  "${OPERATOR_TOKEN}" "GET"  "/api/admin/dashboard"  "403"
check_rbac "operator"  "${OPERATOR_TOKEN}" "GET"  "/api/machines/1/slots" "200"
check_rbac "operator"  "${OPERATOR_TOKEN}" "GET"  "/api/sales/machine/1"  "200"
check_rbac "customer"  "${CUSTOMER_TOKEN}" "GET"  "/api/admin/dashboard"  "403"
check_rbac "customer"  "${CUSTOMER_TOKEN}" "GET"  "/api/auth/me"          "200"
check_rbac "customer"  "${CUSTOMER_TOKEN}" "GET"  "/api/products"         "200"
check_rbac "customer"  "${CUSTOMER_TOKEN}" "GET"  "/api/machines"         "200"

if [ "${FAILED}" -ne 0 ]; then
  echo "One or more ZAP RBAC checks failed. See ${RBAC_REPORT}."
  exit 1
fi

echo "ZAP RBAC checks passed. Report: ${RBAC_REPORT}"
