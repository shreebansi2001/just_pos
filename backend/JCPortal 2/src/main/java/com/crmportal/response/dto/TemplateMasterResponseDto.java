package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateMasterResponseDto {

	private Long id;
	private String name;
	private String createdAt;
	private String headingFontColor;
	private String contentFontColor;
	private String descriptionFontColor;
	private String frontPage;
	private String secondFrontPage;
	private String watermark;
	private String lastMainPage;
	private Boolean isNamePlate;
	private String namePlateBg;
	private String namePlateCoverBg;
	private TemplateModuleMasterResponseDto templateModuleMaster;
	private TemplateMappingResponseDto templateMapping;
	private String dummyPdf;
	private Boolean isActive;
	private Boolean isDelete;
	private Boolean isSelected;
	private String catBgPage;	
	private String extraPage;
	private BigDecimal price;
	private Boolean isDefault;
	private String description;
	private Long catFontId;
	private Long itemFontId;
	private Long sloganFontId;
	private Integer catFontSize;
	private Integer itemFontSize;
	private Integer sloganFontSize;
	private Boolean isCounterNamePlate;
	private Map<Long, String> namePlateImages;
}
