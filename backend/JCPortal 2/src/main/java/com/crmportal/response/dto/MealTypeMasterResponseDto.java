package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealTypeMasterResponseDto {

	private Long id;
	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private String createdAt;
	private Long userId;
}
