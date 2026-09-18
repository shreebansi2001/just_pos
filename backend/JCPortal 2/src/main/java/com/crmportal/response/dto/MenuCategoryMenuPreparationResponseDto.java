package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuCategoryMenuPreparationResponseDto {

	private Long id;
	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private String menuSlogan;
	private BigDecimal price;
	private Boolean isActive;
	private Integer sequence;
	private String imagePath;
	private String createdAt;
}
