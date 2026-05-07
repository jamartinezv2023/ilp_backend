package com.inclusive.adaptiveeducationservice.api.v1;

import com.inclusive.adaptiveeducationservice.application.dto.StudentCharacterizationResponse;
import com.inclusive.adaptiveeducationservice.application.ports.input.CharacterizeStudentUseCase;
import com.inclusive.adaptiveeducationservice.domain.model.StudentCharacterization;
import com.inclusive.adaptiveeducationservice.mapper.StudentCharacterizationMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/characterizations")
public class StudentCharacterizationController {

    private final CharacterizeStudentUseCase characterizeStudentUseCase;

    private final StudentCharacterizationMapper mapper;

    public StudentCharacterizationController(
            CharacterizeStudentUseCase characterizeStudentUseCase,
            StudentCharacterizationMapper mapper
    ) {
        this.characterizeStudentUseCase = characterizeStudentUseCase;
        this.mapper = mapper;
    }

    @GetMapping("/{studentId}")
    public StudentCharacterizationResponse characterizeStudent(
            @PathVariable Long studentId
    ) {

        StudentCharacterization characterization =
                characterizeStudentUseCase
                        .characterizeStudent(studentId);

        return mapper.toResponse(characterization);
    }
}