-- ============================================
-- LIMPIEZA DE DATOS BASE
-- ============================================
TRUNCATE TABLE user_roles RESTART IDENTITY CASCADE;
TRUNCATE TABLE roles RESTART IDENTITY CASCADE;
TRUNCATE TABLE students RESTART IDENTITY CASCADE;
TRUNCATE TABLE users RESTART IDENTITY CASCADE;

-- ============================================
-- USUARIOS BASE
-- Contraseña = password123 (BCrypt)
-- ============================================
INSERT INTO users (id, username, email, password, enabled, email_verified, auth_provider, account_status, created_at)
VALUES
(
  1,
  'admin',
  'admin@inclusive.edu.co',
  '$2a$10$Dow1EhzFo0r5aXh/5MBNe.g3FQuDHH02FRCi5pBFgG.ZaEqW2c9vS', -- password123
  true,
  true,
  'LOCAL',
  'ACTIVE',
  now()
),
(
  2,
  'user',
  'user@example.com',
  '$2a$10$Dow1EhzFo0r5aXh/5MBNe.g3FQuDHH02FRCi5pBFgG.ZaEqW2c9vS', -- password123
  true,
  true,
  'LOCAL',
  'ACTIVE',
  now()
);

-- ============================================
-- ROLES
-- ============================================
INSERT INTO roles (id, name) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER');

-- ============================================
-- RELACIÓN USUARIOS ↔ ROLES
-- ============================================
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin → ROLE_ADMIN
(2, 2); -- user → ROLE_USER

-- ============================================
-- ESTUDIANTES ASOCIADOS A LOS USUARIOS
-- ============================================
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
-- 1 (admin → estudiante asociado)
(1, 1, 'ACTIVE', NOW(),
 'Administrador Principal', '1990-01-01', 'M', 'Latino', 'None', 'High',
 'Nuclear', 'Ciudad Admin', 'Universidad', 'Sistema', 'https://example.com/avatars/admin.jpg',
 'Converger', 'Visual', 'Tecnología',
 'Video', FALSE, 'C1', 'Avanzado', 'Español',
 'Laptop', 'Fibra Óptica', 0, 'Noche',
 10.0, 10.0, 10.0, 10.0, FALSE, 100.0, 100.0,
 'Usuario administrador del sistema', 0,
 FALSE, FALSE,
 'Administración', 'Perfil admin', 'Positivo', 'Muy Bajo', 'Cluster Z'),

-- 2 (user → estudiante asociado)
(2, 2, 'ACTIVE', NOW(),
 'Usuario Regular', '2005-06-15', 'F', 'Latino', 'None', 'Middle',
 'Nuclear', 'Ciudad User', 'Secundaria', 'Tutor User', 'https://example.com/avatars/user.jpg',
 'Diverger', 'Auditivo', 'Educación',
 'Texto', FALSE, 'B2', 'Intermedio', 'Español',
 'Tablet', '4G', 1, 'Mañana',
 8.5, 8.0, 8.5, 8.0, FALSE, 95.0, 90.0,
 'Usuario de prueba con rol básico', 0,
 FALSE, FALSE,
 'Educación General', 'Perfil user', 'Neutral', 'Bajo', 'Cluster Y');
