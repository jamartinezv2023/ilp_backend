-- ==============================================================
-- UNIVERSAL DETAILED DATABASE VERIFICATION SCRIPT (ASCII SAFE)
-- Autor: Inclusive Learning Platform - System Integrity Module
-- Fecha: 2025-11-07
-- Descripción:
--   Auditoría avanzada de los esquemas del ecosistema:
--   - auth_service
--   - inclusive_learning
--   - adaptive_education_db
--   Incluye inicialización automática de funciones auxiliares.
-- ==============================================================

\echo ===========================================================
\echo === UNIVERSAL DETAILED DATABASE VERIFICATION STARTED ===
\echo ===========================================================

\set ON_ERROR_STOP on
\set VERBOSE off
SET client_min_messages TO WARNING;
SET datestyle TO ISO, DMY;

-- ===========================================================
-- STEP 1: Ensure helper functions exist in all databases
-- ===========================================================
\echo === Initializing helper functions in all target databases ===

\connect postgres postgres
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_proc WHERE proname = 'fn_timer') THEN
    CREATE OR REPLACE FUNCTION public.fn_timer(label TEXT)
    RETURNS VOID AS $$
    DECLARE
      v_now TIMESTAMP := clock_timestamp();
    BEGIN
      RAISE NOTICE '[TIMER] % - %', label, v_now;
    END;
    $$ LANGUAGE plpgsql;
  END IF;
END;
$$;

\connect auth_service postgres
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_proc WHERE proname = 'fn_timer') THEN
    CREATE OR REPLACE FUNCTION public.fn_timer(label TEXT)
    RETURNS VOID AS $$
    DECLARE
      v_now TIMESTAMP := clock_timestamp();
    BEGIN
      RAISE NOTICE '[TIMER] % - %', label, v_now;
    END;
    $$ LANGUAGE plpgsql;
  END IF;
END;
$$;

\connect inclusive_learning postgres
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_proc WHERE proname = 'fn_timer') THEN
    CREATE OR REPLACE FUNCTION public.fn_timer(label TEXT)
    RETURNS VOID AS $$
    DECLARE
      v_now TIMESTAMP := clock_timestamp();
    BEGIN
      RAISE NOTICE '[TIMER] % - %', label, v_now;
    END;
    $$ LANGUAGE plpgsql;
  END IF;
END;
$$;

\connect adaptive_education_db postgres
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_proc WHERE proname = 'fn_timer') THEN
    CREATE OR REPLACE FUNCTION public.fn_timer(label TEXT)
    RETURNS VOID AS $$
    DECLARE
      v_now TIMESTAMP := clock_timestamp();
    BEGIN
      RAISE NOTICE '[TIMER] % - %', label, v_now;
    END;
    $$ LANGUAGE plpgsql;
  END IF;
END;
$$;

\connect postgres postgres
\echo === Helper functions verified ===

-- ===========================================================
-- STEP 2: Create structure comparison helper
-- ===========================================================
CREATE OR REPLACE FUNCTION public.fn_compare_structures()
RETURNS TABLE(db_name TEXT, table_count INT, column_count INT) AS $$
BEGIN
  RETURN QUERY
  SELECT current_database(),
         COUNT(*) AS table_count,
         SUM((SELECT COUNT(*) FROM information_schema.columns c
              WHERE c.table_schema='public' AND c.table_name=t.table_name)) AS column_count
  FROM information_schema.tables t
  WHERE table_schema='public';
END;
$$ LANGUAGE plpgsql;

-- ===========================================================
-- SECTION 1 - AUTH_SERVICE
-- ===========================================================
\echo === Verifying schema: auth_service ===
\connect auth_service postgres
SELECT fn_timer('auth_service start');

\echo --- Table row counts ---
SELECT table_name,
       (xpath('/row/cnt/text()', query_to_xml(format('SELECT COUNT(*) AS cnt FROM %I.%I',
       table_schema, table_name), false, true, ''))[1])::int AS total_rows
