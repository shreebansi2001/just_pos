package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NameplateReportDataResponseDto {
	private Long id;
	private Long menuCategoryId;
	private String categoryName;
	private String categoryNamePref;
	private Long menuItemId;
	private String itemName;
	private String itemNamePref;
	private BigDecimal itemCount;
	private BigDecimal itemFontSize;
	private BigDecimal categoryFontSize;
	private Long eventId;
	private Long eventFunctionId;
	private BigDecimal sequence;
}
