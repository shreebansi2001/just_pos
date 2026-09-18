package com.crmportal.response.dto;

import java.math.BigDecimal;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionQuotationItemsResponseDto {

	private Long id;
	private String functionName;
	private Long eventFunctionId;
	private String functionDate;
	private Integer pax;
	private Integer extraPax;
	private BigDecimal ratePerPlate;
	private BigDecimal amount;
	private Boolean isEventFunction;
	private Long defaultFunctionId;	
	private Boolean isAddons;	
	private Long menuCatId;	
	private Long itemId;	
	private BigDecimal extraTax;
	private BigDecimal taxRate;
	private String options;
	private Long   customPackageId;
	private String customPackageName;
	private BigDecimal customPackagePrice;
	private Boolean isLocked;
	private BigDecimal offeredRate;
	private Long extraChargesId;
	private Boolean isExtraCharges;
}
