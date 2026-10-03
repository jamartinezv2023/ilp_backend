package com.inclusive.adaptiveeducationservice.research.production;

import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.scientific.history.GetParticipantAssessmentScientificHistoryService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.application.submission.SubmitAssessmentService;
import com.inclusive.adaptiveeducationservice.assessmentengine.generic.port.out.scientific.ScientificParticipantIdentityPort;
import com.inclusive.adaptiveeducationservice.assessmentresponse.service.AssessmentResponseService;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ConsentRecordRepository;
import com.inclusive.adaptiveeducationservice.fieldwork.repository.ResearchParticipantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

@Configuration
@Profile("scientific-production")
public class ScientificProductionConfiguration {
    @Bean
    ScientificAssignmentStore scientificAssignments(JdbcTemplate jdbc) {
        // Fail startup if the separately reviewed additive schema is absent.
        jdbc.queryForObject("SELECT COUNT(*) FROM scientific_assignments", Long.class);
        return new ScientificAssignmentStore(jdbc);
    }

    @Bean
    ScientificProductionService scientificProductionService(ScientificAssignmentStore assignments,
            ResearchParticipantRepository participants, ConsentRecordRepository consents,
            ScientificParticipantIdentityPort identities, SubmitAssessmentService submissions,
            GetParticipantAssessmentScientificHistoryService histories, AssessmentResponseService responses) {
        return new ScientificProductionService(assignments, participants, consents, identities,
                submissions, histories, responses);
    }

    @Bean
    ScientificTokenVerifier scientificTokenVerifier(
            @Value("${ilp.scientific.jwt.public-key}") Resource resource,
            @Value("${ilp.scientific.jwt.issuer}") String issuer,
            @Value("${ilp.scientific.jwt.audience}") String audience)
            throws IOException, GeneralSecurityException {
        String pem;
        try (var input = resource.getInputStream()) {
            pem = new String(input.readAllBytes(), java.nio.charset.StandardCharsets.US_ASCII);
        }
        String encoded = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "").replaceAll("\\s", "");
        var key = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(encoded)));
        return new ScientificTokenVerifier(key, issuer, audience);
    }

    @Bean
    FilterRegistrationBean<ScientificApiFilter> scientificApiBoundary(ScientificTokenVerifier verifier) {
        var registration = new FilterRegistrationBean<>(new ScientificApiFilter(verifier));
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    FilterRegistrationBean<CorsFilter> scientificCors(
            @Value("${ilp.scientific.cors.origin}") String origin) {
        if (!origin.startsWith("https://") || origin.contains("*") || origin.contains(",")) {
            throw new IllegalArgumentException("An exact HTTPS frontend origin is required");
        }
        var policy = new CorsConfiguration();
        policy.setAllowedOrigins(List.of(origin));
        policy.setAllowedMethods(List.of("GET", "POST"));
        policy.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Tenant-Id", "X-Scientific-Grant"));
        policy.setAllowCredentials(false);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", policy);
        var registration = new FilterRegistrationBean<>(new CorsFilter(source));
        registration.addUrlPatterns("/api/*");
        registration.setOrder(0);
        return registration;
    }
}
