-- ======================================================================
-- Archivo: tenants_colombia.sql  (UTF-8)
-- Carga de 50 instituciones colombianas para Inclusive Learning Platform
-- Se puebla:
--   a) public.tenants_extended  (atributos ricos para IA)
--   b) public.tenants           (tabla minimal que usa el microservicio)
-- JDBC local: jdbc:postgresql://localhost:5432/<identifier>
-- ======================================================================

-- Asegurar tablas (base minimal ya la tienes; creamos/ajustamos la extendida)
CREATE TABLE IF NOT EXISTS public.tenants_extended (
    id               BIGSERIAL PRIMARY KEY,
    identifier       VARCHAR(100) UNIQUE NOT NULL,
    name             VARCHAR(255) NOT NULL,
    code_dane        VARCHAR(20),
    type             VARCHAR(100),
    sector           VARCHAR(50),
    nature           VARCHAR(50),
    academic_level   VARCHAR(200),
    entity_certifier VARCHAR(255),
    continent        VARCHAR(50)  DEFAULT 'América del Sur',
    country_code     VARCHAR(5)   DEFAULT 'CO',
    country_name     VARCHAR(100) DEFAULT 'Colombia',
    department       VARCHAR(100) NOT NULL,
    municipality     VARCHAR(100) NOT NULL,
    zone             VARCHAR(50)  DEFAULT 'Urbana',
    address          VARCHAR(255),
    phone            VARCHAR(50),
    email            VARCHAR(255),
    rector_name      VARCHAR(150),
    contact_person   VARCHAR(150),
    ai_cluster_label VARCHAR(80),
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tx_identifier   ON public.tenants_extended(identifier);
CREATE INDEX IF NOT EXISTS idx_tx_dept_mpio    ON public.tenants_extended(department, municipality);
CREATE INDEX IF NOT EXISTS idx_tx_cluster_zone ON public.tenants_extended(ai_cluster_label, zone);

-- ======================================================================
-- Dataset maestro (50 filas)
-- ======================================================================
WITH dataset(identifier, name, code_dane, type, sector, nature, academic_level, entity_certifier,
             continent, country_code, country_name, department, municipality, zone, address, phone, email,
             rector_name, contact_person, ai_cluster_label) AS (
    VALUES
    -- ------------------------
    -- ANDINA (15) – Antioquia, Cundinamarca, Santander, Boyacá, Tolima, Caldas, N. Santander
    -- ------------------------
    ('ie_ant_med_01','I.E. San Ignacio de Loyola','305001000151','Institución Educativa','Oficial','Pública','Preescolar, Básica y Media','SED Medellín','América del Sur','CO','Colombia','Antioquia','Medellín','Urbana','Cra 45 #67-23','6045123456','contacto@sanignacio.edu.co','María Elena Ruiz','Carlos Restrepo','andina_cluster'),
    ('ie_ant_med_02','I.E. Manuel José Gómez','305001000212','Institución Educativa','Oficial','Pública','Básica y Media','SED Medellín','América del Sur','CO','Colombia','Antioquia','Medellín','Urbana','Calle 49 #65-50','6044486501','info@manuelgomez.edu.co','José Hernán Pérez','Ana María Ríos','andina_cluster'),
    ('ie_ant_env_01','I.E. San José de Envigado','305001000151','Institución Educativa','Oficial','Pública','Preescolar, Básica y Media','SED Envigado','América del Sur','CO','Colombia','Antioquia','Envigado','Urbana','Cra 43A #38 Sur-35','6043394011','contacto@sanjose.edu.co','Gloria Ospina','Juan Restrepo','andina_cluster'),
    ('ie_ant_bel_01','I.E. Pedro Justo Berrío','305001000325','Institución Educativa','Oficial','Pública','Básica y Media','SED Bello','América del Sur','CO','Colombia','Antioquia','Bello','Urbana','Cl 52 #45-10','6044789000','info@pjberrio.edu.co','Carmen Marín','Juan Gómez','andina_cluster'),
    ('ie_ant_ita_01','I.E. El Rosario (Itagüí)','305001000411','Institución Educativa','Oficial','Pública','Básica y Media','SED Itagüí','América del Sur','CO','Colombia','Antioquia','Itagüí','Urbana','Cra 52 #47-21','6042859921','rectoria@elrosario.edu.co','Sonia Arango','Héctor Ríos','andina_cluster'),
    ('ie_ant_mar_01','I.E. Téc. Industrial de Marinilla','305001000812','Institución Educativa','Oficial','Pública','Técnica','SED Marinilla','América del Sur','CO','Colombia','Antioquia','Marinilla','Urbana','Calle 31 #28-10','6045432121','info@ietim.edu.co','Diana Hincapié','Santiago López','andina_cluster'),
    ('ie_ant_rgo_01','I.E. Jorge Alberto Gómez (Rionegro)','305001000920','Institución Educativa','Oficial','Pública','Básica y Media','SED Rionegro','América del Sur','CO','Colombia','Antioquia','Rionegro','Urbana','Cra 45 #54-20','6045612233','contacto@jag.edu.co','Julio Castaño','Paula Vélez','andina_cluster'),
    ('ie_cun_bog_01','IED Kennedy','308001000312','Institución Educativa','Oficial','Pública','Preescolar, Básica y Media','SED Bogotá','América del Sur','CO','Colombia','Cundinamarca','Bogotá','Urbana','Calle 26 #86-45','6014122255','rectoria@kennedy.edu.co','Luis Carvajal','Mónica Trujillo','andina_cluster'),
    ('ie_cun_bog_02','IED Normandía','308001000722','Institución Educativa','Oficial','Pública','Básica y Media','SED Bogotá','América del Sur','CO','Colombia','Cundinamarca','Bogotá','Urbana','Cra 71B #53-32','6012544122','info@normandia.edu.co','Marta Trillos','Andrés Moreno','andina_cluster'),
    ('ie_cun_zba_01','I.E. Departamental Zipaquirá','325001000115','Institución Educativa','Oficial','Pública','Media','Gob. Cundinamarca','América del Sur','CO','Colombia','Cundinamarca','Zipaquirá','Urbana','Cra 10 #7-11','6018523344','contacto@iedz.edu.co','Carolina Nieves','Julián Suárez','andina_cluster'),
    ('ie_san_bga_01','I.E. Camacho Carreño','312001000411','Institución Educativa','Oficial','Pública','Media','SED Bucaramanga','América del Sur','CO','Colombia','Santander','Bucaramanga','Urbana','Cra 12 #23-20','6076457788','rectoria@camacho.edu.co','Héctor Durán','Laura Prada','andina_cluster'),
    ('ie_boy_tun_01','I.E. Silvino Rodríguez (Tunja)','315001000211','Institución Educativa','Oficial','Pública','Básica y Media','SED Tunja','América del Sur','CO','Colombia','Boyacá','Tunja','Urbana','Cl 18 #11-45','6087421122','info@silvino.edu.co','Nelly Mora','Pedro Téllez','andina_cluster'),
    ('ie_tol_iba_01','I.E. San Simón (Ibagué)','321001000310','Institución Educativa','Oficial','Pública','Media','SED Ibagué','América del Sur','CO','Colombia','Tolima','Ibagué','Urbana','Cra 4 #12-50','6082773344','contacto@sansimon.edu.co','César Vargas','Adriana León','andina_cluster'),
    ('ie_cal_man_01','I.E. INEM Baldomero Sanín (Manizales)','317001000118','Institución Educativa','Oficial','Pública','Media Técnica','SED Manizales','América del Sur','CO','Colombia','Caldas','Manizales','Urbana','Av. Santander 45-50','6068872211','inem@manizales.edu.co','Claudia Zapata','Óscar Gil','andina_cluster'),
    ('ie_nds_cuc_01','I.E. Provincial de Occidente (Cúcuta)','313001000217','Institución Educativa','Oficial','Pública','Básica y Media','SED Cúcuta','América del Sur','CO','Colombia','Norte de Santander','Cúcuta','Urbana','Cl 12 #5-20','6075817788','provocc@cucuta.edu.co','Rodolfo Díaz','Angela Vega','andina_cluster'),

    -- ------------------------
    -- CARIBE (10) – Atlántico, Bolívar, Córdoba, Sucre, Magdalena, Cesar, La Guajira, San Andrés
    -- ------------------------
    ('ie_atl_baq_01','I.E. José E. Caro','317001000411','Institución Educativa','Oficial','Pública','Media','SED Barranquilla','América del Sur','CO','Colombia','Atlántico','Barranquilla','Urbana','Cra 45 #54-21','6053321145','rectoria@jecaro.edu.co','Sonia Díaz','Julio Pérez','caribe_cluster'),
    ('ie_atl_sol_01','I.E. Villa Estadio (Soledad)','317001000512','Institución Educativa','Oficial','Pública','Básica y Media','Gob. Atlántico','América del Sur','CO','Colombia','Atlántico','Soledad','Urbana','Cl 30 #22-40','6053012211','villa@soledad.edu.co','Rafael Castro','Katherine Meza','caribe_cluster'),
    ('ie_bol_ctg_01','I.E. INEM Cartagena','318001000210','Institución Educativa','Oficial','Pública','Media Técnica','SED Cartagena','América del Sur','CO','Colombia','Bolívar','Cartagena','Urbana','Av. Pedro de Heredia 25-00','6056643322','inem@cartagena.edu.co','Hernán Narváez','Yuri Porras','caribe_cluster'),
    ('ie_bol_tur_01','I.E. San José (Turbaco)','318001000312','Institución Educativa','Oficial','Pública','Básica y Media','Gob. Bolívar','América del Sur','CO','Colombia','Bolívar','Turbaco','Urbana','Cl 13 #9-10','6056677788','sanjose@turbaco.edu.co','María Parra','Jorge Cotes','caribe_cluster'),
    ('ie_cor_mon_01','I.E. Téc. Montería','423001000111','Institución Educativa','Oficial','Pública','Técnica','SED Montería','América del Sur','CO','Colombia','Córdoba','Montería','Urbana','Cra 15 #24-17','6047894451','rectoria@ietm.edu.co','Beatriz Pérez','Óscar Lora','caribe_cluster'),
    ('ie_suc_sje_01','I.E. Simón Araujo (Sincelejo)','419001000215','Institución Educativa','Oficial','Pública','Media','SED Sincelejo','América del Sur','CO','Colombia','Sucre','Sincelejo','Urbana','Cl 20 #18-33','6052821122','araujo@sincelejo.edu.co','Carlos Beltrán','Dayana Montes','caribe_cluster'),
    ('ie_mag_smr_01','I.E. Distrital El Prado (Santa Marta)','420001000311','Institución Educativa','Oficial','Pública','Básica y Media','SED Santa Marta','América del Sur','CO','Colombia','Magdalena','Santa Marta','Urbana','Av. Libertador 10-20','6054302211','prado@santamarta.edu.co','Luisa Quintero','Mario Campo','caribe_cluster'),
    ('ie_ces_val_01','I.E. Garupal (Valledupar)','421001000118','Institución Educativa','Oficial','Pública','Media','SED Valledupar','América del Sur','CO','Colombia','Cesar','Valledupar','Urbana','Cl 16 #9-40','6055748877','garupal@valledupar.edu.co','Rosa Cantillo','Humberto Mejía','caribe_cluster'),
    ('ie_lag_rio_01','I.E. San José (Riohacha)','422001000211','Institución Educativa','Oficial','Pública','Básica y Media','SED Riohacha','América del Sur','CO','Colombia','La Guajira','Riohacha','Urbana','Cra 7 #12-18','6057283344','sanjose@riohacha.edu.co','Eugenia Ipuana','Manuel Pushaina','caribe_cluster'),
    ('ie_sai_sai_01','I.E. Brooks Hill (San Andrés)','489001000101','Institución Educativa','Oficial','Pública','Básica y Media','Gob. SAI','América del Sur','CO','Colombia','San Andrés y Providencia','San Andrés','Urbana','Avenida Newball 1-10','6085123344','brookshill@sai.gov.co','Ruth Archbold','Michael Livingston','caribe_cluster'),

    -- ------------------------
    -- PACÍFICO (10) – Valle, Cauca, Nariño, Chocó
    -- ------------------------
    ('ie_val_cali_01','I.E. Jorge Eliécer Gaitán (Cali)','376001000210','Institución Educativa','Oficial','Pública','Básica y Media','SED Cali','América del Sur','CO','Colombia','Valle del Cauca','Cali','Urbana','Calle 15 #45-23','6022341123','info@gaitan.edu.co','José Fajardo','Daniela Toro','pacifico_cluster'),
    ('ie_val_pal_01','I.E. Cárdenas Centro (Palmira)','376001000112','Institución Educativa','Oficial','Pública','Media Técnica','SED Palmira','América del Sur','CO','Colombia','Valle del Cauca','Palmira','Urbana','Carrera 28 #28-30','6022860031','rectoria@cardenascentro.edu.co','Valeria Méndez','Sofía Arboleda','pacifico_cluster'),
    ('ie_val_bue_01','I.E. INEM (Buenaventura)','376001000415','Institución Educativa','Oficial','Pública','Media Técnica','SED Buenaventura','América del Sur','CO','Colombia','Valle del Cauca','Buenaventura','Urbana','Av. Simón Bolívar 30-00','6022401122','inem@buenaventura.edu.co','Yolanda Mina','Elkin Riascos','pacifico_cluster'),
    ('ie_val_tul_01','I.E. Julia Restrepo (Tuluá)','376001000520','Institución Educativa','Oficial','Pública','Básica y Media','Gob. Valle','América del Sur','CO','Colombia','Valle del Cauca','Tuluá','Urbana','Cl 25 #36-10','6022253344','julia@tulua.edu.co','Germán Bedoya','Lina Osorio','pacifico_cluster'),
    ('ie_val_bug_01','I.E. Académico (Buga)','376001000530','Institución Educativa','Oficial','Pública','Media','Gob. Valle','América del Sur','CO','Colombia','Valle del Cauca','Guadalajara de Buga','Urbana','Cra 8 #12-20','6022377788','academico@buga.edu.co','Jairo Duque','Paola Serna','pacifico_cluster'),
    ('ie_cau_pop_01','I.E. Gabriela Mistral (Popayán)','401001000210','Institución Educativa','Oficial','Pública','Básica y Media','SED Popayán','América del Sur','CO','Colombia','Cauca','Popayán','Urbana','Cl 5 #4-15','6028322211','gmistral@popayan.edu.co','Martha Salazar','Jhon Caicedo','pacifico_cluster'),
    ('ie_cau_sil_01','I.E. Inzá (Silvia)','401001000330','Institución Educativa','Oficial','Pública','Rural Multigrado','Gob. Cauca','América del Sur','CO','Colombia','Cauca','Silvia','Rural','Vereda La Campana','6028357788','inza@cauca.edu.co','Pedro Ulcue','Amalia Tombé','pacifico_cluster'),
    ('ie_nar_pas_01','I.E. Ciudad de Pasto','405001000118','Institución Educativa','Oficial','Pública','Media','SED Pasto','América del Sur','CO','Colombia','Nariño','Pasto','Urbana','Cra 20 #18-30','6027312211','ciudad@pasto.edu.co','Edgar Rosero','Ingrid Portilla','pacifico_cluster'),
    ('ie_nar_tum_01','I.E. Max Seidel (Tumaco)','405001000420','Institución Educativa','Oficial','Pública','Media','Gob. Nariño','América del Sur','CO','Colombia','Nariño','Tumaco','Urbana','Cl 2 #4-50','6027273344','max@tumaco.edu.co','Luis Zamora','Andrea Quiñones','pacifico_cluster'),
    ('ie_cho_qbd_01','I.E. Carrasquilla (Quibdó)','478001000115','Institución Educativa','Oficial','Pública','Básica y Media','SED Quibdó','América del Sur','CO','Colombia','Chocó','Quibdó','Urbana','Av. Bahía 12-10','6046712211','carrasquilla@quibdo.edu.co','Nubia Palacios','Eider Mosquera','pacifico_cluster'),

    -- ------------------------
    -- ORINOQUÍA (7) – Meta, Casanare, Arauca, Vichada, Guaviare, Vaupés, Guainía
    -- ------------------------
    ('ie_met_vil_01','I.E. INEM Luis López (Villavicencio)','350001000110','Institución Educativa','Oficial','Pública','Media','SED Villavicencio','América del Sur','CO','Colombia','Meta','Villavicencio','Urbana','Cl 33 #28-50','6086723344','inem@villavo.edu.co','Camilo Morales','María Leguizamón','orinoquia_cluster'),
    ('ie_cas_yop_01','I.E. Braulio González (Yopal)','501001000215','Institución Educativa','Oficial','Pública','Media','Gob. Casanare','América del Sur','CO','Colombia','Casanare','Yopal','Urbana','Cra 18 #24-22','6086342211','braulio@yopal.edu.co','Ciro Mejía','Diana Cáceres','orinoquia_cluster'),
    ('ie_ara_ara_01','I.E. Simón Bolívar (Arauca)','810001000112','Institución Educativa','Oficial','Pública','Básica y Media','SED Arauca','América del Sur','CO','Colombia','Arauca','Arauca','Urbana','Cl 20 #14-10','6078856677','sbolivar@arauca.edu.co','Jairo Gutiérrez','Sonia Acosta','orinoquia_cluster'),
    ('ie_vic_pue_01','I.E. Pablo VI (Puerto Carreño)','990001000101','Institución Educativa','Oficial','Pública','Básica y Media','Gob. Vichada','América del Sur','CO','Colombia','Vichada','Puerto Carreño','Urbana','Cl 8 #7-20','6085657788','pablovi@vichada.gov.co','María Tua','Nelson Granados','orinoquia_cluster'),
    ('ie_gua_sjg_01','I.E. San José (San José del Guaviare)','950001000111','Institución Educativa','Oficial','Pública','Básica y Media','Gob. Guaviare','América del Sur','CO','Colombia','Guaviare','San José del Guaviare','Urbana','Calle 7 #6-18','6085842211','sanjose@guaviare.gov.co','Omar Cely','Lina Rincón','orinoquia_cluster'),
    ('ie_vau_mit_01','I.E. Indígena Monfort (Mitú)','970001000101','Institución Educativa','Oficial','Pública','Etnoeducación','Gob. Vaupés','América del Sur','CO','Colombia','Vaupés','Mitú','Rural','Comunidad Monfort','6085683344','monfort@vaupes.gov.co','Javier Yucuna','Yuly Yunda','orinoquia_cluster'),
    ('ie_gni_ini_01','I.E. Custodio García Rovira (Inírida)','940001000101','Institución Educativa','Oficial','Pública','Básica y Media','Gob. Guainía','América del Sur','CO','Colombia','Guainía','Inírida','Urbana','Cl 10 #9-11','6085651122','cgr@guainia.gov.co','Héctor Tabares','Marina Cuéllar','orinoquia_cluster'),

    -- ------------------------
    -- AMAZONÍA (8) – Caquetá, Putumayo, Amazonas (y rurales)
    -- ------------------------
    ('ie_caq_flo_01','I.E. La Salle (Florencia)','180001000115','Institución Educativa','Oficial','Pública','Media','SED Florencia','América del Sur','CO','Colombia','Caquetá','Florencia','Urbana','Cra 12 #8-22','6084322211','lasalle@florencia.edu.co','Sandra Cubillos','Miguel Giraldo','amazonia_cluster'),
    ('ie_caq_sva_01','I.E. Rural San Vicente del Caguán','180001000210','Institución Educativa','Oficial','Pública','Rural Multigrado','Gob. Caquetá','América del Sur','CO','Colombia','Caquetá','San Vicente del Caguán','Rural','Vereda El Rubí','6084367788','srvicente@caqueta.gov.co','Néstor Cagua','Paola Ardila','amazonia_cluster'),
    ('ie_put_moc_01','I.E. Pío XII (Mocoa)','860001000111','Institución Educativa','Oficial','Pública','Media','SED Mocoa','América del Sur','CO','Colombia','Putumayo','Mocoa','Urbana','Cl 9 #6-20','6084203344','pioxii@mocoa.edu.co','Carmen Cuarán','Luis Erazo','amazonia_cluster'),
    ('ie_put_pas_01','I.E. Santa Teresita (Puerto Asís)','860001000214','Institución Educativa','Oficial','Pública','Media','Gob. Putumayo','América del Sur','CO','Colombia','Putumayo','Puerto Asís','Urbana','Cra 7 #10-12','6084225566','teresita@putumayo.gov.co','Óscar Trochez','Natalia Paz','amazonia_cluster'),
    ('ie_ama_let_01','I.E. Marco Fidel Suárez (Leticia)','910001000101','Institución Educativa','Oficial','Pública','Básica y Media','SED Leticia','América del Sur','CO','Colombia','Amazonas','Leticia','Urbana','Cl 11 #9-18','6085921122','mfs@leticia.edu.co','Aura Calderón','Jhonatan Tello','amazonia_cluster'),
    ('ie_ama_pva_01','I.E. Rural Puerto Nariño','910001000205','Institución Educativa','Oficial','Pública','Rural','Gob. Amazonas','América del Sur','CO','Colombia','Amazonas','Puerto Nariño','Rural','Com. San Juan de Atacuari','6085953344','pnal@amazonas.gov.co','Hugo Vásquez','Martha Yucuna','amazonia_cluster'),
    ('ie_caq_cur_01','I.E. El Doncello Rural','180001000318','Institución Educativa','Oficial','Pública','Rural','Gob. Caquetá','América del Sur','CO','Colombia','Caquetá','El Doncello','Rural','Vereda Guadualito','6084387788','doncello@caqueta.gov.co','Yulieth Rojas','Carlos Molina','amazonia_cluster'),
    ('ie_put_orito_01','I.E. Orito Inmediato','860001000320','Institución Educativa','Oficial','Pública','Media','Gob. Putumayo','América del Sur','CO','Colombia','Putumayo','Orito','Urbana','Cl 8 #5-30','6084217788','orito@putumayo.gov.co','Hernán Cuatindioy','María Chamorro','amazonia_cluster')
)

-- a) Poblar tabla extendida (IA)
INSERT INTO public.tenants_extended
(identifier, name, code_dane, type, sector, nature, academic_level, entity_certifier,
 continent, country_code, country_name, department, municipality, zone, address, phone, email,
 rector_name, contact_person, ai_cluster_label)
SELECT * FROM dataset
ON CONFLICT (identifier) DO NOTHING;

-- b) Poblar tabla base minimal (usada por el microservicio)
--    name UNIQUE ya existe; si ya estaba, no lo duplica.
INSERT INTO public.tenants (active, databaseurl, name, password, schema, username)
SELECT
    TRUE                                                     AS active,
    'jdbc:postgresql://localhost:5432/' || identifier        AS databaseurl,
    name                                                     AS name,
    'postgres'                                               AS password,
    identifier                                               AS schema,
    'postgres'                                               AS username
FROM dataset
ON CONFLICT (name) DO NOTHING;

-- Vista de conveniencia para analítica/ML (join minimal+extendida)
CREATE OR REPLACE VIEW public.tenants_ml_view AS
SELECT
    te.identifier, te.name, te.code_dane, te.type, te.sector, te.nature, te.academic_level,
    te.entity_certifier, te.continent, te.country_code, te.country_name, te.department,
    te.municipality, te.zone, te.address, te.phone, te.email, te.rector_name, te.contact_person,
    te.ai_cluster_label,
    t.active, t.databaseurl, t.schema AS schema_name, t.username
FROM public.tenants t
JOIN public.tenants_extended te ON te.identifier = t.schema;

-- Fin
