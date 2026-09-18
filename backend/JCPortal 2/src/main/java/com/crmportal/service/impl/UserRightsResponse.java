package com.crmportal.service.impl;

import java.util.List;

import com.crmportal.response.dto.UserRightsMasterResponseDto;
import com.crmportal.response.dto.UserRightsPageWithModuleResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRightsResponse {
	private Long roleId;
	private String rolaName;
	List<UserRightsPageWithModuleResponseDto> userRights;
}
