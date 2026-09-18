package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemAgencyAlloResponseDto {

	private Long partyId;
    private String partyName;
    private String partyNameHindi;
    private String partyNameGujarati;
    private String agencyType;
    private BigDecimal quantity;
    private BigDecimal price;
    private Long unitId;
    private String unitName;
}
