package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemMasterResponseDto {

	private Long id;
	private String nameEnglish;
	private String nameGujarati;
	private String nameHindi;
	private String instructionEnglish;
	private String instructionHindi;
	private String instructionGujarati;
	private String slogan;
	private BigDecimal price;
	private Integer sequence;
	private String imagePath;
	private Boolean isActive;
	private String createdAt;
	private MenuCategoryMasterResponseDto menuCategory;
	private MenuSubCategoryMasterResponseDto menuSubCategory;
	private Long userId;
	private MenuItemAllocationConfigResponseDto menuItemAllocationConfigs;
	private List<MenuItemRawMaterialsResponseDto> menuItemRawMaterials;
	private List<MenuItemCaptainReceipeResponseDto> menuItemCaptainReceipe;
	private BigDecimal totalRate;
	private BigDecimal dishCosting;
	private String url;
	private String remarks;
}
