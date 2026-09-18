package com.crmportal.response.dto;

import lombok.Data;

@Data
public class PurchaseOrderDetailResponseDto {

    private Long id;

    private Long rawMaterialId;
    private String rawMaterialName;

    private Long rawMaterialCatId;
    private String rawMaterialCatName;

    private Long unitId;
    private String unitName;

    private String hsccode;

    private float cgst;
    private float sgst;
    private float igst;
    private float cess;

    private double qty;
    private float price;
    private float othercharge;
    private float total;

    private double returnedQty;
    private double remainingQty;
    private Boolean isAddInStock;
}
