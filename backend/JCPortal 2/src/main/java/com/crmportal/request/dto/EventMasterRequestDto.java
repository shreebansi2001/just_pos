package com.crmportal.request.dto;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventMasterRequestDto {

	@NotBlank(message = "Inquiry Date is required")
	@JsonFormat(pattern = "dd/MM/yyyy", timezone = "UTC")
	private String inquiryDate;

	@NotBlank(message = "Event Start Date & Time is required")
	private String eventStartDateTime;

	@NotBlank(message = "Event End Date & Time is required")
	private String eventEndDateTime;

	private Long venueId;

	@NotNull(message = "Status is required")
	private Integer status;

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
	
	private Long banquetHallId;
	
	private Long   shiftId;  

	private String bookingDate; 

	private Long userId;

	@NotNull(message = "Manager Id is required")
	private Long managerId;

	@NotNull(message = "Party Id is required")
	private Long partyId;
	
	private String billingNameEnglish;
	
	private String billingNameHindi;
	
	private String billingNameGujarati;

	private Long mealTypeId;

	private Long eventTypeId;
	
	private String cordinatorPersonNameEnglish;
	
	private String cordinatorPersonNameHindi;
	
	private String cordinatorPersonNameGujarati;
	
	private String cordinatorPersonContactNo;

	private List<@Valid EventFunctionMasterRequestDto> eventFunction;
	
	private List<EventRoomMasterRequestDto> eventRooms;
	
	private String permissable_item;
	private String not_permissable_item;
	
	private String rate_discussion;
	private String internal_staff_discussion;
}
