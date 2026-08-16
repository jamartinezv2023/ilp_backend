#!/usr/bin/env bash

set -Eeuo pipefail

# ============================================================
# ILP — ADAPTIVE RESEARCH BDD RUNTIME HARNESS
#
# Canonical Linux execution contract:
#
#   PostgreSQL 16
#        ↓
#   adaptive bootJar
#        ↓
#   adaptive SUT
#        ↓
#   actuator health
#        ↓
#   Research BDD
#
# Intended environments:
#   - GitHub Actions ubuntu-latest
#   - Azure DevOps ubuntu-latest
#   - Linux developer environments
#
# Java:
#   Temurin / OpenJDK 17
# ============================================================

ROOT_DIR="$(
    cd "$(dirname "${BASH_SOURCE[0]}")/../.." &&
    pwd
)"

cd "${ROOT_DIR}"

# ------------------------------------------------------------
# Configuration
# ------------------------------------------------------------

DB_CONTAINER="${ILP_BDD_DB_CONTAINER:-ilp-bdd-postgres}"
DB_IMAGE="${ILP_BDD_DB_IMAGE:-postgres:16-alpine}"

DB_NAME="${ILP_BDD_DB_NAME:-adaptive_education_db}"
DB_USER="${ILP_BDD_DB_USER:-ilp}"
DB_PASSWORD="${ILP_BDD_DB_PASSWORD:-ilp}"

DB_HOST_PORT="${ILP_BDD_DB_HOST_PORT:-55432}"

SUT_PORT="${ILP_BDD_SUT_PORT:-18083}"

SUT_HEALTH_URL="http://127.0.0.1:${SUT_PORT}/actuator/health"
BDD_BASE_URL="http://127.0.0.1:${SUT_PORT}"

RUNTIME_DIR="${ROOT_DIR}/build/ci-runtime"
SUT_LOG="${RUNTIME_DIR}/adaptive-sut.log"

SUT_PID=""

mkdir -p "${RUNTIME_DIR}"

# ------------------------------------------------------------
# Logging
# ------------------------------------------------------------

log() {
    printf '%s\n' "$*"
}

section() {
    printf '\n'
    printf '%s\n' "============================================================"
    printf '%s\n' "$*"
    printf '%s\n' "============================================================"
}

# ------------------------------------------------------------
# Cleanup
# ------------------------------------------------------------

cleanup() {

    local exit_code=$?

    section "CLEANUP"

    if [[ -n "${SUT_PID}" ]]; then

        if kill -0 "${SUT_PID}" 2>/dev/null; then

            log "Stopping SUT PID=${SUT_PID}"

            kill "${SUT_PID}" 2>/dev/null || true

            for _ in $(seq 1 20); do

                if ! kill -0 "${SUT_PID}" 2>/dev/null; then
                    break
                fi

                sleep 1
            done

            if kill -0 "${SUT_PID}" 2>/dev/null; then
                kill -9 "${SUT_PID}" 2>/dev/null || true
            fi
        fi
    fi

    if docker ps -a \
        --format '{{.Names}}' |
        grep -Fxq "${DB_CONTAINER}"
    then

        log "Removing PostgreSQL container=${DB_CONTAINER}"

        docker rm -f "${DB_CONTAINER}" >/dev/null 2>&1 || true
    fi

    log "HARNESS_EXIT=${exit_code}"

    exit "${exit_code}"
}

trap cleanup EXIT INT TERM

# ------------------------------------------------------------
# Tool preflight
# ------------------------------------------------------------

section "TOOL PREFLIGHT"

command -v java >/dev/null
command -v docker >/dev/null
command -v curl >/dev/null

if [[ ! -x "${ROOT_DIR}/gradlew" ]]; then
    chmod +x "${ROOT_DIR}/gradlew"
fi

java -version
docker --version
"${ROOT_DIR}/gradlew" --version

JAVA_MAJOR="$(
    java -version 2>&1 |
    awk -F '"' '/version/ {print $2}' |
    awk -F '.' '{print $1}'
)"

