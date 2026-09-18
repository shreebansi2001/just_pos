package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class RawMaterialOPBRequestDto {
    private List<RawMaterialOPBItemRequestDto> items;
}