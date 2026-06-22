package com.inclusive.adaptiveeducationservice.fieldwork.repository;

import com.inclusive.adaptiveeducationservice.fieldwork.domain.ConsentRecord;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsentRecordRepository extends JpaRepository<ConsentRecord, UUID> {

    Optional<ConsentRecord> findFirstByParticipantCodeOrderByCreatedAtDesc(String participantCode);
}
