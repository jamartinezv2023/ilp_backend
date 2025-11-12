-- ===== USUARIOS BASE PARA RELACIÓN @OneToOne =====
INSERT INTO users (id, username, email, password, account_status, created_at)
VALUES
    (1, 'juan.perez', 'juan.perez@example.com', '{noop}password123', 'ACTIVE', NOW()),
    (2, 'maria.gomez', 'maria.gomez@example.com', '{noop}password123', 'ACTIVE', NOW()),
    (3, 'pedro.lopez', 'pedro.lopez@example.com', '{noop}password123', 'ACTIVE', NOW()),
    (4, 'laura.ramos', 'laura.ramos@example.com', '{noop}password123', 'ACTIVE', NOW()),
    (5, 'carlos.mendez', 'carlos.mendez@example.com', '{noop}password123', 'ACTIVE', NOW()),
    (6, 'ana.silva', 'ana.silva@example.com', '{noop}password123', 'SUSPENDED', NOW()),
    (7, 'roberto.torres', 'roberto.torres@example.com', '{noop}password123', 'SUSPENDED', NOW()),
    (8, 'lucia.fernandez', 'lucia.fernandez@example.com', '{noop}password123', 'GRADUATED', NOW());

-- ===== ESTUDIANTES CON CAMPOS COMPLETOS =====
INSERT INTO students (id, user_id, account_status, created_at,
                      full_name, birth_date, gender, ethnicity, disability_status, socio_economic_status,
                      family_structure, location, school_level, guardian_name, avatar_url,
                      learning_style_kolb, learning_style_felder, vocational_profile_kuder,
                      preferred_content_format, needs_assistive_technology, reading_level, numeracy_level, language_spoken_at_home,
                      device_access, internet_access, siblings_in_school, preferred_study_time,
                      average_grade, math_score, reading_score, science_score, is_repeating_grade, attendance_rate, homework_completion_rate,
                      behavioral_notes, disciplinary_actions,
                      receives_psychological_support, receives_special_education_support,
                      recommended_learning_path, adaptive_content_profile, emotional_state_trend, predicted_dropout_risk, engagement_cluster
) VALUES
-- 1
(1, 1, 'ACTIVE', NOW(),
 'Juan Pérez', '2008-05-15', 'M', 'Latino', 'None', 'Middle',
 'Nuclear', 'Ciudad A', 'Secundaria', 'Carlos Pérez', 'https://example.com/avatars/juan.jpg',
 'Converger', 'Visual', 'Tecnología',
 'Video', FALSE, 'B2', 'Intermedio', 'Español',
 'Laptop', 'Fibra Óptica', 1, 'Noche',
 8.5, 9.0, 8.0, 8.5, FALSE, 95.0, 90.0,
 'Buen comportamiento', 0,
 FALSE, FALSE,
 'Ciencias', 'Perfil adaptado 1', 'Positivo', 'Bajo', 'Cluster A'),

-- 2
(2, 2, 'ACTIVE', NOW(),
 'María Gómez', '2009-08-22', 'F', 'Indígena', 'Visual', 'Low',
 'Monoparental', 'Ciudad B', 'Primaria', 'Ana Gómez', 'https://example.com/avatars/maria.jpg',
 'Diverger', 'Auditivo', 'Educación',
 'Audio', TRUE, 'C1', 'Avanzado', 'Español',
 'Tablet', '4G', 3, 'Mañana',
 9.0, 9.5, 9.0, 8.8, FALSE, 98.0, 95.0,
 'Participativa', 0,
 TRUE, FALSE,
 'Arte', 'Perfil adaptado 2', 'Neutral', 'Muy Bajo', 'Cluster B'),

-- 3
(3, 3, 'ACTIVE', NOW(),
 'Pedro López', '2007-03-10', 'M', 'Afrodescendiente', 'Auditivo', 'Middle',
 'Nuclear', 'Ciudad C', 'Secundaria', 'José López', 'https://example.com/avatars/pedro.jpg',
 'Assimilator', 'Kinestésico', 'Ciencias',
 'Texto', FALSE, 'B1', 'Básico', 'Español',
 'PC', 'ADSL', 0, 'Tarde',
 7.8, 8.0, 7.5, 8.2, FALSE, 90.0, 85.0,
 'Distraído a veces', 1,
 FALSE, TRUE,
 'Ingeniería', 'Perfil adaptado 3', 'Negativo', 'Medio', 'Cluster C'),

