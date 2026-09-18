package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NamePlateResponseDto {

	private Long eventId;
	private String eventNo;
	private Long eventFunctionId;
	private Long menuPreparationId;
	private Long menuItemId;
	private String menuItemName;
	
	private Long categoryId;
	private String categoryName;
	
	private String cmpName;
	private String countryCode;
	private String officeNo;
	private String cmpEmail;
	private String cmpAddress;
	private String cmpLogo;
	
	private String clientName;
	private String clientAdderss;
	private String clientNo;
	
	private String ownerFirstname;
	private String ownerLastname;
	private String ownerMobileNo;
}
