package com.crmportal.request.dto;

import org.springframework.web.bind.annotation.RequestParam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomPackageReportRequestDto {
	private Long catFontId;
	private Long itemFontId;
	private Long sloganFontId;
	private Integer catFontSize;
	private Integer itemFontSize;
	private Integer sloganFontSize;
	private Long customPackageId;
	private Long userId;
	private Long adminTemplateId;
	private Integer lang;
}
