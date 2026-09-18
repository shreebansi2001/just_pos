package com.crmportal.request.dto;

import java.util.List;

import com.crmportal.request.NameplateRequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NamePlateReportRequestDto {
	private Long eventId;
	
	private Long eventFunctionId;
	
	private Long userId;
	
	private Long adminTemplateModuleId;
	
	private Integer itemFontSize;
	
	private Integer categoryFontSize;
	
	private int lang;
	
	private Integer isCompanyDetails;
	
	private List<NameplateRequestDto> namePlateRequests;
}
