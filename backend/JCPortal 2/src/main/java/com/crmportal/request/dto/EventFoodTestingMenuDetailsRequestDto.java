package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFoodTestingMenuDetailsRequestDto {

	private Integer itemSortOrder;
	private Long menuItemId;
	private String menuItemNameEnglish;
	private String menuItemNameHindi;
	private String menuItemNameGujarati;
	private String itemSlogan;
	private String itemNotesEnglish;
	private String itemNotesHindi;
	private String itemNotesGujarati;
	private String clientNotes;
	private String clientNotesHindi;
	private String clientNotesGujarati;
	private String review;
}
