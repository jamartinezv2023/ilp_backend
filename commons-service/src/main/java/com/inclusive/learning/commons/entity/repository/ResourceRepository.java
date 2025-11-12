package com.inclusive.learning.commons.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inclusive.learning.commons.entity.model.Resource;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
}