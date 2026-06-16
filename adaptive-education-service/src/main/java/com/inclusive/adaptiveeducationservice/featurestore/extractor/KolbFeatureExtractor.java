package com.inclusive.adaptiveeducationservice.featurestore.extractor;

import com.inclusive.adaptiveeducationservice.assessmentresponse.entity.AssessmentAnswerEntity;
import com.inclusive.adaptiveeducationservice.assessmentresponse.entity.AssessmentResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KolbFeatureExtractor {

    public KolbFeatures extract(List<AssessmentResponseEntity> responses) {
        var kolbResponses = responses.stream()
                .filter(response -> "KOLB_V1".equalsIgnoreCase(
                        response.getAssessmentCode()
                ))
                .toList();

        int ce = 0;
        int ro = 0;
        int ac = 0;
        int ae = 0;

        for (AssessmentResponseEntity response : kolbResponses) {
            for (AssessmentAnswerEntity answer : response.getAnswers()) {
                if ("CE".equalsIgnoreCase(answer.getValue())) {
                    ce += answer.getScore();
                } else if ("RO".equalsIgnoreCase(answer.getValue())) {
                    ro += answer.getScore();
                } else if ("AC".equalsIgnoreCase(answer.getValue())) {
                    ac += answer.getScore();
                } else if ("AE".equalsIgnoreCase(answer.getValue())) {
                    ae += answer.getScore();
                }
            }
        }

        return new KolbFeatures(ce, ro, ac, ae, resolveStyle(ce, ro, ac, ae));
    }

    private String resolveStyle(int ce, int ro, int ac, int ae) {
        var abstractConcrete = ac - ce;
        var activeReflective = ae - ro;

        if (abstractConcrete >= 0 && activeReflective >= 0) {
            return "CONVERGENT";
        }

        if (abstractConcrete >= 0) {
            return "ASSIMILATOR";
        }

        if (activeReflective >= 0) {
            return "ACCOMMODATOR";
        }

        return "DIVERGENT";
    }

    public record KolbFeatures(
            Integer ce,
            Integer ro,
            Integer ac,
            Integer ae,
            String style
    ) {
    }
}