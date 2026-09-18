package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.ManagerTaskMasterEntity;
import com.crmportal.enums.PriorityType;
import com.crmportal.enums.TaskType;
import com.crmportal.repository.ManagerTaskMasterRepository;
import com.crmportal.request.dto.ManagerTaskMasterRequestDto;
import com.crmportal.response.dto.ManagerTaskMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.ManagerTaskMasterService;

@Service
public class ManagerTaskMasterServiceImpl implements ManagerTaskMasterService {

	@Autowired
	ManagerTaskMasterRepository managerTaskMasterRepository;

	@Autowired
	CommonService commonService;

	@Override
	public Boolean addOrUpdateManagerTask(List<ManagerTaskMasterRequestDto> requests) {

		if (requests.isEmpty()) {
			return false;
		} else {
			List<ManagerTaskMasterEntity> entities = new ArrayList<>();
			for (ManagerTaskMasterRequestDto request : requests) {

				ManagerTaskMasterEntity managerTaskMasterEntity = null;
				if (request.getId() != null && request.getId() != -1) {
					managerTaskMasterEntity = managerTaskMasterRepository.findByIdAndIsDeleteFalse(request.getId())
							.orElseThrow(() -> new RuntimeException("Manager Task Not Found"));
					managerTaskMasterEntity.setUpdatedAt(commonService.getCurrentDateTime());
				} else {
					managerTaskMasterEntity = new ManagerTaskMasterEntity();
					managerTaskMasterEntity.setCreatedAt(commonService.getCurrentDateTime());
				}

				managerTaskMasterEntity.setDescription(request.getDescription());
				managerTaskMasterEntity.setName(request.getName());
				managerTaskMasterEntity.setPriority(PriorityType.valueOf(request.getPriority()));
				managerTaskMasterEntity.setSequence(request.getSequence());
				managerTaskMasterEntity.setType(TaskType.valueOf(request.getType()));
				managerTaskMasterEntity.setUserId(request.getUserId());
				entities.add(managerTaskMasterEntity);
			}
			managerTaskMasterRepository.saveAll(entities);
			return true;
		}
	}

	@Override
	public List<ManagerTaskMasterResponseDto> getAllManagerTask(Long userId, String type) {
		List<ManagerTaskMasterEntity> entities = new ArrayList<>();
		if (type != null && type.trim().isEmpty()) {
			TaskType taskType = TaskType.valueOf(type);
			entities = managerTaskMasterRepository.findAllByUserIdAndIsDeleteFalseAndType(userId, taskType);
		} else {
			entities = managerTaskMasterRepository.findAllByUserIdAndIsDeleteFalse(userId);
		}
		if (entities.isEmpty()) {
			return Collections.emptyList();
		}
		List<ManagerTaskMasterResponseDto> dtos = new ArrayList<>();
		for (ManagerTaskMasterEntity managerTaskMasterEntity : entities) {
			ManagerTaskMasterResponseDto dto = mapEntityToResponse(managerTaskMasterEntity);
			dtos.add(dto);
		}
		return dtos;
	}

	private ManagerTaskMasterResponseDto mapEntityToResponse(ManagerTaskMasterEntity managerTaskMasterEntity) {

		ManagerTaskMasterResponseDto dto = new ManagerTaskMasterResponseDto();
		dto.setDescription(managerTaskMasterEntity.getDescription());
		dto.setId(managerTaskMasterEntity.getId());
		dto.setName(managerTaskMasterEntity.getName());
		dto.setPriority(managerTaskMasterEntity.getPriority().toString());
		dto.setSequence(managerTaskMasterEntity.getSequence());
		dto.setType(managerTaskMasterEntity.getType().toString());
		dto.setUserId(managerTaskMasterEntity.getUserId());

		return dto;
	}

	@Override
	public Boolean deleteById(Long id) {

		if (managerTaskMasterRepository.existsByIdAndIsDeleteFalse(id)) {
			ManagerTaskMasterEntity entity = managerTaskMasterRepository.findByIdAndIsDeleteFalse(id)
					.orElseThrow(() -> new RuntimeException("Manager Task Not Found"));
			entity.setIsDelete(true);
			managerTaskMasterRepository.save(entity);
		} else {
			return false;
		}

		return true;
	}

}
