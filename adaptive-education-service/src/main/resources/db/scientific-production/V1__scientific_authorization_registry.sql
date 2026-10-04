-- Separate migration location: NOT automatically added to the existing Flyway history.
-- Apply only after a reviewed database preflight and backup; no legacy rows are changed.
CREATE TABLE scientific_memberships (
    tenant_id uuid NOT NULL, user_id uuid NOT NULL, active boolean NOT NULL DEFAULT false,
    verified_by uuid NOT NULL, verified_at timestamp with time zone NOT NULL,
    PRIMARY KEY (tenant_id, user_id)
);
CREATE TABLE scientific_participant_bindings (
    tenant_id uuid NOT NULL, participant_uuid uuid NOT NULL, student_id varchar(255) NOT NULL,
    research_subject_id varchar(255) NOT NULL, active boolean NOT NULL DEFAULT false,
    verified_by uuid NOT NULL, verified_at timestamp with time zone NOT NULL,
    PRIMARY KEY (tenant_id, participant_uuid), UNIQUE (participant_uuid),
    UNIQUE (tenant_id, student_id), UNIQUE (research_subject_id)
);
CREATE TABLE scientific_consent_documents (
    document_id uuid PRIMARY KEY, tenant_id uuid NOT NULL, document_version varchar(100) NOT NULL,
    document_text text NOT NULL, document_sha256 varchar(64) NOT NULL,
    approved_by uuid NOT NULL, approved_at timestamp with time zone NOT NULL,
    active boolean NOT NULL DEFAULT false, UNIQUE (tenant_id, document_id),
    UNIQUE (tenant_id, document_version), CHECK (length(document_sha256) = 64)
);
CREATE TABLE scientific_consent_evidence (
    evidence_id uuid PRIMARY KEY, tenant_id uuid NOT NULL, participant_uuid uuid NOT NULL,
    document_id uuid NOT NULL, legacy_consent_id uuid NOT NULL UNIQUE,
    consent_type varchar(100) NOT NULL, accepted_by uuid NOT NULL,
    accepted_document_sha256 varchar(64) NOT NULL,
    signer_capacity varchar(100) NOT NULL, acceptance_method varchar(100) NOT NULL,
    accepted_at timestamp with time zone NOT NULL, withdrawn_at timestamp with time zone,
    withdrawn_by uuid, UNIQUE (tenant_id, evidence_id),
    FOREIGN KEY (tenant_id, participant_uuid) REFERENCES scientific_participant_bindings,
    FOREIGN KEY (tenant_id, document_id) REFERENCES scientific_consent_documents(tenant_id, document_id),
    CHECK ((withdrawn_at IS NULL AND withdrawn_by IS NULL)
        OR (withdrawn_at >= accepted_at AND withdrawn_by IS NOT NULL))
);
CREATE TABLE scientific_instrument_permissions (
    tenant_id uuid NOT NULL, assessment_code varchar(255) NOT NULL, assessment_version varchar(255) NOT NULL,
    approved boolean NOT NULL DEFAULT false, validation_evidence varchar(1000) NOT NULL,
    approved_by uuid NOT NULL, approved_at timestamp with time zone NOT NULL,
    PRIMARY KEY (tenant_id, assessment_code, assessment_version)
);
CREATE TABLE scientific_assignments (
    assignment_id uuid PRIMARY KEY, tenant_id uuid NOT NULL, user_id uuid NOT NULL,
    participant_uuid uuid NOT NULL, consent_evidence_id uuid NOT NULL,
    assessment_code varchar(255) NOT NULL, assessment_version varchar(255) NOT NULL,
    can_submit boolean NOT NULL DEFAULT false, can_read boolean NOT NULL DEFAULT false,
    can_export boolean NOT NULL DEFAULT false, active boolean NOT NULL DEFAULT false,
    valid_until timestamp with time zone NOT NULL, assigned_by uuid NOT NULL,
    assigned_at timestamp with time zone NOT NULL,
    FOREIGN KEY (tenant_id, user_id) REFERENCES scientific_memberships,
    FOREIGN KEY (tenant_id, participant_uuid) REFERENCES scientific_participant_bindings,
    FOREIGN KEY (tenant_id, consent_evidence_id) REFERENCES scientific_consent_evidence(tenant_id, evidence_id),
    FOREIGN KEY (tenant_id, assessment_code, assessment_version) REFERENCES scientific_instrument_permissions,
    CHECK (valid_until > assigned_at)
);
CREATE INDEX scientific_assignments_actor ON scientific_assignments(tenant_id, user_id);
