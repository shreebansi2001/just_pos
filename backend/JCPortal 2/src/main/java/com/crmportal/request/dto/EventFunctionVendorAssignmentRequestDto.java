package com.crmportal.request.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionVendorAssignmentRequestDto {

	private Long id;

	private Long eventId;

	private Long eventFunctionId;

	private Long vendorId;

	private String reportingTime;

	private String arrivalTime;

	private String remarks;

	private String status;

	private String resourceType;

	private Long userId;

	private Long managerId;

	private Boolean isPresent;

	private String latitude;

	private String longitude;

	private Long menuItemId;

	private String instructions;

	private String instructionsHindi;

	private String instructionsGujarati;

	private EventMenuAllocationChefLabourOverviewRequestDto chefLabour;

	private EventMenuAllocationOutSideOverviewRequestDto outside;

	private EventLabourOverviewRequestDto labour;

	private List<EventFunctionStaffImagesRequestDto> vendorImages;

	private List<EventFunctionStaffTaskRequestDto> vendorTasks;
}