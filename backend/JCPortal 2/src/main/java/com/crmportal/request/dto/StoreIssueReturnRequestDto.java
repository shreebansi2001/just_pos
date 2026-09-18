package com.crmportal.request.dto;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StoreIssueReturnRequestDto {

    private Long id;             // null or 0 = new, else update

    private Long storeIssueId;   // original Store PO id

    private Long partyId;

    private String voucher;

    private String returndate;   // "dd/MM/yyyy"

    private String invoicetype;

    private Long stockTypeId;

    private String remarks;
    
    private Long kitchenTypeId;

    private Long userId;
    
    private Long eventId;

    private List<StoreIssueReturnDetailRequestDto> details;
}