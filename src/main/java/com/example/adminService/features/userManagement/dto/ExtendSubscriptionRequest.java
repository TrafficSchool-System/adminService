package com.example.adminService.features.userManagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ExtendSubscriptionRequest {

    @NotNull
    @Min(1)
    private Integer days;

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    } 

    

    

}
