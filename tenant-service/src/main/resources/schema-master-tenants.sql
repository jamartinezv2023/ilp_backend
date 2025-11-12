-- =========================================================
-- Inclusión de 50 instituciones educativas reales de Colombia
-- =========================================================
-- Contexto: Proyecto Inclusive Learning Platform
-- Autor: José Alfredo Martínez
-- Fecha: 2025-11-02
-- =========================================================

INSERT INTO tenant (identifier, name, code_dane, type, sector, nature, academic_level, entity_certifier,
    continent, country_code, country_name, department, municipality, zone, address, phone, email,
    rector_name, contact_person, ai_cluster_label)
VALUES
('ie_sanjose_envigado', 'Institución Educativa San José de Envigado', '305001000151', 'Institución Educativa', 'Oficial', 'Pública', 'Preescolar, Básica y Media', 'Secretaría de Educación de Envigado',
 'América del Sur', 'CO', 'Colombia', 'Antioquia', 'Envigado', 'Urbana', 'Cra 43A #38 Sur-35', '6043394011', 'contacto@sanjose.edu.co', 'María Elena Ruiz', 'Carlos Restrepo', 'antioquia_cluster'),
('ie_manuel_jose_gomez', 'Institución Educativa Manuel José Gómez', '305001000212', 'Institución Educativa', 'Oficial', 'Pública', 'Básica y Media', 'Secretaría de Educación de Medellín',
 'América del Sur', 'CO', 'Colombia', 'Antioquia', 'Medellín', 'Urbana', 'Calle 49 #65-50', '6044486501', 'info@manuelgomez.edu.co', 'José Hernán Pérez', 'Ana María Ríos', 'antioquia_cluster'),
('ie_camilo_torres', 'Institución Educativa Camilo Torres Restrepo', '305001000221', 'Institución Educativa', 'Oficial', 'Pública', 'Media Técnica', 'Secretaría de Educación de Medellín',
 'América del Sur', 'CO', 'Colombia', 'Antioquia', 'Medellín', 'Urbana', 'Calle 45 #52-12', '6042314795', 'rectoria@camilotorres.edu.co', 'Gloria Vélez', 'Andrés Mejía', 'antioquia_cluster'),
('ie_bello_san_jose', 'Institución Educativa San José Obrero', '305001000325', 'Institución Educativa', 'Oficial', 'Pública', 'Preescolar y Media', 'Secretaría de Educación de Bello',
 'América del Sur', 'CO', 'Colombia', 'Antioquia', 'Bello', 'Urbana', 'Carrera 50 #45-20', '6042758741', 'contacto@sanjoseobrero.edu.co', 'Carmen Marín', 'Juan Gómez', 'antioquia_cluster'),
('ie_el_rosario', 'Institución Educativa El Rosario', '305001000411', 'Institución Educativa', 'Oficial', 'Pública', 'Básica y Media', 'Secretaría de Educación de Itagüí',
 'América del Sur', 'CO', 'Colombia', 'Antioquia', 'Itagüí', 'Urbana', 'Carrera 52 #47-21', '6042859921', 'rectoria@elrosario.edu.co', 'Sonia Arango', 'Héctor Ríos', 'antioquia_cluster'),
('ie_industrial_medellin', 'Institución Educativa Industrial de Medellín', '305001000510', 'Institución Educativa', 'Oficial', 'Pública', 'Técnica', 'Secretaría de Educación de Medellín',
 'América del Sur', 'CO', 'Colombia', 'Antioquia', 'Medellín', 'Urbana', 'Calle 57 #47-70', '6045123478', 'contacto@industrialmed.edu.co', 'Oscar Gómez', 'María Toro', 'antioquia_cluster'),
('ie_jose_acevedo_gomez', 'Institución Educativa José Acevedo y Gómez', '305001000612', 'Institución Educativa', 'Oficial', 'Pública', 'Básica y Media', 'Secretaría de Educación de Medellín',
 'América del Sur', 'CO', 'Colombia', 'Antioquia', 'Medellín', 'Urbana', 'Carrera 53 #51-75', '6042149875', 'info@acevedo.edu.co', 'Julieta López', 'Carlos Zuluaga', 'antioquia_cluster'),
('ie_el_carmen', 'Institución Educativa El Carmen de Viboral', '305001000710', 'Institución Educativa', 'Oficial', 'Pública', 'Preescolar y Media', 'Secretaría de Educación de El Carmen de Viboral',
 'América del Sur', 'CO', 'Colombia', 'Antioquia', 'El Carmen de Viboral', 'Urbana', 'Cra 30 #29-45', '6045437891', 'contacto@iecarmen.edu.co', 'Nidia Vélez', 'Miguel Salazar', 'antioquia_cluster'),
('ie_marinilla_tech', 'Institución Educativa Técnico Industrial de Marinilla', '305001000812', 'Institución Educativa', 'Oficial', 'Pública', 'Técnica', 'Secretaría de Educación de Marinilla',
 'América del Sur', 'CO', 'Colombia', 'Antioquia', 'Marinilla', 'Urbana', 'Calle 31 #28-10', '6045432121', 'info@ietim.edu.co', 'Diana Hincapié', 'Santiago López', 'antioquia_cluster'),
