package com.crmportal.request.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuSubCategoryMasterRequestDto {
	
	@NotBlank(message = "Name (English) is required")
	private String nameEnglish;
	
	private String nameHindi;
	private String nameGujarati;
	private Long userId;
	private Long menuCatId;
}
