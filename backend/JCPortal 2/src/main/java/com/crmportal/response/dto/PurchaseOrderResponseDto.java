package com.crmportal.response.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class PurchaseOrderResponseDto {

    private Long id;

    private Long supplierId;
    private String supplierName;

    private String voucher;
    private LocalDate podate;
    private String billno;
    private String invoicetype;
    private String remarks;

    private float subamount;
    private float discountper;
    private float discountval;
    private float adjustamount;
    private float finalamount;
    
    private Long   stockTypeId;
    private String stockTypeName;

    private int potype;

    private LocalDateTime createdAt;
    
    private String pocode;

    private Boolean priceUpdateMaster; 
    
    private String grnNumber;
    
    private List<PurchaseOrderDetailResponseDto> details;
}