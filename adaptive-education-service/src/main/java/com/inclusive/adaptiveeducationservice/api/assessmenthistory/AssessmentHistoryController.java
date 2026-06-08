package com.inclusive.adaptiveeducationservice.api.assessmenthistory;

import com.inclusive.adaptiveeducationservice.assessmenthistory.dto.AssessmentHistoryItemResponse;
import com.inclusive.adaptiveeducationservice.assessmenthistory.dto.AssessmentTimelineItemResponse;
import com.inclusive.adaptiveeducationservice.assessmenthistory.service.AssessmentHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/students/{studentId}")
public class AssessmentHistoryController {

    private final AssessmentHistoryService historyService;

    @GetMapping("/assessment-history")
    public ResponseEntity<List<AssessmentHistoryItemResponse>>
    findStudentAssessmentHistory(@PathVariable String studentId) {
        return ResponseEntity.ok(
                historyService.findStudentAssessmentHistory(studentId)
        );
    }

    @GetMapping("/assessment-history/{assessmentCode}")
    public ResponseEntity<List<AssessmentHistoryItemResponse>>
    findStudentAssessmentHistoryByCode(
            @PathVariable String studentId,
            @PathVariable String assessmentCode
    ) {
        return ResponseEntity.ok(
                historyService.findStudentAssessmentHistoryByCode(
                        studentId,
                        assessmentCode
                )
        );
    }

    @GetMapping("/timeline")
    public ResponseEntity<List<AssessmentTimelineItemResponse>>
    findStudentTimeline(@PathVariable String studentId) {
        return ResponseEntity.ok(historyService.findStudentTimeline(studentId));
    }
}