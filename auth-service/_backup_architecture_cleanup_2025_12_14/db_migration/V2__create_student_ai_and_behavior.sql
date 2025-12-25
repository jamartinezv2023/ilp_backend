-- V2: student related tables for AI and behavior
-- generated 2025-10-12T03:21:04.168616
CREATE TABLE IF NOT EXISTS student_behavior_metric (
  id BIGSERIAL PRIMARY KEY,
  tenant_id UUID NOT NULL,
  student_id BIGINT NOT NULL REFERENCES students(id),
  event_type VARCHAR(128),
  metadata JSONB,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_behavior_student ON student_behavior_metric(student_id);

CREATE TABLE IF NOT EXISTS student_ai_output (
  id BIGSERIAL PRIMARY KEY,
  tenant_id UUID NOT NULL,
  student_id BIGINT NOT NULL REFERENCES students(id),
  model_name VARCHAR(128),
  model_version VARCHAR(64),
  milvus_vector_id VARCHAR(128),
  neo4j_node_id VARCHAR(128),
  explanation_json JSONB,
  score NUMERIC,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_ai_student ON student_ai_output(student_id);

CREATE TABLE IF NOT EXISTS student_learning_profile (
  id BIGSERIAL PRIMARY KEY,
  student_id BIGINT NOT NULL REFERENCES students(id),
  raw_responses_json JSONB,
  scores_json JSONB,
  version INTEGER DEFAULT 1,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS student_support_needs (
  id BIGSERIAL PRIMARY KEY,
  student_id BIGINT NOT NULL REFERENCES students(id),
  needs_json JSONB,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS student_environment (
  id BIGSERIAL PRIMARY KEY,
  student_id BIGINT NOT NULL REFERENCES students(id),
  environment_json JSONB,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);
