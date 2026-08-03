package com.inclusive.adaptiveeducationservice.assessment.service;

import com.inclusive.adaptiveeducationservice.assessment.dto.KolbAssessmentRequest;
import com.inclusive.adaptiveeducationservice.assessment.dto.KolbAssessmentResponse;
import com.inclusive.adaptiveeducationservice.assessment.entity.KolbAssessmentResultEntity;
import com.inclusive.adaptiveeducationservice.assessment.repository.KolbAssessmentResultRepository;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.domain.AssessmentResult;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.strategy.kolb.KolbGenericAssessmentFacade;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class KolbAssessmentService {

    private static final String INSTRUMENT_VERSION =
            "KOLB_BASELINE_V1";

    private final KolbAssessmentEngine kolbAssessmentEngine;

    private final KolbAssessmentResultRepository
            assessmentResultRepository;

    private final StudentProfileRepository
            studentProfileRepository;

    private KolbGenericAssessmentFacade
            genericAssessmentFacade;

    public KolbAssessmentService(
            KolbAssessmentEngine kolbAssessmentEngine,
            KolbAssessmentResultRepository assessmentResultRepository,
            StudentProfileRepository studentProfileRepository
    ) {
        this.kolbAssessmentEngine = kolbAssessmentEngine;
        this.assessmentResultRepository =
                assessmentResultRepository;
        this.studentProfileRepository =
                studentProfileRepository;
    }

    @Autowired
    void setGenericAssessmentFacade(
            KolbGenericAssessmentFacade genericAssessmentFacade
    ) {
        this.genericAssessmentFacade = genericAssessmentFacade;
    }

    @Transactional
    public KolbAssessmentResponse submit(
            KolbAssessmentRequest request
    ) {
        var student =
                studentProfileRepository
                        .findById(request.studentId())
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Student profile not found"
                                        )
                        );

        String assessmentId =
                "KOLB-"
                        + UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase();

        KolbCalculatedResult calculated =
                calculate(
                        assessmentId,
                        request
                );

        var result =
                new KolbAssessmentResultEntity(
                        assessmentId,
                        request.studentId(),
                        calculated.scoreCE(),
                        calculated.scoreRO(),
                        calculated.scoreAC(),
                        calculated.scoreAE(),
                        calculated.learningStyle(),
                        calculated.instrumentVersion(),
                        calculated.createdAt(),
                        request.answers()
                );

        student.updateLearningProfile(
                calculated.learningStyle()
        );

        studentProfileRepository.save(student);

        return toResponse(
                assessmentResultRepository.save(result)
        );
    }

    public List<KolbAssessmentResponse> findByStudentId(
            String studentId
    ) {
        return assessmentResultRepository
                .findByStudentIdOrderByCreatedAtDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private KolbCalculatedResult calculate(
            String assessmentId,
            KolbAssessmentRequest request
    ) {
        if (genericAssessmentFacade != null) {
            AssessmentResult genericResult =
                    genericAssessmentFacade.evaluate(
                            assessmentId,
                            request
                    );

            return new KolbCalculatedResult(
                    requiredIntegerScore(
                            genericResult,
                            "CE"
                    ),
                    requiredIntegerScore(
                            genericResult,
                            "RO"
                    ),
                    requiredIntegerScore(
                            genericResult,
                            "AC"
                    ),
                    requiredIntegerScore(
                            genericResult,
                            "AE"
                    ),
                    genericResult.primaryProfile(),
                    genericResult.scoringAlgorithmVersion(),
                    genericResult.calculatedAt()
            );
        }

        var legacyScores =
                kolbAssessmentEngine.calculate(
                        request.answers()
                );

        return new KolbCalculatedResult(
                legacyScores.scoreCE(),
                legacyScores.scoreRO(),
                legacyScores.scoreAC(),
                legacyScores.scoreAE(),
                legacyScores.learningStyle(),
                INSTRUMENT_VERSION,
                Instant.now()
        );
    }

    private Integer requiredIntegerScore(
            AssessmentResult result,
            String scoreName
    ) {
        Double score = result.scores().get(scoreName);

        if (score == null) {
            throw new IllegalStateException(
                    "Generic Kolb result is missing score: "
                            + scoreName
            );
        }

        long rounded = Math.round(score);

        if (Double.compare(score, (double) rounded) != 0) {
            throw new IllegalStateException(
                    "Generic Kolb score is not an integer: "
                            + scoreName
            );
        }

        return Math.toIntExact(rounded);
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

    private record KolbCalculatedResult(
            Integer scoreCE,
            Integer scoreRO,
            Integer scoreAC,
            Integer scoreAE,
            String learningStyle,
            String instrumentVersion,
            Instant createdAt
    ) {
    }
}