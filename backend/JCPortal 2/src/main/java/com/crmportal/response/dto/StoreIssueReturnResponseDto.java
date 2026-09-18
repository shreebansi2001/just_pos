package com.crmportal.response.dto;

import java.util.List;
import lombok.Data;

@Data
public class StoreIssueReturnResponseDto {

    private Long id;
    private String sircode;

    // original store issue reference
    private Long storeIssueId;
    private String storeIssuePocode;

    private Long partyId;
    private String partyName;

    private String voucher;
    private String returndate;
    private String invoicetype;

    private Long stockTypeId;
    private String stockTypeName;

    private String remarks;
    private String createdAt;
    private Long userId;
    
    private Long kitchenTypeId;
    private String kitchenTypeName;

    private Long eventId;
    private String eventName;
    private String eventDate;
    
    private List<StoreIssueReturnDetailResponseDto> details;
}