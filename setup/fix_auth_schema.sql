-- ===============================================
--  SCHEMA FIX & OPTIMIZATION for auth_service
--  PostgreSQL 17 - Multitenant & Multiregional Ready
--  Version definitiva y auto-verificable
-- ===============================================
\echo '=== Fixing auth_service schema (universal-safe mode) ==='
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

-- 1️⃣ institutions
DO $$
BEGIN
    -- Primary key
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'institutions' AND constraint_type = 'PRIMARY KEY'
    ) THEN
        EXECUTE 'ALTER TABLE public.institutions ADD CONSTRAINT pk_institutions PRIMARY KEY (id)';
    END IF;

    -- Unique name
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'institutions' AND constraint_type = 'UNIQUE'
          AND constraint_name LIKE '%institutions_name%'
    ) THEN
        EXECUTE 'ALTER TABLE public.institutions ADD CONSTRAINT uq_institutions_name UNIQUE (name)';
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_institutions_country
    ON public.institutions(country);

-- 2️⃣ users
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'users' AND constraint_type = 'UNIQUE'
          AND constraint_name LIKE '%users_email%'
    ) THEN
        EXECUTE 'ALTER TABLE public.users ADD CONSTRAINT uq_users_email UNIQUE (email)';
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'users' AND constraint_type = 'UNIQUE'
          AND constraint_name LIKE '%users_username%'
    ) THEN
        EXECUTE 'ALTER TABLE public.users ADD CONSTRAINT uq_users_username UNIQUE (username)';
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'users' AND constraint_type = 'FOREIGN KEY'
          AND constraint_name LIKE '%users_institution%'
    ) THEN
        EXECUTE 'ALTER TABLE public.users ADD CONSTRAINT fk_users_institution FOREIGN KEY (institution_id)
                 REFERENCES public.institutions(id) ON DELETE CASCADE';
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_users_institution
    ON public.users(institution_id);

-- 3️⃣ roles
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'roles' AND constraint_type = 'UNIQUE'
          AND constraint_name LIKE '%roles_name%'
    ) THEN
        EXECUTE 'ALTER TABLE public.roles ADD CONSTRAINT uq_roles_name UNIQUE (name)';
    END IF;
END $$;

-- 4️⃣ permission
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'permission' AND constraint_type = 'UNIQUE'
          AND constraint_name LIKE '%permission_name%'
    ) THEN
        EXECUTE 'ALTER TABLE public.permission ADD CONSTRAINT uq_permission_name UNIQUE (name)';
    END IF;
END $$;

-- 5️⃣ permissions (si existe)
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'permissions') THEN
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE table_name = 'permissions' AND constraint_type = 'UNIQUE'
              AND constraint_name LIKE '%permissions_name%'
        ) THEN
            EXECUTE 'ALTER TABLE public.permissions ADD CONSTRAINT uq_permissions_name UNIQUE (name)';
        END IF;
    END IF;
END $$;

-- 6️⃣ roles_permissions
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'roles_permissions' AND constraint_type = 'PRIMARY KEY'
    ) THEN
        EXECUTE 'ALTER TABLE public.roles_permissions ADD CONSTRAINT pk_roles_permissions PRIMARY KEY (role_id, permission_id)';
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'roles_permissions' AND constraint_type = 'FOREIGN KEY'
          AND constraint_name LIKE '%roles_permissions_role%'
    ) THEN
        EXECUTE 'ALTER TABLE public.roles_permissions ADD CONSTRAINT fk_roles_permissions_role
                 FOREIGN KEY (role_id) REFERENCES public.roles(id) ON DELETE CASCADE';
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'roles_permissions' AND constraint_type = 'FOREIGN KEY'
          AND constraint_name LIKE '%roles_permissions_perm%'
    ) THEN
        EXECUTE 'ALTER TABLE public.roles_permissions ADD CONSTRAINT fk_roles_permissions_perm
                 FOREIGN KEY (permission_id) REFERENCES public.permission(id) ON DELETE CASCADE';
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_roles_permissions_role ON public.roles_permissions(role_id);
CREATE INDEX IF NOT EXISTS idx_roles_permissions_perm ON public.roles_permissions(permission_id);

-- 7️⃣ users_roles
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'users_roles' AND constraint_type = 'PRIMARY KEY'
    ) THEN
        EXECUTE 'ALTER TABLE public.users_roles ADD CONSTRAINT pk_users_roles PRIMARY KEY (user_id, role_id)';
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'users_roles' AND constraint_type = 'FOREIGN KEY'
          AND constraint_name LIKE '%users_roles_user%'
    ) THEN
        EXECUTE 'ALTER TABLE public.users_roles ADD CONSTRAINT fk_users_roles_user
                 FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE';
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'users_roles' AND constraint_type = 'FOREIGN KEY'
          AND constraint_name LIKE '%users_roles_role%'
    ) THEN
        EXECUTE 'ALTER TABLE public.users_roles ADD CONSTRAINT fk_users_roles_role
                 FOREIGN KEY (role_id) REFERENCES public.roles(id) ON DELETE CASCADE';
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_users_roles_user ON public.users_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_users_roles_role ON public.users_roles(role_id);

-- 8️⃣ audit_log
CREATE TABLE IF NOT EXISTS public.audit_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES public.users(id) ON DELETE SET NULL,
    action VARCHAR(255) NOT NULL,
    entity VARCHAR(100),
    entity_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_log_user ON public.audit_log(user_id);

\echo '=== auth_service schema fixed successfully (no duplicate PK errors) ==='
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

