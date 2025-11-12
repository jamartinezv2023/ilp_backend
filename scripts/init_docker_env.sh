#!/usr/bin/env bash
# Initialize .env for docker compose
cat > .env <<EOF
POSTGRES_AUTH_USER=auth_user
POSTGRES_AUTH_PASSWORD=auth_pass
POSTGRES_EDU_USER=edu_user
POSTGRES_EDU_PASSWORD=edu_pass
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=minioadmin
EOF
echo ".env created. Edit credentials before production."
