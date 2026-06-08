#!/usr/bin/env bash
# =============================================================================
#  ██████╗ ██╗     ██╗   ██╗███████╗       ██████╗ ██████╗ ███████╗███████╗███╗   ██╗
#  ██╔══██╗██║     ██║   ██║██╔════╝      ██╔════╝ ██╔══██╗██╔════╝██╔════╝████╗  ██║
#  ██████╔╝██║     ██║   ██║█████╗        ██║  ███╗██████╔╝█████╗  █████╗  ██╔██╗ ██║
#  ██╔══██╗██║     ██║   ██║██╔══╝        ██║   ██║██╔══██╗██╔══╝  ██╔══╝  ██║╚██╗██║
#  ██████╔╝███████╗╚██████╔╝███████╗      ╚██████╔╝██║  ██║███████╗███████╗██║ ╚████║
#  ╚═════╝ ╚══════╝ ╚═════╝ ╚══════╝       ╚═════╝ ╚═╝  ╚═╝╚══════╝╚══════╝╚═╝  ╚═══╝
#
#  VendNet Blue/Green Zero-Downtime Deployment Script
#  Runs on the remote server (vs427.dei.isep.ipp.pt)
#
#  Architecture:
#    - Nginx reverse-proxy listens on the PUBLIC port (e.g., 8280)
#    - App containers run on Docker network, referenced by container name
#    - Blue/Green: two containers (vendnet-<env>-blue, vendnet-<env>-green)
#    - Only one is "active" at a time (receiving traffic via Nginx)
#    - Switch: update Nginx upstream → reload → stop old container
#
#  Usage:
#    bash blue-green-deploy.sh \
#      --env dev|stage|prod \
#      --image ghcr.io/org/vendnet \
#      --tag main-abc1234 \
#      --port 8280 \
#      --registry ghcr.io \
#      --registry-user username \
#      --registry-pass ghp_token \
#      --profile dev \
#      --jwt-secret "secret" \
#      [--db-url "jdbc:mysql://..." \
#       --db-user "user" \
#       --db-pass "pass" \
#       --hmac-secret "secret" \
#       --network "vendnet-dev-net" \
#       --security-opts "no-new-privileges:true" \
#       --cap-drop "ALL" \
#       --cap-add "NET_BIND_SERVICE"]
# =============================================================================

set -euo pipefail

# ── Colors for beautiful output ────────────────────────────────────────────────
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
CYAN='\033[0;36m'
BOLD='\033[1m'
DIM='\033[2m'
NC='\033[0m' # No Color

# ── Emoji shortcuts ────────────────────────────────────────────────────────────
ROCKET="🚀"
CHECK="✅"
CROSS="❌"
WARN="⚠️"
HOURGLASS="⏳"
GEAR="⚙️"
PACKAGE="📦"
WHALE="🐳"
SHIELD="🛡️"
GLOBE="🌐"
TARGET="🎯"
LINK="🔗"
STOP="🛑"
RECYCLE="♻️"
SPARKLES="✨"

# ── Default values ─────────────────────────────────────────────────────────────
ENV=""
IMAGE=""
TAG=""
PORT=""
REGISTRY="ghcr.io"
REGISTRY_USER=""
REGISTRY_PASS=""
PROFILE=""
DB_URL=""
DB_USER=""
DB_PASS=""
JWT_SECRET=""
HMAC_SECRET=""
NETWORK=""
SECURITY_OPTS=""
CAP_DROP=""
CAP_ADD="NET_BIND_SERVICE"
HEALTH_TIMEOUT=120
NGINX_HEALTH_TIMEOUT=30

# ═══════════════════════════════════════════════════════════════════════════════
#  ARGUMENT PARSING
# ═══════════════════════════════════════════════════════════════════════════════

