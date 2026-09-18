package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NamePlateTableMenuWithBgRequestDto {

	private Long id;
	private String itemNameEnglish;
	private String itemNameGujarati;
	private String itemNameHindi;
	private Long menuItemId;
	private BigDecimal sequence;
	private BigDecimal itemCount;
	private Integer is_checked;

}
