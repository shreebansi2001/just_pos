package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequestDetailsResponseDto {

    private Long id;

    private Long rawMaterialId;
    private String rawMaterialName;

    private Long requestQtyUnitId;
    private String requestQtyUnitName;

    private BigDecimal requestQty;

    private BigDecimal avgDailyCons;
    private BigDecimal leadTime;
    private BigDecimal minQty;
    private BigDecimal maxQty;
    private BigDecimal todaysStock;
    private BigDecimal sysReqQty;
    private String sysUnit;
    
    private BigDecimal approvedQty;
    
    private Long approvedQtyUnitId;
    private String approvedQtyUnitName;
    
    private UnitHierarchyDto requestUnitHierarchy;
    
    private UnitHierarchyDto approvedUnitHierarchy;
    
    private UnitHierarchyDto sysUnitHierarchy;
}