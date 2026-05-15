.PHONY: help certs setup db-up db-down obs-up obs-down up down logs restart clean run stop \
        build test verify coverage sast sca pipeline all swagger metrics api-test \
        grafana prometheus jaeger archunit abuse-tests integration-test \
        sbom enforcer docker-build docker-scan secret-scan

MVN = ./mvnw
DC = docker compose -f ../docker-compose.yml
APP_PORT = 8080

## ===== HELP =====
help:
	@echo "VendNet Makefile"
	@echo ""
	@echo "=== SETUP ==="
	@echo "  make certs         Generate mTLS certificates (CA, server, machine client)"
	@echo "  make setup         First-time setup: certs + Docker services"
	@echo ""
	@echo "=== INFRA ==="
	@echo "  make db-up         Start MySQL only"
	@echo "  make db-down       Stop MySQL only"
	@echo "  make obs-up        Start observability (Prometheus+Grafana+Jaeger+Loki+Promtail)"
	@echo "  make obs-down      Stop observability stack"
	@echo "  make up            Start ALL Docker services"
	@echo "  make down          Stop ALL Docker services"
	@echo "  make logs          Tail all Docker logs"
	@echo "  make db-logs       Tail MySQL logs"
	@echo ""
	@echo "=== APP ==="
	@echo "  make run           Start Docker services + Spring Boot app"
	@echo "  make stop          Stop app + Docker services"
	@echo "  make swagger       Open Swagger UI (http://localhost:$(APP_PORT)/swagger-ui.html)"
	@echo "  make grafana       Open Grafana (http://localhost:3000, admin/admin)"
	@echo "  make prometheus    Open Prometheus (http://localhost:9090)"
	@echo "  make jaeger        Open Jaeger (http://localhost:16686)"
	@echo "  make metrics       Show Prometheus metrics endpoint"
	@echo "  make api-test      Quick smoke test of all API endpoints"
	@echo ""
	@echo "=== CI/CD ==="
	@echo "  make build         Compile project"
	@echo "  make test          Run tests (H2 in-memory)"
	@echo "  make verify        verify phase (build + test + coverage)"
	@echo "  make coverage      JaCoCo coverage report"
	@echo "  make sast          SAST: SpotBugs + FindSecBugs"
	@echo "  make sca           SCA: OWASP Dependency Check"
	@echo "  make pipeline      Full CI pipeline: clean + sast + sca + verify"
	@echo "  make all           pipeline + docker-up"
	@echo "  make archunit      Run ArchUnit architecture tests"
	@echo "  make abuse-tests   Run abuse case regression tests"
	@echo "  make integration-test  Run integration tests (Testcontainers)"
	@echo "  make sbom          Generate CycloneDX SBOM"
	@echo "  make enforcer      Check Maven dependency version rules"
	@echo "  make docker-build  Build Docker image"
	@echo "  make docker-scan   Build and scan Docker image with Trivy"
	@echo "  make secret-scan   Scan for secrets with gitleaks"
	@echo ""
	@echo "=== UTILS ==="
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
	$(DC) up -d prometheus grafana jaeger loki promtail
	@echo ""
	@echo "Observability stack ready:"
	@echo "  Prometheus : http://localhost:9090"
	@echo "  Grafana    : http://localhost:3000 (admin/admin)"
	@echo "  Jaeger     : http://localhost:16686"
	@echo "  Loki       : http://localhost:3100"

obs-down:
	$(DC) down prometheus grafana jaeger loki promtail

up: db-up obs-up
	@echo ""
	@echo "All services running."

down:
	$(DC) down

logs:
	$(DC) logs -f

db-logs:
	$(DC) logs -f mysql

metrics:
	@curl -s http://localhost:$(APP_PORT)/actuator/prometheus 2>/dev/null | head -20 || echo "App not running. Start with 'make run'."

## ===== APP =====
run: db-up
	@mkdir -p logs data
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

jaeger:
	@open http://localhost:16686 2>/dev/null || echo "Open http://localhost:16686"

