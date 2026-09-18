package com.crmportal.request.dto;

import lombok.Data;

@Data
public class SotManualPoDetailRequestDto {
    private Long id;
    private Long rawMaterialId;
    private Long rawMaterialCatId;
    private Long unitId;
    private Long partyId;
    private String hsccode;
    private float cgst;
    private float sgst;
    private float igst;
    private double qty;
    private float price;
    private float othercharge;
    private float total;
}