package com.inclusive.adaptiveeducationservice.api.student;

import com.inclusive.adaptiveeducationservice.student.dto.StudentProfileResponse;
import com.inclusive.adaptiveeducationservice.student.service.StudentProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    @GetMapping
    public ResponseEntity<List<StudentProfileResponse>> findAll() {
        return ResponseEntity.ok(studentProfileService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentProfileResponse> findById(@PathVariable String id) {
        return studentProfileService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