usage() {
    cat <<EOF
${BOLD}VendNet Blue/Green Deploy Script${NC}

${BOLD}Usage:${NC}
  $0 --env <dev|stage|prod> --image <image> --tag <tag> --port <port> [OPTIONS]

${BOLD}Required:${NC}
  --env ENV                Environment: dev, stage, prod
  --image IMAGE            Docker image name (e.g., ghcr.io/org/vendnet)
  --tag TAG                Docker image tag
  --port PORT              Public-facing port (e.g., 8280 for dev)
  --registry REGISTRY      Container registry (e.g., ghcr.io)
  --registry-user USER     Registry username
  --registry-pass PASS     Registry password/token
  --profile PROFILE        Spring profile (dev, stage, prod)
  --jwt-secret SECRET      JWT signing secret

${BOLD}Optional:${NC}
  --db-url URL             JDBC database URL
  --db-user USER           Database username
  --db-pass PASS           Database password
  --hmac-secret SECRET     HMAC webhook secret
  --network NETWORK        Docker network name (default: vendnet-ENV-net)
  --security-opts OPTS     Docker security options
  --cap-drop CAPS          Docker capabilities to drop
  --cap-add CAPS           Docker capabilities to add
  --health-timeout SEC     Health check timeout in seconds (default: 120)
  --help                   Show this help message

${BOLD}Examples:${NC}
  # Deploy DEV with H2 (no MySQL)
  $0 --env dev --image ghcr.io/myorg/vendnet --tag develop-abc1234 \\
     --port 8280 --registry ghcr.io --registry-user myuser \\
     --registry-pass ghp_xxx --profile dev --jwt-secret dev-secret

  # Deploy PROD with MySQL
  $0 --env prod --image ghcr.io/myorg/vendnet --tag v1.0.0 \\
     --port 8080 --registry ghcr.io --registry-user myuser \\
     --registry-pass ghp_xxx --profile prod \\
     --jwt-secret prod-secret --hmac-secret hmac-secret \\
     --db-url jdbc:mysql://mysql:3306/vendnet \\
     --db-user vendnet_user --db-pass vendnet_pass
EOF
    exit 0
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        --env)             ENV="$2"; shift 2 ;;
        --image)           IMAGE="$2"; shift 2 ;;
        --tag)             TAG="$2"; shift 2 ;;
        --port)            PORT="$2"; shift 2 ;;
        --registry)        REGISTRY="$2"; shift 2 ;;
        --registry-user)   REGISTRY_USER="$2"; shift 2 ;;
        --registry-pass)   REGISTRY_PASS="$2"; shift 2 ;;
        --profile)         PROFILE="$2"; shift 2 ;;
        --db-url)          DB_URL="$2"; shift 2 ;;
        --db-user)         DB_USER="$2"; shift 2 ;;
        --db-pass)         DB_PASS="$2"; shift 2 ;;
        --jwt-secret)      JWT_SECRET="$2"; shift 2 ;;
        --hmac-secret)     HMAC_SECRET="$2"; shift 2 ;;
        --network)         NETWORK="$2"; shift 2 ;;
        --security-opts)   SECURITY_OPTS="$2"; shift 2 ;;
        --cap-drop)        CAP_DROP="$2"; shift 2 ;;
        --cap-add)         CAP_ADD="$2"; shift 2 ;;
        --health-timeout)  HEALTH_TIMEOUT="$2"; shift 2 ;;
        --help)            usage ;;
        *)                 echo -e "${RED}${CROSS} Unknown argument: $1${NC}"; usage ;;
    esac
done

# ── Validate required arguments ────────────────────────────────────────────────
validate_args() {
    local missing=0
    for var in ENV IMAGE TAG PORT REGISTRY REGISTRY_USER REGISTRY_PASS PROFILE JWT_SECRET; do
        if [ -z "${!var:-}" ]; then
            echo -e "${RED}${CROSS} Missing required argument: --${var,,}${NC}"
            missing=1
        fi
    done
    if [ "$missing" -eq 1 ]; then
        echo ""
        usage
    fi

    # Default network name based on env
    if [ -z "$NETWORK" ]; then
        NETWORK="vendnet-${ENV}-net"
    fi
}

# ═══════════════════════════════════════════════════════════════════════════════
#  HELPER FUNCTIONS
# ═══════════════════════════════════════════════════════════════════════════════

log() {
    echo -e "${BLUE}[$(date '+%H:%M:%S')]${NC} $*"
}

log_section() {
    echo ""
    echo -e "${BOLD}${CYAN}╔══════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BOLD}${CYAN}║${NC}  $*"
    echo -e "${BOLD}${CYAN}╚══════════════════════════════════════════════════════════════╝${NC}"
    echo ""
}

success() {
    echo -e "${GREEN}${CHECK} $*${NC}"
}

