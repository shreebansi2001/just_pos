package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

import com.crmportal.response.dto.MenuPreparationDetailsResponseDto;
import com.crmportal.response.dto.MenuPreparationSelectedItemDetailsResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuPreparationDetailsRequestDto {

	private Long menuCategoryId;
	private String menuCategoryName;
	private Integer menuSortOrder;
	private String menuSlogan;
	private String menuNotes;
	private List<MenuPreparationSelectedItemDetailsResponseDto> selectedMenuPreparationItems;
}
