package com.example.adminService.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO för att uppdatera användarinformation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDto {

    @NotBlank(message = "Förnamn får inte vara tomt")
    @Pattern(regexp = "^[A-Za-zÅÄÖåäö\\s]+$", message = "Förnamn får endast innehålla bokstäver")
    private String firstName;

    @NotBlank(message = "Efternamn får inte vara tomt")
    @Pattern(regexp = "^[A-Za-zÅÄÖåäö\\s]+$", message = "Efternamn får endast innehålla bokstäver")
    private String lastName;

    @NotBlank(message = "E-post får inte vara tom")
    @Email(message = "E-posten måste vara giltig")
    private String email;

    @Pattern(regexp = "^\\d{12}$|^$", message = "Personnummer måste vara 12 siffror (YYYYMMDDXXXX)")
    private String personalNumber;

    @Pattern(regexp = "^\\d{10}$|^$", message = "Mobilnummer måste vara exakt 10 siffror")
    private String phoneNumber;
}
