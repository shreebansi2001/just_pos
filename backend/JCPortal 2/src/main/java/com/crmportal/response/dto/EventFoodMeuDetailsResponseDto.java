package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFoodMeuDetailsResponseDto {

	private Long id;
	private Integer itemSortOrder;
	private Long menuItemId;
	private String itemNameEnglish;
	private String itemNameHindi;
	private String itemNameGujarati;
	private String itemSloganEnglish;
	private String itemInstructionEnglish;
	private String itemInstructionHindi;
	private String itemInstructionGujarati;
	private String clientNotes;
	private String clientNotesHindi;
	private String clientNotesGujarati;
	private String review;
	private String createdAt;
	private String updatedAt;
	private Boolean isDelete;
}
