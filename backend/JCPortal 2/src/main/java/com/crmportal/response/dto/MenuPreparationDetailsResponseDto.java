package com.crmportal.response.dto;

import java.math.BigDecimal;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuPreparationDetailsResponseDto {

	private Long id;
	private String menuItemName;
	private Integer itemSortOrder;
	private BigDecimal itemPrice;
	private String itemNotes;
	private String itemSlogan;
	private Long menuItemId;
	private String menuItemNameHindi;
	private String menuItemNameGujarati;
	private String itemNotesHindi;
	private String itemNotesGujarati;
	private Integer itemSpace;
	private String subItem;
	private String subItemHindi;
	private String subItemGujarati;
	private String itemNickNameEnglish;
	private String itemNickNameHindi;
	private String itemNickNameGujarati;
	private Boolean isCatImage;
	private Boolean isItemAddons = false;
	private String itemStatus;
	private Boolean changedAfterCompletion;
	private String itemHeading;
	private String itemHeadingHindi;
	private String itemHeadingGujarati;
}
