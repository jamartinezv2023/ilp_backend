package com.inclusive.adaptiveeducationservice.fieldwork.repository;

import com.inclusive.adaptiveeducationservice.fieldwork.domain.ResearchParticipant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchParticipantRepository extends JpaRepository<ResearchParticipant, UUID> {
}
