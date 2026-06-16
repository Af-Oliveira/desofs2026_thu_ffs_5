#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# Stage 7 — Docker Build & Push with Trivy Scan (vendnet-ci-cd.yml, lines 681-783)
# Builds multi-arch Docker image, pushes to GHCR + Docker Hub, scans with
# Trivy for CRITICAL/HIGH vulnerabilities, uploads SARIF results.
# ─────────────────────────────────────────────────────────────────────────────

# Compute lowercase image reference
REPO_LOWER=$(echo "${{ github.repository }}" | tr '[:upper:]' '[:lower:]')
echo "full=${{ env.REGISTRY }}/${REPO_LOWER}/vendnet"

# Docker metadata tags (semver + branch-latest + tag + release latest)
# type=raw,value=${{ needs.setup-context.outputs.image_tag }}
# type=raw,value=${{ needs.setup-context.outputs.branch }}-latest
# type=ref,event=tag
# type=raw,value=latest,enable=${{ needs.setup-context.outputs.is_release == 'true' }}

# Build & push (with buildx gha caching)
# context: vendnet/
# push: true
# cache-from: type=gha
# cache-to: type=gha,mode=max

# Trivy image scan
# image-ref: ghcr.io/org/vendnet:{image_tag}
# scan-type: image
# severity: CRITICAL,HIGH
# format: sarif
# output: trivy-image-results.sarif

# Pull commands generated:
# docker pull ghcr.io/org/vendnet:0.0.1-abc1234
# docker pull docker.io/namespace/vendnet:0.0.1-abc1234
