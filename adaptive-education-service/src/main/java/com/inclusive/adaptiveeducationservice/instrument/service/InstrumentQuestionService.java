package com.inclusive.adaptiveeducationservice.instrument.service;

import com.inclusive.adaptiveeducationservice.instrument.dto.InstrumentQuestionResponse;
import com.inclusive.adaptiveeducationservice.instrument.entity.InstrumentQuestionEntity;
import com.inclusive.adaptiveeducationservice.instrument.repository.InstrumentQuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstrumentQuestionService {

    private final InstrumentQuestionRepository questionRepository;

    public InstrumentQuestionService(
            InstrumentQuestionRepository questionRepository
    ) {
        this.questionRepository = questionRepository;
    }

    public List<InstrumentQuestionResponse> findKolbQuestions() {
        return findByInstrument("KOLB");
    }

    public List<InstrumentQuestionResponse> findFelderSilvermanQuestions() {
        return findByInstrument("FELDER_SILVERMAN");
    }

    public List<InstrumentQuestionResponse> findKuderQuestions() {
        return findByInstrument("KUDER");
    }

    private List<InstrumentQuestionResponse> findByInstrument(
            String instrument
    ) {
        return questionRepository.findByInstrumentOrderByQuestionOrderAsc(
                        instrument
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private InstrumentQuestionResponse toResponse(
            InstrumentQuestionEntity question
    ) {
        return new InstrumentQuestionResponse(
                question.getId(),
                question.getInstrument(),
                question.getDimension(),
                question.getQuestionOrder(),
                question.getText(),
                question.getOptions(),
                question.getInstrumentVersion()
        );
    }
}