-- =========================================================
-- Inclusive Learning Platform - Estructura de tabla Tenant
-- =========================================================
-- Autor: José Alfredo Martínez
-- Fecha: 2025-11-02
-- Propósito: Crear una estructura de datos escalable, UTF-8 y
--             preparada para IA, aprendizaje automático y análisis.
-- =========================================================

-- Si existe, eliminarla primero
DROP TABLE IF EXISTS tenant CASCADE;

CREATE TABLE tenant (
    id SERIAL PRIMARY KEY,
    identifier VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    code_dane VARCHAR(50),
    nit VARCHAR(50),
    type VARCHAR(100),
    sector VARCHAR(50),
    nature VARCHAR(50),
    academic_level VARCHAR(100),
    entity_certifier VARCHAR(255),

    -- Jerarquía geográfica
    continent VARCHAR(100) DEFAULT 'América del Sur',
    country_code VARCHAR(5) DEFAULT 'CO',
    country_name VARCHAR(100) DEFAULT 'Colombia',
    department VARCHAR(100),
    municipality VARCHAR(100),
    zone VARCHAR(50),  -- Urbana / Rural
    address VARCHAR(255),
    latitude NUMERIC(10, 6),
    longitude NUMERIC(10, 6),

    -- Estructura interna
    main_headquarters VARCHAR(255),
    num_headquarters INT DEFAULT 1,
    num_journeys INT DEFAULT 1,
    journey_types VARCHAR(255),
    num_students INT DEFAULT 0,
    num_teachers INT DEFAULT 0,
    num_administrative INT DEFAULT 0,

    -- Contacto y metadatos
    phone VARCHAR(50),
    email VARCHAR(150),
    website VARCHAR(255),
    rector_name VARCHAR(255),
    contact_person VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Campos IA y analíticos
    ai_cluster_label VARCHAR(100),
    data_volume_estimate BIGINT DEFAULT 0,
    activity_score NUMERIC(8, 3) DEFAULT 0,
    ai_enabled BOOLEAN DEFAULT TRUE
);

-- Índices optimizados
CREATE INDEX idx_tenant_country_depto ON tenant (country_code, department);
CREATE INDEX idx_tenant_municipality ON tenant (municipality);
CREATE INDEX idx_tenant_ai_score ON tenant (ai_cluster_label, activity_score);
CREATE INDEX idx_tenant_zone ON tenant (zone);
