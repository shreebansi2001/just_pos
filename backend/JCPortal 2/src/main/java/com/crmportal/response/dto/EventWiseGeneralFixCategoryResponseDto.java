package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventWiseGeneralFixCategoryResponseDto {

	private Long rawMaterialCatId;
	
	private String rawMaterialCatName;
	
	private List<EventWiseGeneralFixDetailsResponseDto> items;
}
