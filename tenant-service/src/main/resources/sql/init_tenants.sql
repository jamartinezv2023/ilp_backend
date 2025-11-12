-- Esquema por defecto
CREATE SCHEMA IF NOT EXISTS public;

-- Esquemas de ejemplo para tenants
CREATE SCHEMA IF NOT EXISTS tenant_a;
CREATE SCHEMA IF NOT EXISTS tenant_b;

-- Tabla de ejemplo en cada tenant
CREATE TABLE IF NOT EXISTS public.users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50),
    email VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS tenant_a.users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50),
    email VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS tenant_b.users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50),
    email VARCHAR(100)
);

INSERT INTO public.users (username, email) VALUES ('admin_public', 'admin@public.com');
INSERT INTO tenant_a.users (username, email) VALUES ('admin_tenant_a', 'admin@a.com');
INSERT INTO tenant_b.users (username, email) VALUES ('admin_tenant_b', 'admin@b.com');
