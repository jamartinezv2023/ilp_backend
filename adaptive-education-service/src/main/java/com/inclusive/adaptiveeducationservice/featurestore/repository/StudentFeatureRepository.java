package com.inclusive.adaptiveeducationservice.featurestore.repository;

import com.inclusive.adaptiveeducationservice.featurestore.entity.StudentFeatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentFeatureRepository
        extends JpaRepository<StudentFeatureEntity, String> {

    List<StudentFeatureEntity>
    findByStudentIdOrderByFeatureDateDesc(
            String studentId
    );
}