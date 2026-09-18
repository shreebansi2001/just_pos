package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ChefRequisitionDetailResponseDto {

    private Long id;
    private Long rawMaterialId;
    private String rawMaterialName;
    private String rawMaterialNameHindi;
    private String rawMaterialNameGujarati;
    private Long rawMaterialCatId;
    private String rawMaterialCatName;
    private String rawMaterialCatNameHindi;
    private String rawMaterialCatNameGujarati;
    private Long unitId;
    private String unitName;
    private String unitNameHindi;
    private String unitNameGujarati;
    private BigDecimal price;
    private double qty;
}