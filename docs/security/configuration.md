# Secure Configuration Baseline

This repository must not store real credentials, passwords, JWT secrets, API keys, or cloud access keys.

## Local setup

Copy the example environment file:

cp .env.example .env

Then replace placeholder values locally.

## Required variables

- POSTGRES_USER
- POSTGRES_PASSWORD
- DB_USERNAME
- DB_PASSWORD
- JWT_SECRET
- SONAR_TOKEN (GitHub Actions only)

## Rules

- Never commit .env
- Never hardcode production passwords
- Use GitHub Actions secrets for CI/CD
- Use cloud secret managers for production deployments
