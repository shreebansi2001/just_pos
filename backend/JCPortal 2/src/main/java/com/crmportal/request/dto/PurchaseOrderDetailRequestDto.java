package com.crmportal.request.dto;

import lombok.Data;

@Data
public class PurchaseOrderDetailRequestDto {

    private Long rawMaterialId;
    private String hsccode;

    private float cgst;
    private float sgst;
    private float igst;
    private float cess;

    private double qty;
    private float price;
    private float othercharge;
    private float total;
    private float oldPrice;
    private Boolean isAddInStock;
}