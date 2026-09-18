package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.ContactTypeMasterRequestDto;
import com.crmportal.request.dto.UserRightsPagesRequestDto;
import com.crmportal.response.dto.ContactTypeMasterResponseDto;
import com.crmportal.response.dto.ModuleWiseUserRightsPagesResponseDto;
import com.crmportal.response.dto.UserRightsMasterResponseDto;
import com.crmportal.response.dto.UserRightsPageWithModuleResponseDto;
import com.crmportal.response.dto.UserRightsPagesResponseDto;
import com.crmportal.response.dto.UserRightsRequest;
import com.crmportal.service.impl.UserRightsResponse;

@Service
public interface UserRightsService {

	public List<ModuleWiseUserRightsPagesResponseDto> getActivePages(Boolean isAdminRights, Boolean isCombine);
	
	 public UserRightsPagesResponseDto addOrUpdateUserRightsPage(UserRightsPagesRequestDto request, Long id);
	 
	 public void updateUserRights(UserRightsRequest request);
	 
	 public List<UserRightsPageWithModuleResponseDto> getRightsByRoleId(Long roleId);

	public UserRightsResponse getRightsByUserId(Long userId);
	
	void deleteUserRightsPage(Long pageId);

}
