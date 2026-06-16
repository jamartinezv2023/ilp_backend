package com.inclusive.adaptiveeducationservice.assessment.service;

import com.inclusive.adaptiveeducationservice.assessment.dto.FelderSilvermanAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessment.dto.FelderSilvermanAssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessment.entity.FelderSilvermanAssessmentResultEntity;
import com.inclusive.adaptiveeducationservice.assessment.repository.FelderSilvermanAssessmentRepository;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class FelderSilvermanAssessmentService {

    private static final String INSTRUMENT_VERSION =
            "FELDER_SILVERMAN_BASELINE_V1";

    private final FelderSilvermanAssessmentEngine engine;

    private final FelderSilvermanAssessmentRepository repository;

    private final StudentProfileRepository studentProfileRepository;

    public FelderSilvermanAssessmentService(
            FelderSilvermanAssessmentEngine engine,
            FelderSilvermanAssessmentRepository repository,
            StudentProfileRepository studentProfileRepository
    ) {
        this.engine = engine;
        this.repository = repository;
        this.studentProfileRepository = studentProfileRepository;
    }

    @Transactional
    public FelderSilvermanAssessmentResponse submit(
            FelderSilvermanAssessmentRequest request
    ) {
        var student = studentProfileRepository.findById(request.studentId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student profile not found"
                ));

        var scores = engine.calculate(request.answers());
        var assessmentId = "FS-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        var result = new FelderSilvermanAssessmentResultEntity(
                assessmentId,
                request.studentId(),
                scores.activeReflectiveScore(),
                scores.sensingIntuitiveScore(),
                scores.visualVerbalScore(),
                scores.sequentialGlobalScore(),
                scores.dominantProfile(),
                INSTRUMENT_VERSION,
                Instant.now(),
                request.answers(),
                scores.learningPreferences()
        );

        student.updateLearningPreferences(scores.learningPreferences());
        studentProfileRepository.save(student);

        return toResponse(repository.save(result));
    }

    public List<FelderSilvermanAssessmentResponse> findByStudentId(
            String studentId
    ) {
        return repository.findByStudentIdOrderByCreatedAtDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private FelderSilvermanAssessmentResponse toResponse(
            FelderSilvermanAssessmentResultEntity result
    ) {
        return new FelderSilvermanAssessmentResponse(
                result.getId(),
                result.getStudentId(),
                result.getActiveReflectiveScore(),
                result.getSensingIntuitiveScore(),
                result.getVisualVerbalScore(),
                result.getSequentialGlobalScore(),
                result.getDominantProfile(),
                result.getLearningPreferences(),
                result.getInstrumentVersion(),
                result.getCreatedAt()
        );
    }
}