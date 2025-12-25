CREATE TABLE IF NOT EXISTS auth_role (
  id UUID PRIMARY KEY,
  tenant_id UUID NOT NULL,
  code VARCHAR(80) NOT NULL,
  name VARCHAR(120) NOT NULL,
  description VARCHAR(400),
  is_system BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_auth_role_tenant_code
  ON auth_role (tenant_id, code);

CREATE INDEX IF NOT EXISTS ix_auth_role_tenant_id
  ON auth_role (tenant_id);

CREATE TABLE IF NOT EXISTS auth_permission (
  id UUID PRIMARY KEY,
  tenant_id UUID NOT NULL,
  perm_key VARCHAR(140) NOT NULL,
  name VARCHAR(160) NOT NULL,
  description VARCHAR(400),
  is_system BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_auth_permission_tenant_key
  ON auth_permission (tenant_id, perm_key);

CREATE INDEX IF NOT EXISTS ix_auth_permission_tenant_id
  ON auth_permission (tenant_id);

CREATE TABLE IF NOT EXISTS auth_role_permission (
  id UUID PRIMARY KEY,
  tenant_id UUID NOT NULL,
  role_id UUID NOT NULL,
  permission_id UUID NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_auth_role_permission_tenant_role_perm
  ON auth_role_permission (tenant_id, role_id, permission_id);

CREATE INDEX IF NOT EXISTS ix_auth_rp_tenant_id
  ON auth_role_permission (tenant_id);

CREATE INDEX IF NOT EXISTS ix_auth_rp_role_id
  ON auth_role_permission (role_id);

CREATE INDEX IF NOT EXISTS ix_auth_rp_permission_id
  ON auth_role_permission (permission_id);

CREATE TABLE IF NOT EXISTS auth_user_role (
  id UUID PRIMARY KEY,
  tenant_id UUID NOT NULL,
  user_id UUID NOT NULL,
  role_id UUID NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_auth_user_role_tenant_user_role
  ON auth_user_role (tenant_id, user_id, role_id);

CREATE INDEX IF NOT EXISTS ix_auth_ur_tenant_id
  ON auth_user_role (tenant_id);

CREATE INDEX IF NOT EXISTS ix_auth_ur_user_id
  ON auth_user_role (user_id);

CREATE INDEX IF NOT EXISTS ix_auth_ur_role_id
  ON auth_user_role (role_id);
