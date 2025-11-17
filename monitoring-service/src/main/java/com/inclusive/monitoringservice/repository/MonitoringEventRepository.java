package com.inclusive.monitoringservice.repository;

import com.inclusive.monitoringservice.entity.MonitoringEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonitoringEventRepository extends JpaRepository<MonitoringEvent, Long> {

    List<MonitoringEvent> findByServiceName(String serviceName);

    List<MonitoringEvent> findBySeverity(String severity);
}