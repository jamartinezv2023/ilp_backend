\echo === Fixing adaptive_education_db schema (universal-safe mode) ===
SET client_min_messages TO NOTICE;
SET search_path TO public;

-- ============================================================
-- SECCIÓN 1. Tablas base (audit_log, data_version, tenant_sync_meta)
-- ============================================================

CREATE TABLE IF NOT EXISTS public.audit_log (
  id              bigserial PRIMARY KEY,
  table_name      text NOT NULL,
  operation       text NOT NULL,
  record_id       bigint,
  old_data        jsonb,
  new_data        jsonb,
  changed_at      timestamptz DEFAULT now(),
  changed_by      text
);

CREATE TABLE IF NOT EXISTS public.data_version (
  id              bigserial PRIMARY KEY,
  table_name      text NOT NULL,
  record_id       bigint NOT NULL,
  version         int DEFAULT 1,
  changed_at      timestamptz DEFAULT now(),
  changed_by      text,
  data_snapshot   jsonb
);
-- ============================================================
-- SECCIÓN 0. Crear tabla tenant si no existe (multi-tenant base)
-- ============================================================

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = 'tenant') THEN
    CREATE TABLE public.tenant (
      id              serial PRIMARY KEY,
      institution_name varchar(150) NOT NULL,
      country         varchar(80),
      region          varchar(80),
      municipality    varchar(80),
      zone            varchar(50),
      contact_email   varchar(150),
      created_at      timestamptz DEFAULT now(),
      updated_at      timestamptz DEFAULT now()
    );
  END IF;
END$$;

CREATE INDEX IF NOT EXISTS idx_tenant_country ON public.tenant(country);
CREATE INDEX IF NOT EXISTS idx_tenant_municipality ON public.tenant(municipality);
CREATE INDEX IF NOT EXISTS idx_tenant_zone ON public.tenant(zone);
-- ============================================================
-- SECCIÓN 0. Crear tabla tenant y asegurar columna tenant_id en entidades dependientes
-- ============================================================

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = 'tenant') THEN
    CREATE TABLE public.tenant (
      id              serial PRIMARY KEY,
      institution_name varchar(150) NOT NULL,
      country         varchar(80),
      region          varchar(80),
      municipality    varchar(80),
      zone            varchar(50),
      contact_email   varchar(150),
      created_at      timestamptz DEFAULT now(),
      updated_at      timestamptz DEFAULT now()
    );
  END IF;
END$$;

CREATE INDEX IF NOT EXISTS idx_tenant_country ON public.tenant(country);
CREATE INDEX IF NOT EXISTS idx_tenant_municipality ON public.tenant(municipality);
CREATE INDEX IF NOT EXISTS idx_tenant_zone ON public.tenant(zone);

-- Verificar que las tablas que usan tenant_id realmente tengan la columna
DO $$
DECLARE
  t RECORD;
BEGIN
  FOR t IN SELECT tablename FROM pg_tables WHERE schemaname='public' AND tablename IN ('students','adaptive_tests','tenant_sync_meta')
  LOOP
    IF NOT EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_schema='public' AND table_name=t.tablename AND column_name='tenant_id'
    ) THEN
      EXECUTE format('ALTER TABLE public.%I ADD COLUMN tenant_id int;', t.tablename);
    END IF;
  END LOOP;
END$$;

