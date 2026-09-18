package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.RoleMasterRequestDto;
import com.crmportal.response.dto.RoleMasterResponseDto;

@Service
public interface RoleMasterService {

	RoleMasterResponseDto addOrUpdateRoleMaster(@Valid RoleMasterRequestDto request, long parseLong);

	List<RoleMasterResponseDto> getAllRoleMaster(Long userId, String roleName);

	RoleMasterResponseDto getRoleMasterById(Long id);

	Boolean deleteRoleMasterById(Long id);

}
