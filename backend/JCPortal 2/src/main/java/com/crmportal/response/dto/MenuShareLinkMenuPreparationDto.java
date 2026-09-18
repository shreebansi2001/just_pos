package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuShareLinkMenuPreparationDto {
	private Long id;
	private Integer pax;
	private Integer sortorder;
	private BigDecimal price;
	private BigDecimal defaultPrice;
	private Long packageId;
	private String packageName;
	private BigDecimal packagePrice;
	private Boolean isPackage;
	private EventFunctionMenuPreparationResponseDto eventFunction;
}
