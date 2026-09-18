package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecoreMainCategoryItemMasterRequestDto {

	private Long id;

	@NotBlank(message = "Name English is required")
	private String nameEnglish;

	private String nameGujarati;

	private String nameHindi;

	private String instructionEnglish;

	private String instructionHindi;

	private String instructionGujarati;

	private String slogan;

	private BigDecimal price;

	private Integer sequence = 1;

	@NotNull(message = "Category Id is required")
	private Long decoreMainCategoryId;

	@NotNull(message = "User Id is required")
	private Long userId;

	private String url;

	private String remarks;

	private List<DecoreCatItemImagesRequestDto> imagesPath;
}