package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactTypeMasterRequestDto {

	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private Long userId;
	
}