api-test:
	@echo "=== VendNet API Smoke Test ==="
	@echo ""
	@echo "--- 1. Public endpoint (no auth) ---"
	@curl -sf http://localhost:$(APP_PORT)/api/public/info | python3 -m json.tool 2>/dev/null || echo "App not running"
	@echo ""
	@echo "--- 2. Register user ---"
	@curl -sf -X POST http://localhost:$(APP_PORT)/api/auth/register \
		-H "Content-Type: application/json" \
		-d '{"email":"test@vendnet.io","password":"secret123","name":"Test User"}' | python3 -m json.tool 2>/dev/null || echo "Registration failed"
	@echo ""
	@echo "--- 3. Login ---"
	@TOKEN=$$(curl -s -X POST http://localhost:$(APP_PORT)/api/auth/login \
		-H "Content-Type: application/json" \
		-d '{"email":"test@vendnet.io","password":"secret123"}' | python3 -c "import sys,json; print(json.load(sys.stdin)['token'])" 2>/dev/null); \
	if [ -n "$$TOKEN" ]; then \
		echo "Token obtained: $${TOKEN:0:40}..."; \
		echo ""; \
		echo "--- 4. /me (isAuthenticated) ---"; \
		curl -sf http://localhost:$(APP_PORT)/api/auth/me -H "Authorization: Bearer $$TOKEN" | python3 -m json.tool 2>/dev/null; \
		echo ""; \
		echo "--- 5. /claims (isAuthenticated) ---"; \
		curl -sf http://localhost:$(APP_PORT)/api/auth/claims -H "Authorization: Bearer $$TOKEN" | python3 -m json.tool 2>/dev/null; \
		echo ""; \
		echo "--- 6. /products (isAuthenticated) ---"; \
		curl -sf http://localhost:$(APP_PORT)/api/products -H "Authorization: Bearer $$TOKEN" | python3 -m json.tool 2>/dev/null; \
		echo ""; \
		echo "--- 7. /machines (hasRole USER/ADMIN) ---"; \
		curl -sf http://localhost:$(APP_PORT)/api/machines -H "Authorization: Bearer $$TOKEN" | python3 -m json.tool 2>/dev/null; \
		echo ""; \
		echo "--- 8. /admin/dashboard (hasRole ADMIN, expect 403) ---"; \
		HTTP_CODE=$$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$(APP_PORT)/api/admin/dashboard -H "Authorization: Bearer $$TOKEN"); \
		if [ "$$HTTP_CODE" = "403" ]; then echo "Correctly denied (403) for ROLE_USER"; else echo "Unexpected response: $$HTTP_CODE"; fi; \
		echo ""; \
		echo "--- 9. Health check ---"; \
		curl -sf http://localhost:$(APP_PORT)/actuator/health | python3 -m json.tool 2>/dev/null; \
	else \
		echo "Login failed - app may not be running"; \
	fi; \
	echo ""; \
	echo "=== Done ==="

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

archunit:
	$(MVN) test -Dtest="*ArchitectureTest,*ArchTest"
	@echo "ArchUnit architecture tests completed."

abuse-tests:
	$(MVN) test -Dtest="*AbuseCase*"
	@echo "Abuse case regression tests completed."

integration-test:
	$(MVN) verify -P integration-test
	@echo "Integration tests completed."

sbom:
	$(MVN) package -DskipTests
	@echo "SBOM generated: target/vendnet-sbom.json"

enforcer:
	$(MVN) validate
	@echo "Maven enforcer rules checked."

docker-build:
	docker build -t vendnet:latest .
	@echo "Docker image built: vendnet:latest"

docker-scan:
	docker build -t vendnet:latest .
	trivy image --severity CRITICAL,HIGH vendnet:latest
	@echo "Docker image scan completed."

secret-scan:
	gitleaks detect --verbose
	@echo "Secret scan completed."

## ===== UTILS =====
clean:
	$(MVN) clean
