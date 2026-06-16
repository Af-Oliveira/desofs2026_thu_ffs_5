#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# Stage 0 — Setup Context: Generates traceable artifact naming and shared
# metadata for all downstream jobs (vendnet-ci-cd.yml, lines 122-212)
# ─────────────────────────────────────────────────────────────────────────────

BRANCH="${GITHUB_REF#refs/heads/}"
BRANCH="${BRANCH#refs/tags/}"
BRANCH="$(echo "${BRANCH}" | tr '/' '-')"
if [ "${GITHUB_REF_TYPE}" = "tag" ]; then
    BRANCH="main"
fi

if [ "${GITHUB_REF_TYPE}" = "tag" ]; then
    VERSION="${GITHUB_REF#refs/tags/v}"
    IS_RELEASE="true"
else
    VERSION=$(awk '/<artifactId>vendnet<\/artifactId>/{found=1; next} found && /<version>/{gsub(/.*<version>|<\/version>.*/, ""); print; exit}' vendnet/pom.xml)
    if [ -z "${VERSION}" ]; then
        VERSION="0.0.1-SNAPSHOT"
    fi
    IS_RELEASE="false"
fi

SHORT_SHA="${GITHUB_SHA::7}"
IMAGE_TAG="${VERSION}-${SHORT_SHA}"
ARTIFACT_NAME="${APP_NAME}-${VERSION}-${SHORT_SHA}"

echo "branch=${BRANCH}" >> "${GITHUB_OUTPUT}"
echo "version=${VERSION}" >> "${GITHUB_OUTPUT}"
echo "short_sha=${SHORT_SHA}" >> "${GITHUB_OUTPUT}"
echo "image_tag=${IMAGE_TAG}" >> "${GITHUB_OUTPUT}"
echo "artifact_name=${ARTIFACT_NAME}" >> "${GITHUB_OUTPUT}"
echo "is_release=${IS_RELEASE}" >> "${GITHUB_OUTPUT}"
echo "is_main=${IS_MAIN}" >> "${GITHUB_OUTPUT}"
echo "is_develop=${IS_DEVELOP}" >> "${GITHUB_OUTPUT}"
