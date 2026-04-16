package com.example.adminService.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for admin creating user + subscription
 * 
 * Combines user creation and package assignment in one operation.
 * Used by admin panel to create users with active subscription.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserWithSubscriptionDTO {

    // User information
    @NotBlank(message = "Förnamn får inte vara tom")
    @Pattern(regexp = "^[A-Za-zÅÄÖåäö\\s]+$", message = "Förnamn får endast innehålla bokstäver")
    private String firstName;

    @NotBlank(message = "Efternamn får inte vara tom")
    @Pattern(regexp = "^[A-Za-zÅÄÖåäö\\s]+$", message = "Efternamn får endast innehålla bokstäver")
    private String lastName;

    @NotBlank(message = "E-posten får inte vara tom")
    @Email(message = "E-posten måste vara giltig")
    private String email;

    @NotBlank(message = "Personnummer får inte vara tomt")
    @Pattern(regexp = "^\\d{12}$", message = "Personnummer måste vara 12 siffror (YYYYMMDDXXXX)")
    private String personalNumber;

    @NotBlank(message = "Mobilnummer får inte vara tomt")
    @Pattern(regexp = "^\\d{10}$", message = "Mobilnummer måste vara exakt 10 siffror")
    private String phoneNumber;

    // Package assignment
    @NotNull(message = "Package ID is required")
    private Long packageId;
}
