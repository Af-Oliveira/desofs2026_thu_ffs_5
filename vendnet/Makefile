.PHONY: help certs setup db-up db-down obs-up obs-down up down logs restart clean run stop \
        build test verify coverage sast sca pipeline all swagger metrics api-test

MVN = ./mvnw
DC = docker compose -f ../docker-compose.yml
APP_PORT = 8080

## ===== HELP =====
help:
	@echo "VendNet Makefile"
	@echo ""
	@echo "═══ SETUP ═══"
	@echo "  make certs         Generate mTLS certificates (CA, server, machine client)"
	@echo "  make setup         First-time setup: certs + Docker services"
	@echo ""
	@echo "═══ INFRA ═══"
	@echo "  make db-up         Start MySQL only"
	@echo "  make db-down       Stop MySQL only"
	@echo "  make obs-up        Start observability stack (Prometheus + Grafana + OTEL)"
	@echo "  make obs-down      Stop observability stack"
	@echo "  make up            Start ALL Docker services (MySQL + Prometheus + Grafana + OTEL)"
	@echo "  make down          Stop ALL Docker services"
	@echo "  make restart       Restart ALL Docker services"
	@echo "  make logs          Tail all Docker logs"
	@echo "  make db-logs       Tail MySQL logs"
	@echo "  make metrics       Show Prometheus metrics endpoint"
	@echo ""
	@echo "═══ APP ═══"
	@echo "  make run           Start docker services + run Spring Boot app"
	@echo "  make stop          Stop app + Docker services"
	@echo "  make swagger       Open Swagger UI (http://localhost:$(APP_PORT)/swagger-ui.html)"
	@echo "  make grafana       Open Grafana (http://localhost:3000, admin/admin)"
	@echo "  make prometheus    Open Prometheus (http://localhost:9090)"
	@echo "  make api-test      Quick smoke test of all API endpoints"
	@echo ""
	@echo "═══ CI/CD ═══"
	@echo "  make build         Compile project"
	@echo "  make test          Run tests (H2 in-memory)"
	@echo "  make verify        verify phase (build + test + coverage)"
	@echo "  make coverage      JaCoCo coverage report"
	@echo "  make sast          SAST: SpotBugs + FindSecBugs"
	@echo "  make sca           SCA: OWASP Dependency Check"
	@echo "  make pipeline      Full CI pipeline: clean + sast + sca + verify"
	@echo "  make all           pipeline + docker-up"
	@echo ""
	@echo "═══ UTILS ═══"
	@echo "  make clean         mvn clean (removes target/)"

## ===== SETUP =====
certs:
	@chmod +x ../docker/certs/generate-certs.sh
	@../docker/certs/generate-certs.sh

setup: certs db-up
	@echo ""
	@echo "Setup complete. Run 'make run' to start the application."

## ===== DOCKER INFRA =====
db-up:
	$(DC) up -d mysql
	@echo "Waiting for MySQL to be ready..."
	@until $(DC) exec mysql mysqladmin ping -h localhost --silent 2>/dev/null; do sleep 1; done
	@echo "MySQL is ready (localhost:3306)."

db-down:
	$(DC) down mysql

obs-up:
	$(DC) up -d prometheus grafana otel-collector
	@echo "Observability stack ready:"
	@echo "  Prometheus : http://localhost:9090"
	@echo "  Grafana    : http://localhost:3000 (admin/admin)"

obs-down:
	$(DC) down prometheus grafana otel-collector

up: db-up obs-up
	@echo ""
	@echo "All services running."

down:
	$(DC) down

restart: down up

logs:
	$(DC) logs -f

db-logs:
	$(DC) logs -f mysql

metrics:
	@curl -s http://localhost:$(APP_PORT)/actuator/prometheus 2>/dev/null || echo "App not running. Start with 'make run'."

## ===== APP =====
run: db-up
	@echo "Starting VendNet on http://localhost:$(APP_PORT)..."
	@$(MVN) spring-boot:run -Dspring-boot.run.profiles=dev

stop:
	@lsof -ti:$(APP_PORT) | xargs kill -9 2>/dev/null || true
	$(DC) down
	@echo "Stopped."

swagger:
	@open http://localhost:$(APP_PORT)/swagger-ui.html 2>/dev/null || echo "Open http://localhost:$(APP_PORT)/swagger-ui.html"

grafana:
	@open http://localhost:3000 2>/dev/null || echo "Open http://localhost:3000 (admin/admin)"

prometheus:
	@open http://localhost:9090 2>/dev/null || echo "Open http://localhost:9090"

api-test: up
	@echo "=== VendNet API Smoke Test ==="
	@echo ""
	@echo "--- 1. Public endpoint (no auth) ---"
	@curl -sf http://localhost:$(APP_PORT)/api/public/info | python3 -m json.tool
	@echo ""
	@echo "--- 2. Register user ---"
	@curl -sf -X POST http://localhost:$(APP_PORT)/api/auth/register \
		-H "Content-Type: application/json" \
		-d '{"email":"test@vendnet.io","password":"secret123","name":"Test User"}' | python3 -m json.tool
	@echo ""
	@echo "--- 3. Login ---"
	@TOKEN=$$(curl -s -X POST http://localhost:$(APP_PORT)/api/auth/login \
		-H "Content-Type: application/json" \
		-d '{"email":"test@vendnet.io","password":"secret123"}' | python3 -c "import sys,json; print(json.load(sys.stdin)['token'])"); \
	echo "Token: $${TOKEN:0:30}..."; \
	echo ""; \
	echo "--- 4. /me (isAuthenticated) ---"; \
	curl -sf http://localhost:$(APP_PORT)/api/auth/me -H "Authorization: Bearer $$TOKEN" | python3 -m json.tool; \
	echo ""; \
	echo "--- 5. /claims (isAuthenticated) ---"; \
	curl -sf http://localhost:$(APP_PORT)/api/auth/claims -H "Authorization: Bearer $$TOKEN" | python3 -m json.tool; \
	echo ""; \
	echo "--- 6. /products (isAuthenticated) ---"; \
	curl -sf http://localhost:$(APP_PORT)/api/products -H "Authorization: Bearer $$TOKEN" | python3 -m json.tool; \
	echo ""; \
	echo "--- 7. /machines (hasRole USER/ADMIN) ---"; \
	curl -sf http://localhost:$(APP_PORT)/api/machines -H "Authorization: Bearer $$TOKEN" | python3 -m json.tool; \
	echo ""; \
	echo "--- 8. /admin/dashboard (hasRole ADMIN -- should 403) ---"; \
	curl -s http://localhost:$(APP_PORT)/api/admin/dashboard -H "Authorization: Bearer $$TOKEN" | python3 -m json.tool; \
	echo ""; \
	echo "--- 9. Health check ---"; \
	curl -sf http://localhost:$(APP_PORT)/actuator/health | python3 -m json.tool; \
	echo ""; \
	echo "=== Smoke test complete ==="

## ===== CI/CD =====
build:
	$(MVN) compile -q

test:
	$(MVN) test

verify:
	$(MVN) verify

coverage:
	$(MVN) jacoco:report
	@echo "Coverage report: target/site/jacoco/index.html"

sast:
	$(MVN) spotbugs:check -Dspotbugs.effort=Max -Dspotbugs.threshold=Low

sca:
	$(MVN) org.owasp:dependency-check-maven:check

pipeline: clean sast sca verify
	@echo ""
	@echo "CI Pipeline completed."

all: up pipeline
	@echo ""
	@echo "Full pipeline + Docker completed."

## ===== UTILS =====
clean:
	$(MVN) clean
