package com.crmportal.request.dto;


import lombok.Data;

@Data
public class PurchaseOrderStoreDetailRequestDto {

    private Long rawMaterialId;
    private double qty;
    private Boolean isAddInStock;
}
