package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptainReceipeMasterResponseDto {

	private Long id;
	
	private String name;
	
	private Long userId;
	
	private BigDecimal weight;
	
	private Long unitId;
	
	private String unitName;
	
	private UnitHierarchyDto unitHierarchy;
	
	private BigDecimal rate;
	
	private Boolean isActive;
	
	private Boolean isDelete;
	
	private String createdAt;
	
	private String updatedAt;
	
	private List<CaptainReceipeRawMaterialItemResponseDto> rawMaterial;
}
