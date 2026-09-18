package com.crmportal.request.dto;

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
public class CustomPackageDetailsRequestDto {

	private Long menuId;
	private String menuName;
	private Integer menuSortOrder;
	private String menuInstruction;
	private Integer anyItem;
	private String catNickNameEnglish;
	private String catNickNameHindi;
	private String catNickNameGujarati;
	private List<CustomPackageMenuItemDetailsDto> customPackageMenuItemDetails;
	
}
