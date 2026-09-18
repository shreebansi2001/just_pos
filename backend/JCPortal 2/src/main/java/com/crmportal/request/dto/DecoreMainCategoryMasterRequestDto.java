package com.crmportal.request.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecoreMainCategoryMasterRequestDto {

	private Long id;

	@NotBlank(message = "Name English is required")
	private String nameEnglish;

	private String nameGujarati;

	private String nameHindi;

	private String categorySlogan;

	private BigDecimal price;

	private Integer sequence = 1;

	@NotNull(message = "User Id is required")
	private Long userId;

}