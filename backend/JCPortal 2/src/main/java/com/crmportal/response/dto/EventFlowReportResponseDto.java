package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFlowReportResponseDto {
	private Long eventId;
	private String eventNo;
	private String eventName;
	private String venue;
	private String eventStartTimestamp;
	private String eventEndTimestamp;
	private String foodNotes;
	private String foodNotesHindi;
	private String foodNotesGujarati;
	private String foodType;
	private String partyName;
	private String mobileNo;
	private String remark;
	private String cmpName;
	private String cmpPhone;
	private String cmpAddress;
	private String pax;
	private String userFirstName;
	private String userLastName;
	private String countrycode;
	private String email;
	private String logo;
	private String billingNameEnglish;
	private String billingNameHindi;
	private String billingNameGujarati;
	private String serviceHindi;
	private String serviceGujarati;
	private String themeHindi;
	private String themeGujarati;
	private String service;
	private String theme;
	private Long banquetHallId;
	private String banquetHallName;
	private String eventTime;

	private Long eventManagerId;
	private String eventManagerName;

	private String cordinationPersonName;
	private String cordinationPersonContactno;

	private String partyAddress;

	private String permissable_item;

	private String not_permissable_item;

	private String reference;

	private String venue_img;

	private String prefix;

	private List<EventFunctionFlowResponseDto> functionFlow;
}