log "JAVA_MAJOR=${JAVA_MAJOR}"

if [[ "${JAVA_MAJOR}" != "17" ]]; then

    log "ERROR: Java 17 is required."

    exit 17
fi

docker info >/dev/null

log "TOOL_PREFLIGHT=PASS"

# ------------------------------------------------------------
# Remove stale DB container
# ------------------------------------------------------------

section "DATABASE CLEAN START"

if docker ps -a \
    --format '{{.Names}}' |
    grep -Fxq "${DB_CONTAINER}"
then

    log "Removing stale container=${DB_CONTAINER}"

    docker rm -f "${DB_CONTAINER}" >/dev/null
fi

# ------------------------------------------------------------
# PostgreSQL 16
# ------------------------------------------------------------

section "START POSTGRESQL 16"

docker run \
    --detach \
    --name "${DB_CONTAINER}" \
    --publish "127.0.0.1:${DB_HOST_PORT}:5432" \
    --env "POSTGRES_DB=${DB_NAME}" \
    --env "POSTGRES_USER=${DB_USER}" \
    --env "POSTGRES_PASSWORD=${DB_PASSWORD}" \
    --health-cmd="pg_isready -U ${DB_USER} -d ${DB_NAME}" \
    --health-interval=2s \
    --health-timeout=5s \
    --health-retries=30 \
    "${DB_IMAGE}" \
    >/dev/null

log "DB_CONTAINER=${DB_CONTAINER}"
log "DB_IMAGE=${DB_IMAGE}"
log "DB_PORT=${DB_HOST_PORT}"

# ------------------------------------------------------------
# Wait for PostgreSQL
# ------------------------------------------------------------

section "WAIT FOR POSTGRESQL"

DB_READY=0

for attempt in $(seq 1 60); do

    STATUS="$(
        docker inspect \
            --format '{{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}}' \
            "${DB_CONTAINER}" \
            2>/dev/null || true
    )"

    log "DB_WAIT_ATTEMPT=${attempt} STATUS=${STATUS}"

    if [[ "${STATUS}" == "healthy" ]]; then

        DB_READY=1

        break
    fi

    if [[ "${STATUS}" == "unhealthy" ]]; then

        docker logs "${DB_CONTAINER}" || true

        exit 31
    fi

    sleep 2
done

if [[ "${DB_READY}" != "1" ]]; then

    docker logs "${DB_CONTAINER}" || true

    log "ERROR: PostgreSQL did not become healthy."

    exit 32
fi

docker exec \
    "${DB_CONTAINER}" \
    pg_isready \
        -U "${DB_USER}" \
        -d "${DB_NAME}"

log "POSTGRESQL_READY=PASS"

# ------------------------------------------------------------
# Build adaptive bootJar
# ------------------------------------------------------------

section "BUILD ADAPTIVE BOOTJAR"

"${ROOT_DIR}/gradlew" \
    :adaptive-education-service:bootJar \
    --console=plain \
    --no-daemon \
    --no-configuration-cache

