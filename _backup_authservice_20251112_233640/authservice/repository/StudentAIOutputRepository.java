package com.inclusive.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inclusive.authservice.model.StudentAIOutput;

@Repository
public interface StudentAIOutputRepository extends JpaRepository<StudentAIOutput, Long> {
}