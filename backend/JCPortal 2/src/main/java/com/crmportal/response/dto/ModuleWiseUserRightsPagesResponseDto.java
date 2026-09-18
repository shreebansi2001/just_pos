package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleWiseUserRightsPagesResponseDto {

	private Long moduleId;
	private String moduleName;
	private List<UserRightsPagesResponseDto> userRightsPages;
}
