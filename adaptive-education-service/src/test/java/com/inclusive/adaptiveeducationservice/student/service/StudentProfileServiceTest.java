package com.inclusive.adaptiveeducationservice.student.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StudentProfileServiceTest {

    private final StudentProfileService service = new StudentProfileService();

    @Test
    void shouldReturnAllStudents() {
        var students = service.findAll();

        assertThat(students).hasSize(3);
        assertThat(students.get(0).id()).isEqualTo("ST-001");
    }

    @Test
    void shouldReturnStudentById() {
        var student = service.findById("ST-001");

        assertThat(student).isPresent();
        assertThat(student.get().fullName()).contains("Mariana");
    }

    @Test
    void shouldReturnEmptyWhenStudentDoesNotExist() {
        var student = service.findById("ST-999");

        assertThat(student).isEmpty();
    }
}
