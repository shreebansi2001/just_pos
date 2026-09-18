package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecorePreparationDetailsResponseDto {

	private Long id;

	private Long decoreItemId;
	private String decoreItemName;

	private Integer decoreItemSortOrder;
	private BigDecimal decoreItemPrice;

	private String decoreItemNotes;

	private String decoreItemSlogan;

	private String decoreItemNameHindi;
	private String decoreItemNameGujarati;

	private String decoreItemNotesHindi;
	private String decoreItemNotesGujarati;

	private Integer itemSpace;

	private String subItem;
	private String subItemHindi;
	private String subItemGujarati;

	private Boolean isDecoreItemAddons = false;

	private Integer itemQty;

	private Long vendorId;
	
	private String vendorName;
	
	private List<String> images;
}