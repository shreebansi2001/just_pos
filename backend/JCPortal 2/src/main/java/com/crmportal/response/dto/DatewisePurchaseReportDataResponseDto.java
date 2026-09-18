package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatewisePurchaseReportDataResponseDto {

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
}
