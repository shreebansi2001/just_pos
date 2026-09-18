package com.crmportal.response.dto;

import java.util.List;
import lombok.Data;

@Data
public class StoreRequisitionResponseDto {

    private Long id;
    private String crcode;
    private Long partyId;
    private String partyName;
    private String partyNameHindi;
    private String partyNameGujarati;
    private String voucher;
    private String crdate;
    private String invoicetype;
    private Long stockTypeId;
    private String stockTypeName;
    private String stockTypeNameHindi;
    private String stockTypeNameGujarati;
    private String remarks;
    private String status;
    private String createdAt;
    private Long userId;
    private List<StoreRequisitionDetailResponseDto> details;
}