package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModuleRightsResponseDto {

	private Long id;
	private String name;
	private Boolean isAdminModule;
	private Boolean isActive;
	private Boolean isDelete;
	private String createdAt;
}
