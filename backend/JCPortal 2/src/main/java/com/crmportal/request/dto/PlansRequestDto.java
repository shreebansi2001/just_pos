package com.crmportal.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
public class PlansRequestDto {

    @NotBlank(message = "Plan name is required")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotBlank(message = "Billing cycle is required (e.g. MONTHLY, YEARLY)")
    private String billingCycle;

    private String description;

    private Boolean isPopular;

    private List<PlanFeatureRequestDto> features;
    
    public String getName() {
        return name != null ? name.trim() : null;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getBillingCycle() {
        return billingCycle != null ? billingCycle.trim() : null;
    }

    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
    }
    
    
}
