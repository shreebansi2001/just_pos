package com.crmportal.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventFunctionRevisionHistoryEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.EventFunctionRevisionHistoryRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.EventFunctionRevisionHistoryRequestDto;
import com.crmportal.response.dto.EventFunctionRevisionHistoryResponseDto;
import com.crmportal.service.EventFunctionRevisionHistoryService;

@Service
public class EventFunctionRevisionHistoryServiceImpl implements EventFunctionRevisionHistoryService {

	@Autowired
	EventFunctionRevisionHistoryRepository eventFunctionRevisionHistoryRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Override
	public Boolean addOrUpdate(EventFunctionRevisionHistoryRequestDto request) {

		try {

			UserMasterEntity approvedUser = userMasterRepository.findByIdAndIsDeleteFalse(request.getApprovedBy())
					.orElseThrow(() -> new RuntimeException("Approved User not found"));

			UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found"));
			EventFunctionRevisionHistoryEntity entity;

			if (request.getId() != null && request.getId() != 0) {
				entity = eventFunctionRevisionHistoryRepository.findByIdAndIsDeleteFalse(request.getId())
						.orElse(new EventFunctionRevisionHistoryEntity());
			} else {
				entity = new EventFunctionRevisionHistoryEntity();
			}

			if (request.getRevisionDate() != null && !request.getRevisionDate().trim().isEmpty()) {

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

				entity.setRevisionDate(LocalDateTime.parse(request.getRevisionDate(), formatter));
			}

			entity.setApprovedBy(approvedUser.getId());
			entity.setChanges(request.getChanges());
			entity.setEventId(request.getEventId());
			entity.setUserId(user.getId());
			entity.setUpdatedAt(LocalDateTime.now());

			if (entity.getIsDelete() == null) {
				entity.setIsDelete(false);
			}

			eventFunctionRevisionHistoryRepository.save(entity);

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<EventFunctionRevisionHistoryResponseDto> getAll(Long userId, Long eventId) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		List<EventFunctionRevisionHistoryEntity> list = eventFunctionRevisionHistoryRepository.findAllByFilters(userId,
				eventId);

		return list.stream().map(entity -> {

			EventFunctionRevisionHistoryResponseDto dto = new EventFunctionRevisionHistoryResponseDto();

			dto.setId(entity.getId());

			if (entity.getRevisionDate() != null) {
				dto.setRevisionDate(entity.getRevisionDate().format(formatter));
			}

			dto.setApprovedBy(entity.getApprovedBy());

			if (entity.getApprovedBy() != null) {

				UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(entity.getApprovedBy())
						.orElse(null);

				if (user != null) {
					dto.setApprovedByName((user.getFirstName() != null ? user.getFirstName() : "") + " "
							+ (user.getLastName() != null ? user.getLastName() : ""));
				}
			}

			dto.setChanges(entity.getChanges());
			dto.setEventId(entity.getEventId());
			dto.setUserId(entity.getUserId());

			return dto;

		}).collect(Collectors.toList());
	}

	@Override
	public Boolean deleteById(Long id) {
		EventFunctionRevisionHistoryEntity entity = eventFunctionRevisionHistoryRepository.findByIdAndIsDeleteFalse(id)
				.orElse(null);

		if (entity == null) {
			return false;
		}

		entity.setIsDelete(true);
		entity.setUpdatedAt(LocalDateTime.now());

		eventFunctionRevisionHistoryRepository.save(entity);

		return true;
	}

}
