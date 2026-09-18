package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateMasterRequestDto {

	private String name;

	private Long userId;

	private String headingFontColor;
	
	private String contentFontColor;
	
	private String descriptionFontColor;
	
	private Boolean isNamePlate;

	private Long templateModuleId;

	private Long templateMappingId;
	
	private BigDecimal price;
	
	private Boolean isDefault;
	
	private String description;
	
	private Long catFontId;
	
	private Long itemFontId;
	
	private Long sloganFontId;
	
	private Integer catFontSize;
	
	private Integer itemFontSize;
	
	private Integer sloganFontSize;
	
}
