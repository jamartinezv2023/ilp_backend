#!/usr/bin/env bash
# Run local environment with docker compose
set -e
export COMPOSE_HTTP_TIMEOUT=200
docker compose up --build
