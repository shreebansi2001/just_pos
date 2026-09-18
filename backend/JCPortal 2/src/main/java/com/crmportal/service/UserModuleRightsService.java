package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.UserModuleRightsRequestDto;
import com.crmportal.response.dto.UserModuleRightsResponseDto;

@Service
public interface UserModuleRightsService {

	UserModuleRightsResponseDto addOrUpdateModuleRights(@Valid UserModuleRightsRequestDto request, Long valueOf);

	List<UserModuleRightsResponseDto> getAllModuleRights();

	UserModuleRightsResponseDto getModuleRightsById(Long id);

	Boolean deleteModuleRightsById(Long id);

}
