package com.inclusive.adaptiveeducationservice.research.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RepositoryHygieneServiceTest {

    private final RepositoryHygieneService service =
            new RepositoryHygieneService();

    @Test
    void shouldGenerateRepositoryHygienePreview() {
        var response = service.generateRepositoryHygienePreview();

        assertThat(response.repositoryHygieneStatus()).contains("REPOSITORY_HYGIENE");
        assertThat(response.gitIgnoreReadiness()).contains("GITIGNORE");
        assertThat(response.artifactControlStatus()).contains("LOCAL_REPORTS");
        assertThat(response.sensitiveFileProtection()).contains("SECRET");
        assertThat(response.hygieneEvidence()).isNotEmpty();
        assertThat(response.recommendedRepositoryAction()).contains(".gitignore");
    }
}
