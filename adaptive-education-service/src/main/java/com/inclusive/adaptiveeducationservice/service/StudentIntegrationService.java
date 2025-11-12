// adaptive-education-service/src/main/java/com/inclusive/adaptiveeducationservice/service/StudentIntegrationService.java
package com.inclusive.adaptiveeducationservice.service;

import com.inclusive.common.dto.StudentDTO;
import com.inclusive.adaptiveeducationservice.client.StudentClient;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentIntegrationService {

    private final StudentClient studentClient;

    public StudentIntegrationService(StudentClient studentClient) {
        this.studentClient = studentClient;
    }

    public List<StudentDTO> getStudents() {
        return studentClient.getAllStudents();
    }

    public StudentDTO getStudent(Long id) {
        return studentClient.getStudentById(id);
    }
}




