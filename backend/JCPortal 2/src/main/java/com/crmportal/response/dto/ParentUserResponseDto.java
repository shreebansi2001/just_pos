package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParentUserResponseDto {

	private Long userId;
	private String userName;
	private String mobileNo;
	private Long roleId;
	private String roleName;
}
