package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuPreparationCombResponseDto {
	
	private MenuPreparationResponseDto menuPreparation;
	private List<MenuPreparationItemResponseDto> MenuPreparationItems;
	private List<MenuPreparationSelectedItemDetailsResponseDto> selectedMenuPreparationItems;
	private List<CustomPackageDetailsResponseDto> customPackageDetails;
	private Integer totalPage;
	private Integer totalItems;
	private Integer currentPage;
}
