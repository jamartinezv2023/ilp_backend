package com.inclusive.adaptiveeducationservice.api.student;

import com.inclusive.adaptiveeducationservice.student.dto.StudentProfileRequest;
import com.inclusive.adaptiveeducationservice.student.dto.StudentProfileResponse;
import com.inclusive.adaptiveeducationservice.student.service.StudentProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<StudentProfileResponse> findById(
            @PathVariable String id
    ) {
        return studentProfileService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StudentProfileResponse> create(
            @Valid @RequestBody StudentProfileRequest request
    ) {
        var id = "ST-" + UUID.randomUUID().toString().substring(0, 8)
                .toUpperCase();

        return ResponseEntity.ok(studentProfileService.create(id, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentProfileResponse> update(
            @PathVariable String id,
            @Valid @RequestBody StudentProfileRequest request
    ) {
        return studentProfileService.update(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}