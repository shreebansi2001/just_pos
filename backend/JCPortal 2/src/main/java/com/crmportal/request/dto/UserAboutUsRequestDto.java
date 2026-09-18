package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAboutUsRequestDto {

	private String nameEnglish;
	private String nameGujarati;
	private String nameHindi;
	private Long userId;
}