FROM information_schema.tables
WHERE table_schema='public'
ORDER BY total_rows DESC NULLS LAST;

\echo --- Table sizes (MB) ---
SELECT table_name,
       pg_size_pretty(pg_total_relation_size(quote_ident(table_name))) AS total_size
FROM information_schema.tables
WHERE table_schema='public'
ORDER BY pg_total_relation_size(quote_ident(table_name)) DESC;

\echo --- Structure summary ---
SELECT * FROM fn_compare_structures();

SELECT fn_timer('auth_service end');
\echo === auth_service verification complete ===

-- ===========================================================
-- SECTION 2 - INCLUSIVE_LEARNING
-- ===========================================================
\echo === Verifying schema: inclusive_learning ===
\connect inclusive_learning postgres
SELECT fn_timer('inclusive_learning start');

\echo --- Table row counts ---
SELECT table_name,
       (xpath('/row/cnt/text()', query_to_xml(format('SELECT COUNT(*) AS cnt FROM %I.%I',
       table_schema, table_name), false, true, ''))[1])::int AS total_rows
FROM information_schema.tables
WHERE table_schema='public'
ORDER BY total_rows DESC NULLS LAST;

\echo --- Table sizes (MB) ---
SELECT table_name,
       pg_size_pretty(pg_total_relation_size(quote_ident(table_name))) AS total_size
FROM information_schema.tables
WHERE table_schema='public'
ORDER BY pg_total_relation_size(quote_ident(table_name)) DESC;

\echo --- Structure summary ---
SELECT * FROM fn_compare_structures();

SELECT fn_timer('inclusive_learning end');
\echo === inclusive_learning verification complete ===

-- ===========================================================
-- SECTION 3 - ADAPTIVE_EDUCATION_DB
-- ===========================================================
\echo === Verifying schema: adaptive_education_db ===
\connect adaptive_education_db postgres
SELECT fn_timer('adaptive_education_db start');

\echo --- Table row counts ---
SELECT table_name,
       (xpath('/row/cnt/text()', query_to_xml(format('SELECT COUNT(*) AS cnt FROM %I.%I',
       table_schema, table_name), false, true, ''))[1])::int AS total_rows
FROM information_schema.tables
WHERE table_schema='public'
ORDER BY total_rows DESC NULLS LAST;

\echo --- Table sizes (MB) ---
SELECT table_name,
       pg_size_pretty(pg_total_relation_size(quote_ident(table_name))) AS total_size
FROM information_schema.tables
WHERE table_schema='public'
ORDER BY pg_total_relation_size(quote_ident(table_name)) DESC;

\echo --- Structure summary ---
SELECT * FROM fn_compare_structures();

SELECT fn_timer('adaptive_education_db end');
\echo === adaptive_education_db verification complete ===

-- ===========================================================
-- SECTION 4 - GLOBAL STRUCTURAL COMPARISON
-- ===========================================================
\echo === Comparing schema structures across all databases ===
\connect auth_service postgres

CREATE EXTENSION IF NOT EXISTS dblink;

WITH global_summary AS (
  SELECT 'auth_service' AS db, * FROM fn_compare_structures()
  UNION ALL
  SELECT 'inclusive_learning' AS db,
         * FROM dblink('dbname=inclusive_learning',
         'SELECT * FROM fn_compare_structures()')
         AS t(db_name TEXT, table_count INT, column_count INT)
  UNION ALL
  SELECT 'adaptive_education_db' AS db,
         * FROM dblink('dbname=adaptive_education_db',
         'SELECT * FROM fn_compare_structures()')
         AS t(db_name TEXT, table_count INT, column_count INT)
)
SELECT db, table_count, column_count FROM global_summary;

\echo ===========================================================
\echo === DETAILED VERIFICATION COMPLETED SUCCESSFULLY ===
\echo ===========================================================


