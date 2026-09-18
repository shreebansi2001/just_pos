package com.crmportal.response.dto;

import java.math.BigDecimal;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomPackageMenuItemDetailsResponseDto {

	private Long id;
	private String itemName;
	private String itemInstruction;
	private Integer itemSortOrder;
	private BigDecimal itemPrice;
	private Long userId;
	private Long menuItemId;
	private String itemNickNameEnglish;
	private String itemNickNameHindi;
	private String itemNickNameGujarati;
}
