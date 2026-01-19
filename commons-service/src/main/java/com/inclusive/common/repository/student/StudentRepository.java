// commons-service/src/main/java/com/inclusive/common/repository/student/StudentRepository.java
package com.inclusive.common.repository.student;

import com.inclusive.common.domain.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Student domain entity.
 *
 * This repository is the single source of truth
 * for persistence access to students.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUserId(Long userId);

    List<Student> findByAccountStatus(String accountStatus);

}
