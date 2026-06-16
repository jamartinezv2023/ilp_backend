package com.inclusive.adaptiveeducationservice.instrument.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "instrument_questions")
public class InstrumentQuestionEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String instrument;

    @Column(nullable = false)
    private String dimension;

    @Column(nullable = false)
    private Integer questionOrder;

    @Column(nullable = false, length = 1500)
    private String text;

    @Column(nullable = false)
    private String instrumentVersion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "instrument_question_options",
            joinColumns = @JoinColumn(name = "question_id")
    )
    @Column(name = "option_text", nullable = false, length = 1000)
    private List<String> options = new ArrayList<>();

    protected InstrumentQuestionEntity() {
    }

    public InstrumentQuestionEntity(
            String id,
            String instrument,
            String dimension,
            Integer questionOrder,
            String text,
            List<String> options,
            String instrumentVersion
    ) {
        this.id = id;
        this.instrument = instrument;
        this.dimension = dimension;
        this.questionOrder = questionOrder;
        this.text = text;
        this.options = new ArrayList<>(options);
        this.instrumentVersion = instrumentVersion;
    }

    public String getId() {
        return id;
    }

    public String getInstrument() {
        return instrument;
    }

    public String getDimension() {
        return dimension;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return List.copyOf(options);
    }

    public String getInstrumentVersion() {
        return instrumentVersion;
    }
}