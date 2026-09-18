package com.crmportal.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventFunctionManagerTaskEntity;
import com.crmportal.entity.EventFunctionManagerTaskImagesEntity;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.enums.PriorityType;
import com.crmportal.enums.StatusType;
import com.crmportal.enums.TaskType;
import com.crmportal.repository.EventFunctionManagerTaskImagesRepository;
import com.crmportal.repository.EventFunctionManagerTaskRepository;
import com.crmportal.request.dto.EventFunctionManagerTaskMainRequestDto;
import com.crmportal.request.dto.EventFunctionManagerTaskRequestDto;
import com.crmportal.request.dto.SpecialNotesImagesRequestDto;
import com.crmportal.response.dto.EventFunctionManagerTaskResponseDto;
import com.crmportal.response.dto.EventFunctionManagerTaskSummaryResponseDto;
import com.crmportal.response.dto.EventFunctionWiseManagerTaskResponseDto;
import com.crmportal.response.dto.ManagerTaskSummaryResponseDto;
import com.crmportal.response.dto.SpecialNotesImagesResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventFunctionManagerTaskService;
import com.crmportal.service.UserFileService;

@Service
public class EventFunctionManagerTaskServiceImpl implements EventFunctionManagerTaskService {

	@Autowired
	EventFunctionManagerTaskRepository eventFunctionManagerTaskRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	UserFileService userFileService;

	@Autowired
	EventFunctionManagerTaskImagesRepository eventFunctionManagerTaskImagesRepository;

	@Override
	@Transactional
	public Boolean addOrUpdate(EventFunctionManagerTaskMainRequestDto requests, Long userId) {

		if (requests.getEventFunctionManagerTasks() == null || requests.getEventFunctionManagerTasks().isEmpty()) {
			return false;
		}

		for (EventFunctionManagerTaskRequestDto request : requests.getEventFunctionManagerTasks()) {

			EventFunctionManagerTaskEntity entity = getOrCreateEntity(request);

			entity.setEventFunctionId(request.getEventFunctionId());
			entity.setEventId(request.getEventId());
			entity.setLatitude(request.getLatitude());
			entity.setLongitude(request.getLongitude());
			entity.setManagerId(request.getManagerId());
			entity.setManagerTaskId(request.getManagerTaskId());
			entity.setRemarks(request.getRemarks());
			entity.setStatus(StatusType.valueOf(request.getStatus().toUpperCase()));

			if (StatusType.COMPLETED.name().equalsIgnoreCase(request.getStatus())) {
				entity.setCompletedAt(commonService.getCurrentDateTime());
			} else {
				entity.setCompletedAt(null);
			}

			entity = eventFunctionManagerTaskRepository.save(entity);

			saveTaskImages(request, entity.getId(), userId);
		}

		return true;
	}

	private EventFunctionManagerTaskEntity getOrCreateEntity(EventFunctionManagerTaskRequestDto request) {

		if (request.getId() != null && request.getId() > 0) {
			return eventFunctionManagerTaskRepository.findByIdAndIsDeleteFalse(request.getId()).orElseThrow(
					() -> new RuntimeException("Event Function Manager Task not found with id : " + request.getId()));
		}

		return new EventFunctionManagerTaskEntity();
	}

	private void saveTaskImages(EventFunctionManagerTaskRequestDto request, Long taskId, Long userId) {

		if (request.getFiles() == null || request.getFiles().isEmpty()) {
			return;
		}

		for (SpecialNotesImagesRequestDto file : request.getFiles()) {

			if (file.getFile() == null || file.getFile().isEmpty()) {
				continue;
			}

			EventFunctionManagerTaskImagesEntity imageEntity = new EventFunctionManagerTaskImagesEntity();
			imageEntity.setEventFunctionManagerTaskId(taskId);
			imageEntity.setIsDelete(false);

			imageEntity = eventFunctionManagerTaskImagesRepository.save(imageEntity);

			try {
				userFileService.storeFile(userId, ModuleName.MANAGERTASK.name(), imageEntity.getId(),
						FileType.IMAGE.name(), file.getFile());
			} catch (IOException e) {
				throw new RuntimeException("Failed to store task image", e);
			}
		}
	}

