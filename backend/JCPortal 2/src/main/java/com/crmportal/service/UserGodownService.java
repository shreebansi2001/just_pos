package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.UserGodownRequestDto;
import com.crmportal.response.dto.UserGodownResponseDto;

@Service
public interface UserGodownService {

	UserGodownResponseDto addOrUserGodownMaster(UserGodownRequestDto request);

	List<UserGodownResponseDto> getAllGodownMaster(Long userId);

	UserGodownResponseDto getGodownMasterById(Long id);

	Boolean deleteGodownMasterById(Long id);

}