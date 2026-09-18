package com.crmportal.request.dto;

import lombok.Data;

@Data
public class EventFunctionFilterRequestDto {

    private Long userId;

    private Long functionId;

    private Integer maxFunctionsCount;

    private Boolean isPriceFilterApplied = false;

    private Integer fromPrice = 0;

    private Integer toPrice = 0;
}
