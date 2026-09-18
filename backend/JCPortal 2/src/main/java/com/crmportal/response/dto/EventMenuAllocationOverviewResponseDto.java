package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventMenuAllocationOverviewResponseDto {

	private Long vendorId;

	private String vendorNameEnglish;
	private String vendorNameHindi;
	private String vendorNameGujarati;

	private Long eventId;
	private Long eventFunctionId;

	private String eventNameEnglish;
	private String functionNameEnglish;

	private String eventNameHindi;
	private String functionNameHindi;

	private String eventNameGujarati;
	private String functionNameGujarati;

	private Integer functionPax;

	private String venue;

	private String reportingTime;
	private String arrivalTime;
	private String remarks;
	private String status;
	private Boolean isPresent;

	private String latitude;
	private String longitude;

	private List<EventMenuAllocationItemOverviewResponseDto> items;

	private List<EventFunctionStaffImagesResponseDto> vendorImages;

	private List<EventFunctionStaffTaskResponseDto> vendorTasks;
}