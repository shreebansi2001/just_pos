package com.crmportal.request.dto;


import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PurchaseOrderRequestDto {

    private Long id; // for update
    private Long supplierId;
    private String voucher;
    @NotBlank(message = "PO Date is mandatory")
    private String podate; // dd/MM/yyyy
    private String billno;
    private String invoicetype;
    private String remarks;

    private float subamount;
    private float discountper;
    private float discountval;
    private float adjustamount;
    private float finalamount;
    private Long stockTypeId;

    @NotNull(message = "User is mandatory")
    private Long userId;
    
    private Boolean priceUpdateMaster;
    
    private String grnNumber;
    
    private List<PurchaseOrderDetailRequestDto> details;
}