package com.example.adminService.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO för betalningsinformation från PaymentService
 * Matchar Payment entity från PaymentService
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfoDto {
    private String id; // instructionUUID from Swish
    private Long userId;
    private Long packageId;
    private String payerAlias; // Format: 46712345678
    private BigDecimal amount;
    private String status; // PaymentStatus enum: CREATED, PENDING, PAID, DECLINED, ERROR, CANCELLED
    private String paymentReference; // From Swish when PAID
    private String callbackIdentifier;
    private LocalDateTime paidAt;
    private String errorCode;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed getter for frontend compatibility
    public String getPaymentMethod() {
        return "Swish"; // All payments in this system use Swish
    }

    // Computed getter for frontend compatibility
    public String getTransactionId() {
        return paymentReference != null ? paymentReference : id;
    }
}
