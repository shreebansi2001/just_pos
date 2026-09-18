package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreIssueCostingReportResponseDto {

	private Long eventId;
	private String eventNo;
	private String eventName;
	private String eventDate;
	private Long partyId;
	private String partyName;
	private String partyContactNo;
	private String partyEmail;
	private String partyAddress;
	private String eventVenue;
	
	private Long userId;
	private String cmpName;
	private String logo;
	private String cmpContactNo;
	private String cmpEmail;
	private String cmpAddress;

	private List<StoreIssueDetailsResponseDto> categories;
}
