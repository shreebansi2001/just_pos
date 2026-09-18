package com.crmportal.response.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgencyRawMaterialItemsResponseDto {

	private Long rawItemId;
	private String rawItemNameEnglish;
	private String rawItemNameHindi;
	private String rawItemNameGujarati;
	private Double totalWeight;
	private Long unitId;
	private String unitName;
	private Boolean isApplyCal;
	private Map<Long, Double> unitQtyMap = new HashMap<>();

}
