package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CostingReportResponseDto {

	private Long eventId;
	private Long eventFunctionId;
	private String functionName;
	private BigDecimal functionPerson;
	private String functionStartTime;
	private String functionEndTime;
	private Long menuCategoryId;
	private String menuCategoryName;
	private Long menuItemId;
	private String menuItemName;
	private Long rawMaterialCatId;
	private String rawMaterialCatName;
	private Long rawMaterialItemId;
	private String rawMaterialItemName;
	private BigDecimal quantity;
	private String unitName;
	private BigDecimal rate;
	private BigDecimal totalprice;
	private BigDecimal perplateprice;
	private Integer totalRawMaterialItems;
	private String partyName;
	private String partyMobileNo;
	private BigDecimal itemPerson;
	private String venueName;
}
