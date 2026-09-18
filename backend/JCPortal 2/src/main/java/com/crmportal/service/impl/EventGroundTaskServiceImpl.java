package com.crmportal.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventGroundTaskMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.AllocationType;
import com.crmportal.repository.EventGroundTaskMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.EventGroundTaskRequestDto;
import com.crmportal.response.dto.EventGroundTaskResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventGroundTaskService;

@Service
public class EventGroundTaskServiceImpl implements EventGroundTaskService {

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	EventGroundTaskMasterRepository eventGroundTaskMasterRepository;

	@Autowired
	CommonService commonService;

	@Override
	public Boolean addOrUpdate(EventGroundTaskRequestDto request) {

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found"));

		AllocationType resourceType = AllocationType.valueOf(request.getResourceType());

		EventGroundTaskMasterEntity entity;

		if (request.getId() == null || request.getId() == -1) {

			if (eventGroundTaskMasterRepository.existsByNameEnglishIgnoreCaseAndResourceTypeAndIsDeleteFalse(
					request.getNameEnglish(), resourceType)) {

				throw new RuntimeException("Task name already exists.");
			}

			entity = new EventGroundTaskMasterEntity();
			entity.setIsTrue(true);
			entity.setIsDelete(false);

		} else {

			entity = eventGroundTaskMasterRepository
					.findByIdAndIsDeleteFalse(request.getId())
					.orElseThrow(() -> new RuntimeException("Task Not Found With Id : " + request.getId()));

			if (eventGroundTaskMasterRepository.existsByNameEnglishIgnoreCaseAndResourceTypeAndIdNotAndIsDeleteFalse(
					request.getNameEnglish(), resourceType, request.getId())) {

				throw new RuntimeException("Task name already exists.");
			}

			entity.setUpdatedAt(commonService.getCurrentDateTime());
		}

		entity.setNameEnglish(request.getNameEnglish());
		entity.setNameHindi(request.getNameHindi());
		entity.setNameGujarati(request.getNameGujarati());
		entity.setResourceType(resourceType);
		entity.setUser(user);

		eventGroundTaskMasterRepository.save(entity);

		return true;
	}

	@Override
	public List<EventGroundTaskResponseDto> getAll(String resourceType, Boolean isActive, Long userId) {

		AllocationType allocationType = null;

		if (resourceType != null && !resourceType.trim().isEmpty()) {
			try {
				allocationType = AllocationType.valueOf(resourceType.trim().toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("Invalid Resource Type : " + resourceType);
			}
		}

		List<EventGroundTaskMasterEntity> entities = eventGroundTaskMasterRepository.getAllTasks(allocationType,
				isActive, userId);

		List<EventGroundTaskResponseDto> responseList = new ArrayList<>();

		for (EventGroundTaskMasterEntity entity : entities) {

			EventGroundTaskResponseDto dto = new EventGroundTaskResponseDto();

			dto.setId(entity.getId());
			dto.setResourceType(entity.getResourceType() != null ? entity.getResourceType().name() : null);
			dto.setNameEnglish(entity.getNameEnglish());
			dto.setNameHindi(entity.getNameHindi());
			dto.setNameGujarati(entity.getNameGujarati());
			dto.setIsDelete(entity.getIsDelete());
			dto.setIsTrue(entity.getIsTrue());

			if (entity.getUser() != null) {
				dto.setUserId(entity.getUser().getId());
			}

			responseList.add(dto);
		}

		return responseList;
	}

	@Override
	public void deleteTask(Long id) {

		EventGroundTaskMasterEntity entity = eventGroundTaskMasterRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Task not found"));

		if (Boolean.TRUE.equals(entity.getIsDelete())) {
			throw new RuntimeException("Task already deleted");
		}

		entity.setIsDelete(true);
		entity.setUpdatedAt(LocalDateTime.now());

		eventGroundTaskMasterRepository.save(entity);
	}

	@Override
	public void updateStatus(Long id, Boolean isActive) {

		EventGroundTaskMasterEntity entity = eventGroundTaskMasterRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Task not found"));

		if (Boolean.TRUE.equals(entity.getIsDelete())) {
			throw new RuntimeException("Deleted task cannot be updated");
		}

		entity.setIsTrue(isActive);
		entity.setUpdatedAt(LocalDateTime.now());

		eventGroundTaskMasterRepository.save(entity);
	}
}
