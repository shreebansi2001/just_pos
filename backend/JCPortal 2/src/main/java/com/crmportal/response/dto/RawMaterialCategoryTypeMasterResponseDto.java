package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawMaterialCategoryTypeMasterResponseDto {

	private Long id;
	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private Boolean isManually;
	private String createdAt;
	private Boolean isActive;
	private Long userId;
}
