package com.crmportal.request.dto;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StoreRequisitionRequestDto {

    private Long id;

    private Long partyId;

    private String voucher;

    @NotNull
    private String crdate;   // dd/MM/yyyy

    private String invoicetype;

    private Long stockTypeId;

    private String remarks;

    @NotNull
    private Long userId;

    private List<StoreRequisitionDetailRequestDto> details;
}