warn() {
    echo -e "${YELLOW}${WARN} $*${NC}"
}

error() {
    echo -e "${RED}${CROSS} $*${NC}"
    exit 1
}

# ── Determine container naming ──────────────────────────────────────────────────
get_container_name() {
    local color="$1"
    echo "vendnet-${ENV}-${color}"
}

get_nginx_container_name() {
    echo "vendnet-${ENV}-nginx"
}

get_mysql_container_name() {
    echo "vendnet-${ENV}-mysql"
}

get_nginx_conf_path() {
    echo "/tmp/vendnet-${ENV}-nginx.conf"
}

# ── Determine which color is currently active ───────────────────────────────────
determine_active_color() {
    local blue_name
    local green_name
    blue_name=$(get_container_name "blue")
    green_name=$(get_container_name "green")

    local blue_running
    local green_running
    blue_running=$(docker inspect -f '{{.State.Running}}' "$blue_name" 2>/dev/null || echo "false")
    green_running=$(docker inspect -f '{{.State.Running}}' "$green_name" 2>/dev/null || echo "false")

    if [ "$blue_running" = "true" ] && [ "$green_running" = "true" ]; then
        # Both running — check which one nginx is proxying to
        local nginx_container
        nginx_container=$(get_nginx_container_name)

        if docker inspect -f '{{.State.Running}}' "$nginx_container" 2>/dev/null | grep -q "true"; then
            if docker exec "$nginx_container" cat /etc/nginx/conf.d/default.conf 2>/dev/null | grep -q "${blue_name}:8080"; then
                echo "blue"
                return
            elif docker exec "$nginx_container" cat /etc/nginx/conf.d/default.conf 2>/dev/null | grep -q "${green_name}:8080"; then
                echo "green"
                return
            fi
        fi
        # Fallback: assume blue is active
        echo "blue"
    elif [ "$blue_running" = "true" ]; then
        echo "blue"
    elif [ "$green_running" = "true" ]; then
        echo "green"
    else
        # Neither running — this is first deploy, start with blue
        echo "none"
    fi
}

# ═══════════════════════════════════════════════════════════════════════════════
#  DOCKER REGISTRY LOGIN
# ═══════════════════════════════════════════════════════════════════════════════

docker_login() {
    log "${ROCKET} Logging into container registry: ${REGISTRY}"

    # Strip protocol prefix for the login command
    local registry_host="${REGISTRY#https://}"
    registry_host="${registry_host#http://}"

    echo "$REGISTRY_PASS" | docker login "$registry_host" -u "$REGISTRY_USER" --password-stdin 2>/dev/null || \
        error "Failed to login to registry ${REGISTRY}. Check credentials."

    success "Registry login successful"
}

# ═══════════════════════════════════════════════════════════════════════════════
#  DOCKER IMAGE PULL
# ═══════════════════════════════════════════════════════════════════════════════

pull_image() {
    local full_image="${IMAGE}:${TAG}"
    log "${WHALE} Pulling image: ${full_image}"

    docker pull "$full_image" || error "Failed to pull image ${full_image}"

    success "Image pulled: ${full_image}"
}

# ═══════════════════════════════════════════════════════════════════════════════
#  DATABASE (MySQL) — shared between blue and green for staging/prod
# ═══════════════════════════════════════════════════════════════════════════════

