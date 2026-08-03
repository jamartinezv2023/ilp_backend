package com.inclusive.adaptiveeducationservice.featurestore.scientific.provider.kolb;

import com.inclusive.adaptiveeducationservice.assessment.entity.KolbAssessmentResultEntity;
import com.inclusive.adaptiveeducationservice.assessment.repository.KolbAssessmentResultRepository;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureCode;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureValue;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.generation.model.ScientificFeatureGenerationRequest;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.out.provider.ScientificFeatureProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public final class KolbScientificFeatureProvider
        implements ScientificFeatureProvider {

    static final String SOURCE_ASSESSMENT_CODE = "KOLB";

    static final String CE_SCORE_CODE = "KOLB_CE_SCORE";
    static final String RO_SCORE_CODE = "KOLB_RO_SCORE";
    static final String AC_SCORE_CODE = "KOLB_AC_SCORE";
    static final String AE_SCORE_CODE = "KOLB_AE_SCORE";
    static final String AC_MINUS_CE_CODE = "KOLB_AC_MINUS_CE";
    static final String AE_MINUS_RO_CODE = "KOLB_AE_MINUS_RO";
    static final String LEARNING_STYLE_CODE = "KOLB_LEARNING_STYLE";

    private final KolbAssessmentResultRepository resultRepository;

    public KolbScientificFeatureProvider(
            KolbAssessmentResultRepository resultRepository
    ) {
        this.resultRepository = Objects.requireNonNull(
                resultRepository,
                "resultRepository is required"
        );
    }

    @Override
    public List<ScientificFeatureItem> provide(
            ScientificFeatureGenerationRequest request
    ) {
        Objects.requireNonNull(
                request,
                "request is required"
        );

        return resultRepository
                .findFirstByStudentIdAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
                        request.participantId().value(),
                        request.featureCutoffAt()
                )
                .map(this::toScientificFeatures)
                .orElseGet(List::of);
    }

    private List<ScientificFeatureItem> toScientificFeatures(
            KolbAssessmentResultEntity result
    ) {
        double abstractConcreteAxis =
                result.getScoreAC() - result.getScoreCE();

        double activeReflectiveAxis =
                result.getScoreAE() - result.getScoreRO();

        return List.of(
                numericFeature(
                        result,
                        CE_SCORE_CODE,
                        result.getScoreCE()
                ),
                numericFeature(
                        result,
                        RO_SCORE_CODE,
                        result.getScoreRO()
                ),
                numericFeature(
                        result,
                        AC_SCORE_CODE,
                        result.getScoreAC()
                ),
                numericFeature(
                        result,
                        AE_SCORE_CODE,
                        result.getScoreAE()
                ),
                numericFeature(
                        result,
                        AC_MINUS_CE_CODE,
                        abstractConcreteAxis
                ),
                numericFeature(
                        result,
                        AE_MINUS_RO_CODE,
                        activeReflectiveAxis
                ),
                textFeature(
                        result,
                        LEARNING_STYLE_CODE,
                        result.getLearningStyle()
                )
        );
    }

    private ScientificFeatureItem numericFeature(
            KolbAssessmentResultEntity result,
            String featureCode,
            double value
    ) {
        return feature(
                result,
                featureCode,
                FeatureValue.numeric(value)
        );
    }

    private ScientificFeatureItem textFeature(
            KolbAssessmentResultEntity result,
            String featureCode,
            String value
    ) {
        return feature(
                result,
                featureCode,
                FeatureValue.text(value)
        );
    }

    private ScientificFeatureItem feature(
            KolbAssessmentResultEntity result,
            String featureCode,
            FeatureValue value
    ) {
        return new ScientificFeatureItem(
                featureId(result.getId(), featureCode),
                new FeatureCode(featureCode),
                value,
                SOURCE_ASSESSMENT_CODE,
                result.getId()
        );
    }

    private String featureId(
            String administrationId,
            String featureCode
    ) {
        return administrationId + "-" + featureCode;
    }
}