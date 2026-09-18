package com.crmportal.request.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactCategoryMasterRequestDto {

	@NotBlank(message = "Name (English) is required")
	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private Long contcatTypeId;
	private Integer sequence = 1;
	private Long userId;
}
