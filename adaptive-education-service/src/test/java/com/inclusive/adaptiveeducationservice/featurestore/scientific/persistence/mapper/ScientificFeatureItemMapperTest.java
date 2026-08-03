package com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.mapper;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.model.ScientificFeatureItem;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureCode;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureValue;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureDataType;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.persistence.entity.ScientificFeatureItemEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScientificFeatureItemMapperTest {

    private final ScientificFeatureItemMapper mapper =
            new ScientificFeatureItemMapper();

    @Test
    void shouldMapNumericDomainItemToEntity() {
        ScientificFeatureItem domain =
                new ScientificFeatureItem(
                        "SFI-NUMERIC",
                        new FeatureCode("KOLB_CE"),
                        FeatureValue.numeric(30.5),
                        "KOLB_V1",
                        "ADMIN-001"
                );

        ScientificFeatureItemEntity entity =
                mapper.toEntity(domain);

        assertThat(entity.getId())
                .isEqualTo("SFI-NUMERIC");

        assertThat(entity.getFeatureCode())
                .isEqualTo("KOLB_CE");

        assertThat(entity.getDataType())
                .isEqualTo(
                        ScientificFeatureDataType.NUMERIC
                );

        assertThat(entity.getNumericValue())
                .isEqualTo(30.5);

        assertThat(entity.getTextValue())
                .isNull();

        assertThat(entity.getBooleanValue())
                .isNull();

        assertThat(entity.getSourceAssessmentCode())
                .isEqualTo("KOLB_V1");

        assertThat(entity.getSourceAdministrationId())
                .isEqualTo("ADMIN-001");
    }

    @Test
    void shouldMapTextDomainItemToEntity() {
        ScientificFeatureItem domain =
                new ScientificFeatureItem(
                        "SFI-TEXT",
                        new FeatureCode("KOLB_PROFILE"),
                        FeatureValue.text("DIVERGENT"),
                        null,
                        null
                );

        ScientificFeatureItemEntity entity =
                mapper.toEntity(domain);

        assertThat(entity.getDataType())
                .isEqualTo(
                        ScientificFeatureDataType.TEXT
                );

        assertThat(entity.getTextValue())
                .isEqualTo("DIVERGENT");

        assertThat(entity.getNumericValue())
                .isNull();

        assertThat(entity.getBooleanValue())
                .isNull();
    }

    @Test
    void shouldMapBooleanDomainItemToEntity() {
        ScientificFeatureItem domain =
                new ScientificFeatureItem(
                        "SFI-BOOLEAN",
                        new FeatureCode("PROFILE_CHANGED"),
                        FeatureValue.bool(false),
                        null,
                        null
                );

        ScientificFeatureItemEntity entity =
                mapper.toEntity(domain);

        assertThat(entity.getDataType())
                .isEqualTo(
                        ScientificFeatureDataType.BOOLEAN
                );

        assertThat(entity.getBooleanValue())
                .isFalse();

        assertThat(entity.getNumericValue())
                .isNull();

        assertThat(entity.getTextValue())
                .isNull();
    }

    @Test
    void shouldMapNumericEntityToDomain() {
        ScientificFeatureItemEntity entity =
                ScientificFeatureItemEntity.numeric(
                        "SFI-NUMERIC",
                        "KOLB_CE",
                        30.5,
                        "KOLB_V1",
                        "ADMIN-001"
                );

        ScientificFeatureItem domain =
                mapper.toDomain(entity);

        assertThat(domain.id())
                .isEqualTo("SFI-NUMERIC");

        assertThat(domain.featureCode())
                .isEqualTo(
                        new FeatureCode("KOLB_CE")
                );

        assertThat(domain.value())
                .isEqualTo(
                        FeatureValue.numeric(30.5)
                );

        assertThat(domain.sourceAssessmentCode())
                .isEqualTo("KOLB_V1");

        assertThat(domain.sourceAdministrationId())
                .isEqualTo("ADMIN-001");
    }

    @Test
    void shouldMapTextEntityToDomain() {
        ScientificFeatureItemEntity entity =
                ScientificFeatureItemEntity.text(
                        "SFI-TEXT",
                        "KOLB_PROFILE",
                        "DIVERGENT",
                        null,
                        null
                );

        ScientificFeatureItem domain =
                mapper.toDomain(entity);

        assertThat(domain.value())
                .isEqualTo(
                        FeatureValue.text("DIVERGENT")
                );
    }

    @Test
    void shouldMapBooleanEntityToDomain() {
        ScientificFeatureItemEntity entity =
                ScientificFeatureItemEntity.bool(
                        "SFI-BOOLEAN",
                        "PROFILE_CHANGED",
                        false,
                        null,
                        null
                );

        ScientificFeatureItem domain =
                mapper.toDomain(entity);

        assertThat(domain.value())
                .isEqualTo(
                        FeatureValue.bool(false)
                );
    }

    @Test
    void shouldRoundTripAllSupportedTypes() {
        ScientificFeatureItem numeric =
                new ScientificFeatureItem(
                        "SFI-1",
                        new FeatureCode("KOLB_CE"),
                        FeatureValue.numeric(30.0),
                        "KOLB_V1",
                        "ADMIN-001"
                );

        ScientificFeatureItem text =
                new ScientificFeatureItem(
                        "SFI-2",
                        new FeatureCode("KOLB_PROFILE"),
                        FeatureValue.text("DIVERGENT"),
                        "KOLB_V1",
                        "ADMIN-001"
                );

        ScientificFeatureItem bool =
                new ScientificFeatureItem(
                        "SFI-3",
                        new FeatureCode("PROFILE_CHANGED"),
                        FeatureValue.bool(true),
                        "KOLB_V1",
                        "ADMIN-001"
                );

        assertThat(
                mapper.toDomain(
                        mapper.toEntity(numeric)
                )
        ).isEqualTo(numeric);

        assertThat(
                mapper.toDomain(
                        mapper.toEntity(text)
                )
        ).isEqualTo(text);

        assertThat(
                mapper.toDomain(
                        mapper.toEntity(bool)
                )
        ).isEqualTo(bool);
    }

    @Test
    void shouldRejectNullDomainItem() {
        assertThatThrownBy(() ->
                mapper.toEntity(null)
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessageContaining(
                        "domain feature item"
                );
    }

    @Test
    void shouldRejectNullEntity() {
        assertThatThrownBy(() ->
                mapper.toDomain(null)
        )
                .isInstanceOf(
                        NullPointerException.class
                )
                .hasMessageContaining(
                        "scientific feature item entity"
                );
    }
}