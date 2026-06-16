package com.inclusive.adaptiveeducationservice.assessmenthistory.service;

import com.inclusive.adaptiveeducationservice.assessmenthistory.dto.AssessmentHistoryItemResponse;
import com.inclusive.adaptiveeducationservice.assessmenthistory.dto.AssessmentTimelineItemResponse;
import com.inclusive.adaptiveeducationservice.assessmentresponse.entity.AssessmentResponseEntity;
import com.inclusive.adaptiveeducationservice.assessmentresponse.repository.AssessmentResponseRepository;
import com.inclusive.adaptiveeducationservice.student.repository.StudentProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
public class AssessmentHistoryService {

    private final AssessmentResponseRepository responseRepository;

    private final StudentProfileRepository studentProfileRepository;

    public AssessmentHistoryService(
            AssessmentResponseRepository responseRepository,
            StudentProfileRepository studentProfileRepository
    ) {
        this.responseRepository = responseRepository;
        this.studentProfileRepository = studentProfileRepository;
    }

    public List<AssessmentHistoryItemResponse> findStudentAssessmentHistory(
            String studentId
    ) {
        validateStudent(studentId);

        return responseRepository.findByStudentIdOrderBySubmittedAtDesc(studentId)
                .stream()
                .map(this::toHistoryItem)
                .toList();
    }

    public List<AssessmentHistoryItemResponse> findStudentAssessmentHistoryByCode(
            String studentId,
            String assessmentCode
    ) {
        validateStudent(studentId);

        return responseRepository.findByStudentIdOrderBySubmittedAtDesc(studentId)
                .stream()
                .filter(response -> response.getAssessmentCode()
                        .equalsIgnoreCase(assessmentCode))
                .map(this::toHistoryItem)
                .toList();
    }

    public List<AssessmentTimelineItemResponse> findStudentTimeline(
            String studentId
    ) {
        validateStudent(studentId);

        return responseRepository.findByStudentIdOrderBySubmittedAtDesc(studentId)
                .stream()
                .map(this::toTimelineItem)
                .sorted(Comparator.comparing(
                        AssessmentTimelineItemResponse::occurredAt
                ).reversed())
                .toList();
    }

    private void validateStudent(String studentId) {
        if (!studentProfileRepository.existsById(studentId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Student profile not found"
            );
        }
    }

    private AssessmentHistoryItemResponse toHistoryItem(
            AssessmentResponseEntity response
    ) {
        var totalScore = response.getAnswers()
                .stream()
                .mapToInt(answer -> answer.getScore())
                .sum();

        return new AssessmentHistoryItemResponse(
                response.getId(),
                response.getStudentId(),
                response.getAssessmentCode(),
                response.getAssessmentVersion(),
                response.getStatus(),
                response.getAnswers().size(),
                totalScore,
                response.getSubmittedAt()
        );
    }

    private AssessmentTimelineItemResponse toTimelineItem(
            AssessmentResponseEntity response
    ) {
        return new AssessmentTimelineItemResponse(
                response.getId(),
                response.getStudentId(),
                "ASSESSMENT_SUBMITTED",
                "Assessment submitted: " + response.getAssessmentCode(),
                "Student completed "
                        + response.getAssessmentCode()
                        + " version "
                        + response.getAssessmentVersion()
                        + " with "
                        + response.getAnswers().size()
                        + " answers.",
                response.getSubmittedAt()
        );
    }
}