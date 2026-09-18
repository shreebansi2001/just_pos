package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecoreMainCategoryItemMasterResponseDto {

	private Long id;

	private String nameEnglish;

	private String nameHindi;

	private String nameGujarati;

	private String instructionEnglish;

	private String instructionHindi;

	private String instructionGujarati;

	private String slogan;

	private BigDecimal price;

	private Integer sequence;

	private Long decoreMainCategoryId;

	private String decoreMainCategoryName;

	private Long userId;

	private String url;

	private String remarks;

	private Boolean isActive;

	private String createdAt;

	private List<DecoreItemImageResponseDto> images;
}