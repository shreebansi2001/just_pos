package com.crmportal.response.dto;

import lombok.Data;

@Data
public class SotDetailResponseDto {
    private Long id;
    private Long rawMaterialId;
    private String rawMaterialName;
    private Long rawMaterialCatId;
    private String rawMaterialCatName;
    private Long unitId;
    private String unitName;
    private Long partyId;
    private String partyName;
    private double qty;
    private double acceptedQty;
    private double returnQty;
    private double availableStock;
    private Long availableStockUnitId;
    private String availableStockUnitName;
}