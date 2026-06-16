package com.inclusive.adaptiveeducationservice.assessment.service;

import com.inclusive.adaptiveeducationservice.assessment.dto.KuderAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessment.dto.KuderAssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessment.entity.KuderAssessmentResultEntity;
import com.inclusive.adaptiveeducationservice.assessment.repository.KuderAssessmentRepository;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class KuderAssessmentService {

    private static final String INSTRUMENT_VERSION = "KUDER_BASELINE_V1";

    private final KuderAssessmentEngine engine;

    private final KuderAssessmentRepository repository;

    private final StudentProfileRepository studentProfileRepository;

    public KuderAssessmentService(
            KuderAssessmentEngine engine,
            KuderAssessmentRepository repository,
            StudentProfileRepository studentProfileRepository
    ) {
        this.engine = engine;
        this.repository = repository;
        this.studentProfileRepository = studentProfileRepository;
    }

    @Transactional
    public KuderAssessmentResponse submit(KuderAssessmentRequest request) {
        var student = studentProfileRepository.findById(request.studentId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student profile not found"
                ));

        var scores = engine.calculate(request.answers());
        var assessmentId = "KUDER-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        var result = new KuderAssessmentResultEntity(
                assessmentId,
                request.studentId(),
                scores.dominantVocationalArea(),
                scores.topVocationalAreas(),
                scores.scientificScore(),
                scores.artisticScore(),
                scores.socialScore(),
                scores.mechanicalScore(),
                scores.administrativeScore(),
                INSTRUMENT_VERSION,
                Instant.now(),
                request.answers()
        );

        student.updateVocationalInterest(scores.dominantVocationalArea());
        studentProfileRepository.save(student);

        return toResponse(repository.save(result));
    }

    public List<KuderAssessmentResponse> findByStudentId(String studentId) {
        return repository.findByStudentIdOrderByCreatedAtDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private KuderAssessmentResponse toResponse(
            KuderAssessmentResultEntity result
    ) {
        return new KuderAssessmentResponse(
                result.getId(),
                result.getStudentId(),
                result.getDominantVocationalArea(),
                result.getTopVocationalAreas(),
                result.getScientificScore(),
                result.getArtisticScore(),
                result.getSocialScore(),
                result.getMechanicalScore(),
                result.getAdministrativeScore(),
                result.getInstrumentVersion(),
                result.getCreatedAt()
        );
    }
}