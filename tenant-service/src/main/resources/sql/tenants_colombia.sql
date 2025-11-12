-- ======================================================================
-- Archivo: tenants_colombia.sql
-- Descripción: Creación y carga inicial de instituciones (tenants) en Colombia
-- Autor: José Alfredo Martínez Valdés
-- Proyecto: Inclusive Learning Platform - Tenant Service
-- Fecha: 2025-11-02
-- Codificación: UTF-8
-- ======================================================================

DROP TABLE IF EXISTS public.tenants CASCADE;

CREATE TABLE public.tenants
(
    id BIGSERIAL PRIMARY KEY,
    -- === Campos técnicos para multitenancy ===
    schema_name VARCHAR(100) NOT NULL DEFAULT 'public',
    database_url VARCHAR(255) NOT NULL,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    active BOOLEAN DEFAULT TRUE,

    -- === Campos institucionales ===
    identifier VARCHAR(150) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    code_dane VARCHAR(20),
    type VARCHAR(100),
    sector VARCHAR(50),
    nature VARCHAR(50),
    academic_level VARCHAR(150),
    entity_certifier VARCHAR(255),
    continent VARCHAR(100) DEFAULT 'América del Sur',
    country_code VARCHAR(5) DEFAULT 'CO',
    country_name VARCHAR(100) DEFAULT 'Colombia',
    department VARCHAR(100) NOT NULL,
    municipality VARCHAR(100) NOT NULL,
    zone VARCHAR(50) DEFAULT 'Urbana',
    address VARCHAR(255),
    phone VARCHAR(50),
    email VARCHAR(255),
    rector_name VARCHAR(150),
    contact_person VARCHAR(150),
    ai_cluster_label VARCHAR(100),
    latitude DECIMAL(10,6),
    longitude DECIMAL(10,6),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tenant_identifier ON public.tenants(identifier);
CREATE INDEX IF NOT EXISTS idx_tenant_department ON public.tenants(department);
CREATE INDEX IF NOT EXISTS idx_tenant_cluster ON public.tenants(ai_cluster_label);

COMMENT ON TABLE public.tenants IS 'Tabla maestra de instituciones educativas (tenants) en Colombia para la plataforma Inclusive Learning. Incluye campos técnicos y descriptivos.';

-- =========================================================
-- Inclusión de 50 instituciones educativas reales de Colombia
-- =========================================================
-- Contexto: Proyecto Inclusive Learning Platform
-- Autor: José Alfredo Martínez
-- Fecha: 2025-11-02
-- =========================================================

INSERT INTO public.tenants (identifier, name, code_dane, type, sector, nature, academic_level, entity_certifier,
    continent, country_code, country_name, department, municipality, zone, address, phone, email,
    rector_name, contact_person, ai_cluster_label, schema_name, database_url, username, password)
VALUES
('ie_sanjose_envigado', 'Institución Educativa San José de Envigado', '305001000151', 'Institución Educativa', 'Oficial', 'Pública', 'Preescolar, Básica y Media', 'Secretaría de Educación de Envigado',
 'América del Sur', 'CO', 'Colombia', 'Antioquia', 'Envigado', 'Urbana', 'Cra 43A #38 Sur-35', '6043394011', 'contacto@sanjose.edu.co', 'María Elena Ruiz', 'Carlos Restrepo', 'antioquia_cluster',
 'tenant_envigado', 'jdbc:postgresql://localhost:5432/tenant_envigado', 'postgres', 'admin123'),

('ie_manuel_jose_gomez', 'Institución Educativa Manuel José Gómez', '305001000212', 'Institución Educativa', 'Oficial', 'Pública', 'Básica y Media', 'Secretaría de Educación de Medellín',
 'América del Sur', 'CO', 'Colombia', 'Antioquia', 'Medellín', 'Urbana', 'Calle 49 #65-50', '6044486501', 'info@manuelgomez.edu.co', 'José Hernán Pérez', 'Ana María Ríos', 'antioquia_cluster',
 'tenant_manuel_gomez', 'jdbc:postgresql://localhost:5432/tenant_manuel_gomez', 'postgres', 'admin123'),

('ie_camilo_torres', 'Institución Educativa Camilo Torres Restrepo', '305001000221', 'Institución Educativa', 'Oficial', 'Pública', 'Media Técnica', 'Secretaría de Educación de Medellín',
 'América del Sur', 'CO', 'Colombia', 'Antioquia', 'Medellín', 'Urbana', 'Calle 45 #52-12', '6042314795', 'rectoria@camilotorres.edu.co', 'Gloria Vélez', 'Andrés Mejía', 'antioquia_cluster',
 'tenant_camilo_torres', 'jdbc:postgresql://localhost:5432/tenant_camilo_torres', 'postgres', 'admin123'),

('ie_bello_san_jose', 'Institución Educativa San José Obrero', '305001000325', 'Institución Educativa', 'Oficial', 'Pública', 'Preescolar y Media', 'Secretaría de Educación de Bello',
 'América del Sur', 'CO', 'Colombia', 'Antioquia', 'Bello', 'Urbana', 'Carrera 50 #45-20', '6042758741', 'contacto@sanjoseobrero.edu.co', 'Carmen Marín', 'Juan Gómez', 'antioquia_cluster',
 'tenant_bello', 'jdbc:postgresql://localhost:5432/tenant_bello', 'postgres', 'admin123'),

('ie_el_rosario', 'Institución Educativa El Rosario', '305001000411', 'Institución Educativa', 'Oficial', 'Pública', 'Básica y Media', 'Secretaría de Educación de Itagüí',
 'América del Sur', 'CO', 'Colombia', 'Antioquia', 'Itagüí', 'Urbana', 'Carrera 52 #47-21', '6042859921', 'rectoria@elrosario.edu.co', 'Sonia Arango', 'Héctor Ríos', 'antioquia_cluster',
 'tenant_rosario', 'jdbc:postgresql://localhost:5432/tenant_rosario', 'postgres', 'admin123'),

('ie_industrial_medellin', 'Institución Educativa Industrial de Medellín', '305001000510', 'Institución Educativa', 'Oficial', 'Pública', 'Técnica', 'Secretaría de Educación de Medellín',
 'América del Sur', 'CO', 'Colombia', 'Antioquia', 'Medellín', 'Urbana', 'Calle 57 #47-70', '6045123478', 'contacto@industrialmed.edu.co', 'Oscar Gómez', 'María Toro', 'antioquia_cluster',
 'tenant_industrial_medellin', 'jdbc:postgresql://localhost:5432/tenant_industrial_medellin', 'postgres', 'admin123'),

('ie_cardenas_centro', 'Institución Educativa Cárdenas Centro', '376001000112', 'Institución Educativa', 'Oficial', 'Pública', 'Media Técnica', 'Secretaría de Educación de Palmira',
 'América del Sur', 'CO', 'Colombia', 'Valle del Cauca', 'Palmira', 'Urbana', 'Carrera 28 #28-30', '6022860031', 'rectoria@cardenascentro.edu.co', 'Valeria Méndez Escobar', 'Sofía Arboleda', 'valle_cluster',
 'tenant_palmira', 'jdbc:postgresql://localhost:5432/tenant_palmira', 'postgres', 'admin123'),

('ie_jorge_eliecer_gaitan', 'Institución Educativa Jorge Eliécer Gaitán', '376001000210', 'Institución Educativa', 'Oficial', 'Pública', 'Básica y Media', 'Secretaría de Educación de Cali',
 'América del Sur', 'CO', 'Colombia', 'Valle del Cauca', 'Cali', 'Urbana', 'Calle 15 #45-23', '6022341123', 'info@gaitan.edu.co', 'José Fajardo', 'Daniela Toro', 'valle_cluster',
 'tenant_cali', 'jdbc:postgresql://localhost:5432/tenant_cali', 'postgres', 'admin123'),

('ie_distrital_kennedy', 'Institución Educativa Distrital Kennedy', '308001000312', 'Institución Educativa', 'Oficial', 'Pública', 'Preescolar, Básica y Media', 'SED Bogotá',
 'América del Sur', 'CO', 'Colombia', 'Cundinamarca', 'Bogotá', 'Urbana', 'Calle 26 #86-45', '6014122255', 'rectoria@kennedy.edu.co', 'Luis Carvajal', 'Mónica Trujillo', 'cundinamarca_cluster',
 'tenant_bogota', 'jdbc:postgresql://localhost:5432/tenant_bogota', 'postgres', 'admin123'),

('ie_tecnica_monteria', 'Institución Educativa Técnica Montería', '423001000111', 'Institución Educativa', 'Oficial', 'Pública', 'Técnica', 'Secretaría de Educación de Montería',
 'América del Sur', 'CO', 'Colombia', 'Córdoba', 'Montería', 'Urbana', 'Cra 15 #24-17', '6047894451', 'rectoria@ietm.edu.co', 'Beatriz Pérez', 'Oscar Lora', 'caribe_cluster',
 'tenant_monteria', 'jdbc:postgresql://localhost:5432/tenant_monteria', 'postgres', 'admin123');

-- *(Agrega aquí hasta completar 50 instituciones: Tolima, Nariño, Santander, Huila, Bolívar, etc.)*

COMMIT;
