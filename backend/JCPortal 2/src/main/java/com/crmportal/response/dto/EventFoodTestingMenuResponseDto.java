package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFoodTestingMenuResponseDto {

	private Long menuCategoryId;
	private String menuCategoryName;
	private Integer menuSortOrder;
	private String menuCategoryNameHindi;
	private String menuCategoryNameGujarati;
	private String menuNotesEnglish;
	private String menuNotesHindi;
	private String menuNotesGujarati;
	private List<EventFoodMeuDetailsResponseDto> selectedMenuDetails;
}
