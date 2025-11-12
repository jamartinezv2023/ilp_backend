CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(120) UNIQUE NOT NULL,
  email VARCHAR(160) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT now()
);
CREATE TABLE IF NOT EXISTS roles (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(60) UNIQUE NOT NULL
);
CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
  PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS students (
  id BIGSERIAL PRIMARY KEY,
  code VARCHAR(60) UNIQUE,
  full_name VARCHAR(160) NOT NULL,
  grade VARCHAR(40),
  created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS quiz (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  subject VARCHAR(80),
  created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS quiz_attempts (
  id BIGSERIAL PRIMARY KEY,
  quiz_id BIGINT NOT NULL REFERENCES quiz(id) ON DELETE CASCADE,
  student_id BIGINT NOT NULL REFERENCES students(id) ON DELETE CASCADE,
  score NUMERIC(5,2),
  started_at TIMESTAMP NOT NULL DEFAULT now(),
  finished_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS events_log (
  id BIGSERIAL PRIMARY KEY,
  event_type VARCHAR(80) NOT NULL,
  payload JSONB,
  created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_students_code  ON students(code);
CREATE INDEX IF NOT EXISTS idx_attempts_quiz  ON quiz_attempts(quiz_id);
CREATE INDEX IF NOT EXISTS idx_attempts_stud  ON quiz_attempts(student_id);
CREATE INDEX IF NOT EXISTS idx_events_type    ON events_log(event_type);
