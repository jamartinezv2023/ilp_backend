CREATE TABLE fieldwork_research_subject_identities (
    id UUID PRIMARY KEY,
    participant_uuid UUID NOT NULL,
    research_subject_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    deactivated_at TIMESTAMP NULL,

    CONSTRAINT uq_research_identity_participant
        UNIQUE (participant_uuid),

    CONSTRAINT uq_research_identity_subject
        UNIQUE (research_subject_id)
);

CREATE INDEX idx_research_identity_participant_active
    ON fieldwork_research_subject_identities (
        participant_uuid,
        deactivated_at
    );
