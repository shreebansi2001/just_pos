package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.EventGroundTaskRequestDto;
import com.crmportal.response.dto.EventGroundTaskResponseDto;

@Service
public interface EventGroundTaskService {

	Boolean addOrUpdate(EventGroundTaskRequestDto request);

	List<EventGroundTaskResponseDto> getAll(String resourcseType, Boolean isActive, Long userId);

	void updateStatus(Long id, Boolean isActive);

	void deleteTask(Long id);

}
