package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuQuantityReponseDto {
	
	private Long eventId;
	private String eventStartTime;
	private Long eventFunctionId;
	private String eventName;
	private String functionName;
	private String functionTime;
	private Integer person;
	private String remarks;
	
	private Long menuItemId;
	private String itemName;
	private Long rawMaterialId;
	private String rawMaterialName;
	private Double qty;
	private Double rate;
	private Long unitId;
	private String unitSymbol;
	private Long rawMaterialCatId;
	private String rawMaterialCatName;
	private Boolean inside;
	private Boolean outside;
	private Boolean chefLabour;
	
	private Long partyId;
	private String partyName;
	private String partyMobile;
	private String eventDate;
	private Long venueId;
	private String venueName;
	private String eventNo;
	
	private String companyName;
	private String countryCode;
	private String officeNo;
	private String companyEmail;
	private String logo;
	private String companyAddress;
	private String instructions;
	
	private String fnVenue;
}
