package com.inclusive.adaptiveeducationservice.config;

import com.inclusive.adaptiveeducationservice.entity.model.Student;
import com.inclusive.adaptiveeducationservice.entity.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initStudents(StudentRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            OffsetDateTime now = OffsetDateTime.now();

            for (int i = 1; i <= 10; i++) {
                Student s = new Student();
                s.setFullName("Demo Student " + i);
                s.setEmail("student" + i + "@example.com");
                s.setAge(10 + i);
                s.setGender(i % 2 == 0 ? "F" : "M");
                s.setSchoolLevel("Secondary");
                s.setSocioEconomicStatus(i % 2 == 0 ? "Medium" : "Low");
                s.setAttendanceRate(0.9);
                s.setAverageGrade(4.0);
                s.setMathScore(4.2);
                s.setReadingScore(3.9);
                s.setScienceScore(4.1);
                s.setHomeworkCompletionRate(0.85);
                s.setDisabilityStatus(i % 3 == 0 ? "Learning" : "None");
                s.setNeedsAssistiveTechnology(i % 3 == 0);
                s.setAdaptiveContentProfile("standard");
                s.setEngagementCluster("medium");
                s.setPredictedDropoutRisk(0.1);
                s.setAccountStatus("ACTIVE");
                s.setBirthDate(LocalDate.now().minusYears(10 + i));
                s.setCreatedAt(now);
                s.setUpdatedAt(now);
                repository.save(s);
            }
        };
    }
}
