package com.inclusive.adaptiveeducationservice.research.production;

import com.inclusive.adaptiveeducationservice.research.application.ScientificApplicationGrant;
import java.util.UUID;

public record ScientificAssignment(ScientificApplicationGrant grant, UUID evidenceId,
        UUID documentId, String documentSha256, UUID acceptedBy, boolean canSubmit,
        boolean canRead, boolean canExport) { }
