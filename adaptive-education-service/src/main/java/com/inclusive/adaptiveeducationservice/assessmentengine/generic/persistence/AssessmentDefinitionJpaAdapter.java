package com.inclusive.adaptiveeducationservice.assessmentengine.generic.persistence;

import com.inclusive.adaptiveeducationservice.assessmentdefinition.repository.AssessmentDefinitionRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentDefinition;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.AssessmentDefinitionRepositoryPort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class AssessmentDefinitionJpaAdapter
        implements AssessmentDefinitionRepositoryPort {

    private final AssessmentDefinitionRepository repository;
    private final AssessmentDefinitionPersistenceMapper mapper;

    public AssessmentDefinitionJpaAdapter(
            AssessmentDefinitionRepository repository,
            AssessmentDefinitionPersistenceMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public AssessmentDefinition save(
            AssessmentDefinition definition
    ) {
        return mapper.toDomain(
                repository.save(mapper.toEntity(definition))
        );
    }

    @Override
    public boolean existsByCodeAndVersion(
            String assessmentCode,
            String version
    ) {
        return repository.findByCode(assessmentCode)
                .filter(entity -> version.equals(entity.getVersion()))
                .isPresent();
    }

    @Override
    public Optional<AssessmentDefinition> findByCodeAndVersion(
            String assessmentCode,
            String version
    ) {
        return repository.findByCode(assessmentCode)
                .filter(entity -> version.equals(entity.getVersion()))
                .map(mapper::toDomain);
    }

    @Override
    public Optional<AssessmentDefinition> findLatestActiveByCode(
            String assessmentCode
    ) {
        return repository.findByCodeAndActiveTrue(assessmentCode)
                .map(mapper::toDomain);
    }

    @Override
    public List<AssessmentDefinition> findAllByCode(
            String assessmentCode
    ) {
        return repository.findByCode(assessmentCode)
                .map(mapper::toDomain)
                .map(List::of)
                .orElseGet(List::of);
    }

    @Override
    public List<AssessmentDefinition> findAllActive() {
        return repository.findAllByActiveTrueOrderByCodeAsc()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}