ensure_mysql_running() {
    # DEV uses H2 in-memory — no MySQL needed
    if [ "$ENV" = "dev" ]; then
        log "${GEAR} DEV uses H2 in-memory, skipping MySQL setup"
        return 0
    fi

    local mysql_name
    mysql_name=$(get_mysql_container_name)

    if docker inspect -f '{{.State.Running}}' "$mysql_name" 2>/dev/null | grep -q "true"; then
        success "MySQL container '${mysql_name}' is already running"
        return 0
    fi

    # Remove stopped/conflicting container from previous failed deploy
    if docker inspect "$mysql_name" >/dev/null 2>&1; then
        log "  ${RECYCLE} Removing stale MySQL container: ${mysql_name}"
        docker rm "$mysql_name" 2>/dev/null || true
    fi

    log "${PACKAGE} Starting MySQL container: ${mysql_name}"

    # Ensure Docker network exists
    docker network create "$NETWORK" 2>/dev/null || true

    # Default MySQL credentials if not provided
    local mysql_root_pass="${MYSQL_ROOT_PASSWORD:-vendnet_root_pass}"
    local mysql_db="${MYSQL_DATABASE:-vendnet}"
    local mysql_user="${DB_USER:-vendnet_user}"
    local mysql_pass="${DB_PASS:-vendnet_pass}"

    docker run -d \
        --name "$mysql_name" \
        --network "$NETWORK" \
        --restart unless-stopped \
        -e "MYSQL_ROOT_PASSWORD=${mysql_root_pass}" \
        -e "MYSQL_DATABASE=${mysql_db}" \
        -e "MYSQL_USER=${mysql_user}" \
        -e "MYSQL_PASSWORD=${mysql_pass}" \
        -v "${mysql_name}-data:/var/lib/mysql" \
        --health-cmd='mysqladmin ping -h localhost --silent' \
        --health-interval=10s \
        --health-timeout=5s \
        --health-retries=5 \
        --health-start-period=30s \
        mysql:8.4 || error "Failed to start MySQL container"

    # Wait for MySQL to be healthy
    log "${HOURGLASS} Waiting for MySQL to become healthy..."
    local waited=0
    while [ $waited -lt 60 ]; do
        local health
        health=$(docker inspect -f '{{.State.Health.Status}}' "$mysql_name" 2>/dev/null || echo "starting")
        if [ "$health" = "healthy" ]; then
            success "MySQL is healthy after ${waited}s"
            return 0
        fi
        sleep 3
        waited=$((waited + 3))
        if [ $((waited % 15)) -eq 0 ]; then
            log "  ${HOURGLASS} Still waiting for MySQL... (${waited}s, status: ${health})"
        fi
    done

    warn "MySQL did not report healthy within 60s — continuing anyway"
}

# ═══════════════════════════════════════════════════════════════════════════════
#  APP CONTAINER START
# ═══════════════════════════════════════════════════════════════════════════════

start_app_container() {
    local color="$1"
    local container_name
    container_name=$(get_container_name "$color")
    local full_image="${IMAGE}:${TAG}"

    log "${ROCKET} Starting ${BOLD}${color}${NC} container: ${container_name}"
    log "  ${PACKAGE} Image:  ${full_image}"
    log "  ${GLOBE} Profile: ${PROFILE}"

    # Stop and remove if a container with this name already exists (stale)
    if docker inspect "$container_name" >/dev/null 2>&1; then
        log "  ${RECYCLE} Removing stale container: ${container_name}"
        docker stop --time=10 "$container_name" 2>/dev/null || true
        docker rm "$container_name" 2>/dev/null || true
    fi

    # Build docker run arguments
    local run_args=(
        -d
        --name "$container_name"
        --network "$NETWORK"
        --restart unless-stopped
        -e "SPRING_PROFILES_ACTIVE=${PROFILE}"
        -e "APP_JWT_SECRET=${JWT_SECRET}"
    )

    # Database configuration
    if [ "$ENV" = "dev" ]; then
        run_args+=(
            -e "SPRING_DATASOURCE_URL=jdbc:h2:mem:vendnet_dev;DB_CLOSE_DELAY=-1"
            -e "SPRING_DATASOURCE_DRIVER=org.h2.Driver"
            -e "SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop"
        )
    else
        # Staging/Prod — use MySQL (connect to shared MySQL container)
        if [ -n "$DB_URL" ]; then
            run_args+=(-e "SPRING_DATASOURCE_URL=${DB_URL}")
        else
            local mysql_name
            mysql_name=$(get_mysql_container_name)
            run_args+=(-e "SPRING_DATASOURCE_URL=jdbc:mysql://${mysql_name}:3306/vendnet")
        fi
        if [ -n "$DB_USER" ]; then
            run_args+=(-e "SPRING_DATASOURCE_USERNAME=${DB_USER}")
        fi
        if [ -n "$DB_PASS" ]; then
            run_args+=(-e "SPRING_DATASOURCE_PASSWORD=${DB_PASS}")
        fi
    fi

    # HMAC secret (optional)
    if [ -n "$HMAC_SECRET" ]; then
        run_args+=(-e "APP_PAYMENT_WEBHOOK_SECRET=${HMAC_SECRET}")
    fi

    # Security hardening
    if [ -n "$SECURITY_OPTS" ]; then
        run_args+=(--security-opt "$SECURITY_OPTS")
    fi
    if [ -n "$CAP_DROP" ]; then
        run_args+=(--cap-drop "$CAP_DROP")
    fi
    if [ -n "$CAP_ADD" ]; then
        run_args+=(--cap-add "$CAP_ADD")
    fi

    # Health check (internal, Docker-level)
    run_args+=(
        --health-cmd="wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1"
        --health-interval=10s
        --health-timeout=5s
        --health-retries=3
        --health-start-period=60s
    )

    run_args+=("$full_image")

    docker run "${run_args[@]}" || error "Failed to start container: ${container_name}"

    success "Container started: ${container_name}"
}

