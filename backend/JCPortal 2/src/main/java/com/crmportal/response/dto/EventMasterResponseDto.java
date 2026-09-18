package com.crmportal.response.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventMasterResponseDto {

	private Long id;
	private String inquiryDate;
	private String eventStartDateTime;
	private String eventEndDateTime;
	private Integer status;
	private Boolean isRMenu;
	private String address;
	private String mobileno;
	private String isHighPriority;
	private String reference;
	private String meal_notes;
	private String meal_notes_hindi;
	private String meal_notes_gujarati;
	private String service;
	private String serviceHindi;
	private String serviceGujarati;
	private String theme;
	private String themeHindi;
	private String themeGujarati;
	private String remark;
	private String remarksHindi;
	private String remarksGujarati;
	private String groomName;
	private String groomInstaLink;
	private String groomBirthDate;
	private String groom_community;
	private String groomMobileno;
	private String brideName;
	private String brideInstaLink;
	private String brideBirthDate;
	private String bride_community;
	private String brideMobileno;
	private String prefix;
	private String createdAt;
	private String eventNo;
	private Long banquetHallId;
	private String banquetHallName; // "ODC" if no hall selected, else hall name
	private Long shiftId;
	private String shiftName;
	private String shiftStartTime;
	private String shiftEndTime;
	private String bookingDate;
	private Long userId;
	private String companyName;
	private String companyMobileNo;
	private Long managerId;
	private String managerName;
	private String managerMobileNo;
	private Boolean isDelete;
	private String partyName;
	private String partyMobileNo;
	private PartyMasterResponseDto party;
	private MealTypeMasterResponseDto mealType;
	private EventTypeMasterResponseDto eventType;
	private List<EventFunctionMasterResponseDto> eventFunctions;
	private VenueMasterResponseDto venue;
	private String billingNameEnglish;
	private String billingNameHindi;
	private String billingNameGujarati;
	private Long childuserid = 0l;
	private Boolean isExtraFunction;
	private String menuPreparationStatus;
	private String cordinatorPersonNameEnglish;
	private String cordinatorPersonNameHindi;
	private String cordinatorPersonNameGujarati;
	private String cordinatorPersonContactNo;
	private List<EventRoomMasterResponseDto> eventRooms;
	private String permissable_item;
	private String not_permissable_item;
	private String rate_discussion;
	private String internal_staff_discussion;
	private Boolean isEventStatusChanged;
}
