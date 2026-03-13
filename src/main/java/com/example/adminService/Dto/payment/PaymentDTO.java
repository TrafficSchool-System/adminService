package com.example.adminService.Dto.payment;

import com.example.adminService.Enum.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Entity DTO
 * Representerar payment data från payment-service
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {

    private String id;
    private Long userId;
    private Long packageId;
    private String payerAlias;
    private BigDecimal amount;
    private PaymentStatus status;
    private String paymentReference;
    private String callbackIdentifier;
    private LocalDateTime paidAt;
    private String errorCode;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
