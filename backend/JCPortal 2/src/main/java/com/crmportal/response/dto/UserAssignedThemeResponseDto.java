package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAssignedThemeResponseDto {

	private Long id;
	private Long themeId;
	private String themeName;
}
