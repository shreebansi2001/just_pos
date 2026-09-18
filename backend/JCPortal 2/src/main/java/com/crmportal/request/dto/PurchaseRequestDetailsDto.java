package com.crmportal.request.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequestDetailsDto {

    private Long id;

    @NotNull(message = "Raw material ID is mandatory")
    private Long rawMaterialId;

    @NotNull(message = "Request Quantity is mandatory")
    @Positive(message = "Request Quantity must be greater than zero")
    private BigDecimal requestQty; 

    @NotNull(message = "Request Qty Unit ID is mandatory")
    private Long requestQtyUnitId;
    
    private BigDecimal approvedQty = BigDecimal.ZERO; 

    private Long approvedQtyUnitId;
    
}