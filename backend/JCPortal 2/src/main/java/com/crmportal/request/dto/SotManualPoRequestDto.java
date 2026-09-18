package com.crmportal.request.dto;

import java.util.List;
import lombok.Data;

@Data
public class SotManualPoRequestDto {
    private Long id;
    private Long partyId;      
    private Long userId;
    private String status;
    private String voucherDate;
    private String billno;
    private String invoicetype;
    private String remarks;
    private float subamount;
    private float discountper;
    private float discountval;
    private float adjustamount;
    private float finalamount;
    private Boolean isPurchaseApprove;
    private Long purchaseApproveRequestId;
    private List<SotManualPoDetailRequestDto> details;
}