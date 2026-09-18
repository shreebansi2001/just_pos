package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NamePlateTableMenuWithBgResponseDto {

	private Long id;
	private Long menuItemId;
	private String itemNameEnglish;
	private String itemNameHindi;
	private String itemNameGujarati;
	private BigDecimal itemCount;
	private BigDecimal sequence;
	private Integer is_checked;
	private Long menuCatId;
	private String catNameEnglish;
	private String catNameHindi;
	private String catNameGujarati;
}
