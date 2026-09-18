package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleMenuPlanReportRequestDto {

	private Integer isCategorySlogan;
	private Integer isCategoryInstruction;
	private Integer isCategoryImage;
	private Integer isItemSlogan;
	private Integer isItemInstruction;
	private Integer isItemImage;
	private Integer isUserLogo;
	private Integer isUserDetails;
	private Integer isPartyDetails;
	private Long userId;
}
