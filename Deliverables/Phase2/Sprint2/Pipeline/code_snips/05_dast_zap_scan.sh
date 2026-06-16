#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# Stage 8 — DAST: OWASP ZAP (vendnet-ci-cd.yml, lines 789-928)
# Starts the application with H2 in-memory database, obtains JWT tokens
# for all RBAC roles, and runs ZAP baseline + authenticated OpenAPI
# scans with RBAC proxy checks.
# ─────────────────────────────────────────────────────────────────────────────

# ── Start VendNet for DAST ──
cd vendnet
JAR="$(find target -maxdepth 1 -name 'vendnet-*.jar' ! -name '*.original' | head -n 1)"
java -Dspring.profiles.active=zap,bootstrap -jar "${JAR}" \
    > /tmp/vendnet-dast.log 2>&1 &

# ── Wait for health ──
for i in $(seq 1 60); do
    if curl -s http://localhost:8080/actuator/health 2>/dev/null | grep -q '"status":"UP"'; then
        echo "✅ App healthy after $((i * 2))s"
        sleep 3
        break
    fi
    sleep 2
done

# ── Obtain JWT tokens for RBAC scans ──
for creds in "admin:Admin@123456:admin" "operator:Operator@123456:operator" "customer:Customer@123456:customer"; do
    USERNAME="${creds%%:*}"
    REST="${creds#*:}"
    PASS="${REST%%:*}"
    ROLE="${REST##*:}"
    for attempt in 1 2 3; do
        HTTP_CODE=$(curl -s -o /tmp/login-resp.json -w "%{http_code}" \
            -X POST http://localhost:8080/api/auth/login \
            -H "Content-Type: application/json" \
            -d "{\"username\":\"${USERNAME}\",\"password\":\"${PASS}\"}")
        if [ "${HTTP_CODE}" = "200" ]; then
            TOKEN=$(python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('accessToken') or d.get('token',''))" < /tmp/login-resp.json)
            echo "${TOKEN}" > ".zap/tokens/${ROLE}.jwt"
            echo "✅ Logged in as ${USERNAME} (${ROLE})"
            break
        fi
        sleep 2
    done
done

# ── Run ZAP scans ──
docker run --rm --network host \
    -e APP_URL="http://localhost:8080" \
    -e ADMIN_TOKEN="$(cat .zap/tokens/admin.jwt)" \
    -e OPERATOR_TOKEN="$(cat .zap/tokens/operator.jwt)" \
    -e CUSTOMER_TOKEN="$(cat .zap/tokens/customer.jwt)" \
    -v "${GITHUB_WORKSPACE}/.zap:/zap/wrk" \
    "${ZAP_IMAGE}" \
    sh /zap/wrk/run-fast-zap.sh
