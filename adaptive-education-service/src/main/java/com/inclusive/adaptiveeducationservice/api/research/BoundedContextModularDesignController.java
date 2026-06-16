package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.BoundedContextModularDesignResponse;
import com.inclusive.adaptiveeducationservice.research.service.BoundedContextModularDesignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/architecture")
@RequiredArgsConstructor
public class BoundedContextModularDesignController {

    private final BoundedContextModularDesignService boundedContextModularDesignService;

    @GetMapping("/bounded-context-preview")
    public ResponseEntity<BoundedContextModularDesignResponse>
    boundedContextPreview() {

        return ResponseEntity.ok(
                boundedContextModularDesignService.generateBoundedContextPreview()
        );
    }
}
