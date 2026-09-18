package com.crmportal.request.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFoodTestingMenuRequestDto {

	private Long menuCatId;
	private Integer catSortOrder;
	private String menuCatNameEnglish;
	private String menuCatNameHindi;
	private String menuCatNameGujarati;
	private String catNotesEnglish;
	private String catNotesHindi;
	private String catNotesGujarati;
	private List<EventFoodTestingMenuDetailsRequestDto> selectedMenuDetails;
}