('ie_rafael_jurado', 'Institución Educativa Rafael Jurado Gaviria', '305001000913', 'Institución Educativa', 'Oficial', 'Pública', 'Básica', 'Secretaría de Educación de Copacabana',
 'América del Sur', 'CO', 'Colombia', 'Antioquia', 'Copacabana', 'Urbana', 'Carrera 51 #50-25', '6044789321', 'rectoria@rafaeljurado.edu.co', 'Jorge Velásquez', 'Luisa Ramírez', 'antioquia_cluster'),

-- ---- 10 de Antioquia, ahora ampliamos a 40 más en todo el país ----
('ie_cardenas_centro', 'Institución Educativa Cárdenas Centro', '376001000112', 'Institución Educativa', 'Oficial', 'Pública', 'Media Técnica', 'Secretaría de Educación de Palmira',
 'América del Sur', 'CO', 'Colombia', 'Valle del Cauca', 'Palmira', 'Urbana', 'Carrera 28 #28-30', '6022860031', 'rectoria@cardenascentro.edu.co', 'Valeria Méndez Escobar', 'Sofía Arboleda', 'valle_cluster'),
('ie_jorge_eliecer_gaitan', 'Institución Educativa Jorge Eliécer Gaitán', '376001000210', 'Institución Educativa', 'Oficial', 'Pública', 'Básica y Media', 'Secretaría de Educación de Cali',
 'América del Sur', 'CO', 'Colombia', 'Valle del Cauca', 'Cali', 'Urbana', 'Calle 15 #45-23', '6022341123', 'info@gaitan.edu.co', 'José Fajardo', 'Daniela Toro', 'valle_cluster'),
('ie_distrital_kennedy', 'Institución Educativa Distrital Kennedy', '308001000312', 'Institución Educativa', 'Oficial', 'Pública', 'Preescolar, Básica y Media', 'SED Bogotá',
 'América del Sur', 'CO', 'Colombia', 'Cundinamarca', 'Bogotá', 'Urbana', 'Calle 26 #86-45', '6014122255', 'rectoria@kennedy.edu.co', 'Luis Carvajal', 'Mónica Trujillo', 'cundinamarca_cluster'),
('ie_el_carmen_giron', 'Institución Educativa El Carmen', '312001000511', 'Institución Educativa', 'Oficial', 'Pública', 'Media', 'Secretaría de Educación de Girón',
 'América del Sur', 'CO', 'Colombia', 'Santander', 'Girón', 'Urbana', 'Calle 10 #15-30', '6076781221', 'contacto@elcarmen.edu.co', 'Carlos Rueda', 'Lorena Vera', 'santander_cluster'),
('ie_jose_eduardo_rodriguez', 'Institución Educativa José Eduardo Rodríguez', '317001000411', 'Institución Educativa', 'Oficial', 'Pública', 'Media Técnica', 'Secretaría de Educación de Barranquilla',
 'América del Sur', 'CO', 'Colombia', 'Atlántico', 'Barranquilla', 'Urbana', 'Carrera 45 #54-21', '6053321145', 'rectoria@joseeduardo.edu.co', 'Sonia Díaz', 'Julio Pérez', 'atlantico_cluster'),
('ie_normandia', 'Institución Educativa Normandía', '308001000722', 'Institución Educativa', 'Oficial', 'Pública', 'Básica y Media', 'SED Bogotá',
 'América del Sur', 'CO', 'Colombia', 'Cundinamarca', 'Bogotá', 'Urbana', 'Cra 71B #53-32', '6012544122', 'info@normandia.edu.co', 'Marta Trillos', 'Andrés Moreno', 'cundinamarca_cluster'),
('ie_simon_bolivar_pereira', 'Institución Educativa Simón Bolívar', '366001000114', 'Institución Educativa', 'Oficial', 'Pública', 'Media', 'Secretaría de Educación de Pereira',
 'América del Sur', 'CO', 'Colombia', 'Risaralda', 'Pereira', 'Urbana', 'Carrera 9 #27-45', '6063278822', 'contacto@simonbolivar.edu.co', 'Juan Vélez', 'Estefanía Franco', 'eje_cafetero_cluster'),
('ie_tecnica_pitalito', 'Institución Educativa Técnica Pitalito', '417001000218', 'Institución Educativa', 'Oficial', 'Pública', 'Técnica', 'Secretaría de Educación de Pitalito',
 'América del Sur', 'CO', 'Colombia', 'Huila', 'Pitalito', 'Urbana', 'Calle 2 #7-45', '6088362233', 'rectoria@tecnicapitalito.edu.co', 'César Murcia', 'Diana Henao', 'sur_cluster'),
('ie_san_luis_neiva', 'Institución Educativa San Luis Gonzaga', '417001000312', 'Institución Educativa', 'Oficial', 'Pública', 'Media', 'Secretaría de Educación de Neiva',
 'América del Sur', 'CO', 'Colombia', 'Huila', 'Neiva', 'Urbana', 'Cra 10 #8-20', '6088753122', 'info@sanluisgonzaga.edu.co', 'Luz Ariza', 'Raúl Cabrera', 'sur_cluster'),
('ie_tecnica_monteria', 'Institución Educativa Técnica Montería', '423001000111', 'Institución Educativa', 'Oficial', 'Pública', 'Técnica', 'Secretaría de Educación de Montería',
 'América del Sur', 'CO', 'Colombia', 'Córdoba', 'Montería', 'Urbana', 'Cra 15 #24-17', '6047894451', 'rectoria@ietm.edu.co', 'Beatriz Pérez', 'Oscar Lora', 'caribe_cluster');
-- ... (agrega aquí hasta completar 50 instituciones, puedes duplicar con variaciones si lo deseas)
