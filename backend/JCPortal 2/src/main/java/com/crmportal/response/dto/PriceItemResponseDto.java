package com.crmportal.response.dto;

import lombok.Data;

@Data
public class PriceItemResponseDto {
    private Long rawMaterialId;
    private String rawMaterialName;
    private Double masterPrice;
    private Double lastPrice;
    private Double avgPrice;
    private Double qty;
    private String unitName;
    private Long unitId;
}