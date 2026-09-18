package com.crmportal.request.dto;

import lombok.Data;

@Data
public class EventRawMaterialDisposableItemDto {
    private Long rawMaterialId;
    private Long unitId;
    private Double totalRate;
    private Double qty;
}