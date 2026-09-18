package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategoryMasterResponseDto {

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
	private Long userId;
	private String reportNameEnglish;

	private String reportNameHindi;

	private String reportNameGujarati;
}
