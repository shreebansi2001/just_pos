package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleTreeResponseDto {

	private Long roleId;

	private String roleName;

	private List<RoleTreeResponseDto> children;
}
