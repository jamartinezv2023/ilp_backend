package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.DockerfileHardeningResponse;
import com.inclusive.adaptiveeducationservice.research.service.DockerfileHardeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/deployment")
@RequiredArgsConstructor
public class DockerfileHardeningController {

    private final DockerfileHardeningService dockerfileHardeningService;

    @GetMapping("/dockerfile-preview")
    public ResponseEntity<DockerfileHardeningResponse>
    dockerfilePreview() {

        return ResponseEntity.ok(
                dockerfileHardeningService.generateDockerfilePreview()
        );
    }
}