	@Override
	public EventFunctionManagerTaskSummaryResponseDto getEventFunctionManagerTask(Long eventFunctionId, Long managerId,
			String type) {

		TaskType taskType = (type == null || type.trim().isEmpty()) ? null : TaskType.valueOf(type.toUpperCase());

		String taskTypeValue = (taskType != null) ? taskType.toString() : null;

		List<Object[]> datas = eventFunctionManagerTaskRepository.getEventFunctionManagerTask(eventFunctionId,
				managerId, taskTypeValue);

		List<Object[]> summaryRows = eventFunctionManagerTaskRepository.getManagerTaskSummary(managerId,
				eventFunctionId, taskTypeValue);

		EventFunctionManagerTaskSummaryResponseDto summaryDto = new EventFunctionManagerTaskSummaryResponseDto();

		if (summaryRows != null && !summaryRows.isEmpty()) {

			Object[] summary = summaryRows.get(0);

			long totalTasks = getLong(summary, 0);
			long totalPending = getLong(summary, 1);
			long totalInProgress = getLong(summary, 2);
			long totalCompleted = getLong(summary, 3);
			long totalCancel = getLong(summary, 4);

			summaryDto.setTotalTasks(totalTasks);
			summaryDto.setTotalPending(totalPending);
			summaryDto.setTotalInProgress(totalInProgress);
			summaryDto.setTotalCompleted(totalCompleted);
			summaryDto.setTotalCancel(totalCancel);

			if (totalTasks > 0) {
				summaryDto.setPendingPercentage(totalPending * 100.0 / totalTasks);
				summaryDto.setInProgressPercentage(totalInProgress * 100.0 / totalTasks);
				summaryDto.setCompletedPercentage(totalCompleted * 100.0 / totalTasks);
				summaryDto.setCancelPercentage(totalCancel * 100.0 / totalTasks);
			} else {
				summaryDto.setPendingPercentage(0.0);
				summaryDto.setInProgressPercentage(0.0);
				summaryDto.setCompletedPercentage(0.0);
				summaryDto.setCancelPercentage(0.0);
			}
		}

		List<EventFunctionManagerTaskResponseDto> response = new ArrayList<>();

		for (Object[] row : datas) {

			EventFunctionManagerTaskResponseDto dto = new EventFunctionManagerTaskResponseDto();

			dto.setId(row[0] != null ? ((Number) row[0]).longValue() : null);
			dto.setManagerTaskId(row[1] != null ? ((Number) row[1]).longValue() : null);
			dto.setName(row[2] != null ? row[2].toString() : null);
			dto.setDescription(row[3] != null ? row[3].toString() : null);
			dto.setManagerId(row[4] != null ? ((Number) row[4]).longValue() : null);
			dto.setEventFunctionId(row[5] != null ? ((Number) row[5]).longValue() : null);
			dto.setEventId(row[6] != null ? ((Number) row[6]).longValue() : null);
			dto.setRemarks(row[7] != null ? row[7].toString() : null);
			dto.setLatitude(row[8] != null ? row[8].toString() : null);
			dto.setLongitude(row[9] != null ? row[9].toString() : null);

			if (row[10] != null) {
				if (row[10] instanceof StatusType) {
					dto.setStatus(((StatusType) row[10]).name());
				} else {
					dto.setStatus(row[10].toString());
				}
			}

			if (row[11] != null) {
				dto.setCompletedAt(row[11].toString());
			}

			if (row[12] != null) {
				if (row[12] instanceof PriorityType) {
					dto.setPriority(((PriorityType) row[12]).name());
				} else {
					dto.setPriority(row[12].toString());
				}
			}

			// Task Type
			if (row[13] != null) {
				if (row[13] instanceof TaskType) {
					dto.setType(((TaskType) row[13]).name());
				} else {
					dto.setType(row[13].toString());
				}
			}

			// Images
			List<SpecialNotesImagesResponseDto> files = new ArrayList<>();

			if (dto.getId() != null) {

				List<EventFunctionManagerTaskImagesEntity> images = eventFunctionManagerTaskImagesRepository
						.findByEventFunctionManagerTaskIdAndIsDeleteFalse(dto.getId());

				for (EventFunctionManagerTaskImagesEntity image : images) {
					files.add(new SpecialNotesImagesResponseDto(image.getId(), image.getImagePath()));
				}
			}

			dto.setFiles(files);

			response.add(dto);
		}
		summaryDto.setFunctionManagerTasks(response);
		return summaryDto;
	}

