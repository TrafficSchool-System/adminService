package com.example.adminService.Dto.payment;

import java.math.BigDecimal;

/**
 * CreatePackageRequestDTO
 * Används för att skapa nya paket
 */
public class CreatePackageRequestDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer validityDays;

    public CreatePackageRequestDTO() {
    }

    public CreatePackageRequestDTO(String name, String description, BigDecimal price, Integer validityDays) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.validityDays = validityDays;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getValidityDays() {
        return validityDays;
    }

    public void setValidityDays(Integer validityDays) {
        this.validityDays = validityDays;
    }
}
