package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetEventLaborResponseDto {

	private Long partyId;
	private String partyName;
	private String mobileNo;
	private Long eventId;
	private String eventNo;
	private Long eventFunctionId;
	private String functionVenue;
	private String functionStartDate;
	private String functionStartTime;
	private String functionName;
	private Long laborId;
	private String laborName;
	private String laborMobile;
	private String laborDateTime;
	private String laborDate;
	private String laborTime;
	private String laborShift;
	private String notes;
	private String venue;
	private Long contactCategoryId;
	private String contactCategoryName;
	private String contactCategoryNameHindi;
	private String contactCategoryNameGujarati;
	private BigDecimal price;
	private Integer qty;
	private BigDecimal totalPrice;
	private Long userId;
	private String logo;
	private String companyName;
	private String companyEmail;
	private String countryCode;
	private String companyMobile;
	private String cmpAddress;
	private LocalDateTime eventStartDateTime;
	private String functionNameHindi;
	private String functionNameGujarati;
	private String eventNameEnglish;
	private String eventNameHindi;
	private String eventNameGujarati;
	private BigDecimal shiftTransPrice;
	private String functionVenueHindi;
	private String functionVenueGujarati;
}
