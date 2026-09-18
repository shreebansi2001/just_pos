package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.UserAmcRequestDto;
import com.crmportal.response.dto.UserAmcResponseDto;

@Service
public interface UserAmcService {

	Boolean deleteUserAmcById(Long id);

	List<UserAmcResponseDto> getAllUserAmc();
	
}
