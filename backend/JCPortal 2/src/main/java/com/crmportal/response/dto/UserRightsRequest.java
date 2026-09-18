package com.crmportal.response.dto;

import java.util.List;

import com.crmportal.request.dto.UserRightsMasterRequestDto;

import lombok.Data;

@Data
public class UserRightsRequest {
	 private Long roleId;
	 private List<UserRightsMasterRequestDto> rightsList;
}
