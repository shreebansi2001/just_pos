package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleHierarchyResponseDto {

	private Long hierarchyId;

	private Long parentRoleId;
	private String parentRoleName;

	private Long childRoleId;
	private String childRoleName;

	private Long userId;

}
