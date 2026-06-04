package com.inclusive.adaptiveeducationservice.instrument.repository;

import com.inclusive.adaptiveeducationservice.instrument.entity.InstrumentQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstrumentQuestionRepository
        extends JpaRepository<InstrumentQuestionEntity, String> {

    List<InstrumentQuestionEntity> findByInstrumentOrderByQuestionOrderAsc(
            String instrument
    );
}