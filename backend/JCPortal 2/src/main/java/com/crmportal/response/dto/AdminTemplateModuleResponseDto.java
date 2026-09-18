package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminTemplateModuleResponseDto {

	private Long id;
	private Long userId;
	private String userName;
	private String mobileNo;
	private String email;
	private Boolean isPayment;
	private TemplateModuleMasterResponseDto templateModuleMaster;
	private TemplateMappingResponseDto templateMappingResponseDto;
	private TemplateMasterResponseDto templateMaster;
	private String createdAt;
	private Long catFontId;
	private Long itemFontId;
	private Long sloganFontId;
	private Integer catFontSize;
	private Integer itemFontSize;
	private Integer sloganFontSize;
}