	@Override
	public ManagerTaskSummaryResponseDto getManagerTaskSummary(Long managerId, Long eventFunctionId) {

		List<Object[]> rows = eventFunctionManagerTaskRepository.getManagerTaskSummary(managerId, eventFunctionId,
				null);

		if (rows.isEmpty()) {
			return new ManagerTaskSummaryResponseDto();
		}

		Object[] row = rows.get(0);

		long totalTasks = getLong(row, 0);
		long totalPending = getLong(row, 1);
		long totalInProgress = getLong(row, 2);
		long totalCompleted = getLong(row, 3);
		long totalCancel = getLong(row, 4);

		ManagerTaskSummaryResponseDto dto = new ManagerTaskSummaryResponseDto();
		dto.setTotalTasks(totalTasks);
		dto.setTotalPending(totalPending);
		dto.setTotalInProgress(totalInProgress);
		dto.setTotalCompleted(totalCompleted);
		dto.setTotalCancel(totalCancel);

		if (totalTasks > 0) {
			dto.setPendingPercentage(totalPending * 100.0 / totalTasks);
			dto.setInProgressPercentage(totalInProgress * 100.0 / totalTasks);
			dto.setCompletedPercentage(totalCompleted * 100.0 / totalTasks);
			dto.setCancelPercentage(totalCancel * 100.0 / totalTasks);
		} else {
			dto.setPendingPercentage(0.0);
			dto.setInProgressPercentage(0.0);
			dto.setCompletedPercentage(0.0);
			dto.setCancelPercentage(0.0);
		}

		return dto;
	}

	private long getLong(Object[] row, int index) {
		if (row == null || row.length <= index || row[index] == null) {
			return 0L;
		}
		return ((Number) row[index]).longValue();
	}

	@Override
	public List<EventFunctionWiseManagerTaskResponseDto> getAllFunctionWiseManagerTask(Long eventId) {
		List<EventFunctionManagerTaskEntity> entities = eventFunctionManagerTaskRepository
				.findAllByEventIdAndIsDeleteFalse(eventId);

		List<EventFunctionWiseManagerTaskResponseDto> dtos = new ArrayList<>();
		for (EventFunctionManagerTaskEntity eventFunctionManagerTaskEntity : entities) {
			EventFunctionWiseManagerTaskResponseDto dto = new EventFunctionWiseManagerTaskResponseDto();
			dto.setEventFunctionId(eventFunctionManagerTaskEntity.getEventFunctionId());
			dto.setManagerId(eventFunctionManagerTaskEntity.getManagerId());
			dto.setManagerTaskId(eventFunctionManagerTaskEntity.getManagerTaskId());
			dto.setEventFunctionManagerTaskId(eventFunctionManagerTaskEntity.getId());
			dtos.add(dto);
		}
		return dtos;
	}

	@Override
	public Boolean deleteManagerTaskImage(Long id) {

		if (eventFunctionManagerTaskImagesRepository.existsById(id)) {
			eventFunctionManagerTaskImagesRepository.deleteById(id);
			return true;
		}
		return false;
	}
}
