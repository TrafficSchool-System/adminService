package com.example.adminService.features.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginResponse {

    private String token;
    private Long adminId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;

}
