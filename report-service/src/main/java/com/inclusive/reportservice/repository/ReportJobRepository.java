package com.inclusive.reportservice.repository;

import com.inclusive.reportservice.entity.ReportJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportJobRepository extends JpaRepository<ReportJob, Long> {
}