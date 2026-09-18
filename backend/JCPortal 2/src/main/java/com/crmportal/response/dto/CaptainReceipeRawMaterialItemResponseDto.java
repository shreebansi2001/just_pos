package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptainReceipeRawMaterialItemResponseDto {

	private Long id;
	
	private Long rawItemId;
	
	private String rawItemName;
	
	private Long captainReceipeId;
	
	private BigDecimal qty;
	
	private Long unitId;
	
	private String unitName;
	
	private Boolean isDelete;
	
	private String createdAt;
	
	private String updatedAt;
	
	private BigDecimal rate;
}
