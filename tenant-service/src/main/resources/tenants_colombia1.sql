-- ======================================================================
-- Archivo: tenants_colombia.sql  (UTF-8)
-- Descripción:
--   Script para poblar las tablas `public.tenants` y `public.tenants_extended`
--   con 50 instituciones educativas colombianas para el ecosistema
--   Inclusive Learning Platform.
--   
-- Autor: José Alfredo Martínez
-- Fecha: 2025-11-03
-- Base de datos: PostgreSQL 14+
-- ======================================================================

-- ===================================================
-- 🔁 Reinicialización segura
-- ===================================================
DROP VIEW IF EXISTS public.tenants_view CASCADE;
DROP TABLE IF EXISTS public.tenants_extended CASCADE;
DROP TABLE IF EXISTS public.tenants CASCADE;

-- ===================================================
-- 🏗️ Tabla principal usada por el microservicio
-- ===================================================
CREATE TABLE IF NOT EXISTS public.tenants (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    schema VARCHAR(255) NOT NULL,
    databaseUrl VARCHAR(512) NOT NULL,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- ===================================================
-- 📚 Tabla extendida con metadatos educativos
-- ===================================================
CREATE TABLE IF NOT EXISTS public.tenants_extended (
    id BIGSERIAL PRIMARY KEY,
    identifier VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    department VARCHAR(100) NOT NULL,
    municipality VARCHAR(100) NOT NULL,
    type VARCHAR(100),
    sector VARCHAR(50),
    academic_level VARCHAR(200),
    rector VARCHAR(150),
    email VARCHAR(150),
    phone VARCHAR(50),
    address VARCHAR(200),
    cluster_region VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===================================================
-- 🔗 Vista unificada de datos
-- ===================================================
CREATE OR REPLACE VIEW public.tenants_view AS
SELECT 
    t.id,
    t.name,
    t.schema,
    t.databaseUrl,
    t.username,
    t.password,
    e.department,
    e.municipality,
    e.type,
    e.sector,
    e.academic_level,
    e.rector,
    e.cluster_region
FROM public.tenants t
JOIN public.tenants_extended e ON LOWER(t.schema) = LOWER(e.identifier);

-- ===================================================
-- 🌍 Registros iniciales de instituciones por región
-- ===================================================

-- ===========================================
-- 🏝️ REGIÓN CARIBE (10 instituciones)
-- ===========================================
INSERT INTO public.tenants (name, schema, databaseUrl, username, password, active) VALUES
('Colegio Simón Bolívar - Barranquilla', 'tenant_barranquilla', 'jdbc:postgresql://localhost:5432/tenant_barranquilla', 'postgres', 'postgres', true),
('Institución Educativa San José - Cartagena', 'tenant_cartagena', 'jdbc:postgresql://localhost:5432/tenant_cartagena', 'postgres', 'postgres', true),
('Colegio Técnico de Santa Marta', 'tenant_santamarta', 'jdbc:postgresql://localhost:5432/tenant_santamarta', 'postgres', 'postgres', true),
('Instituto La Candelaria - Montería', 'tenant_monteria', 'jdbc:postgresql://localhost:5432/tenant_monteria', 'postgres', 'postgres', true),
('Colegio Rafael Núñez - Sincelejo', 'tenant_sincelejo', 'jdbc:postgresql://localhost:5432/tenant_sincelejo', 'postgres', 'postgres', true),
('Colegio Departamental La Paz - Valledupar', 'tenant_valledupar', 'jdbc:postgresql://localhost:5432/tenant_valledupar', 'postgres', 'postgres', true),
('Institución Educativa Ciénaga del Oro', 'tenant_cienagaoro', 'jdbc:postgresql://localhost:5432/tenant_cienagaoro', 'postgres', 'postgres', true),
('Colegio San Martín - Riohacha', 'tenant_riohacha', 'jdbc:postgresql://localhost:5432/tenant_riohacha', 'postgres', 'postgres', true),
('Colegio Liceo Caribe - Magangué', 'tenant_magangue', 'jdbc:postgresql://localhost:5432/tenant_magangue', 'postgres', 'postgres', true),
('Institución Educativa Los Olivos - Lorica', 'tenant_lorica', 'jdbc:postgresql://localhost:5432/tenant_lorica', 'postgres', 'postgres', true);

-- ===========================================
-- 🌊 REGIÓN PACÍFICO (10 instituciones)
-- ===========================================
INSERT INTO public.tenants (name, schema, databaseUrl, username, password, active) VALUES
('Instituto Tecnológico del Pacífico - Buenaventura', 'tenant_buenaventura', 'jdbc:postgresql://localhost:5432/tenant_buenaventura', 'postgres', 'postgres', true),
('Colegio María Auxiliadora - Tumaco', 'tenant_tumaco', 'jdbc:postgresql://localhost:5432/tenant_tumaco', 'postgres', 'postgres', true),
('Institución Educativa San Antonio - Quibdó', 'tenant_quibdo', 'jdbc:postgresql://localhost:5432/tenant_quibdo', 'postgres', 'postgres', true),
('Colegio Nuestra Señora del Mar - Guapi', 'tenant_guapi', 'jdbc:postgresql://localhost:5432/tenant_guapi', 'postgres', 'postgres', true),
('Colegio Las Aguas - Palmira', 'tenant_palmira', 'jdbc:postgresql://localhost:5432/tenant_palmira', 'postgres', 'postgres', true),
('Instituto del Litoral Pacífico - Tumaco', 'tenant_litoral', 'jdbc:postgresql://localhost:5432/tenant_litoral', 'postgres', 'postgres', true),
('Colegio San Vicente - Cali', 'tenant_cali', 'jdbc:postgresql://localhost:5432/tenant_cali', 'postgres', 'postgres', true),
('Institución Educativa El Carmen - Dagua', 'tenant_dagua', 'jdbc:postgresql://localhost:5432/tenant_dagua', 'postgres', 'postgres', true),
('Colegio Buenaventura del Mar - Buenaventura', 'tenant_mar', 'jdbc:postgresql://localhost:5432/tenant_mar', 'postgres', 'postgres', true),
('Colegio San Pedro Claver - Cali', 'tenant_sanpedrocali', 'jdbc:postgresql://localhost:5432/tenant_sanpedrocali', 'postgres', 'postgres', true);

-- ===========================================
-- 🏔️ REGIÓN ANDINA (15 instituciones)
-- ===========================================
INSERT INTO public.tenants (name, schema, databaseUrl, username, password, active) VALUES
('Colegio Distrital Santa María - Bogotá', 'tenant_bogota', 'jdbc:postgresql://localhost:5432/tenant_bogota', 'postgres', 'postgres', true),
('Colegio Técnico Industrial - Medellín', 'tenant_medellin', 'jdbc:postgresql://localhost:5432/tenant_medellin', 'postgres', 'postgres', true),
('Instituto Rafael Pombo - Tunja', 'tenant_tunja', 'jdbc:postgresql://localhost:5432/tenant_tunja', 'postgres', 'postgres', true),
('Colegio Los Andes - Bucaramanga', 'tenant_bucaramanga', 'jdbc:postgresql://localhost:5432/tenant_bucaramanga', 'postgres', 'postgres', true),
('Colegio San José - Cúcuta', 'tenant_cucuta', 'jdbc:postgresql://localhost:5432/tenant_cucuta', 'postgres', 'postgres', true),
('Colegio Central - Manizales', 'tenant_manizales', 'jdbc:postgresql://localhost:5432/tenant_manizales', 'postgres', 'postgres', true),
('Colegio San Francisco - Pereira', 'tenant_pereira', 'jdbc:postgresql://localhost:5432/tenant_pereira', 'postgres', 'postgres', true),
('Colegio Nuestra Señora de la Paz - Ibagué', 'tenant_ibague', 'jdbc:postgresql://localhost:5432/tenant_ibague', 'postgres', 'postgres', true),
('Instituto Técnico Boyacá - Tunja', 'tenant_itboyaca', 'jdbc:postgresql://localhost:5432/tenant_itboyaca', 'postgres', 'postgres', true),
('Colegio La Salle - Neiva', 'tenant_neiva', 'jdbc:postgresql://localhost:5432/tenant_neiva', 'postgres', 'postgres', true),
('Colegio Camacho - Pasto', 'tenant_pasto', 'jdbc:postgresql://localhost:5432/tenant_pasto', 'postgres', 'postgres', true),
('Colegio del Norte - Sogamoso', 'tenant_sogamoso', 'jdbc:postgresql://localhost:5432/tenant_sogamoso', 'postgres', 'postgres', true),
('Colegio San Luis - Duitama', 'tenant_duitama', 'jdbc:postgresql://localhost:5432/tenant_duitama', 'postgres', 'postgres', true),
('Colegio Salesiano - Armenia', 'tenant_armenia', 'jdbc:postgresql://localhost:5432/tenant_armenia', 'postgres', 'postgres', true),
('Colegio Mayor del Tolima - Ibagué', 'tenant_mayortolima', 'jdbc:postgresql://localhost:5432/tenant_mayortolima', 'postgres', 'postgres', true);

-- ===========================================
-- 🏞️ REGIÓN ORINOQUÍA (7 instituciones)
-- ===========================================
INSERT INTO public.tenants (name, schema, databaseUrl, username, password, active) VALUES
('Institución Educativa Los Llanos - Villavicencio', 'tenant_villavicencio', 'jdbc:postgresql://localhost:5432/tenant_villavicencio', 'postgres', 'postgres', true),
('Colegio San Martín - Yopal', 'tenant_yopal', 'jdbc:postgresql://localhost:5432/tenant_yopal', 'postgres', 'postgres', true),
('Colegio Meta Real - Puerto Gaitán', 'tenant_puertogaitan', 'jdbc:postgresql://localhost:5432/tenant_puertogaitan', 'postgres', 'postgres', true),
('Instituto Regional del Casanare - Paz de Ariporo', 'tenant_pazdeariporo', 'jdbc:postgresql://localhost:5432/tenant_pazdeariporo', 'postgres', 'postgres', true),
('Colegio Nueva Esperanza - Arauca', 'tenant_arauca', 'jdbc:postgresql://localhost:5432/tenant_arauca', 'postgres', 'postgres', true),
('Instituto del Orinoco - Tame', 'tenant_tame', 'jdbc:postgresql://localhost:5432/tenant_tame', 'postgres', 'postgres', true),
('Colegio Los Fundadores - Puerto Carreño', 'tenant_puertocarreno', 'jdbc:postgresql://localhost:5432/tenant_puertocarreno', 'postgres', 'postgres', true);

-- ===========================================
-- 🌳 REGIÓN AMAZONÍA (8 instituciones)
-- ===========================================
INSERT INTO public.tenants (name, schema, databaseUrl, username, password, active) VALUES
('Instituto Educativo Amazonas - Leticia', 'tenant_leticia', 'jdbc:postgresql://localhost:5432/tenant_leticia', 'postgres', 'postgres', true),
('Colegio San José - Puerto Nariño', 'tenant_puertonarino', 'jdbc:postgresql://localhost:5432/tenant_puertonarino', 'postgres', 'postgres', true),
('Instituto Frontera Verde - La Chorrera', 'tenant_lachorrera', 'jdbc:postgresql://localhost:5432/tenant_lachorrera', 'postgres', 'postgres', true),
('Colegio Indígena del Amazonas - Tarapacá', 'tenant_tarapaca', 'jdbc:postgresql://localhost:5432/tenant_tarapaca', 'postgres', 'postgres', true),
('Instituto Ecológico de Araracuara', 'tenant_araracuara', 'jdbc:postgresql://localhost:5432/tenant_araracuara', 'postgres', 'postgres', true),
('Colegio San Francisco del Río - Mirití', 'tenant_miriti', 'jdbc:postgresql://localhost:5432/tenant_miriti', 'postgres', 'postgres', true),
('Instituto Educativo La Pedrera', 'tenant_lapedrera', 'jdbc:postgresql://localhost:5432/tenant_lapedrera', 'postgres', 'postgres', true),
('Colegio Frontera Sur - Puerto Leguízamo', 'tenant_puertoleguizamo', 'jdbc:postgresql://localhost:5432/tenant_puertoleguizamo', 'postgres', 'postgres', true);

-- ======================================================================
-- ✅ Fin del script de carga de tenants Colombia
-- ======================================================================
