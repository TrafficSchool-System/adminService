package com.example.adminService.Enum;

/**
 * PaymentStatus Enum
 * Måste vara identisk med PaymentStatus i payment-service
 */
public enum PaymentStatus {
    CREATED, // När vi precis skapat en betalning lokalt
    PENDING, // Skickat till Swish, väntar på svar
    PAID, // Betalning genomförd
    DECLINED, // Användaren nekade
    ERROR, // Något gick fel
    CANCELLED // Avbruten
}
