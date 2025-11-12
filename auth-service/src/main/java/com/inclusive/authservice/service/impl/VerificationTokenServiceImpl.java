package com.inclusive.authservice.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.inclusive.authservice.model.VerificationToken;
import com.inclusive.authservice.repository.VerificationTokenRepository;
import com.inclusive.authservice.service.VerificationTokenService;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    @Autowired
    private VerificationTokenRepository repository;

    @Override
    public List<VerificationToken> listAll() { return repository.findAll(); }

    @Override
    public VerificationToken getById(Long id) { return repository.findById(id).orElse(null); }

    @Override
    public VerificationToken create(VerificationToken entity) { return repository.save(entity); }

    @Override
    public VerificationToken update(Long id, VerificationToken entity) {
        Optional<VerificationToken> opt = repository.findById(id);
        if (!opt.isPresent()) return null;
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) { repository.deleteById(id); }
}