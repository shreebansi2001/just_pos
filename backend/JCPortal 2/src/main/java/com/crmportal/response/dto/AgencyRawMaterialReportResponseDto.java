package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgencyRawMaterialReportResponseDto {

	private Long agencyId;
	private String agencyName;
	
	private List<AgencyMenuCategoryResponseDto> menuCategories;
	private List<AgencyRawMaterialCategoryResponseDto> rawMaterials;
}
