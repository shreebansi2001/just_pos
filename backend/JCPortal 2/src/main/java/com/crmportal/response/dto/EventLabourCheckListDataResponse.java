package com.crmportal.response.dto;

import java.util.List;

import com.crmportal.request.dto.EventLabourCheckListImagesRequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventLabourCheckListDataResponse {

	private Long id;
	private Long eventId;
	private Long eventFunctionId;
	private Long contactCatId;
	private String contactCatName;
	private Long VendorId;
	private String vendorName;
	private Long shiftId;
	private String shiftName;
	private Integer totalQty;
	private Integer inQty;
	private Boolean isStatus;
	private String inTime;
	private String outTime;
	private String labordatetime;
	private Boolean isEditable;
	private List<EventLabourCheckListImagesResponseDto> files;
	
	
}
