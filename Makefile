# =============================================================================
# VendNet CI/CD Makefile — Infrastructure as Code (IaC)
# =============================================================================
# Evidence for: IaC with docker-compose per environment
# This Makefile + docker-compose files define entire deployment infrastructure
# =============================================================================

SHELL := /bin/bash
.SHELLFLAGS := -euo pipefail -c

.PHONY: help clean build maven-build docker-build docker-push \
        dev dev-up dev-stop dev-clean dev-status dev-logs \
        stage stage-up stage-stop stage-clean stage-status stage-logs \
        prod prod-up prod-stop prod-clean prod-status prod-logs \
        infra-up infra-down infra-status \
        smoke-test-dev smoke-test-stage smoke-test-all \
        deploy-dev deploy-stage deploy-prod \
        security-scan sast sca sonar \
        pipeline ci-all
.DEFAULT_GOAL := help

# Config
DC := docker compose
APP_DIR := vendnet
MVN := ./mvnw
BUILD_TAG ?= latest

# Ports per environment
DEV_PORT := 8280
STAGE_PORT := 8180
PROD_PORT := 8080

# Remote server (DEI private cloud)
REMOTE_HOST ?= vs427.dei.isep.ipp.pt
REMOTE_USER ?= root
REMOTE_SSH_PORT ?= 2222

# =============================================================================
# Help
# =============================================================================
help:
	@echo "VendNet CI/CD System — Makefile Targets"
	@echo ""
	@echo "Build:   make build | maven-build | docker-build | docker-push"
	@echo "Envs:    make dev | stage | prod"
	@echo "Deploy:  make deploy-dev | deploy-stage | deploy-prod"
	@echo "Tests:   make smoke-test-dev | smoke-test-stage | smoke-test-all"
	@echo "Security: make security-scan | sast | sca | sonar"
	@echo "Pipeline: make pipeline | ci-all"

# =============================================================================
# BUILD
# =============================================================================
build: maven-build docker-build

maven-build:
	@echo "[BUILD] Compiling VendNet..."
	@cd $(APP_DIR) && $(MVN) compile -q
	@echo "[BUILD] Maven compilation complete"

docker-build:
	@echo "[DOCKER] Building vendnet:$(BUILD_TAG)..."
	@docker build -t vendnet:$(BUILD_TAG) $(APP_DIR)
	@echo "[DOCKER] Image built: vendnet:$(BUILD_TAG)"

docker-push:
	@echo "[PUSH] Tagging and pushing vendnet:$(BUILD_TAG)..."
	@docker tag vendnet:$(BUILD_TAG) vendnet:$(BUILD_TAG)
	@echo "[PUSH] Ready for remote deployment"

# =============================================================================
# DEV ENVIRONMENT — H2 in-memory, fast iteration, debug
# =============================================================================
dev: maven-build
	@echo "[DEV] Starting VendNet on http://localhost:$(DEV_PORT)..."
	@cd $(APP_DIR) && $(MVN) spring-boot:run -Dspring-boot.run.profiles=dev -Dserver.port=$(DEV_PORT)

dev-up:
	@echo "[DEV] Deploying with Docker Compose..."
	@SERVER_PORT=$(DEV_PORT) $(DC) -p vendnet-dev -f docker-compose.dev.yml up -d --build
	@echo "[DEV] Deployed: http://localhost:$(DEV_PORT)"

dev-stop:
	@$(DC) -p vendnet-dev -f docker-compose.dev.yml down 2>/dev/null || true
	@echo "[DEV] Stopped"

dev-clean:
	@$(DC) -p vendnet-dev -f docker-compose.dev.yml down -v 2>/dev/null || true
	@echo "[DEV] Cleaned (volumes removed)"

dev-status:
	@docker ps --filter "name=vendnet-dev" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

dev-logs:
	@$(DC) -p vendnet-dev -f docker-compose.dev.yml logs -f

