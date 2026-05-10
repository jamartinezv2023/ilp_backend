#!/usr/bin/env bash
set -euo pipefail

BROKER="${BROKER:-localhost:9092}"

docker exec ilp-redpanda rpk topic create ilp.assessment.completed.v1 \
  --brokers "$BROKER" \
  --partitions 3 \
  --replicas 1 || true

docker exec ilp-redpanda rpk topic create ilp.learning-path.inference-requested.v1 \
  --brokers "$BROKER" \
  --partitions 3 \
  --replicas 1 || true

docker exec ilp-redpanda rpk topic create ilp.learning-path.recommended.v1 \
  --brokers "$BROKER" \
  --partitions 3 \
  --replicas 1 || true

docker exec ilp-redpanda rpk topic list --brokers "$BROKER"
