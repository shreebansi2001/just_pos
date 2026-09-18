package com.crmportal.request.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PurchaseOrderStoreRequestDto {

    private Long id; // null or -1 for add

    private Long partyId;

    private Long eventId;
    
    private String voucher;

    private String podate; // dd/MM/yyyy

    private String invoicetype;

    private String status;  // PENDING / COMPLETED
    
    private Long crId;
    private String crcode;
    
    private Long stockTypeId;

    private String remarks;

    private Long userId;
    
    private Long kitchenTypeId;

    private List<PurchaseOrderStoreDetailRequestDto> details;
}