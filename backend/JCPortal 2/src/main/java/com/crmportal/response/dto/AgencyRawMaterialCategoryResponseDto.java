package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgencyRawMaterialCategoryResponseDto {

	private Long rawCatId;
	private String rawCatNameEnglish;
	private String rawCatNameHindi;
	private String rawCatNameGujarati;
	
	private List<AgencyRawMaterialItemsResponseDto> rawItems;
}