-- 4
(4, 4, 'SUSPENDED', NOW(),
 'Laura Ramos', '2010-01-05', 'F', 'Latino', 'None', 'High',
 'Nuclear', 'Ciudad D', 'Primaria', 'Luis Ramos', 'https://example.com/avatars/laura.jpg',
 'Accommodator', 'Visual', 'Medicina',
 'Video', FALSE, 'C2', 'Avanzado', 'Español',
 'Smartphone', '4G', 2, 'Mañana',
 8.0, 8.5, 8.0, 8.0, FALSE, 96.0, 92.0,
 'Atenta', 0,
 FALSE, FALSE,
 'Salud', 'Perfil adaptado 4', 'Positivo', 'Bajo', 'Cluster D'),

-- 5
(5, 5, 'ACTIVE', NOW(),
 'Carlos Méndez', '2006-11-30', 'M', 'Latino', 'None', 'Middle',
 'Nuclear', 'Ciudad E', 'Secundaria', 'Juan Méndez', 'https://example.com/avatars/carlos.jpg',
 'Converger', 'Auditivo', 'Economía',
 'Video', FALSE, 'B2', 'Intermedio', 'Español',
 'Laptop', 'Fibra Óptica', 1, 'Noche',
 8.3, 8.7, 8.2, 8.1, FALSE, 94.0, 89.0,
 'Buen liderazgo', 0,
 FALSE, FALSE,
 'Finanzas', 'Perfil adaptado 5', 'Positivo', 'Bajo', 'Cluster E'),

-- 6
(6, 6, 'SUSPENDED', NOW(),
 'Ana Silva', '2008-04-18', 'F', 'Indígena', 'Auditivo', 'Low',
 'Monoparental', 'Ciudad F', 'Secundaria', 'Laura Silva', 'https://example.com/avatars/ana.jpg',
 'Assimilator', 'Visual', 'Arte',
 'Audio', TRUE, 'B1', 'Básico', 'Español',
 'Tablet', 'ADSL', 4, 'Tarde',
 7.5, 8.0, 7.0, 7.8, TRUE, 85.0, 80.0,
 'Inquieta', 2,
 TRUE, TRUE,
 'Arte Digital', 'Perfil adaptado 6', 'Neutral', 'Medio', 'Cluster F'),

-- 7
(7, 7, 'ACTIVE', NOW(),
 'Roberto Torres', '2007-09-12', 'M', 'Afrodescendiente', 'None', 'Middle',
 'Nuclear', 'Ciudad G', 'Secundaria', 'Pablo Torres', 'https://example.com/avatars/roberto.jpg',
 'Diverger', 'Kinestésico', 'Deportes',
 'Video', FALSE, 'B2', 'Intermedio', 'Español',
 'PC', 'Fibra Óptica', 2, 'Noche',
 8.6, 9.0, 8.5, 8.7, FALSE, 97.0, 94.0,
 'Muy participativo', 0,
 FALSE, FALSE,
 'Educación Física', 'Perfil adaptado 7', 'Positivo', 'Bajo', 'Cluster G'),

-- 8
(8, 8, 'GRADUATED', NOW(),
 'Lucía Fernández', '2005-02-25', 'F', 'Latino', 'None', 'High',
 'Nuclear', 'Ciudad H', 'Universidad', 'Jorge Fernández', 'https://example.com/avatars/lucia.jpg',
 'Accommodator', 'Visual', 'Derecho',
 'Texto', FALSE, 'C1', 'Avanzado', 'Español',
 'Laptop', 'Fibra Óptica', 0, 'Mañana',
 9.2, 9.5, 9.1, 9.0, FALSE, 99.0, 97.0,
 'Sobresaliente', 0,
 FALSE, FALSE,
 'Derecho Penal', 'Perfil adaptado 8', 'Positivo', 'Muy Bajo', 'Cluster H');
