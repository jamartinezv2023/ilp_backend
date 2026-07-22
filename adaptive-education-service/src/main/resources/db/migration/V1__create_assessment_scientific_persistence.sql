CREATE TABLE assessment_results (
    id VARCHAR(255) PRIMARY KEY,

    administration_id VARCHAR(255) NOT NULL,

    participant_id VARCHAR(255) NOT NULL,

    assessment_code VARCHAR(255) NOT NULL,

    assessment_version VARCHAR(255) NOT NULL,

    primary_profile VARCHAR(255),

    scoring_algorithm_version VARCHAR(255) NOT NULL,

    interpretation_version VARCHAR(255),

    calculated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    submitted_at TIMESTAMP WITH TIME ZONE NOT NULL,

    feature_cutoff_at TIMESTAMP WITH TIME ZONE NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_assessment_results_administration
        UNIQUE (administration_id),

    CONSTRAINT fk_assessment_results_administration
        FOREIGN KEY (administration_id)
        REFERENCES assessment_responses(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_assessment_results_participant_time
    ON assessment_results(
        participant_id,
        calculated_at
    );

CREATE INDEX idx_assessment_results_instrument
    ON assessment_results(
        assessment_code,
        assessment_version
    );

CREATE INDEX idx_assessment_results_feature_cutoff
    ON assessment_results(feature_cutoff_at);


CREATE TABLE assessment_score_items (
    id VARCHAR(255) PRIMARY KEY,

    result_id VARCHAR(255) NOT NULL,

    administration_id VARCHAR(255) NOT NULL,

    dimension_code VARCHAR(255) NOT NULL,

    numeric_value DOUBLE PRECISION NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_assessment_score_items_result
        FOREIGN KEY (result_id)
        REFERENCES assessment_results(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_assessment_score_items_administration
        FOREIGN KEY (administration_id)
        REFERENCES assessment_responses(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_assessment_score_dimension
        UNIQUE (
            result_id,
            dimension_code
        )
);

CREATE INDEX idx_assessment_score_administration
    ON assessment_score_items(administration_id);

CREATE INDEX idx_assessment_score_dimension
    ON assessment_score_items(dimension_code);


CREATE TABLE assessment_interpretations (
    id VARCHAR(255) PRIMARY KEY,

    result_id VARCHAR(255) NOT NULL,

    administration_id VARCHAR(255) NOT NULL,

    interpretation_code VARCHAR(255) NOT NULL,

    interpretation_text TEXT,

    interpretation_version VARCHAR(255),

    created_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_assessment_interpretations_result
        FOREIGN KEY (result_id)
        REFERENCES assessment_results(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_assessment_interpretations_administration
        FOREIGN KEY (administration_id)
        REFERENCES assessment_responses(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_assessment_interpretation_code
        UNIQUE (
            result_id,
            interpretation_code
        )
);

CREATE INDEX idx_assessment_interpretation_administration
    ON assessment_interpretations(administration_id);


CREATE TABLE assessment_submission_context (
    id VARCHAR(255) PRIMARY KEY,

    administration_id VARCHAR(255) NOT NULL,

    institution_id VARCHAR(255),

    campus_id VARCHAR(255),

    program_id VARCHAR(255),

    course_id VARCHAR(255),

    cohort_id VARCHAR(255),

    teacher_id VARCHAR(255),

    grade VARCHAR(100),

    academic_year VARCHAR(20),

    academic_period VARCHAR(100),

    fieldwork_phase VARCHAR(100),

    intervention_id VARCHAR(255),

    intervention_group VARCHAR(100),

    source VARCHAR(100),

    delivery_mode VARCHAR(100),

    language VARCHAR(20),

    device_type VARCHAR(100),

    browser VARCHAR(255),

    operating_system VARCHAR(255),

    timezone VARCHAR(100),

    application_version VARCHAR(100),

    consent_id VARCHAR(255),

    consent_version VARCHAR(100),

    ethics_protocol VARCHAR(255),

    feature_set_version VARCHAR(100),

    preprocessing_version VARCHAR(100),

    normalization_version VARCHAR(100),

    started_at TIMESTAMP WITH TIME ZONE,

    duration_seconds BIGINT,

    feature_cutoff_at TIMESTAMP WITH TIME ZONE NOT NULL,

    context_json JSONB NOT NULL DEFAULT '{}'::jsonb,

    created_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_assessment_submission_context_administration
        UNIQUE (administration_id),

    CONSTRAINT fk_assessment_submission_context_administration
        FOREIGN KEY (administration_id)
        REFERENCES assessment_responses(id)
        ON DELETE CASCADE,

    CONSTRAINT ck_assessment_submission_duration
        CHECK (
            duration_seconds IS NULL
            OR duration_seconds >= 0
        )
);

CREATE INDEX idx_assessment_context_institution_period
    ON assessment_submission_context(
        institution_id,
        academic_year,
        academic_period
    );

CREATE INDEX idx_assessment_context_course
    ON assessment_submission_context(course_id);

CREATE INDEX idx_assessment_context_cohort
    ON assessment_submission_context(cohort_id);

CREATE INDEX idx_assessment_context_fieldwork
    ON assessment_submission_context(fieldwork_phase);

CREATE INDEX idx_assessment_context_feature_cutoff
    ON assessment_submission_context(feature_cutoff_at);