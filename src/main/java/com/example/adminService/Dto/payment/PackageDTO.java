package com.example.adminService.Dto.payment;

import lombok.*;

import java.math.BigDecimal;

/**
 * Package Entity DTO
 * Representerar package data från payment-service
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageDTO {

    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private Integer validityDays;
    private Integer validityHours;
}