CREATE TABLE IF NOT EXISTS public.tenant_sync_meta (
  tenant_id    int  NOT NULL,
  last_sync_at timestamptz,
  sync_token   text,
  PRIMARY KEY (tenant_id),
  CONSTRAINT fk_tenant_sync_meta FOREIGN KEY (tenant_id)
    REFERENCES public.tenant(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_sync_last_at ON public.tenant_sync_meta(last_sync_at);

-- ============================================================
-- SECCIÓN 2. Funciones de auditoría genéricas
-- ============================================================

CREATE OR REPLACE FUNCTION public.fn_audit_row()
RETURNS trigger AS $$
BEGIN
  INSERT INTO public.audit_log(table_name, operation, record_id, old_data, new_data, changed_by)
  VALUES (TG_TABLE_NAME, TG_OP, NEW.id, to_jsonb(OLD), to_jsonb(NEW), current_user);
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION public.fn_version_row()
RETURNS trigger AS $$
BEGIN
  INSERT INTO public.data_version(table_name, record_id, data_snapshot, changed_by)
  VALUES (TG_TABLE_NAME, NEW.id, to_jsonb(NEW), current_user);
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION public.fn_timestamp_update()
RETURNS trigger AS $$
BEGIN
  NEW.updated_at := now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- SECCIÓN 3. Normalización de entidades clave
-- ============================================================

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'students') THEN
    CREATE TABLE public.students (
      id              bigserial PRIMARY KEY,
      tenant_id       int NOT NULL,
      first_name      varchar(100),
      last_name       varchar(100),
      email           varchar(150) UNIQUE,
      birth_date      date,
      gender          varchar(20),
      created_at      timestamptz DEFAULT now(),
      updated_at      timestamptz DEFAULT now(),
      CONSTRAINT fk_students_tenant FOREIGN KEY (tenant_id)
        REFERENCES public.tenant(id) ON DELETE CASCADE
    );
  END IF;
END$$;

CREATE INDEX IF NOT EXISTS idx_students_tenant ON public.students(tenant_id);
CREATE INDEX IF NOT EXISTS idx_students_email ON public.students(email);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'educational_levels') THEN
    CREATE TABLE public.educational_levels (
      id          serial PRIMARY KEY,
      code        varchar(20) UNIQUE NOT NULL,
      name        varchar(100) NOT NULL,
      description text
    );
  END IF;
END$$;

-- ============================================================
-- SECCIÓN 4. Tablas de cuestionarios y resultados
-- ============================================================

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'adaptive_tests') THEN
    CREATE TABLE public.adaptive_tests (
      id              bigserial PRIMARY KEY,
      tenant_id       int NOT NULL,
      student_id      bigint NOT NULL,
      level_id        int,
      test_type       varchar(50),
      started_at      timestamptz DEFAULT now(),
      completed_at    timestamptz,
      score           numeric(5,2),
      status          varchar(20) DEFAULT 'pending',
      created_at      timestamptz DEFAULT now(),
      updated_at      timestamptz DEFAULT now(),
      CONSTRAINT fk_adaptive_test_tenant FOREIGN KEY (tenant_id)
        REFERENCES public.tenant(id) ON DELETE CASCADE,
      CONSTRAINT fk_adaptive_test_student FOREIGN KEY (student_id)
        REFERENCES public.students(id) ON DELETE CASCADE,
      CONSTRAINT fk_adaptive_test_level FOREIGN KEY (level_id)
        REFERENCES public.educational_levels(id)
    );
  END IF;
END$$;

CREATE INDEX IF NOT EXISTS idx_adaptive_tests_tenant ON public.adaptive_tests(tenant_id);
CREATE INDEX IF NOT EXISTS idx_adaptive_tests_student ON public.adaptive_tests(student_id);
CREATE INDEX IF NOT EXISTS idx_adaptive_tests_level ON public.adaptive_tests(level_id);

-- ============================================================
-- SECCIÓN 5. Triggers automáticos de auditoría y versionamiento
-- ============================================================

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_audit_adaptive_tests') THEN
    CREATE TRIGGER trg_audit_adaptive_tests
      AFTER INSERT OR UPDATE OR DELETE ON public.adaptive_tests
      FOR EACH ROW EXECUTE FUNCTION public.fn_audit_row();
  END IF;
END$$;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_version_adaptive_tests') THEN
    CREATE TRIGGER trg_version_adaptive_tests
      AFTER INSERT OR UPDATE ON public.adaptive_tests
      FOR EACH ROW EXECUTE FUNCTION public.fn_version_row();
  END IF;
END$$;

\echo === adaptive_education_db schema fixed successfully ===

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

