package com.crmportal.request.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventLabourCheckListRequestDto {

	private Long id;
	private Long eventId;
	private Long eventFunctionId;
	private Long contactCatId;
	private Long VendorId;
	private Long shiftId;
	private Integer totalQty;
	private Integer inQty;
	private Boolean isStatus;
	private String inTime;
	private String outTime;
	private String labordatetime;
	private Boolean isEditable;
	private List<EventLabourCheckListImagesRequestDto> files;
}
