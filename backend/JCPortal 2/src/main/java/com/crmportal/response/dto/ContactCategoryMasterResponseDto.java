package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactCategoryMasterResponseDto {

	private Long id;
	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private Integer sequence;
	private Boolean isActive;
	private String createdAt;
	private ContactTypeMasterResponseDto contactType;
	private Long userId;
}
