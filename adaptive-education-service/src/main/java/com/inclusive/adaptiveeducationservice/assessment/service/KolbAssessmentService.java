package com.inclusive.adaptiveeducationservice.assessment.service;

import com.inclusive.adaptiveeducationservice.assessment.dto.KolbAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessment.dto.KolbAssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessment.entity.KolbAssessmentResultEntity;
import com.inclusive.adaptiveeducationservice.assessment.repository.KolbAssessmentResultRepository;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class KolbAssessmentService {

    private static final String INSTRUMENT_VERSION = "KOLB_BASELINE_V1";

    private final KolbAssessmentEngine kolbAssessmentEngine;

    private final KolbAssessmentResultRepository assessmentResultRepository;

    private final StudentProfileRepository studentProfileRepository;

    public KolbAssessmentService(
            KolbAssessmentEngine kolbAssessmentEngine,
            KolbAssessmentResultRepository assessmentResultRepository,
            StudentProfileRepository studentProfileRepository
    ) {
        this.kolbAssessmentEngine = kolbAssessmentEngine;
        this.assessmentResultRepository = assessmentResultRepository;
        this.studentProfileRepository = studentProfileRepository;
    }

    @Transactional
    public KolbAssessmentResponse submit(KolbAssessmentRequest request) {
        var student = studentProfileRepository.findById(request.studentId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student profile not found"
                ));

        var scores = kolbAssessmentEngine.calculate(request.answers());
        var assessmentId = "KOLB-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        var result = new KolbAssessmentResultEntity(
                assessmentId,
                request.studentId(),
                scores.scoreCE(),
                scores.scoreRO(),
                scores.scoreAC(),
                scores.scoreAE(),
                scores.learningStyle(),
                INSTRUMENT_VERSION,
                Instant.now(),
                request.answers()
        );

        student.updateLearningProfile(scores.learningStyle());
        studentProfileRepository.save(student);

        return toResponse(assessmentResultRepository.save(result));
    }

    public List<KolbAssessmentResponse> findByStudentId(String studentId) {
        return assessmentResultRepository
                .findByStudentIdOrderByCreatedAtDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private KolbAssessmentResponse toResponse(
            KolbAssessmentResultEntity result
    ) {
        return new KolbAssessmentResponse(
                result.getId(),
                result.getStudentId(),
                result.getScoreCE(),
                result.getScoreRO(),
                result.getScoreAC(),
                result.getScoreAE(),
                result.getLearningStyle(),
                result.getInstrumentVersion(),
                result.getCreatedAt()
        );
    }
}