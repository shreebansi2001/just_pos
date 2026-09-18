package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAssignedAllModuleWiseThemeResponseDto {

	private String templateModuleId;
	private String templateModuleName;
	private List<UserAssignedThemeResponseDto> themes;
}
