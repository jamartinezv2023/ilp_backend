package com.inclusive.common.domain.student;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    List<Student> findAll();

    Optional<Student> findById(Long id);

    Student create(Student student);

    Optional<Student> update(Long id, Student student);

    void deactivate(Long id);
}
