-- patch_auth_seed.sql
\echo === Patch auth_service: insertar institutions, usuarios y roles_permissions ===
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

-- 1) 20 institutions (dominios realistas). No falla si ya existen.
INSERT INTO public.institutions(active, code, country, domain, language, logourl, name, settings) VALUES
(true,'CO-ANT-0001','CO','udea.edu.co','es-CO',NULL,'Universidad de Antioquia',NULL),
(true,'CO-BOG-0001','CO','unal.edu.co','es-CO',NULL,'Universidad Nacional de Colombia',NULL),
(true,'ES-ALC-0001','ES','ua.es','es-ES',NULL,'Universidad de Alicante',NULL),
(true,'CO-VAL-0001','CO','univalle.edu.co','es-CO',NULL,'Universidad del Valle',NULL),
(true,'CO-ANT-0002','CO','itm.edu.co','es-CO',NULL,'Instituto Tecnológico Metropolitano',NULL),
(true,'CO-ANT-0003','CO','ces.edu.co','es-CO',NULL,'Universidad CES',NULL),
(true,'CO-ANT-0004','CO','eafit.edu.co','es-CO',NULL,'Universidad EAFIT',NULL),
(true,'CO-CAL-0001','CO','icesi.edu.co','es-CO',NULL,'Universidad Icesi',NULL),
(true,'CO-ATL-0001','CO','uninorte.edu.co','es-CO',NULL,'Universidad del Norte',NULL),
(true,'CO-SAN-0001','CO','uis.edu.co','es-CO',NULL,'Universidad Industrial de Santander',NULL),
(true,'CO-BOG-0002','CO','uniandes.edu.co','es-CO',NULL,'Universidad de los Andes',NULL),
(true,'CO-BOG-0003','CO','javeriana.edu.co','es-CO',NULL,'Pontificia Universidad Javeriana',NULL),
(true,'CO-BOG-0004','CO','ucatolica.edu.co','es-CO',NULL,'Universidad Católica de Colombia',NULL),
(true,'CO-ANT-0005','CO','upb.edu.co','es-CO',NULL,'Universidad Pontificia Bolivariana',NULL),
(true,'CO-ANT-0006','CO','unac.edu.co','es-CO',NULL,'Universidad Adventista de Colombia',NULL),
(true,'CO-ANT-0007','CO','salazarherrera.edu.co','es-CO',NULL,'Institución Universitaria Salazar y Herrera',NULL),
(true,'CO-VAL-0002','CO','correounivalle.edu.co','es-CO',NULL,'Red Académica Univalle',NULL),
(true,'CO-ANT-0008','CO','colmayor.edu.co','es-CO',NULL,'Institución Universitaria Colegio Mayor',NULL),
(true,'CO-BOG-0005','CO','pedagogica.edu.co','es-CO',NULL,'Universidad Pedagógica Nacional',NULL),
(true,'CO-BOG-0006','CO','esap.edu.co','es-CO',NULL,'ESAP Escuela Superior de Administración Pública',NULL)
ON CONFLICT (domain) DO NOTHING;

-- 2) Asegurar que las 4 permissions existan
INSERT INTO public.permission(name) VALUES
('READ_USERS'),('CREATE_USERS'),('DELETE_USERS'),('MANAGE_ROLES')
ON CONFLICT (name) DO NOTHING;

-- 3) Completar el mapeo de roles_permissions
WITH r_admin AS (SELECT id FROM public.roles WHERE name='ADMIN'),
     r_user  AS (SELECT id FROM public.roles WHERE name='USER')
INSERT INTO public.roles_permissions(role_id, permission_id)
SELECT r_admin.id, p.id
FROM r_admin, public.permission p
ON CONFLICT DO NOTHING;

WITH r_user AS (SELECT id FROM public.roles WHERE name='USER'),
     p_read AS (SELECT id FROM public.permission WHERE name='READ_USERS')
INSERT INTO public.roles_permissions(role_id, permission_id)
SELECT r_user.id, p_read.id
FROM r_user, p_read
ON CONFLICT DO NOTHING;

-- 4) Crear usuarios administrativos con contraseña encriptada
WITH map(domain,email,username) AS (
  VALUES
   ('udea.edu.co','admin@udea.edu.co','admin.udea'),
   ('unal.edu.co','admin@unal.edu.co','admin.unal'),
   ('ua.es','rectoria@ua.es','rectoria.ua'),
   ('univalle.edu.co','admin@univalle.edu.co','admin.univalle'),
   ('itm.edu.co','admin@itm.edu.co','admin.itm'),
   ('ces.edu.co','admin@ces.edu.co','admin.ces'),
   ('eafit.edu.co','admin@eafit.edu.co','admin.eafit'),
   ('icesi.edu.co','rectoria@icesi.edu.co','rectoria.icesi'),
   ('uninorte.edu.co','admin@uninorte.edu.co','admin.uninorte'),
   ('uis.edu.co','admin@uis.edu.co','admin.uis'),
   ('uniandes.edu.co','admin@uniandes.edu.co','admin.uniandes'),
   ('javeriana.edu.co','rectoria@javeriana.edu.co','rectoria.javeriana'),
   ('ucatolica.edu.co','admin@ucatolica.edu.co','admin.ucatolica'),
   ('upb.edu.co','admin@upb.edu.co','admin.upb'),
   ('unac.edu.co','admin@unac.edu.co','admin.unac'),
   ('salazarherrera.edu.co','admin@salazarherrera.edu.co','admin.salazarherrera'),
   ('correounivalle.edu.co','admin@correounivalle.edu.co','admin.redunivalle'),
   ('colmayor.edu.co','admin@colmayor.edu.co','admin.colmayor'),
   ('pedagogica.edu.co','admin@pedagogica.edu.co','admin.pedagogica'),
   ('esap.edu.co','admin@esap.edu.co','admin.esap')
),
inst AS (
  SELECT id, domain FROM public.institutions
)
INSERT INTO public.users(auth_provider,email,email_verified,enabled,password,username,account_status,created_at,institution_id)
SELECT
  'LOCAL',
  m.email,
  true,
  true,
  '$2a$10$Dow1EhzFo0r5aXh/5MBNe.g3FQuDHH02FRCi5pBFgG.ZaEqW2c9vS', -- BCrypt(Password123!)
  m.username,
  'ACTIVE',
  now(),
  i.id
FROM map m
JOIN inst i USING (domain)
ON CONFLICT (email) DO NOTHING;

-- 5) Asignar roles
WITH r_admin AS (SELECT id FROM public.roles WHERE name='ADMIN'),
     r_user  AS (SELECT id FROM public.roles WHERE name='USER'),
     u AS (
       SELECT id, ROW_NUMBER() OVER (ORDER BY id) rn
       FROM public.users
     )
INSERT INTO public.users_roles(user_id, role_id)
SELECT u.id, CASE WHEN u.rn <= 10 THEN r_admin.id ELSE r_user.id END
FROM u, r_admin, r_user
ON CONFLICT DO NOTHING;

\echo === Patch auth_service COMPLETADO ===
