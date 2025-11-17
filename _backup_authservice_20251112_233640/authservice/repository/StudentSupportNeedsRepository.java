package com.inclusive.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inclusive.authservice.model.StudentSupportNeeds;

@Repository
public interface StudentSupportNeedsRepository extends JpaRepository<StudentSupportNeeds, Long> {
}