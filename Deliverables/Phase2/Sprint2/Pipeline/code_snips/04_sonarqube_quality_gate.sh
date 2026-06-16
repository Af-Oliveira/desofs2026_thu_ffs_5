#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# Stage 6 — SonarQube Quality Gate (vendnet-ci-cd.yml, lines 568-675)
# Runs SonarQube analysis with JaCoCo XML integration, waits for the quality
# gate result, and fails the pipeline if coverage < 80% or gate fails.
# ─────────────────────────────────────────────────────────────────────────────

cd vendnet

if [ -f "target/site/jacoco/jacoco.xml" ]; then
    echo "✅ JaCoCo XML report found — SonarQube will use it"
else
    echo "::warning::⚠️ JaCoCo XML not found — coverage may be incomplete"
fi

./mvnw -B sonar:sonar \
    -Dsonar.host.url="${SONAR_HOST_URL}" \
    -Dsonar.token="${SONAR_TOKEN}" \
    -Dsonar.projectDate="$(date -u +%Y-%m-%dT%H:%M:%S%z)" \
    -Dsonar.qualitygate.wait=true \
    -Dsonar.qualitygate.timeout=300 \
    -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml

SONAR_EXIT=$?

if [ ${SONAR_EXIT} -ne 0 ]; then
    echo "::error::❌ SonarQube Quality Gate FAILED!"
    echo "::error::Coverage may be below 80% or other metrics failed."
else
    echo "✅ SonarQube Quality Gate PASSED"
fi

exit ${SONAR_EXIT}
