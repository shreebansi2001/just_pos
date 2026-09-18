package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.crmportal.enums.AllocationType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventMenuAllocationItemOverviewResponseDto {

	private Long id;

	private Long menuItemId;

	private String menuItemNameEnglish;
	private String menuItemNameHindi;
	private String menuItemNameGujarati;

	private String instructions;
	private String instructionsHindi;
	private String instructionsGujarati;

	private AllocationType resourceType;

	private EventMenuAllocationChefLabourOverviewResponseDto chefLabour;

	private EventMenuAllocationOutSideOverviewResponseDto outside;

	private EventLabourOverviewResponse labour;
}