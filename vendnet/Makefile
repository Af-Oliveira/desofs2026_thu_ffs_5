.PHONY: help certs setup \
        db-up db-down obs-up obs-down up down restart \
        logs db-logs app-logs loki-logs obs-logs \
        run stop debug ping \
        lint format format-check lint-fix \
        swagger grafana prometheus jaeger \
        metrics traces status urls \
        api-test api-test-full \
        build test verify coverage sast sca pipeline all \
        db-reset db-shell \
        clean kill \
        archunit abuse-tests integration-test \
        sbom enforcer docker-build docker-scan secret-scan

MVN = ./mvnw
DC = docker compose -f ../docker-compose.yml
APP_PORT = 8080

# --- Colors ---
C_RESET = \033[0m
C_BOLD  = \033[1m
C_DIM   = \033[2m
C_RED   = \033[31m
C_GREEN = \033[32m
C_YELLOW= \033[33m
C_BLUE  = \033[34m
C_MAG   = \033[35m
C_CYAN  = \033[36m
C_WHITE = \033[37m
C_OK    = $(C_GREEN)✓$(C_RESET)
C_FAIL  = $(C_RED)✗$(C_RESET)
C_ARROW = $(C_BLUE)→$(C_RESET)

## ======================================================================
##  HELP
## ======================================================================
help:
	@printf "$(C_BOLD)$(C_CYAN)\n"
	@printf "  VendNet Makefile\n"
	@printf "$(C_RESET)\n"
	@printf "$(C_BOLD)═══ SETUP ═══$(C_RESET)\n"
	@printf "  $(C_GREEN)make certs$(C_RESET)          Generate mTLS certificates\n"
	@printf "  $(C_GREEN)make setup$(C_RESET)          First-time: certs + MySQL\n"
	@printf "  $(C_GREEN)make db-reset$(C_RESET)       Drop + recreate vendnet database\n"
	@printf "  $(C_GREEN)make db-shell$(C_RESET)       Open MySQL shell\n"
	@printf "\n"
	@printf "$(C_BOLD)═══ INFRA ═══$(C_RESET)\n"
	@printf "  $(C_GREEN)make up$(C_RESET)             Start ALL services (MySQL + obs stack)\n"
	@printf "  $(C_GREEN)make down$(C_RESET)           Stop ALL services\n"
	@printf "  $(C_GREEN)make db-up$(C_RESET)          Start MySQL only\n"
	@printf "  $(C_GREEN)make obs-up$(C_RESET)         Start Prometheus + Grafana + Jaeger + Loki + Promtail\n"
	@printf "  $(C_GREEN)make objs-down$(C_RESET)      Stop observability stack\n"
	@printf "  $(C_GREEN)make restart$(C_RESET)        Restart all Docker services\n"
	@printf "\n"
	@printf "$(C_BOLD)═══ APP ═══$(C_RESET)\n"
	@printf "  $(C_GREEN)make run$(C_RESET)            Start MySQL + Spring Boot (foreground)\n"
	@printf "  $(C_GREEN)make debug$(C_RESET)          Start app with debug port 5005\n"
	@printf "  $(C_GREEN)make ping$(C_RESET)           Ping the health endpoint (no auth)\n"
	@printf "  $(C_GREEN)make stop$(C_RESET)           Kill app + stop all Docker\n"
	@printf "  $(C_GREEN)make kill$(C_RESET)           Force-kill port $(APP_PORT)\n"
	@printf "\n"
	@printf "$(C_BOLD)═══ LOGS ═══$(C_RESET)\n"
	@printf "  $(C_GREEN)make logs$(C_RESET)           Tail ALL Docker logs\n"
	@printf "  $(C_GREEN)make db-logs$(C_RESET)        Tail MySQL logs\n"
	@printf "  $(C_GREEN)make app-logs$(C_RESET)       Tail app JSON log file\n"
	@printf "  $(C_GREEN)make obs-logs$(C_RESET)       Tail observability containers\n"
	@printf "  $(C_GREEN)make loki-logs$(C_RESET)      Query recent logs from Loki\n"
	@printf "\n"
	@printf "$(C_BOLD)═══ STATUS ═══$(C_RESET)\n"
	@printf "  $(C_GREEN)make status$(C_RESET)         Health-check ALL services + endpoints\n"
	@printf "  $(C_GREEN)make urls$(C_RESET)           Print all service URLs\n"
	@printf "\n"
	@printf "$(C_BOLD)═══ MONITORING ═══$(C_RESET)\n"
	@printf "  $(C_GREEN)make grafana$(C_RESET)        Open Grafana (http://localhost:3000)\n"
	@printf "  $(C_GREEN)make prometheus$(C_RESET)     Open Prometheus (http://localhost:9090)\n"
	@printf "  $(C_GREEN)make jaeger$(C_RESET)         Open Jaeger (http://localhost:16686)\n"
	@printf "  $(C_GREEN)make swagger$(C_RESET)        Open Swagger UI\n"
	@printf "  $(C_GREEN)make metrics$(C_RESET)        Show raw Prometheus metrics\n"
	@printf "  $(C_GREEN)make traces$(C_RESET)         Show recent Jaeger traces\n"
	@printf "\n"
	@printf "$(C_BOLD)═══ TEST ═══$(C_RESET)\n"
	@printf "  $(C_GREEN)make api-test$(C_RESET)       Smoke test: register + login + all endpoints\n"
	@printf "  $(C_GREEN)make api-test-full$(C_RESET)  Full test: 50 requests for traffic gen\n"
	@printf "\n"
	@printf "$(C_BOLD)═══ LINT ═══$(C_RESET)\n"
	@printf "  $(C_GREEN)make lint$(C_RESET)           Run Checkstyle + PMD (warnings & errors)\n"
	@printf "  $(C_GREEN)make format$(C_RESET)         Auto-format code with Spotless (Google AOSP)\n"
	@printf "  $(C_GREEN)make format-check$(C_RESET)   Check formatting without changing files\n"
	@printf "  $(C_GREEN)make lint-fix$(C_RESET)       Format + lint (iterate until clean)\n"
	@printf "\n"
	@printf "$(C_BOLD)═══ CI/CD ═══$(C_RESET)\n"
	@printf "  $(C_GREEN)make build$(C_RESET)          Compile\n"
	@printf "  $(C_GREEN)make test$(C_RESET)           Run tests (H2)\n"
	@printf "  $(C_GREEN)make verify$(C_RESET)         Build + test + coverage\n"
	@printf "  $(C_GREEN)make coverage$(C_RESET)       JaCoCo report\n"
	@printf "  $(C_GREEN)make sast$(C_RESET)           SpotBugs + FindSecBugs\n"
	@printf "  $(C_GREEN)make sca$(C_RESET)            OWASP Dependency Check\n"
	@printf "  $(C_GREEN)make pipeline$(C_RESET)       Full CI: clean + sast + sca + verify\n"
	@printf "  $(C_GREEN)make all$(C_RESET)            up + pipeline\n"
	@printf "  $(C_GREEN)make archunit$(C_RESET)       Run ArchUnit architecture tests\n"
	@printf "  $(C_GREEN)make abuse-tests$(C_RESET)    Run abuse case regression tests\n"
	@printf "  $(C_GREEN)make integration-test$(C_RESET) Run integration tests (Testcontainers)\n"
	@printf "  $(C_GREEN)make sbom$(C_RESET)           Generate CycloneDX SBOM\n"
	@printf "  $(C_GREEN)make enforcer$(C_RESET)       Check Maven dependency version rules\n"
	@printf "  $(C_GREEN)make docker-build$(C_RESET)   Build Docker image\n"
	@printf "  $(C_GREEN)make docker-scan$(C_RESET)    Build and scan Docker image with Trivy\n"
	@printf "  $(C_GREEN)make secret-scan$(C_RESET)    Scan for secrets with gitleaks\n"
	@printf "\n"
	@printf "$(C_BOLD)═══ UTILS ═══$(C_RESET)\n"
	@printf "  $(C_GREEN)make clean$(C_RESET)          mvn clean\n"
	@printf "\n"

## ======================================================================
##  SETUP
## ======================================================================
certs:
	@chmod +x ../docker/certs/generate-certs.sh
	@../docker/certs/generate-certs.sh
	@printf "$(C_OK) Certificates generated\n"

setup: certs db-up
	@printf "\n$(C_OK) Setup complete. Run 'make run' to start.\n"

db-reset:
	@$(DC) exec mysql mysql -u vendnet_user -pvendnet_pass -e "DROP DATABASE IF EXISTS vendnet; CREATE DATABASE vendnet;" 2>/dev/null
	@printf "$(C_OK) Database reset\n"

db-shell:
	@$(DC) exec mysql mysql -u vendnet_user -pvendnet_pass vendnet

## ======================================================================
##  INFRA
## ======================================================================
db-up:
	@printf "$(C_ARROW) Starting MySQL... "
	@$(DC) up -d mysql 2>/dev/null
	@until $(DC) exec mysql mysqladmin ping -h localhost --silent 2>/dev/null; do sleep 1; done
	@printf "$(C_OK) Ready (localhost:3306)\n"

db-down:
	@$(DC) down mysql 2>/dev/null; printf "$(C_OK) MySQL stopped\n"

obs-up:
	@printf "$(C_ARROW) Starting observability stack...\n"
	@$(DC) up -d prometheus grafana jaeger loki promtail 2>/dev/null
	@printf "  $(C_GREEN)Prometheus$(C_RESET)  http://localhost:9090\n"
	@printf "  $(C_GREEN)Grafana$(C_RESET)     http://localhost:3000 (admin/admin)\n"
	@printf "  $(C_GREEN)Jaeger$(C_RESET)      http://localhost:16686\n"
	@printf "  $(C_GREEN)Loki$(C_RESET)        http://localhost:3100\n"
	@printf "$(C_OK) Observability stack ready\n"

obs-down:
	@$(DC) down prometheus grafana jaeger loki promtail 2>/dev/null; printf "$(C_OK) Observability stopped\n"

up: db-up obs-up
	@printf "\n$(C_OK) All services running.\n"

down:
	@$(DC) down 2>/dev/null; printf "$(C_OK) All services stopped.\n"

restart: down up

logs:
	@$(DC) logs -f

db-logs:
	@$(DC) logs -f mysql

app-logs:
	@tail -f ../vendnet/logs/audit/vendnet.log

obs-logs:
	@$(DC) logs -f prometheus grafana jaeger loki promtail

loki-logs:
	@curl -s "http://localhost:3100/loki/api/v1/query_range" --data-urlencode 'query={job="vendnet"}' --data-urlencode 'limit=10' 2>/dev/null | python3 -c "import sys,json,datetime; d=json.load(sys.stdin); [print(f'  [{datetime.datetime.fromtimestamp(int(v[0])/1e9).strftime(\"%H:%M:%S\")}] {json.loads(v[1]).get(\"message\",v[1][:100])[:120]}') for stream in d.get('data',{}).get('result',[]) for v in stream.get('values',[])]" 2>/dev/null || printf "$(C_FAIL) Loki not running\n"

## ======================================================================
##  STATUS
## ======================================================================
ping:
	@printf "\n$(C_BOLD)Ping VendNet$(C_RESET)\n"
	@printf "$(C_ARROW) GET /api/health/ping -> "
	@curl -s http://localhost:$(APP_PORT)/api/health/ping | python3 -m json.tool 2>/dev/null || printf "$(C_FAIL) App not running\n"
	@printf "\n$(C_DIM)Now check: make grafana (dashboard), make jaeger (traces), make loki-logs (logs)$(C_RESET)\n"

status:
	@printf "\n$(C_BOLD)$(C_CYAN)  ════════════════════════════════════$(C_RESET)\n"
	@printf "$(C_BOLD)$(C_CYAN)    VendNet Service Status$(C_RESET)\n"
	@printf "$(C_BOLD)$(C_CYAN)  ════════════════════════════════════$(C_RESET)\n\n"
	@printf "$(C_BOLD)  Containers$(C_RESET)\n"
	@docker ps --format "    {{.Names}}  {{.Status}}" 2>/dev/null | grep desofs || printf "    $(C_DIM)(none)$(C_RESET)\n"
	@printf "\n$(C_BOLD)  Service URLs$(C_RESET)\n"
	@bash -c '\
	check() { NAME="$$1"; URL="$$2"; printf "    %-14s " "$$NAME"; CODE=$$(curl -s -o /dev/null -w "%{http_code}" --max-time 3 "$$URL" 2>/dev/null); if [ "$$CODE" = "200" ]; then printf "$(C_OK) UP\n"; else printf "$(C_FAIL) DOWN ($$CODE)\n"; fi; }; \
	check "App"         "http://localhost:$(APP_PORT)/actuator/health"; \
	check "Swagger"     "http://localhost:$(APP_PORT)/swagger-ui/index.html"; \
	check "Prometheus"  "http://localhost:9090/-/healthy"; \
	check "Grafana"     "http://localhost:3000/api/health"; \
	check "Jaeger"      "http://localhost:16686/api/services"; \
	check "Loki"        "http://localhost:3100/ready"'
	@printf "\n$(C_BOLD)  API$(C_RESET)\n"
	@printf "    GET  /api/health/ping      " && curl -sf http://localhost:$(APP_PORT)/api/health/ping > /dev/null && printf "$(C_OK) UP\n" || printf "$(C_FAIL) DOWN\n"
	@printf "    GET  /api/public/info       " && curl -sf http://localhost:$(APP_PORT)/api/public/info > /dev/null && printf "$(C_OK) UP\n" || printf "$(C_FAIL) DOWN\n"
	@printf "\n"

urls:
	@printf "\n$(C_BOLD)  VendNet URLs$(C_RESET)\n\n"
	@printf "  $(C_GREEN)App$(C_RESET)         http://localhost:$(APP_PORT)\n"
	@printf "  $(C_GREEN)Swagger$(C_RESET)     http://localhost:$(APP_PORT)/swagger-ui/index.html\n"
	@printf "  $(C_GREEN)Ping$(C_RESET)        http://localhost:$(APP_PORT)/api/health/ping\n"
	@printf "  $(C_GREEN)Health$(C_RESET)      http://localhost:$(APP_PORT)/actuator/health\n"
	@printf "  $(C_GREEN)Metrics$(C_RESET)     http://localhost:$(APP_PORT)/actuator/prometheus\n"
	@printf "\n"
	@printf "  $(C_GREEN)Grafana$(C_RESET)     http://localhost:3000  (admin / admin)\n"
	@printf "  $(C_GREEN)Prometheus$(C_RESET)  http://localhost:9090\n"
	@printf "  $(C_GREEN)Jaeger$(C_RESET)      http://localhost:16686\n"
	@printf "  $(C_GREEN)Loki$(C_RESET)        http://localhost:3100\n"
	@printf "\n"

## ======================================================================
##  APP
## ======================================================================
run: db-up
	@mkdir -p logs data
	@printf "$(C_ARROW) Starting VendNet on http://localhost:$(APP_PORT)...\n"
	@$(MVN) spring-boot:run -Dspring-boot.run.profiles=dev

debug: db-up
	@mkdir -p logs data
	@printf "$(C_ARROW) Starting VendNet (debug port 5005)...\n"
	@$(MVN) spring-boot:run -Dspring-boot.run.profiles=dev -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

stop:
	@lsof -ti:$(APP_PORT) | xargs kill -9 2>/dev/null || true
	@$(DC) down 2>/dev/null || true
	@printf "$(C_OK) Stopped.\n"

kill:
	@lsof -ti:$(APP_PORT) | xargs kill -9 2>/dev/null && printf "$(C_OK) Killed port $(APP_PORT)\n" || printf "$(C_DIM)Nothing on port $(APP_PORT)$(C_RESET)\n"

swagger:
	@open http://localhost:$(APP_PORT)/swagger-ui/index.html 2>/dev/null || printf "  http://localhost:$(APP_PORT)/swagger-ui/index.html\n"

grafana:
	@open http://localhost:3000 2>/dev/null || printf "  http://localhost:3000 (admin/admin)\n"

prometheus:
	@open http://localhost:9090 2>/dev/null || printf "  http://localhost:9090\n"

jaeger:
	@open http://localhost:16686 2>/dev/null || printf "  http://localhost:16686\n"

metrics:
	@curl -s http://localhost:$(APP_PORT)/actuator/prometheus 2>/dev/null | head -40 || printf "$(C_FAIL) App not running.\n"

traces:
	@printf "$(C_BOLD)Recent Jaeger traces:$(C_RESET)\n"
	@curl -s "http://localhost:16686/api/traces?service=vendnet&limit=5" 2>/dev/null | python3 -c "import sys,json; d=json.load(sys.stdin); [print('  trace %s... %d spans %d us' % (tr['traceID'][:16], len(tr.get('spans',[])), sum(s['duration'] for s in tr.get('spans',[])))) for tr in d.get('data',[])[:5]]" 2>/dev/null || printf "$(C_FAIL) Jaeger not running.\n"

## ======================================================================
##  TEST
## ======================================================================
api-test:
	@printf "\n$(C_BOLD)$(C_CYAN)  ═══ VendNet API Smoke Test ═══$(C_RESET)\n\n"
	@printf "  [1] /api/health/ping             " && curl -sf http://localhost:$(APP_PORT)/api/health/ping > /dev/null && printf "$(C_OK) 200\n" || printf "$(C_FAIL) DOWN\n"
	@printf "  [2] /api/public/info             " && curl -sf http://localhost:$(APP_PORT)/api/public/info > /dev/null && printf "$(C_OK) 200\n" || printf "$(C_FAIL) DOWN\n"
	@printf "  [3] POST /api/auth/register      " && curl -sf -X POST http://localhost:$(APP_PORT)/api/auth/register -H "Content-Type: application/json" -d '{"email":"test@vendnet.io","password":"test123","name":"Test"}' > /dev/null && printf "$(C_OK) 200\n" || printf "$(C_FAIL) Fail\n"
	@TOKEN=$$(curl -s -X POST http://localhost:$(APP_PORT)/api/auth/login -H "Content-Type: application/json" -d '{"email":"test@vendnet.io","password":"test123"}' | python3 -c "import sys,json; print(json.load(sys.stdin).get('token',''))" 2>/dev/null); \
	if [ -n "$$TOKEN" ]; then \
		printf "  [4] GET /api/auth/me             " && curl -sf http://localhost:$(APP_PORT)/api/auth/me -H "Authorization: Bearer $$TOKEN" > /dev/null && printf "$(C_OK) 200\n" || printf "$(C_FAIL) Fail\n"; \
		printf "  [5] GET /api/auth/claims         " && curl -sf http://localhost:$(APP_PORT)/api/auth/claims -H "Authorization: Bearer $$TOKEN" > /dev/null && printf "$(C_OK) 200\n" || printf "$(C_FAIL) Fail\n"; \
		printf "  [6] GET /api/products            " && curl -sf http://localhost:$(APP_PORT)/api/products -H "Authorization: Bearer $$TOKEN" > /dev/null && printf "$(C_OK) 200\n" || printf "$(C_FAIL) Fail\n"; \
		printf "  [7] GET /api/machines            " && curl -sf http://localhost:$(APP_PORT)/api/machines -H "Authorization: Bearer $$TOKEN" > /dev/null && printf "$(C_OK) 200\n" || printf "$(C_FAIL) Fail\n"; \
		printf "  [8] GET /api/admin/dashboard     " && CODE=$$(curl -s -o /dev/null -w '%{http_code}' http://localhost:$(APP_PORT)/api/admin/dashboard -H "Authorization: Bearer $$TOKEN"); if [ "$$CODE" = "403" ]; then printf "$(C_OK) 403 (RBAC working)\n"; else printf "$(C_FAIL) $$CODE\n"; fi; \
	else \
		printf "  $(C_FAIL) Login failed - is the app running?\n"; \
	fi
	@printf "  [9] GET /actuator/health         " && curl -sf http://localhost:$(APP_PORT)/actuator/health > /dev/null && printf "$(C_OK) 200\n" || printf "$(C_FAIL) DOWN\n"
	@printf "  [A] GET /swagger-ui              " && curl -sf http://localhost:$(APP_PORT)/swagger-ui/index.html > /dev/null && printf "$(C_OK) 200\n" || printf "$(C_FAIL) DOWN\n"
	@printf "\n$(C_OK) Done.\n\n"

api-test-full:
	@printf "\n$(C_BOLD)  ═══ Full API Test (generating traffic) ═══$(C_RESET)\n"
	@TOKEN=$$(curl -s -X POST http://localhost:$(APP_PORT)/api/auth/login -H "Content-Type: application/json" -d '{"email":"test@vendnet.io","password":"test123"}' | python3 -c "import sys,json; print(json.load(sys.stdin).get('token',''))" 2>/dev/null); \
	if [ -z "$$TOKEN" ]; then \
		curl -s -X POST http://localhost:$(APP_PORT)/api/auth/register -H "Content-Type: application/json" -d '{"email":"test@vendnet.io","password":"test123","name":"Test"}' > /dev/null; \
		TOKEN=$$(curl -s -X POST http://localhost:$(APP_PORT)/api/auth/login -H "Content-Type: application/json" -d '{"email":"test@vendnet.io","password":"test123"}' | python3 -c "import sys,json; print(json.load(sys.stdin).get('token',''))" 2>/dev/null); \
	fi; \
	if [ -z "$$TOKEN" ]; then printf "$(C_FAIL) Cannot authenticate\n"; exit 1; fi; \
	printf "  Generating 55 requests...\n"; \
	for i in 1 2 3 4 5 6 7 8 9 10; do \
		curl -s http://localhost:$(APP_PORT)/api/health/ping > /dev/null & \
		curl -s http://localhost:$(APP_PORT)/api/auth/me -H "Authorization: Bearer $$TOKEN" > /dev/null & \
		curl -s http://localhost:$(APP_PORT)/api/products -H "Authorization: Bearer $$TOKEN" > /dev/null & \
		curl -s http://localhost:$(APP_PORT)/api/machines -H "Authorization: Bearer $$TOKEN" > /dev/null & \
		curl -s http://localhost:$(APP_PORT)/api/public/info > /dev/null & \
		curl -s http://localhost:$(APP_PORT)/api/auth/claims -H "Authorization: Bearer $$TOKEN" > /dev/null & \
	done; wait; \
	printf "  $(C_OK) Done. Check:\n"; \
	printf "    $(C_GREEN)Grafana$(C_RESET) → VendNet Dashboard\n"; \
	printf "    $(C_GREEN)Jaeger$(C_RESET)  → vendnet service traces\n"; \
	printf "    $(C_GREEN)Loki$(C_RESET)    → app logs\n"; \
	printf "\n"

## ======================================================================
##  LINT & FORMAT
## ======================================================================
lint:
	@printf "\n$(C_BOLD)$(C_MAG)  ═══ Lint: Checkstyle + PMD ═══$(C_RESET)\n\n"
	@printf "$(C_BOLD)  Checkstyle (code style):$(C_RESET)\n"
	@$(MVN) checkstyle:check -q 2>&1 | grep -E "WARN|ERROR|BUILD|warn|error" || printf "  $(C_OK) 0 warnings\n"
	@printf "\n$(C_BOLD)  PMD (static analysis):$(C_RESET)\n"
	@$(MVN) pmd:check -q 2>&1 | grep -E "WARN|ERROR|violation|BUILD" || printf "  $(C_OK) 0 violations\n"
	@printf "\n$(C_OK) Lint complete. Run 'make format' to auto-fix style issues.\n"

format:
	@printf "$(C_ARROW) Formatting code with Spotless...\n"
	@$(MVN) spotless:apply -q
	@printf "$(C_OK) Code formatted.\n"

format-check:
	@printf "$(C_ARROW) Checking format...\n"
	@$(MVN) spotless:check -q && printf "$(C_OK) All files correctly formatted.\n" || printf "$(C_FAIL) Some files need formatting. Run 'make format'.\n"

lint-fix: format
	@make lint
	@printf "\n$(C_OK) Format applied + lint checked.\n"

## ======================================================================
##  CI/CD
## ======================================================================
build:
	@$(MVN) compile -q && printf "$(C_OK) Build OK\n"

test:
	@$(MVN) test

verify:
	@$(MVN) verify

coverage:
	@$(MVN) jacoco:report
	@printf "$(C_ARROW) Coverage report: target/site/jacoco/index.html\n"

sast:
	@$(MVN) spotbugs:check -Dspotbugs.effort=Max -Dspotbugs.threshold=Low

sca:
	@$(MVN) org.owasp:dependency-check-maven:check -Danalyzer.ossindex.enabled=false

pipeline: clean sast sca verify
	@printf "\n$(C_OK) CI Pipeline completed.\n"

all: up pipeline
	@printf "\n$(C_OK) Full pipeline + Docker completed.\n"

archunit:
	@$(MVN) test -Dtest="*ArchitectureTest,*ArchTest"
	@printf "$(C_OK) ArchUnit architecture tests completed.\n"

abuse-tests:
	@$(MVN) test -Dtest="*AbuseCase*"
	@printf "$(C_OK) Abuse case regression tests completed.\n"

integration-test:
	@$(MVN) verify -P integration-test
	@printf "$(C_OK) Integration tests completed.\n"

sbom:
	@$(MVN) package -DskipTests
	@printf "$(C_OK) SBOM generated: target/vendnet-sbom.json\n"

enforcer:
	@$(MVN) validate
	@printf "$(C_OK) Maven enforcer rules checked.\n"

docker-build:
	docker build -t vendnet:latest .
	@printf "$(C_OK) Docker image built: vendnet:latest\n"

docker-scan:
	docker build -t vendnet:latest .
	trivy image --severity CRITICAL,HIGH vendnet:latest
	@printf "$(C_OK) Docker image scan completed.\n"

secret-scan:
	gitleaks detect --verbose
	@printf "$(C_OK) Secret scan completed.\n"

## ======================================================================
##  UTILS
## ======================================================================
clean:
	@$(MVN) clean
	@printf "$(C_OK) target/ cleaned.\n"
