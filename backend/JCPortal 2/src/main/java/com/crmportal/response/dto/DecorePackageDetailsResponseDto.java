package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecorePackageDetailsResponseDto {

	private Long id;
	private Long decoreMainCategoryId;
	private String categoryName;
	private Integer menuSortOrder;
	private BigDecimal price;
	private List<DecorePackageItemResponseDto> items;
}