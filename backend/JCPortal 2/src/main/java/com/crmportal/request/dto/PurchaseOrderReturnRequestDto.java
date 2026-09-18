package com.crmportal.request.dto;

import java.util.List;
import lombok.Data;

@Data
public class PurchaseOrderReturnRequestDto {

    private Long id;           
    private Long poId;         
    private Long supplierId;
    private String voucher;
    private String returndate; 
    private String billno;
    private String invoicetype;
    private String remarks;
    private float subamount;
    private float discountper;
    private float discountval;
    private float adjustamount;
    private float finalamount;
    private Long stockTypeId;
    private Long userId;

    private List<PurchaseOrderReturnDetailRequestDto> details;
}