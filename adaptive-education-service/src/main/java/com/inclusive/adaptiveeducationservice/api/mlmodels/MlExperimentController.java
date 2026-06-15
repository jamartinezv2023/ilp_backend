package com.inclusive.adaptiveeducationservice.api.mlmodels;

import com.inclusive.adaptiveeducationservice.mlmodels.dto.MlExperimentResponse;
import com.inclusive.adaptiveeducationservice.mlmodels.service.MlExperimentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ml/experiments")
public class MlExperimentController {

    private final MlExperimentQueryService queryService;

    @GetMapping
    public ResponseEntity<List<MlExperimentResponse>>
    findAll() {

        return ResponseEntity.ok(
                queryService.findAll()
        );
    }

    @GetMapping("/algorithm/{algorithm}")
    public ResponseEntity<List<MlExperimentResponse>>
    findByAlgorithm(
            @PathVariable String algorithm
    ) {

        return ResponseEntity.ok(
                queryService.findByAlgorithm(
                        algorithm
                )
        );
    }
}
