CREATE TABLE scientific_feature_vectors (
    id VARCHAR(100) PRIMARY KEY,

    participant_id VARCHAR(100) NOT NULL,

    feature_set_version VARCHAR(100) NOT NULL,

    generator_version VARCHAR(100) NOT NULL,

    feature_cutoff_at TIMESTAMP WITH TIME ZONE NOT NULL,

    generated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    source_observation_count INTEGER NOT NULL,

    status VARCHAR(30) NOT NULL,

    checksum VARCHAR(128) NOT NULL,

    CONSTRAINT chk_scientific_feature_vector_observation_count
        CHECK (source_observation_count >= 0),

    CONSTRAINT chk_scientific_feature_vector_status
        CHECK (
            status IN (
                'GENERATING',
                'COMPLETED',
                'FAILED'
            )
        ),

    CONSTRAINT uq_scientific_feature_vector_identity
        UNIQUE (
            participant_id,
            feature_set_version,
            feature_cutoff_at
        )
);

CREATE INDEX idx_scientific_feature_vectors_participant
    ON scientific_feature_vectors(participant_id);

CREATE INDEX idx_scientific_feature_vectors_cutoff
    ON scientific_feature_vectors(feature_cutoff_at);

CREATE INDEX idx_scientific_feature_vectors_latest
    ON scientific_feature_vectors(
        participant_id,
        feature_set_version,
        feature_cutoff_at DESC
    );

CREATE TABLE scientific_feature_items (
    id VARCHAR(120) PRIMARY KEY,

    feature_vector_id VARCHAR(100) NOT NULL,

    feature_code VARCHAR(100) NOT NULL,

    data_type VARCHAR(30) NOT NULL,

    numeric_value DOUBLE PRECISION,

    text_value TEXT,

    boolean_value BOOLEAN,

    source_assessment_code VARCHAR(100),

    source_administration_id VARCHAR(100),

    CONSTRAINT fk_scientific_feature_item_vector
        FOREIGN KEY (feature_vector_id)
        REFERENCES scientific_feature_vectors(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_scientific_feature_item_data_type
        CHECK (
            data_type IN (
                'NUMERIC',
                'TEXT',
                'BOOLEAN'
            )
        ),

    CONSTRAINT chk_scientific_feature_item_single_value
        CHECK (
            (
                data_type = 'NUMERIC'
                AND numeric_value IS NOT NULL
                AND text_value IS NULL
                AND boolean_value IS NULL
            )
            OR
            (
                data_type = 'TEXT'
                AND numeric_value IS NULL
                AND text_value IS NOT NULL
                AND boolean_value IS NULL
            )
            OR
            (
                data_type = 'BOOLEAN'
                AND numeric_value IS NULL
                AND text_value IS NULL
                AND boolean_value IS NOT NULL
            )
        ),

    CONSTRAINT uq_scientific_feature_item_code
        UNIQUE (
            feature_vector_id,
            feature_code
        )
);

CREATE INDEX idx_scientific_feature_items_vector
    ON scientific_feature_items(feature_vector_id);

CREATE INDEX idx_scientific_feature_items_code
    ON scientific_feature_items(feature_code);

CREATE INDEX idx_scientific_feature_items_source_administration
    ON scientific_feature_items(source_administration_id);

CREATE TABLE scientific_feature_generation_runs (
    id VARCHAR(100) PRIMARY KEY,

    participant_id VARCHAR(100) NOT NULL,

    feature_set_version VARCHAR(100) NOT NULL,

    generator_version VARCHAR(100) NOT NULL,

    feature_cutoff_at TIMESTAMP WITH TIME ZONE NOT NULL,

    started_at TIMESTAMP WITH TIME ZONE NOT NULL,

    completed_at TIMESTAMP WITH TIME ZONE,

    status VARCHAR(30) NOT NULL,

    input_observation_count INTEGER NOT NULL,

    error_message TEXT,

    feature_vector_id VARCHAR(100),

    CONSTRAINT fk_scientific_feature_run_vector
        FOREIGN KEY (feature_vector_id)
        REFERENCES scientific_feature_vectors(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_scientific_feature_run_input_count
        CHECK (input_observation_count >= 0),

    CONSTRAINT chk_scientific_feature_run_status
        CHECK (
            status IN (
                'STARTED',
                'COMPLETED',
                'FAILED'
            )
        ),

    CONSTRAINT chk_scientific_feature_run_completion
        CHECK (
            (
                status = 'STARTED'
                AND completed_at IS NULL
                AND error_message IS NULL
            )
            OR
            (
                status = 'COMPLETED'
                AND completed_at IS NOT NULL
                AND error_message IS NULL
                AND feature_vector_id IS NOT NULL
            )
            OR
            (
                status = 'FAILED'
                AND completed_at IS NOT NULL
                AND error_message IS NOT NULL
            )
        )
);

CREATE INDEX idx_scientific_feature_runs_participant
    ON scientific_feature_generation_runs(participant_id);

CREATE INDEX idx_scientific_feature_runs_cutoff
    ON scientific_feature_generation_runs(feature_cutoff_at);

CREATE INDEX idx_scientific_feature_runs_status
    ON scientific_feature_generation_runs(status);