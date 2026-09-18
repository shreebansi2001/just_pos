package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecorePackageRequestDto {

	private Long id;

	@NotBlank(message = "Name (English) is required")
	private String nameEnglish;

	private String nameGujarati;

	private String nameHindi;

	private BigDecimal price;

	private Integer sequence = 1;

	private Long userId;

	private List<DecorePackageDetailsRequestDto> decorePackageDetails;
}