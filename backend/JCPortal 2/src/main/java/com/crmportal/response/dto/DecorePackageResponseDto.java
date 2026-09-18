package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecorePackageResponseDto {

	private Long id;

	private String nameEnglish;
	private String nameGujarati;
	private String nameHindi;

	private BigDecimal price;

	private Integer sequence;

	private Boolean isActive;
	private Boolean isPublished;

	private Long userId;

	private List<DecorePackageDetailsResponseDto> decorePackageDetails;
}