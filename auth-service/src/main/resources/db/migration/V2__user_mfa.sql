-- Crea tabla para MFA TOTP
CREATE TABLE IF NOT EXISTS users_mfa (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    totp_secret VARCHAR(128),
    mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    last_enabled_at TIMESTAMP,
    last_disabled_at TIMESTAMP,
    last_rotated_at TIMESTAMP,
    issuer VARCHAR(100),
    label VARCHAR(150)
);