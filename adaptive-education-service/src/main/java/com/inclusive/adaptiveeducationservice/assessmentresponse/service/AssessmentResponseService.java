package com.inclusive.adaptiveeducationservice.assessmentresponse.service;

import com.inclusive.adaptiveeducationservice.assessmentdefinition.repository.AssessmentDefinitionRepository;
import com.inclusive.adaptiveeducationservice.assessmentresponse.dto.AssessmentAnswerRequest;
import com.inclusive.adaptiveeducationservice.assessmentresponse.dto.AssessmentAnswerResponse;
import com.inclusive.adaptiveeducationservice.assessmentresponse.dto.AssessmentResponseRequest;
import com.inclusive.adaptiveeducationservice.assessmentresponse.dto.AssessmentResponseResponse;
import com.inclusive.adaptiveeducationservice.assessmentresponse.entity.AssessmentAnswerEntity;
import com.inclusive.adaptiveeducationservice.assessmentresponse.entity.AssessmentResponseEntity;
import com.inclusive.adaptiveeducationservice.assessmentresponse.repository.AssessmentResponseRepository;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AssessmentResponseService {

    private final AssessmentResponseRepository responseRepository;

    private final StudentProfileRepository studentProfileRepository;

    private final AssessmentDefinitionRepository definitionRepository;

    public AssessmentResponseService(
            AssessmentResponseRepository responseRepository,
            StudentProfileRepository studentProfileRepository,
            AssessmentDefinitionRepository definitionRepository
    ) {
        this.responseRepository = responseRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.definitionRepository = definitionRepository;
    }

    public AssessmentResponseResponse submit(AssessmentResponseRequest request) {
        if (!studentProfileRepository.existsById(request.studentId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Student profile not found"
            );
        }

        if (!definitionRepository.existsByCode(request.assessmentCode())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Assessment definition not found"
            );
        }

        var response = new AssessmentResponseEntity(
                "AR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                request.studentId(),
                request.assessmentCode(),
                request.assessmentVersion(),
                "SUBMITTED",
                Instant.now()
        );

        for (int index = 0; index < request.answers().size(); index++) {
            response.addAnswer(toAnswerEntity(response.getId(), index, request.answers().get(index)));
        }

        return toResponse(responseRepository.save(response));
    }

    public AssessmentResponseResponse findById(String id) {
        return responseRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Assessment response not found"
                ));
    }

    public List<AssessmentResponseResponse> findByStudentId(String studentId) {
        return responseRepository.findByStudentIdOrderBySubmittedAtDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AssessmentAnswerEntity toAnswerEntity(
            String responseId,
            int index,
            AssessmentAnswerRequest answer
    ) {
        return new AssessmentAnswerEntity(
                responseId + "-A" + String.format("%03d", index + 1),
                answer.questionId(),
                answer.optionId(),
                answer.dimension(),
                answer.value(),
                answer.score()
        );
    }

    private AssessmentResponseResponse toResponse(
            AssessmentResponseEntity response
    ) {
        return new AssessmentResponseResponse(
                response.getId(),
                response.getStudentId(),
                response.getAssessmentCode(),
                response.getAssessmentVersion(),
                response.getStatus(),
                response.getSubmittedAt(),
                response.getAnswers()
                        .stream()
                        .map(this::toAnswerResponse)
                        .toList()
        );
    }

    private AssessmentAnswerResponse toAnswerResponse(
            AssessmentAnswerEntity answer
    ) {
        return new AssessmentAnswerResponse(
                answer.getId(),
                answer.getQuestionId(),
                answer.getOptionId(),
                answer.getDimension(),
                answer.getValue(),
                answer.getScore()
        );
    }
}