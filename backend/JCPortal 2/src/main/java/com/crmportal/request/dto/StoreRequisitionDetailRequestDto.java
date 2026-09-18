package com.crmportal.request.dto;

import lombok.Data;

@Data
public class StoreRequisitionDetailRequestDto {
    private Long rawMaterialId;
    private double qty;
}