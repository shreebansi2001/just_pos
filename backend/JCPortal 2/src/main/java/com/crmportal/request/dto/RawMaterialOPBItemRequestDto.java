package com.crmportal.request.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class RawMaterialOPBItemRequestDto {
    private Long rawMaterialId;
    private BigDecimal opbStock;
    private BigDecimal minStock;
    private String expiryDate;   
    private BigDecimal supplierRate;
}