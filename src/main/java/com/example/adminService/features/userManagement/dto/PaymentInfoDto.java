package com.example.adminService.features.userManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO för betalningsinformation från PaymentService
 * Matchar Payment entity från PaymentService
 * 
 * CLEANAD VERSION - Dubbletter borttagna:
 * ❌ Borttaget: getTransactionId() (dubblett av id/paymentReference)
 * ❌ Borttaget: getPaymentMethod() (alltid "Swish", ingen mening)
 * 
 * FRONTEND KAN:
 * - Visa paymentReference om den finns (= lyckad betalning)
 * - Annars visa id (= pending/failed betalning)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfoDto {
    private String id;
    private Long userId;
    private Long packageId;
    private String payerAlias;
    private BigDecimal amount;
    private String status;
    private String paymentReference;
    private String callbackIdentifier;
    private LocalDateTime paidAt;
    private String errorCode;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
