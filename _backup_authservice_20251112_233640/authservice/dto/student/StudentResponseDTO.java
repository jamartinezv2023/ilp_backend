package com.inclusive.authservice.dto.student;

import lombok.*;

import java.io.Serializable;

/**
 * DTO for returning student data to clients.
 * Used for outbound data (responses).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponseDTO implements Serializable {

    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String grade;
    private String institution;

    private StudentEnvironmentDTO environment;
    private StudentSupportNeedsDTO supportNeeds;
}





