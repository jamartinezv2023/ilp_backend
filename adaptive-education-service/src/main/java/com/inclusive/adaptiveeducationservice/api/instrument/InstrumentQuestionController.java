package com.inclusive.adaptiveeducationservice.api.instrument;

import com.inclusive.adaptiveeducationservice.instrument.dto.InstrumentQuestionResponse;
import com.inclusive.adaptiveeducationservice.instrument.service.InstrumentQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/instruments")
public class InstrumentQuestionController {

    private final InstrumentQuestionService questionService;

    @GetMapping("/kolb/questions")
    public ResponseEntity<List<InstrumentQuestionResponse>> kolbQuestions() {
        return ResponseEntity.ok(questionService.findKolbQuestions());
    }

    @GetMapping("/felder-silverman/questions")
    public ResponseEntity<List<InstrumentQuestionResponse>>
    felderSilvermanQuestions() {
        return ResponseEntity.ok(
                questionService.findFelderSilvermanQuestions()
        );
    }

    @GetMapping("/kuder/questions")
    public ResponseEntity<List<InstrumentQuestionResponse>> kuderQuestions() {
        return ResponseEntity.ok(questionService.findKuderQuestions());
    }
}