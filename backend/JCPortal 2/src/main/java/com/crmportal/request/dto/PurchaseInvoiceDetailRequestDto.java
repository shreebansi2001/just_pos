package com.crmportal.request.dto;

import lombok.Data;

@Data
public class PurchaseInvoiceDetailRequestDto {

    private Long sotPoDetailId;    // to identify which item
    private Long rawMaterialId;
    private Long unitId;
    private String hsccode;
    private float cgst;
    private float sgst;
    private float igst;
    private float cess;
    private double qty;
    private float price;
    private float othercharge;
    private float total;
    private Boolean isAddInStock;
}