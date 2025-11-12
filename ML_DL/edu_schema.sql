-- ============================================
-- 1️⃣ ESQUEMAS BASE
-- ============================================
CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS cat;
CREATE SCHEMA IF NOT EXISTS edu;
CREATE SCHEMA IF NOT EXISTS ml;

-- ============================================
-- 2️⃣ CATÁLOGOS NORMALIZADOS
-- ============================================

CREATE TABLE cat.gender (
  id SERIAL PRIMARY KEY,
  code VARCHAR(10) UNIQUE NOT NULL,
  description VARCHAR(50)
);

CREATE TABLE cat.ethnicity (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) UNIQUE NOT NULL,
  description TEXT
);

CREATE TABLE cat.disability_status (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) UNIQUE NOT NULL,
  description TEXT
);

CREATE TABLE cat.school_level (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) UNIQUE NOT NULL,
  description TEXT
);

CREATE TABLE cat.ses (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) UNIQUE NOT NULL,
  description TEXT
);

CREATE TABLE cat.content_format (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) UNIQUE NOT NULL,
  description TEXT
);

CREATE TABLE cat.learning_style_felder (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) UNIQUE NOT NULL,
  description TEXT
);

CREATE TABLE cat.learning_style_kolb (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) UNIQUE NOT NULL,
  description TEXT
);

CREATE TABLE cat.vocational_profile_kuder (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) UNIQUE NOT NULL,
  description TEXT
);

-- ============================================
-- 3️⃣ USUARIOS Y AUTENTICACIÓN
-- ============================================

CREATE TABLE auth.users (
  id SERIAL PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash TEXT NOT NULL,
  role VARCHAR(50),
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 4️⃣ ESTUDIANTES
-- ============================================

CREATE TABLE edu.students (
  id SERIAL PRIMARY KEY,
  tenant_id INT NOT NULL,
  user_id INT NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  full_name VARCHAR(255),
  guardian_name VARCHAR(255),
  gender_id INT REFERENCES cat.gender(id),
  ethnicity_id INT REFERENCES cat.ethnicity(id),
  disability_status_id INT REFERENCES cat.disability_status(id),
  school_level_id INT REFERENCES cat.school_level(id),
  socio_economic_status_id INT REFERENCES cat.ses(id),
  birth_date DATE,
  avatar_url TEXT,
  location VARCHAR(255),
  receives_psychological_support BOOLEAN DEFAULT FALSE,
  receives_special_education_support BOOLEAN DEFAULT FALSE,
  needs_assistive_technology BOOLEAN DEFAULT FALSE,
  preferred_content_format_id INT REFERENCES cat.content_format(id),
  learning_style_felder_id INT REFERENCES cat.learning_style_felder(id),
  learning_style_kolb_id INT REFERENCES cat.learning_style_kolb(id),
  vocational_profile_kuder_id INT REFERENCES cat.vocational_profile_kuder(id),
  numeracy_level INT,
  reading_level INT,
  device_access BOOLEAN,
  internet_access BOOLEAN,
  preferred_study_time VARCHAR(50),
  siblings_in_school INT,
  languages_spoken_at_home VARCHAR(255),
  family_structure VARCHAR(100),
  account_status VARCHAR(50) DEFAULT 'active',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 5️⃣ SERIES TEMPORALES EDUCATIVAS
-- ============================================

CREATE TABLE edu.student_timeseries (
  id SERIAL PRIMARY KEY,
  student_id INT REFERENCES edu.students(id) ON DELETE CASCADE,
  tenant_id INT,
  ts_date DATE,
  attendance_rate NUMERIC(5,2),
  average_grade NUMERIC(5,2),
  behavior_notes TEXT,
  disciplinary_actions INT,
  homework_completion_rate NUMERIC(5,2),
  is_repeating_grade BOOLEAN,
  math_score NUMERIC(5,2),
  reading_score NUMERIC(5,2),
  science_score NUMERIC(5,2),
  emotional_state_trend VARCHAR(50),
  engagement_cluster VARCHAR(50),
  predicted_dropout_risk NUMERIC(5,2),
  recommended_learning_path TEXT,
  adaptive_content_profile TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 6️⃣ VISTA MATERIALIZADA PARA ML/DL
-- ============================================

CREATE MATERIALIZED VIEW ml.students_dataset_mv AS
WITH last_ts AS (
  SELECT DISTINCT ON (student_id)
         student_id, ts_date,
         attendance_rate, average_grade, behavior_notes, disciplinary_actions,
         homework_completion_rate, is_repeating_grade,
         math_score, reading_score, science_score,
         emotional_state_trend, engagement_cluster,
         predicted_dropout_risk, recommended_learning_path, adaptive_content_profile
  FROM edu.student_timeseries
  ORDER BY student_id, ts_date DESC
)
SELECT
  s.id AS student_id,
  s.user_id,
  s.full_name,
  s.guardian_name,
  g.code AS gender,
  e.code AS ethnicity,
  d.code AS disability_status,
  sl.code AS school_level,
  ses.code AS socio_economic_status,
  s.birth_date,
  s.avatar_url,
  s.location,
  s.receives_psychological_support,
  s.receives_special_education_support,
  s.needs_assistive_technology,
  cf.code AS preferred_content_format,
  lf.code AS learning_style_felder,
  lk.code AS learning_style_kolb,
  vk.code AS vocational_profile_kuder,
  s.device_access,
  s.internet_access,
  s.preferred_study_time,
  s.languages_spoken_at_home,
  s.family_structure,
  s.account_status,
  s.created_at,
  s.updated_at,
  l.attendance_rate,
  l.average_grade,
  l.behavior_notes,
  l.disciplinary_actions,
  l.homework_completion_rate,
  l.is_repeating_grade,
  l.math_score,
  l.reading_score,
  l.science_score,
  l.emotional_state_trend,
  l.engagement_cluster,
  l.predicted_dropout_risk,
  l.recommended_learning_path,
  l.adaptive_content_profile
FROM edu.students s
LEFT JOIN last_ts l ON l.student_id = s.id
LEFT JOIN cat.gender g ON g.id = s.gender_id
LEFT JOIN cat.ethnicity e ON e.id = s.ethnicity_id
LEFT JOIN cat.disability_status d ON d.id = s.disability_status_id
LEFT JOIN cat.school_level sl ON sl.id = s.school_level_id
LEFT JOIN cat.ses ses ON ses.id = s.socio_economic_status_id
LEFT JOIN cat.content_format cf ON cf.id = s.preferred_content_format_id
LEFT JOIN cat.learning_style_felder lf ON lf.id = s.learning_style_felder_id
LEFT JOIN cat.learning_style_kolb lk ON lk.id = s.learning_style_kolb_id
LEFT JOIN cat.vocational_profile_kuder vk ON vk.id = s.vocational_profile_kuder_id
WITH NO DATA;

CREATE UNIQUE INDEX idx_students_dataset_mv_id ON ml.students_dataset_mv (student_id);