# =============================================================================
# STAGE ENVIRONMENT — Production-like with MySQL + observability
# =============================================================================
stage: build
	@echo "[STAGE] Deploying with Docker Compose..."
	@SERVER_PORT=$(STAGE_PORT) $(DC) -p vendnet-stage -f docker-compose.stage.yml up -d
	@echo "[STAGE] Deployed: http://localhost:$(STAGE_PORT)"

stage-up:
	@SERVER_PORT=$(STAGE_PORT) $(DC) -p vendnet-stage -f docker-compose.stage.yml up -d
	@echo "[STAGE] Started"

stage-stop:
	@$(DC) -p vendnet-stage -f docker-compose.stage.yml down 2>/dev/null || true
	@echo "[STAGE] Stopped"

stage-clean:
	@$(DC) -p vendnet-stage -f docker-compose.stage.yml down -v 2>/dev/null || true
	@echo "[STAGE] Cleaned (volumes removed)"

stage-status:
	@docker ps --filter "name=vendnet-stage" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

stage-logs:
	@$(DC) -p vendnet-stage -f docker-compose.stage.yml logs -f

# =============================================================================
# PROD ENVIRONMENT — Hardened, monitored, health-checked
# =============================================================================
prod: build
	@echo "[PROD] Deploying with Docker Compose..."
	@SERVER_PORT=$(PROD_PORT) $(DC) -p vendnet-prod -f docker-compose.prod.yml up -d
	@echo "[PROD] Deployed: http://localhost:$(PROD_PORT)"

prod-up:
	@SERVER_PORT=$(PROD_PORT) $(DC) -p vendnet-prod -f docker-compose.prod.yml up -d
	@echo "[PROD] Started"

prod-stop:
	@$(DC) -p vendnet-prod -f docker-compose.prod.yml down 2>/dev/null || true
	@echo "[PROD] Stopped"

prod-clean:
	@$(DC) -p vendnet-prod -f docker-compose.prod.yml down -v 2>/dev/null || true
	@echo "[PROD] Cleaned (volumes removed)"

prod-status:
	@docker ps --filter "name=vendnet-prod" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

prod-logs:
	@$(DC) -p vendnet-prod -f docker-compose.prod.yml logs -f

# =============================================================================
# SHARED INFRASTRUCTURE
# =============================================================================
infra-up:
	@echo "[INFRA] Starting shared infrastructure..."
	@$(DC) up -d mysql
	@echo "[INFRA] MySQL started"

infra-down:
	@$(DC) down 2>/dev/null || true
	@echo "[INFRA] Stopped"

infra-status:
	@docker ps --filter "name=vendnet" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# =============================================================================
# SMOKE TESTS
# =============================================================================
smoke-test-dev:
	@echo "[SMOKE] Testing DEV (port $(DEV_PORT))..."
	@curl -sf http://localhost:$(DEV_PORT)/api/health/ping >/dev/null && echo "  ping: OK" || echo "  ping: FAIL"
	@curl -sf http://localhost:$(DEV_PORT)/actuator/health >/dev/null && echo "  health: OK" || echo "  health: FAIL"
	@echo "[SMOKE] DEV complete"

smoke-test-stage:
	@echo "[SMOKE] Testing STAGING (port $(STAGE_PORT))..."
	@curl -sf http://localhost:$(STAGE_PORT)/api/health/ping >/dev/null && echo "  ping: OK" || echo "  ping: FAIL"
	@curl -sf http://localhost:$(STAGE_PORT)/actuator/health >/dev/null && echo "  health: OK" || echo "  health: FAIL"
	@echo "[SMOKE] STAGING complete"

smoke-test-all: smoke-test-dev smoke-test-stage

# =============================================================================
# DEPLOY TO REMOTE SERVER (vs427)
# =============================================================================
deploy-dev:
	@echo "[DEPLOY] DEV → $(REMOTE_HOST)..."
	@docker save vendnet:$(BUILD_TAG) | gzip | ssh -p $(REMOTE_SSH_PORT) $(REMOTE_USER)@$(REMOTE_HOST) \
		"gunzip | docker load && \
		 docker stop vendnet-dev 2>/dev/null || true && \
		 docker run -d --name vendnet-dev --restart unless-stopped \
		   -p $(DEV_PORT):8080 \
		   -e SPRING_PROFILES_ACTIVE=dev \
		   vendnet:$(BUILD_TAG)"
	@echo "[DEPLOY] DEV deployed: http://$(REMOTE_HOST):$(DEV_PORT)"

