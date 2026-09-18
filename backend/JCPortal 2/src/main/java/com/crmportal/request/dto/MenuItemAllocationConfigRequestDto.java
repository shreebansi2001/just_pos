package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemAllocationConfigRequestDto {

	private Long id;
	private String godownLocation;
	private String remarks;
	private Boolean selectOutsideAgency = Boolean.FALSE;
	private Boolean selectInsideAgency = Boolean.FALSE;
	private Boolean selectChefLabourAgency = Boolean.FALSE;
	private Long partyId;
	private ChefLabourItemRequestDto chefLabourItem;
	private OutsideItemRequestDto outsideItem;
	private InsideItemRequestDto insideItem;

}