# ═══════════════════════════════════════════════════════════════════════════════
#  HEALTH CHECK
# ═══════════════════════════════════════════════════════════════════════════════

wait_for_health() {
    local container_name="$1"
    local max_wait="${2:-$HEALTH_TIMEOUT}"
    local waited=0

    log "${HOURGLASS} Waiting for ${BOLD}${container_name}${NC} to pass health check..."

    while [ $waited -lt "$max_wait" ]; do
        # Check Docker-level health
        local health
        health=$(docker inspect -f '{{.State.Health.Status}}' "$container_name" 2>/dev/null || echo "starting")

        if [ "$health" = "healthy" ]; then
            success "Container ${container_name} is healthy (${waited}s)"
            return 0
        fi

        # Also try direct HTTP check as fallback
        local http_code
        http_code=$(docker exec "$container_name" wget -q --spider http://localhost:8080/actuator/health 2>&1; echo $?)
        if [ "$http_code" = "0" ]; then
            success "Container ${container_name} responds to HTTP health (${waited}s)"
            return 0
        fi

        sleep 2
        waited=$((waited + 2))

        # Show progress every 15 seconds
        if [ $((waited % 15)) -eq 0 ] && [ $waited -gt 0 ]; then
            log "  ${HOURGLASS} Still waiting... (${waited}s/${max_wait}s, health: ${health})"
        fi
    done

    error "Container ${container_name} failed health check after ${max_wait}s!
       ${YELLOW}Debug:${NC}
         docker logs --tail 50 ${container_name}
         docker inspect ${container_name} | jq '.[0].State.Health'"
}

# ═══════════════════════════════════════════════════════════════════════════════
#  NGINX REVERSE PROXY
#  Listens on the public PORT, proxies to the active app container.
#  Nginx runs as a Docker container on the same network.
# ═══════════════════════════════════════════════════════════════════════════════

setup_nginx() {
    local active_color="$1"
    local active_container
    active_container=$(get_container_name "$active_color")
    local nginx_name
    nginx_name=$(get_nginx_container_name)
    local nginx_conf
    nginx_conf=$(get_nginx_conf_path)

    log "${GLOBE} Configuring Nginx reverse proxy → ${active_container}:8080"

    # Generate nginx configuration
    cat > "$nginx_conf" <<NGINX_EOF
# VendNet ${ENV} — Blue/Green Nginx Configuration
# Auto-generated by blue-green-deploy.sh at $(date -u +%Y-%m-%dT%H:%M:%SZ)
# Active upstream: ${active_container}:8080

upstream vendnet_backend {
    server ${active_container}:8080 max_fails=3 fail_timeout=10s;
    keepalive 32;
}

server {
    listen 80;
    server_name _;

    # Increase timeouts for slow operations
    proxy_read_timeout 300s;
    proxy_send_timeout 300s;
    proxy_connect_timeout 10s;

    # Pass real client IP
    proxy_set_header X-Real-IP \$remote_addr;
    proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto \$scheme;
    proxy_set_header Host \$host;

    # Do not buffer responses (streaming)
    proxy_buffering off;

    location / {
        proxy_pass http://vendnet_backend;
    }

    # Health check endpoint (no auth required)
    location /actuator/health {
        proxy_pass http://vendnet_backend;
        access_log off;
    }
}
NGINX_EOF

    success "Nginx config written: ${nginx_conf}"

    # Check if nginx container already exists
    if docker inspect "$nginx_name" >/dev/null 2>&1; then
        log "${RECYCLE} Updating existing Nginx container..."

        # Copy new config into running container
        docker cp "$nginx_conf" "${nginx_name}:/etc/nginx/conf.d/default.conf"

        # Test and reload nginx configuration
        if docker exec "$nginx_name" nginx -t 2>&1; then
            docker exec "$nginx_name" nginx -s reload
            success "Nginx reloaded with new upstream: ${active_container}:8080"
        else
            error "Nginx configuration test failed!"
        fi
    else
        log "${WHALE} Creating new Nginx container: ${nginx_name}"

        # Ensure network exists
        docker network create "$NETWORK" 2>/dev/null || true

        docker run -d \
            --name "$nginx_name" \
            --network "$NETWORK" \
            --restart unless-stopped \
            -p "${PORT}:80" \
            -v "${nginx_conf}:/etc/nginx/conf.d/default.conf:ro" \
            --health-cmd="nginx -t && service nginx status || exit 1" \
            --health-interval=15s \
            --health-timeout=5s \
            --health-retries=3 \
            --health-start-period=5s \
            nginx:alpine || error "Failed to start Nginx container"

        # Wait for Nginx to be ready
        log "${HOURGLASS} Waiting for Nginx to become ready..."
        sleep 3

        if docker inspect -f '{{.State.Running}}' "$nginx_name" 2>/dev/null | grep -q "true"; then
            success "Nginx container started and proxying to ${active_container}:8080"
        else
            error "Nginx container failed to start. Check: docker logs ${nginx_name}"
        fi
    fi
}

# ═══════════════════════════════════════════════════════════════════════════════
#  GRACEFUL SHUTDOWN OF OLD CONTAINER
# ═══════════════════════════════════════════════════════════════════════════════

graceful_shutdown() {
    local color="$1"
    local container_name
    container_name=$(get_container_name "$color")

    if docker inspect -f '{{.State.Running}}' "$container_name" 2>/dev/null | grep -q "true"; then
        log "${STOP} Gracefully shutting down ${BOLD}${color}${NC} container: ${container_name}"

        # Send SIGTERM and wait up to 30s for graceful shutdown
        docker stop --time=30 "$container_name" 2>/dev/null || true
        docker rm "$container_name" 2>/dev/null || true

        success "Old container removed: ${container_name}"
    else
        log "${RECYCLE} No running ${color} container to shut down"

        # Clean up any leftover stopped container
        docker rm "$container_name" 2>/dev/null || true
    fi
}

# ═══════════════════════════════════════════════════════════════════════════════
#  CLEANUP — remove dangling images to free disk space
# ═══════════════════════════════════════════════════════════════════════════════

cleanup_images() {
    log "${RECYCLE} Cleaning up unused Docker images..."

    # Remove old vendnet images (keep the 2 most recent)
    local old_images
    old_images=$(docker images --format '{{.Repository}}:{{.Tag}} {{.ID}}' \
        | grep "${IMAGE}" \
        | grep -v "${TAG}" \
        | awk '{print $2}' \
        | head -n -2 || true)

    if [ -n "$old_images" ]; then
        echo "$old_images" | while read -r img_id; do
            docker rmi "$img_id" 2>/dev/null || true
        done
        success "Old vendnet images cleaned up"
    fi

    # Remove dangling images
    docker image prune -f 2>/dev/null || true
}

# ═══════════════════════════════════════════════════════════════════════════════
#  DEPLOYMENT STATUS SUMMARY
# ═══════════════════════════════════════════════════════════════════════════════

print_status() {
    local active_color="$1"
    local active_container
    active_container=$(get_container_name "$active_color")
    local nginx_name
    nginx_name=$(get_nginx_container_name)

    echo ""
    echo -e "${BOLD}${GREEN}╔══════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BOLD}${GREEN}║  ${SPARKLES}  VendNet Deployment Complete!                         ║${NC}"
    echo -e "${BOLD}${GREEN}╠══════════════════════════════════════════════════════════════╣${NC}"
    echo -e "${BOLD}${GREEN}║${NC}                                                            "
    echo -e "${BOLD}${GREEN}║${NC}  ${TARGET} Environment:  ${BOLD}${ENV^^}${NC}"
    echo -e "${BOLD}${GREEN}║${NC}  ${GLOBE} Public URL:   ${CYAN}http://$(hostname -I 2>/dev/null | awk '{print $1}'):${PORT}${NC}"
    echo -e "${BOLD}${GREEN}║${NC}  ${WHALE} Active Color: ${BOLD}${active_color^^}${NC} (${active_container})"
    echo -e "${BOLD}${GREEN}║${NC}  ${PACKAGE} Image:        ${DIM}${IMAGE}:${TAG}${NC}"
    echo -e "${BOLD}${GREEN}║${NC}  ${SHIELD} Nginx:        ${nginx_name}"
    echo -e "${BOLD}${GREEN}║${NC}  ${LINK} Health:       ${CYAN}http://localhost:${PORT}/actuator/health${NC}"
    echo -e "${BOLD}${GREEN}║${NC}                                                            "
    echo -e "${BOLD}${GREEN}╚══════════════════════════════════════════════════════════════╝${NC}"
    echo ""

    # Show running containers
    echo -e "${BOLD}${CYAN}Running VendNet containers:${NC}"
    docker ps --filter "name=vendnet-${ENV}" --format "  {{.Names}}\t{{.Status}}\t{{.Ports}}" 2>/dev/null || true
    echo ""
}

# ═══════════════════════════════════════════════════════════════════════════════
#  MAIN — ORCHESTRATION
# ═══════════════════════════════════════════════════════════════════════════════

main() {
    validate_args

    log_section "${ROCKET} VendNet Blue/Green Deploy — ${ENV^^} Environment ${ROCKET}"
    log "Environment: ${BOLD}${ENV}${NC}"
    log "Image:       ${BOLD}${IMAGE}:${TAG}${NC}"
    log "Public Port: ${BOLD}${PORT}${NC}"
    log "Network:     ${BOLD}${NETWORK}${NC}"
    log "Profile:     ${BOLD}${PROFILE}${NC}"

    # ── Phase 1: Prepare ──────────────────────────────────────────────────────
    log_section "1. ${PACKAGE} Preparing Deployment"

    docker_login
    pull_image

    # ── Phase 2: Determine colors ─────────────────────────────────────────────
    log_section "2. ${TARGET} Determining Blue/Green State"

    local current_color
    current_color=$(determine_active_color)
    log "Current active color: ${BOLD}${current_color:-none}${NC}"

    local new_color
    local old_color

    if [ "$current_color" = "blue" ]; then
        new_color="green"
        old_color="blue"
    else
        new_color="blue"
        old_color="green"
    fi

    log "New color to deploy:   ${BOLD}${new_color^^}${NC}"
    log "Old color (to retire): ${BOLD}${old_color^^}${NC}"

    # ── Phase 3: Ensure database ──────────────────────────────────────────────
    log_section "3. ${PACKAGE} Database Setup"

    ensure_mysql_running

    # ── Phase 4: Start new (inactive) container ───────────────────────────────
    log_section "4. ${ROCKET} Starting New Container (${new_color^^})"

    start_app_container "$new_color"
    wait_for_health "$(get_container_name "$new_color")"

    # ── Phase 5: Switch traffic via Nginx ─────────────────────────────────────
    log_section "5. ${GLOBE} Switching Traffic to ${new_color^^}"

    setup_nginx "$new_color"

    # Verify Nginx can reach the new backend
    log "${HOURGLASS} Verifying traffic switch..."
    sleep 2
    local nginx_container
    nginx_container=$(get_nginx_container_name)

    # Quick HTTP check through nginx
    local health_url="http://localhost:${PORT}/actuator/health"
    if curl -sf --max-time 10 "$health_url" 2>/dev/null | grep -q '"status":"UP"'; then
        success "Traffic switched SUCCESSFULLY — ${health_url} returns UP"
    else
        warn "Health check through Nginx did not confirm UP — check manually: curl ${health_url}"
    fi

    # ── Phase 6: Gracefully shutdown old container ────────────────────────────
    log_section "6. ${STOP} Retiring Old Container (${old_color^^})"

    if [ "$current_color" != "none" ]; then
        graceful_shutdown "$old_color"
    else
        log "First deployment — no old container to retire"
    fi

    # ── Phase 7: Cleanup ──────────────────────────────────────────────────────
    log_section "7. ${RECYCLE} Cleanup"

    cleanup_images

    # ── Phase 8: Status ───────────────────────────────────────────────────────
    print_status "$new_color"
}

# ── Run ────────────────────────────────────────────────────────────────────────
main "$@"
