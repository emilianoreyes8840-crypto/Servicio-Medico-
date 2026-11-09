package com.hospital.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    private Long id;
    private String authUsername;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String professionalLicense;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