BOOT_JARS=(
    "${ROOT_DIR}"/adaptive-education-service/build/libs/*.jar
)

SUT_JAR=""

for candidate in "${BOOT_JARS[@]}"; do

    [[ -f "${candidate}" ]] || continue

    case "${candidate}" in
        *-plain.jar)
            ;;
        *)
            SUT_JAR="${candidate}"
            break
            ;;
    esac
done

if [[ -z "${SUT_JAR}" ]]; then

    log "ERROR: adaptive bootJar not found."

    exit 41
fi

log "SUT_JAR=${SUT_JAR}"
log "BOOTJAR_BUILD=PASS"

# ------------------------------------------------------------
# Start adaptive SUT
# ------------------------------------------------------------

section "START ADAPTIVE SUT"

export SERVER_PORT="${SUT_PORT}"

export SPRING_DATASOURCE_URL="jdbc:postgresql://127.0.0.1:${DB_HOST_PORT}/${DB_NAME}"
export SPRING_DATASOURCE_USERNAME="${DB_USER}"
export SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}"
export SPRING_DATASOURCE_DRIVER_CLASS_NAME="org.postgresql.Driver"

export SPRING_FLYWAY_ENABLED="true"
export SPRING_FLYWAY_BASELINE_ON_MIGRATE="true"
export SPRING_FLYWAY_BASELINE_VERSION="0"

# Preserve the application runtime behaviour certified by Gate 84E-R.
export SPRING_JPA_HIBERNATE_DDL_AUTO="update"

export EUREKA_CLIENT_ENABLED="false"
export EUREKA_CLIENT_REGISTER_WITH_EUREKA="false"
export EUREKA_CLIENT_FETCH_REGISTRY="false"
export SPRING_CLOUD_DISCOVERY_ENABLED="false"

export MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE="health,info"
export MANAGEMENT_ENDPOINT_HEALTH_PROBES_ENABLED="true"

: > "${SUT_LOG}"

java \
    -jar "${SUT_JAR}" \
    >"${SUT_LOG}" \
    2>&1 &

SUT_PID=$!

log "SUT_PID=${SUT_PID}"
log "SUT_PORT=${SUT_PORT}"
log "SUT_LOG=${SUT_LOG}"

# ------------------------------------------------------------
# Wait for SUT health
# ------------------------------------------------------------

section "WAIT FOR SUT"

SUT_READY=0

for attempt in $(seq 1 90); do

    if ! kill -0 "${SUT_PID}" 2>/dev/null; then

        log "ERROR: SUT terminated before becoming healthy."

        cat "${SUT_LOG}" || true

        exit 51
    fi

    HEALTH="$(
        curl \
            --silent \
            --show-error \
            --max-time 3 \
            "${SUT_HEALTH_URL}" \
            2>/dev/null || true
    )"

    log "SUT_WAIT_ATTEMPT=${attempt} HEALTH=${HEALTH}"

    if printf '%s' "${HEALTH}" |
        grep -Eq '"status"[[:space:]]*:[[:space:]]*"UP"'
    then

        SUT_READY=1

        break
    fi

    sleep 2
done

if [[ "${SUT_READY}" != "1" ]]; then

    log "ERROR: SUT did not become healthy."

    cat "${SUT_LOG}" || true

    exit 52
fi

log "SUT_HEALTH=${SUT_HEALTH_URL}"
log "SUT_READY=PASS"

# ------------------------------------------------------------
# Research BDD
# ------------------------------------------------------------

section "RUN RESEARCH BDD"

export JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-} -Dservices.adaptive.base-url=${BDD_BASE_URL}"

log "BDD_BASE_URL=${BDD_BASE_URL}"

"${ROOT_DIR}/gradlew" \
    :bdd-tests:test \
    --console=plain \
    --no-daemon \
    --no-configuration-cache \
    --rerun-tasks

BDD_EXIT=$?

log "BDD_TEST_EXIT=${BDD_EXIT}"

if [[ "${BDD_EXIT}" -ne 0 ]]; then

    log "BDD_TEST_RESULT=FAIL"

    cat "${SUT_LOG}" || true

    exit "${BDD_EXIT}"
fi

log "BDD_TEST_RESULT=PASS"

# ------------------------------------------------------------
# Final certification
# ------------------------------------------------------------

section "RUNTIME CERTIFICATION"

FINAL_HEALTH="$(
    curl \
        --silent \
        --show-error \
        --max-time 5 \
        "${SUT_HEALTH_URL}"
)"

log "FINAL_HEALTH=${FINAL_HEALTH}"

if ! printf '%s' "${FINAL_HEALTH}" |
    grep -Eq '"status"[[:space:]]*:[[:space:]]*"UP"'
then

    log "FINAL_SUT_HEALTH=FAIL"

    exit 61
fi

log "FINAL_SUT_HEALTH=PASS"
log "POSTGRESQL_16=PASS"
log "ADAPTIVE_BOOTJAR=PASS"
log "RESEARCH_BDD=PASS"

section "GATE 85B RESULT"

log "GATE_85B_RUNTIME_HARNESS=PASS"
