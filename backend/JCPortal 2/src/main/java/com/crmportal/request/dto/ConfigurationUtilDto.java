package com.crmportal.request.dto;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfigurationUtilDto {
	
	@NotNull(message = "user cannot be null")
    private Long user;
	private String counterNamePlate;
	private String dateFormat;
	private String timeZone;
	private String timeFormat;
	private String pageSize;
	private String twoLanguageDefault;
	private String twoLanguagePreferred;
	private String choiceOfMenu;
	private String directShare;
	private String sacNumber;
	private Boolean displayMaxPerson = true;
	private Boolean displayAutoTime = true;
	private Boolean totalRawMaterialReport = true;
	private Boolean editRawmaterialQuantityBeforeGenReport = true;
	private String fontColor;
	private String bgColor;
	private String combineReportConfiguration;
	private Long catFontId;
	private Long itemFontId;
	private Long sloganFontId;
	private Integer catFontSize;
	private Integer itemFontSize;
	private Integer sloganFontSize;
}