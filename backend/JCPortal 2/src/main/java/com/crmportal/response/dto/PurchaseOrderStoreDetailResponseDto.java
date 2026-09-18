package com.crmportal.response.dto;


import lombok.Data;

@Data
public class PurchaseOrderStoreDetailResponseDto {

    private Long id;

    private Long rawMaterialId;
    private String rawMaterialName;

    private Long rawMaterialCatId;
    private String rawMaterialCatName;

    private Long unitId;
    private String unitName;

    private double qty;
    private double returnedQty;  
    private double remainingQty;
    
    private Double closingStock;
    private Boolean isAddInStock; 
}