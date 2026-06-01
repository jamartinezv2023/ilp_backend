package com.inclusive.adaptiveeducationservice.api.education;

import com.inclusive.adaptiveeducationservice.education.dto.EducationModuleSummaryResponse;
import com.inclusive.adaptiveeducationservice.education.service.EducationalSupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/education")
@RequiredArgsConstructor
public class EducationalSupportController {

    private final EducationalSupportService educationalSupportService;

    @GetMapping("/institutional-overview")
    public ResponseEntity<EducationModuleSummaryResponse> institutionalOverview() {
        return ResponseEntity.ok(educationalSupportService.institutionalOverview());
    }

    @GetMapping("/teacher-workspace")
    public ResponseEntity<EducationModuleSummaryResponse> teacherWorkspace() {
        return ResponseEntity.ok(educationalSupportService.teacherWorkspace());
    }

    @GetMapping("/student-support")
    public ResponseEntity<EducationModuleSummaryResponse> studentSupport() {
        return ResponseEntity.ok(educationalSupportService.studentSupport());
    }

    @GetMapping("/inclusion-piar")
    public ResponseEntity<EducationModuleSummaryResponse> inclusionPiar() {
        return ResponseEntity.ok(educationalSupportService.inclusionPiar());
    }

    @GetMapping("/family-engagement")
    public ResponseEntity<EducationModuleSummaryResponse> familyEngagement() {
        return ResponseEntity.ok(educationalSupportService.familyEngagement());
    }
}
