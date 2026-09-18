package com.crmportal.response.dto;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateMappingResponseDto {

	private Long id;
	
	private String nameEnglish;

	private String nameHindi;

	private String nameGujarati;
	
	private Long templateModuleId;
	
	private String templateModuleNameEnglish;
	
	private String templateModuleNameHindi;
	
	private String templateModuleNameGujarati;
	
	private Integer sortorder;

	private String namePlateType;
	
	private Integer isDate;
}
