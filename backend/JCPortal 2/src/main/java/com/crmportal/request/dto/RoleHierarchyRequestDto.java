package com.crmportal.request.dto;

import lombok.Data;

@Data
public class RoleHierarchyRequestDto {

	private Long hierarchyId;
	private Long parentRoleId;
	private Long childRoleId;
	private Long userId;

}
