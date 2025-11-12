// adaptive-education-service/src/main/java/com/inclusive/adaptiveeducationservice/controller/StudentProxyController.java
package com.inclusive.adaptiveeducationservice.controller;

import com.inclusive.common.dto.StudentDTO;
import com.inclusive.adaptiveeducationservice.service.StudentIntegrationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proxy/students")
public class StudentProxyController {

    private final StudentIntegrationService integrationService;

    public StudentProxyController(StudentIntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @GetMapping
    public List<StudentDTO> getAllStudents() {
        return integrationService.getStudents();
    }

    @GetMapping("/{id}")
    public StudentDTO getStudentById(@PathVariable Long id) {
        return integrationService.getStudent(id);
    }
}




