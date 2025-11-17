package com.inclusive.adaptiveeducationservice.client;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Stub client prepared for future cross-service calls.
 * Currently returns null to avoid coupling until endpoints are defined.
 */
@Component
public class StudentClient {

    private final RestTemplate restTemplate;

    public StudentClient() {
        this.restTemplate = new RestTemplate();
    }

    public StudentDTO fetchStudentById(Long id) {
        return null;
    }
}
