package com.example.adminService.Dto;

import com.example.adminService.Enum.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO för grundläggande användarinformation från UserService
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicInfoDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String personalNumber;
    private String phoneNumber;
    private LocalDateTime createdAt;

    @JsonProperty("active")
    private Boolean isActive;

    private UserRole role;
}
