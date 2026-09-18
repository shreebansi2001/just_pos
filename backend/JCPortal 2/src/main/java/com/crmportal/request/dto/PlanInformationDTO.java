package com.crmportal.request.dto;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanInformationDTO {
	@NotBlank(message = "Plan name is required")
    private String planName;

    @NotBlank(message = "Plan start date is required")
    private String planStartDate;

    @NotBlank(message = "Plan end date is required")
    private String planEndDate;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    private BigDecimal amount;
}