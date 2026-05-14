.PHONY: help sast sca build test verify coverage docker-up docker-down clean all pipeline

MVN = ./mvnw
DOCKER_COMPOSE = docker compose -f ../docker-compose.yml

help:
	@echo "VendNet Pipeline Makefile"
	@echo ""
	@echo "  make help          Show this help"
	@echo "  make sast          SAST: SpotBugs + FindSecBugs (OWASP Top 10 auto)"
	@echo "  make sca           SCA: OWASP Dependency Check"
	@echo "  make build         Compile project"
	@echo "  make test          Run unit + integration tests"
	@echo "  make verify        verify phase (build + test + coverage report)"
	@echo "  make coverage      Generate JaCoCo coverage report"
	@echo "  make docker-up     Start MySQL container"
	@echo "  make docker-down   Stop MySQL container"
	@echo "  make docker-logs   Show MySQL container logs"
	@echo "  make clean         Clean target directory"
	@echo "  make all           Full pipeline locally"
	@echo "  make pipeline      Same as CI pipeline (used in GitHub Actions)"

sast:
	$(MVN) spotbugs:check -Dspotbugs.effort=Max -Dspotbugs.threshold=Low

sca:
	$(MVN) org.owasp:dependency-check-maven:check

build:
	$(MVN) compile -q

test:
	$(MVN) test

verify:
	$(MVN) verify

coverage:
	$(MVN) jacoco:report
	@echo "Coverage report: target/site/jacoco/index.html"

docker-up:
	$(DOCKER_COMPOSE) up -d
	@echo "Waiting for MySQL to be ready..."
	@until $(DOCKER_COMPOSE) exec mysql mysqladmin ping -h localhost --silent 2>/dev/null; do sleep 1; done
	@echo "MySQL is ready."

docker-down:
	$(DOCKER_COMPOSE) down

docker-logs:
	$(DOCKER_COMPOSE) logs -f mysql

clean:
	$(MVN) clean

all: docker-up clean sast sca verify
	@echo ""
	@echo "Pipeline completed successfully."

pipeline: clean sast sca verify
	@echo ""
	@echo "CI Pipeline completed."
