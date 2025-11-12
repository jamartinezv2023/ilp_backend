package com.inclusive.adaptiveeducationservice.config;

import com.inclusive.adaptiveeducationservice.entity.Student;
import com.inclusive.adaptiveeducationservice.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(StudentRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                for (int i = 1; i <= 10; i++) {
                    Student s = new Student();
                    s.setName("Estudiante " + i);
                    s.setEmail("estudiante" + i + "@ejemplo.com");
                    s.setAge(10 + i);
                    repository.save(s);
                }
                System.out.println("âœ… 10 estudiantes creados automÃ¡ticamente en la base de datos PostgreSQL.");
            } else {
                System.out.println("â„¹ï¸ Ya existen estudiantes en la base de datos. No se generaron nuevos.");
            }
        };
    }
}



