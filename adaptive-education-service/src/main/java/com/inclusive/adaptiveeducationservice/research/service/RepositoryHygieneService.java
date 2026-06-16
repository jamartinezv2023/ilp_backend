package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.RepositoryHygieneResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepositoryHygieneService {

    public RepositoryHygieneResponse generateRepositoryHygienePreview() {

        return new RepositoryHygieneResponse(
                "REPOSITORY_HYGIENE_REQUIRED",
                "GITIGNORE_HARDENING_RECOMMENDED",
                "LOCAL_REPORTS_AND_OFFICE_TEMP_FILES_SHOULD_BE_EXCLUDED",
                "SECRET_AND_ENVIRONMENT_FILE_PROTECTION_REQUIRED",
                "ACTIVE_WITH_GIT_COMMITS",
                List.of(
                        "Git status exposes local thesis documents as untracked files",
                        "Office temporary files detected as untracked artifacts",
                        "Generated reports should be managed outside source control or under docs when intentional",
                        "OpenAPI artifacts can be versioned intentionally",
                        "Source code changes remain traceable through commits"
                ),
                "Harden .gitignore to exclude local Office temporary files, logs, environment files and non-source artifacts while versioning intentional documentation explicitly."
        );
    }
}
