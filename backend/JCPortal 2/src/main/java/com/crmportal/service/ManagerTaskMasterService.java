package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.ManagerTaskMasterRequestDto;
import com.crmportal.response.dto.ManagerTaskMasterResponseDto;

@Service
public interface ManagerTaskMasterService {

	Boolean addOrUpdateManagerTask(List<ManagerTaskMasterRequestDto> request);

	List<ManagerTaskMasterResponseDto> getAllManagerTask(Long userId, String type);

	Boolean deleteById(Long id);

}
