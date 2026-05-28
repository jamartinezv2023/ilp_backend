package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.RepositoryHygieneResponse;
import com.inclusive.adaptiveeducationservice.research.service.RepositoryHygieneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/security")
@RequiredArgsConstructor
public class RepositoryHygieneController {

    private final RepositoryHygieneService repositoryHygieneService;

    @GetMapping("/repository-hygiene-preview")
    public ResponseEntity<RepositoryHygieneResponse>
    repositoryHygienePreview() {

        return ResponseEntity.ok(
                repositoryHygieneService.generateRepositoryHygienePreview()
        );
    }
}
