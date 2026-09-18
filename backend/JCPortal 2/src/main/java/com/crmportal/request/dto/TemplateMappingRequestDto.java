package com.crmportal.request.dto;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateMappingRequestDto {

	private Long id;
	
	private String nameEnglish;

	private String nameHindi;
	
	private String nameGujarati;
	
	private Long templateModuleId;
	
	private Integer sortorder;
	
	private Integer isDate;

	private String namePlateType;
}
