// adaptive-education-service/src/main/java/com/inclusive/adaptiveeducationservice/client/StudentClient.java
package com.inclusive.adaptiveeducationservice.client;

import com.inclusive.common.dto.StudentDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

/**
 * Cliente REST para comunicarse con el auth-service
 * y obtener informaciÃ³n de los estudiantes existentes.
 */
@Service
public class StudentClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "http://localhost:8081/api/students"; // Puerto del auth-service

    public List<StudentDTO> getAllStudents() {
        StudentDTO[] students = restTemplate.getForObject(BASE_URL, StudentDTO[].class);
        return Arrays.asList(students);
    }

    public StudentDTO getStudentById(Long id) {
        return restTemplate.getForObject(BASE_URL + "/" + id, StudentDTO.class);
    }
}




