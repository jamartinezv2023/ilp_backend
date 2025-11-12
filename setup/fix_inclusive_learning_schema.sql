\echo === Fixing inclusive_learning schema (universal-safe mode) ===
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

-- ============================================================================
-- 1) Infra común: auditoría + versionamiento
-- ============================================================================

-- 1.1) Tabla de auditoría (idempotente)
CREATE TABLE IF NOT EXISTS public.audit_log(
  id           BIGSERIAL PRIMARY KEY,
  table_name   text        NOT NULL,
  row_pk       text        NOT NULL,
  op           text        NOT NULL,   -- I,U,D
  changed_at   timestamptz NOT NULL DEFAULT now(),
  changed_by   text        NULL,
  before_row   jsonb       NULL,
  after_row    jsonb       NULL
);

-- 1.2) Tabla de versionamiento global (una fila por base)
CREATE TABLE IF NOT EXISTS public.data_version(
  id       SMALLINT PRIMARY KEY DEFAULT 1,
  version  BIGINT   NOT NULL DEFAULT 1,
  updated_at timestamptz NOT NULL DEFAULT now()
);

INSERT INTO public.data_version(id,version)
VALUES (1,1)
ON CONFLICT (id) DO NOTHING;

-- 1.3) Funciones utilitarias (set_updated_at y auditoría)
CREATE OR REPLACE FUNCTION public.fn_touch_updated_at()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  NEW.updated_at := now();
  RETURN NEW;
END$$;

CREATE OR REPLACE FUNCTION public.fn_bump_data_version()
RETURNS void LANGUAGE plpgsql AS $$
BEGIN
  UPDATE public.data_version
     SET version = version + 1,
         updated_at = now()
   WHERE id = 1;
END$$;

CREATE OR REPLACE FUNCTION public.fn_audit_row()
RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE
  v_pk text;
BEGIN
  -- Se intenta construir una PK textual (id o (id,identifier))
  IF TG_OP = 'INSERT' THEN
    v_pk := COALESCE(NEW.id::text, COALESCE(NEW.identifier::text,'?'));
    INSERT INTO public.audit_log(table_name,row_pk,op,changed_by,before_row,after_row)
    VALUES (TG_TABLE_NAME, v_pk, 'I', current_user, NULL, to_jsonb(NEW));
    PERFORM public.fn_bump_data_version();
    RETURN NEW;
  ELSIF TG_OP = 'UPDATE' THEN
    v_pk := COALESCE(NEW.id::text, COALESCE(NEW.identifier::text,'?'));
    INSERT INTO public.audit_log(table_name,row_pk,op,changed_by,before_row,after_row)
    VALUES (TG_TABLE_NAME, v_pk, 'U', current_user, to_jsonb(OLD), to_jsonb(NEW));
    PERFORM public.fn_bump_data_version();
    RETURN NEW;
  ELSIF TG_OP = 'DELETE' THEN
    v_pk := COALESCE(OLD.id::text, COALESCE(OLD.identifier::text,'?'));
    INSERT INTO public.audit_log(table_name,row_pk,op,changed_by,before_row,after_row)
    VALUES (TG_TABLE_NAME, v_pk, 'D', current_user, to_jsonb(OLD), NULL);
    PERFORM public.fn_bump_data_version();
    RETURN OLD;
  END IF;
  RETURN NULL;
END$$;

-- ============================================================================
-- 2) Normalización de tablas core (tenant, users)
-- ============================================================================

-- 2.1) TENANT (crear si no existe, compat. con tu carga previa)
CREATE TABLE IF NOT EXISTS public.tenant (
  id                SERIAL PRIMARY KEY,
  identifier        varchar(50)  NOT NULL,
  name              varchar(255) NOT NULL,
  code_dane         varchar(50),
  type              varchar(100),
  sector            varchar(50),
  nature            varchar(50),
  academic_level    varchar(100),
  entity_certifier  varchar(255),
  continent         varchar(100) DEFAULT 'South America',
  country_code      varchar(5)   DEFAULT 'CO',
  country_name      varchar(100) DEFAULT 'Colombia',
  department        varchar(100),
  municipality      varchar(100),
  zone              varchar(50),
  address           varchar(255),
  phone             varchar(50),
  email             varchar(150),
  ai_cluster_label  varchar(100),
  created_at        timestamp without time zone DEFAULT now(),
  updated_at        timestamp without time zone DEFAULT now()
);

-- UNIQUEs y CHECKs seguros
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
     WHERE conname='uq_tenant_identifier'
       AND conrelid='public.tenant'::regclass
  ) THEN
    ALTER TABLE public.tenant
      ADD CONSTRAINT uq_tenant_identifier UNIQUE(identifier);
  END IF;

  -- ISO-like (acepta 2–5 para multi-región pero evita basura)
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
     WHERE conname='chk_tenant_country_code_len'
       AND conrelid='public.tenant'::regclass
  ) THEN
    ALTER TABLE public.tenant
      ADD CONSTRAINT chk_tenant_country_code_len
      CHECK (char_length(country_code) BETWEEN 2 AND 5);
  END IF;
END$$;

-- Índices de descubrimiento y filtrado
CREATE INDEX IF NOT EXISTS idx_tenant_country          ON public.tenant(country_code, department);
CREATE INDEX IF NOT EXISTS idx_tenant_municipality     ON public.tenant(municipality);
CREATE INDEX IF NOT EXISTS idx_tenant_zone             ON public.tenant(zone);
CREATE INDEX IF NOT EXISTS idx_tenant_ai               ON public.tenant(ai_cluster_label);
CREATE INDEX IF NOT EXISTS idx_tenant_identifier_name  ON public.tenant(identifier, name);

