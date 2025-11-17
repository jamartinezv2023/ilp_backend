package com.inclusive.authservice.service;

import java.util.List;
import com.inclusive.authservice.model.VerificationToken;

public interface VerificationTokenService {
    List<VerificationToken> listAll();
    VerificationToken getById(Long id);
    VerificationToken create(VerificationToken entity);
    VerificationToken update(Long id, VerificationToken entity);
    void delete(Long id);
}