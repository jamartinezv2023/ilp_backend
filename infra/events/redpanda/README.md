# Redpanda Event Streaming Platform

Local Kafka-compatible event broker for ILP backend.

## Start

```bash
docker compose -f infra/events/redpanda/docker-compose.redpanda.yml up -d
```

## Create topics

```bash
./infra/events/redpanda/create-topics.sh
```

## Topics

- ilp.assessment.completed.v1
- ilp.learning-path.inference-requested.v1
- ilp.learning-path.recommended.v1

## Stop

```bash
docker compose -f infra/events/redpanda/docker-compose.redpanda.yml down
```

## Reset data

```bash
docker compose -f infra/events/redpanda/docker-compose.redpanda.yml down -v
```