-- Triggers de updated_at + auditoría
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_trigger WHERE tgname='trg_tenant_touch'
  ) THEN
    CREATE TRIGGER trg_tenant_touch
      BEFORE UPDATE ON public.tenant
      FOR EACH ROW EXECUTE FUNCTION public.fn_touch_updated_at();
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_trigger WHERE tgname='trg_audit_tenant'
  ) THEN
    CREATE TRIGGER trg_audit_tenant
      AFTER INSERT OR UPDATE OR DELETE ON public.tenant
      FOR EACH ROW EXECUTE FUNCTION public.fn_audit_row();
  END IF;
END$$;

-- 2.2) USERS (usuarios plataforma central del módulo inclusive_learning)
CREATE TABLE IF NOT EXISTS public.users (
  id         SERIAL PRIMARY KEY,
  username   varchar(50)  UNIQUE,
  email      varchar(120) UNIQUE,
  full_name  varchar(120),
  password   varchar(100),
  created_at timestamp without time zone DEFAULT now(),
  updated_at timestamp without time zone DEFAULT now()
);

-- Índices y constraints (si no existen)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
     WHERE conname='uq_users_username_incl'
       AND conrelid='public.users'::regclass
  ) THEN
    ALTER TABLE public.users ADD CONSTRAINT uq_users_username_incl UNIQUE (username);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
     WHERE conname='uq_users_email_incl'
       AND conrelid='public.users'::regclass
  ) THEN
    ALTER TABLE public.users ADD CONSTRAINT uq_users_email_incl UNIQUE (email);
  END IF;
END$$;

CREATE INDEX IF NOT EXISTS idx_users_username_incl ON public.users(username);
CREATE INDEX IF NOT EXISTS idx_users_email_incl    ON public.users(email);

-- Triggers para users
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname='trg_users_touch_incl') THEN
    CREATE TRIGGER trg_users_touch_incl
      BEFORE UPDATE ON public.users
      FOR EACH ROW EXECUTE FUNCTION public.fn_touch_updated_at();
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname='trg_audit_users_incl') THEN
    CREATE TRIGGER trg_audit_users_incl
      AFTER INSERT OR UPDATE OR DELETE ON public.users
      FOR EACH ROW EXECUTE FUNCTION public.fn_audit_row();
  END IF;
END$$;

-- ============================================================================
-- 3) Preparación multi-tenant / replicación (metadatos recomendados)
--    (no FK cross-database; se habilita compatibilidad para federación)
-- ============================================================================

-- Tabla opcional de metadatos de sincronización por tenant (para CDC / colas)
CREATE TABLE IF NOT EXISTS public.tenant_sync_meta (
  tenant_id    int  NOT NULL,
  last_sync_at timestamptz,
  sync_token   text,
  PRIMARY KEY (tenant_id),
  CONSTRAINT fk_tenant_sync_meta FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE
);

-- Índices útiles
CREATE INDEX IF NOT EXISTS idx_sync_last_at ON public.tenant_sync_meta(last_sync_at);

-- Auditoría
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname='trg_audit_tenant_sync_meta') THEN
    CREATE TRIGGER trg_audit_tenant_sync_meta
      AFTER INSERT OR UPDATE OR DELETE ON public.tenant_sync_meta
      FOR EACH ROW EXECUTE FUNCTION public.fn_audit_row();
  END IF;
END$$;

\echo === inclusive_learning schema fixed successfully ===

-- ============================================================
-- 🔍 BLOQUE DE VERIFICACIÓN UNIVERSAL DE INTEGRIDAD Y AUDITORÍA
-- ============================================================

\echo === Starting schema verification summary ===

-- Listar todas las tablas del esquema público
\echo --- Tables in schema public ---
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

-- Verificar claves primarias
\echo --- Primary Keys ---
SELECT conrelid::regclass AS table_name,
       conname AS constraint_name,
       pg_get_constraintdef(c.oid)
FROM pg_constraint c
JOIN pg_namespace n ON n.oid = c.connamespace
WHERE contype = 'p'
  AND n.nspname = 'public'
ORDER BY table_name;

-- Verificar claves foráneas
\echo --- Foreign Keys ---
SELECT conrelid::regclass AS table_name,
       conname AS constraint_name,
       pg_get_constraintdef(c.oid)
FROM pg_constraint c
JOIN pg_namespace n ON n.oid = c.connamespace
WHERE contype = 'f'
  AND n.nspname = 'public'
ORDER BY table_name;

-- Verificar índices creados
\echo --- Indexes ---
SELECT tablename AS table_name,
       indexname AS index_name,
       indexdef AS definition
FROM pg_indexes
WHERE schemaname = 'public'
ORDER BY tablename;

-- Verificar triggers (auditoría, versionado, etc.)
\echo --- Triggers ---
SELECT event_object_table AS table_name,
       trigger_name,
       event_manipulation AS event,
       action_statement AS definition
FROM information_schema.triggers
WHERE trigger_schema = 'public'
ORDER BY event_object_table;

-- Verificar funciones de auditoría activas
\echo --- Audit Functions ---
SELECT p.proname AS function_name,
       pg_get_functiondef(p.oid) AS definition
FROM pg_proc p
JOIN pg_namespace n ON n.oid = p.pronamespace
WHERE n.nspname = 'public'
  AND p.proname LIKE 'fn_audit_%'
ORDER BY function_name;

\echo --- Verification complete: schema integrity OK ✅ ---

