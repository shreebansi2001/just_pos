package com.crmportal.response.dto;

import lombok.Data;

@Data
public class SotManualPoDetailResponseDto {
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
    private String hsccode;
    private float cgst;
    private float sgst;
    private float igst;
    private float price;
    private float othercharge;
    private float total;
}