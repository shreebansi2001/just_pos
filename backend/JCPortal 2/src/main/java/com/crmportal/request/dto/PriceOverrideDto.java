package com.crmportal.request.dto;

import lombok.Data;

@Data
public class PriceOverrideDto {
    private Long rawMaterialId;
    private Double price;
}