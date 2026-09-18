package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemForDatewiseReportResponseDto {

	private String itemNameEnglish;
	private String itemNameHindi;
	private String itemNameGujarati;
	private String dateTime;
	private Integer rate;
	private Integer qty;
	private String remarks;
	private String totalPrice;
	private String serviceType;
	private Integer counterQty;
	private Integer counterPrice;
	private Integer helperQty;
	private Integer helperPrice;
	private Long unitId;
	private String unitNameEnglish;
	private String unitNameHindi;
	private String unitNameGujarati;
	private Integer personCount;
}
