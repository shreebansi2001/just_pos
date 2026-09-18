package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.crmportal.entity.MenuCategoryMasterEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.UserMasterEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomPackageDetailsResponseDto {

	private Long menuId;
	private String menuName;
	private Integer menuSortOrder;
	private String menuInstruction;
    private String  itemName;
    private String  itemInstruction;
    private Integer itemSortOrder;
    private Integer anyItem;
    private Long    menuCategoryId;
    private Long    menuItemId;
    private BigDecimal itemPrice;
	private String catNickNameEnglish;
	private String catNickNameHindi;
	private String catNickNameGujarati;
	private List<CustomPackageMenuItemDetailsResponseDto> customPackageMenuItemDetails;
	
}
