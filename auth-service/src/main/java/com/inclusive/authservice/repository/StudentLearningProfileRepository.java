package com.inclusive.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inclusive.authservice.model.StudentLearningProfile;

@Repository
public interface StudentLearningProfileRepository extends JpaRepository<StudentLearningProfile, Long> {
}