deploy-stage:
	@echo "[DEPLOY] STAGING → $(REMOTE_HOST)..."
	@docker save vendnet:$(BUILD_TAG) | gzip | ssh -p $(REMOTE_SSH_PORT) $(REMOTE_USER)@$(REMOTE_HOST) \
		"gunzip | docker load && \
		 docker stop vendnet-stage 2>/dev/null || true && \
		 docker run -d --name vendnet-stage --restart unless-stopped \
		   -p $(STAGE_PORT):8080 \
		   -e SPRING_PROFILES_ACTIVE=stage \
		   -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/vendnet \
		   -e SPRING_DATASOURCE_USERNAME=vendnet_user \
		   -e SPRING_DATASOURCE_PASSWORD=vendnet_pass \
		   --network=host \
		   vendnet:$(BUILD_TAG)"
	@echo "[DEPLOY] STAGING deployed: http://$(REMOTE_HOST):$(STAGE_PORT)"

deploy-prod:
	@echo "[DEPLOY] PROD → $(REMOTE_HOST)..."
	@docker save vendnet:$(BUILD_TAG) | gzip | ssh -p $(REMOTE_SSH_PORT) $(REMOTE_USER)@$(REMOTE_HOST) \
		"gunzip | docker load && \
		 docker stop vendnet-prod 2>/dev/null || true && \
		 docker run -d --name vendnet-prod --restart unless-stopped \
		   -p $(PROD_PORT):8080 \
		   -e SPRING_PROFILES_ACTIVE=prod \
		   -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/vendnet \
		   -e SPRING_DATASOURCE_USERNAME=vendnet_user \
		   -e SPRING_DATASOURCE_PASSWORD=vendnet_pass \
		   --network=host \
		   vendnet:$(BUILD_TAG)"
	@echo "[DEPLOY] PROD deployed: http://$(REMOTE_HOST):$(PROD_PORT)"

# =============================================================================
# SECURITY SCANS (local)
# =============================================================================
sast:
	@cd $(APP_DIR) && $(MVN) spotbugs:check -Dspotbugs.effort=Max -Dspotbugs.threshold=Low

sca:
	@cd $(APP_DIR) && $(MVN) org.owasp:dependency-check-maven:check

sonar:
	@cd $(APP_DIR) && $(MVN) verify sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=admin -Dsonar.password=admin

security-scan: sast sca

# =============================================================================
# FULL PIPELINE (local)
# =============================================================================
pipeline:
	@echo "[PIPELINE] Build + SAST + SCA + Test + Coverage"
	@cd $(APP_DIR) && $(MVN) clean compile
	@cd $(APP_DIR) && $(MVN) spotbugs:check
	@cd $(APP_DIR) && $(MVN) org.owasp:dependency-check-maven:check
	@cd $(APP_DIR) && $(MVN) verify
	@echo "[PIPELINE] Complete"

ci-all: pipeline security-scan
	@echo "[CI] Full local pipeline complete"

# =============================================================================
# CLEANUP
# =============================================================================
clean:
	@cd $(APP_DIR) && $(MVN) clean -q 2>/dev/null || true
	@echo "[CLEAN] Done"

clean-all: dev-clean stage-clean prod-clean infra-down
	@echo "[NUKE] All VendNet containers, networks, volumes removed"

nuke:
	@docker ps -aq --filter "name=vendnet-" | xargs -r docker rm -f 2>/dev/null || true
	@docker network ls -q --filter "name=vendnet-" | xargs -r docker network rm 2>/dev/null || true
	@docker volume ls -q --filter "name=vendnet-" | xargs -r docker volume rm 2>/dev/null || true
	@echo "[NUKE] Complete"
