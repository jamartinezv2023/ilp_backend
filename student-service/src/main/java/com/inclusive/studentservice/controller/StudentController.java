package com.inclusive.studentservice.controller;

import com.inclusive.studentservice.domain.Student;
import com.inclusive.studentservice.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> getAll() {
        return studentService.findAll();
    }

    @PostMapping
    public ResponseEntity<Student> create(@Valid @RequestBody Student student) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.save(student));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> update(@PathVariable Long id, @Valid @RequestBody Student student) {
        if (studentService.findById(id) == null) return ResponseEntity.notFound().build();
        student.setId(id);
        return ResponseEntity.ok(studentService.save(student));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Student student = studentService.findById(id);
        if (student == null) return ResponseEntity.notFound().build();
        student.deactivate();
        studentService.save(student);
        return ResponseEntity.noContent().build();
    }
}