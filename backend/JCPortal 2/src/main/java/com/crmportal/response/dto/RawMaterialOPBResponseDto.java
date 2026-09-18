package com.crmportal.response.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class RawMaterialOPBResponseDto {
    private Long rawMaterialId;
    private String rawMaterialName;
    private Long categoryId;
    private String categoryName;
    private String unitName;
    private BigDecimal opbStock;
    private BigDecimal minStock;
    private String expiryDate;
    private String createdAt;       
    private BigDecimal supplierRate;
}