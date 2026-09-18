package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionNameplateResponseDto {

	private Long id;

	private Long menuItemId;
	
	private String itemNameEnglish;
	
	private String itemNameHindi;
	
	private String itemNameGujarati;
	
	private BigDecimal sequence;

	private BigDecimal itemCount;
	
	private Integer isStandyChecked;

	private Integer isTableMenuChecked;

}
