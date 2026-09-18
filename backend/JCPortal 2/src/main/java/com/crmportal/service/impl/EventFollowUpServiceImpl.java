package com.crmportal.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.controller.EventFollowUpEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.EventFollowupRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.EventFollowupRequestDto;
import com.crmportal.response.dto.EventFollowupResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventFollowUpService;
import com.crmportal.utility.DateUtils;

@Service
public class EventFollowUpServiceImpl implements EventFollowUpService {

	@Autowired
	EventFollowupRepository eventFollowupRepository;

	@Autowired
	UserMasterRepository userRepository;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	CommonService commonService;

	@Override
	public EventFollowupResponseDto saveOrUpdate(EventFollowupRequestDto dto) {

		EventFollowUpEntity entity;

		if (dto.getId() != null) {

			entity = eventFollowupRepository.findById(dto.getId())
					.orElseThrow(() -> new RuntimeException("Followup not found"));

			entity.setEventId(dto.getEventId());

		} else {

			entity = new EventFollowUpEntity();
			entity.setEventId(dto.getEventId());
			entity.setIsDelete(false);
		}
		entity.setIsDone(dto.getIsDone());
		entity.setFollowUpDate(commonService.dateFormatted(dto.getFollowupDate()));
		entity.setDescription(dto.getDescription());
		entity.setUserId(dto.getUserId());
		entity.setManagerId(dto.getManagerId());
		entity.setUpdatedAt(LocalDateTime.now());

		eventFollowupRepository.save(entity);

		return mapSingle(entity);
	}

	@Override
	public EventFollowupResponseDto getById(Long id) {

		EventFollowUpEntity entity = eventFollowupRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Followup not found"));

		if (Boolean.TRUE.equals(entity.getIsDelete())) {
			throw new RuntimeException("Followup not found");
		}

		return mapSingle(entity);
	}

	@Override
	public List getAll(Long userId, Long managerId, String startDate, String endDate, Boolean isDone, Long eventId) {

		LocalDate start = null;
		LocalDate end = null;

		if (startDate != null && !startDate.trim().isEmpty()) {
			start = commonService.dateFormatted(startDate);
		}

		if (endDate != null && !endDate.trim().isEmpty()) {
			end = commonService.dateFormatted(endDate);
		}

		List<EventFollowUpEntity> list = eventFollowupRepository.findAllWithFilters(userId, managerId, eventId, isDone,
				start, end);

		return map(list);
	}

	@Override
	public Boolean deleteById(Long id) {

		EventFollowUpEntity entity = eventFollowupRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Not found"));

		entity.setIsDelete(true);
		entity.setUpdatedAt(LocalDateTime.now());

		eventFollowupRepository.save(entity);
		return true;
	}

	public List<EventFollowupResponseDto> map(List<EventFollowUpEntity> list) {

		if (list == null || list.isEmpty()) {
			return new ArrayList<>();
		}

		List<Long> managerIds = list.stream().map(EventFollowUpEntity::getManagerId).filter(Objects::nonNull).distinct()
				.collect(Collectors.toList());

		List<Long> eventIds = list.stream().map(EventFollowUpEntity::getEventId).filter(Objects::nonNull).distinct()
				.collect(Collectors.toList());

		Map<Long, String> managerMap = userRepository
				.findByIdInAndIsDeleteFalse(
						managerIds)
				.stream()
				.collect(Collectors.toMap(UserMasterEntity::getId,
						u -> (u.getFirstName() != null ? u.getFirstName() : "") + " "
								+ (u.getLastName() != null ? u.getLastName() : ""),
						(a, b) -> a));

		Map<Long, String> eventMap = eventMasterRepository.findByIdInAndIsDeleteFalse(eventIds).stream()
				.collect(Collectors.toMap(EventMasterEntity::getId,
						e -> e.getEventType() != null ? e.getEventType().getNameEnglish() : "", (a, b) -> a));

		return list.stream().filter(e -> !Boolean.TRUE.equals(e.getIsDelete())).map(e -> {

			String managerName = managerMap.getOrDefault(e.getManagerId(), "");

			String eventName = eventMap.getOrDefault(e.getEventId(), "");

			return new EventFollowupResponseDto(e.getId(), e.getEventId(), eventName,
					e.getFollowUpDate() != null ? DateUtils.formatLocalDate(e.getFollowUpDate()) : null,
					e.getDescription(), e.getUserId(), e.getManagerId(), managerName,
					e.getCreatedAt() != null ? DateUtils.formatLocalDateTime(e.getCreatedAt()) : null, e.getIsDone());
		}).collect(Collectors.toList());
	}

	private EventFollowupResponseDto mapSingle(EventFollowUpEntity entity) {

		List<EventFollowupResponseDto> result = map(Collections.singletonList(entity));

		return result.isEmpty() ? null : result.get(0);
	}
}
