package com.crmportal.response.dto;

import java.util.List;
import lombok.Data;

@Data
public class SotManualPoResponseDto {
    private Long id;
    private String voucherNo;
    private Long sotId;
    private String sotNo;
    private Long partyId;
    private String partyName;
    private Long eventId;
    private String eventName;
    private String status;
    private String createdAt;
    private String voucherDate;
    private String billno;
    private String invoicetype;
    private String remarks;
    private float subamount;
    private float discountper;
    private float discountval;
    private float adjustamount;
    private float finalamount;
    private List<SotManualPoDetailResponseDto> details;
}