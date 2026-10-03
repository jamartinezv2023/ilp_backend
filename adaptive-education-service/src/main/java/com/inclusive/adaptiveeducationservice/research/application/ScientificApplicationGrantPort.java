package com.inclusive.adaptiveeducationservice.research.application;

import java.util.Optional;
import java.util.UUID;

/** No production adapter is registered until institutional assignments are implemented. */
public interface ScientificApplicationGrantPort {
    Optional<ScientificApplicationGrant> findById(UUID grantId);
}
