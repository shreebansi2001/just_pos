package com.crmportal.request.dto;

import lombok.Data;

@Data
public class PurchaseOrderReturnDetailRequestDto {

    private Long rawMaterialId;
    private String hsccode;
    private float cgst;
    private float sgst;
    private float igst;
    private double qty;
    private float price;
    private float othercharge;
    private float total;
}