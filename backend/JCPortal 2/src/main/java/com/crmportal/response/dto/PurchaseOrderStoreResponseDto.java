package com.crmportal.response.dto;


import java.util.List;

import lombok.Data;

@Data
public class PurchaseOrderStoreResponseDto {

    private Long id;
    private String pocode;

    private Long partyId;
    private String partyName;

    private String voucher;
    private String podate;
    private String invoicetype;

    private Long stockTypeId;
    private String stockTypeName;
    
    private String status;
    
    private Long   crId;
    private String crcode;

    private String remarks;
    private String createdAt;

    private Long userId;
    
    private Long kitchenTypeId;
    private String kitchenTypeName;
    
    private Long eventId;
    private String eventName;
    private String eventDate;
    
    private List<PurchaseOrderStoreDetailResponseDto> details;
}
