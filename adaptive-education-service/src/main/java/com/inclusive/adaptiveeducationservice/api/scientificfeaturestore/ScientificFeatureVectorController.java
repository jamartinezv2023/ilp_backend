package com.inclusive.adaptiveeducationservice.api.scientificfeaturestore;

import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.FeatureSetVersion;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.domain.valueobject.ParticipantId;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.ScientificFeatureVectorQueryUseCase;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model.FindExactScientificFeatureVectorQuery;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.model.FindLatestScientificFeatureVectorQuery;
import com.inclusive.adaptiveeducationservice.featurestore.scientific.port.in.query.result.ScientificFeatureVectorResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/scientific-feature-store")
@Tag(
        name = "Scientific Feature Store",
        description = "Read operations for immutable scientific feature vectors."
)
public class ScientificFeatureVectorController {

    private final ScientificFeatureVectorQueryUseCase queryUseCase;

    public ScientificFeatureVectorController(
            ScientificFeatureVectorQueryUseCase queryUseCase
    ) {
        this.queryUseCase =
                Objects.requireNonNull(
                        queryUseCase,
                        "queryUseCase is required"
                );
    }

    @GetMapping("/exact")
    @Operation(
            operationId = "findExactScientificFeatureVector",
            summary = "Find an exact scientific feature vector",
            description = """
                    Returns the scientific feature vector matching the
                    participant, feature-set version and cutoff instant.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Exact scientific feature vector found.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation =
                                            ScientificFeatureVectorResult.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "A required parameter is missing or invalid.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation =
                                            ScientificFeatureApiErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Exact scientific feature vector not found.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation =
                                            ScientificFeatureApiErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected scientific feature error.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation =
                                            ScientificFeatureApiErrorResponse.class
                            )
                    )
            )
    })
    public ResponseEntity<ScientificFeatureVectorResult> findExact(
            @Parameter(
                    description = "Participant identifier.",
                    required = true,
                    example = "PARTICIPANT-REST-001"
            )
            @RequestParam
            String participantId,

            @Parameter(
                    description = "Scientific feature-set version.",
                    required = true,
                    example = "SCIENTIFIC-FEATURES-V1"
            )
            @RequestParam
            String featureSetVersion,

            @Parameter(
                    description = "ISO-8601 cutoff instant used for the exact query.",
                    required = true,
                    example = "2026-08-02T12:00:00Z"
            )
            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME
            )
            Instant featureCutoffAt
    ) {
        FindExactScientificFeatureVectorQuery query =
                new FindExactScientificFeatureVectorQuery(
                        new ParticipantId(participantId),
                        new FeatureSetVersion(
                                featureSetVersion
                        ),
                        featureCutoffAt
                );

        ScientificFeatureVectorResult result =
                queryUseCase.findExact(query)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Exact scientific feature vector not found"
                                )
                        );

        return ResponseEntity.ok(result);
    }

    @GetMapping(
            "/latest-completed/{participantId}"
    )
    @Operation(
            operationId = "findLatestCompletedScientificFeatureVector",
            summary = "Find the latest completed scientific feature vector",
            description = """
                    Returns the latest completed vector for a participant
                    and a scientific feature-set version.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Latest completed scientific feature vector found.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation =
                                            ScientificFeatureVectorResult.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "A path or query parameter is invalid.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation =
                                            ScientificFeatureApiErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Latest completed scientific feature vector not found.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation =
                                            ScientificFeatureApiErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected scientific feature error.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation =
                                            ScientificFeatureApiErrorResponse.class
                            )
                    )
            )
    })
    public ResponseEntity<ScientificFeatureVectorResult>
    findLatestCompleted(
            @Parameter(
                    description = "Participant identifier.",
                    required = true,
                    example = "PARTICIPANT-REST-001"
            )
            @PathVariable
            String participantId,

            @Parameter(
                    description = "Scientific feature-set version.",
                    required = true,
                    example = "SCIENTIFIC-FEATURES-V1"
            )
            @RequestParam
            String featureSetVersion
    ) {
        FindLatestScientificFeatureVectorQuery query =
                new FindLatestScientificFeatureVectorQuery(
                        new ParticipantId(participantId),
                        new FeatureSetVersion(
                                featureSetVersion
                        )
                );

        ScientificFeatureVectorResult result =
                queryUseCase.findLatestCompleted(query)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Latest completed scientific feature vector not found"
                                )
                        );

        return ResponseEntity.ok(result);
    }
}
