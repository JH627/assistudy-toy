#!/usr/bin/env bash
# k6 load test runner (Docker 기반)
# 사용법: bash k6/run.sh [시나리오번호] [이메일] [비밀번호]
#   bash k6/run.sh 1
#   bash k6/run.sh 2 k6test@test.com Test@1234
#   bash k6/run.sh 3 k6test@test.com Test@1234

set -e

SCENARIO=${1:-1}
TEST_EMAIL=${2:-""}
TEST_PASSWORD=${3:-""}

BASE_URL="http://host.docker.internal:8080"
INFLUXDB_URL="http://host.docker.internal:8087/k6"

# 절대 경로 계산 (Windows Git Bash 호환)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RESULTS_DIR="${SCRIPT_DIR}/results"
mkdir -p "${RESULTS_DIR}"

case $SCENARIO in
  1)
    echo "=== Scenario 01: Auth Flow ==="
    MSYS_NO_PATHCONV=1 docker run --rm \
      -v "${SCRIPT_DIR}:/scripts" \
      -v "${RESULTS_DIR}:/results" \
      grafana/k6 run \
      --out "influxdb=${INFLUXDB_URL}" \
      -e "BASE_URL=${BASE_URL}" \
      /scripts/scenarios/01-auth.js
    ;;
  2)
    if [ -z "$TEST_EMAIL" ] || [ -z "$TEST_PASSWORD" ]; then
      echo "Usage: bash k6/run.sh 2 <email> <password>"
      exit 1
    fi
    echo "=== Scenario 02: Study Room ==="
    MSYS_NO_PATHCONV=1 docker run --rm \
      -v "${SCRIPT_DIR}:/scripts" \
      -v "${RESULTS_DIR}:/results" \
      grafana/k6 run \
      --out "influxdb=${INFLUXDB_URL}" \
      -e "BASE_URL=${BASE_URL}" \
      -e "TEST_EMAIL=${TEST_EMAIL}" \
      -e "TEST_PASSWORD=${TEST_PASSWORD}" \
      /scripts/scenarios/02-study-room.js
    ;;
  3)
    if [ -z "$TEST_EMAIL" ] || [ -z "$TEST_PASSWORD" ]; then
      echo "Usage: bash k6/run.sh 3 <email> <password>"
      exit 1
    fi
    echo "=== Scenario 03: Log Pipeline ==="
    MSYS_NO_PATHCONV=1 docker run --rm \
      -v "${SCRIPT_DIR}:/scripts" \
      -v "${RESULTS_DIR}:/results" \
      grafana/k6 run \
      --out "influxdb=${INFLUXDB_URL}" \
      -e "BASE_URL=${BASE_URL}" \
      -e "TEST_EMAIL=${TEST_EMAIL}" \
      -e "TEST_PASSWORD=${TEST_PASSWORD}" \
      /scripts/scenarios/03-log-pipeline.js
    ;;
  *)
    echo "Unknown scenario: $SCENARIO (use 1, 2, or 3)"
    exit 1
    ;;
esac
