package com.crmportal.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.mapper.EventFunctionMasterMapper;
import com.crmportal.mapper.EventMasterMapper;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.FunctionMasterRepository;
import com.crmportal.request.dto.EventFunctionFilterRequestDto;
import com.crmportal.request.dto.EventFunctionMasterRequestDto;
import com.crmportal.response.dto.AllEventFunctionMasterResponseDto;
import com.crmportal.response.dto.EventFunctionFilterResponseDto;
import com.crmportal.response.dto.EventFunctionMasterResponseDto;
import com.crmportal.response.dto.FunctionMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventFunctionMasterService;

@Service
public class EventFunctionMasterServiceImpl implements EventFunctionMasterService {

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	EventFunctionMasterRepository eventFunctionMasterRepository;

	@Autowired
	EventFunctionMasterMapper eventFunctionMasterMapper;

	@Autowired
	FunctionMasterRepository functionMasterRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	EventMasterMapper eventMasterMapper;

	@Override
	public List<EventFunctionMasterResponseDto> getAllEventFunctionByEventId(Long eventId) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		Optional<EventMasterEntity> optEvent = eventMasterRepository.findByIdAndIsDeleteFalse(eventId);
		if (!optEvent.isPresent()) {
			return Collections.emptyList();
		}

		EventMasterEntity event = optEvent.get();
		List<EventFunctionMasterEntity> eventFunctionMasterEntities = eventFunctionMasterRepository
				.findAllByEventIdAndIsDeleteFalseOrderBySortorderAsc(event.getId());
		if (eventFunctionMasterEntities.isEmpty()) {
			return Collections.emptyList();
		} else {
			List<EventFunctionMasterResponseDto> eventFunctionMasterResponseDtos = new ArrayList<>();
			for (EventFunctionMasterEntity eventFunctionMasterEntity : eventFunctionMasterEntities) {
				EventFunctionMasterResponseDto functionMasterResponseDto = eventFunctionMasterMapper
						.entityToResponse(eventFunctionMasterEntity);

				if (eventFunctionMasterEntity.getCreatedAt() != null) {
					functionMasterResponseDto
							.setCreatedAt(eventFunctionMasterEntity.getCreatedAt().format(dateFormatter));
				}
				if (eventFunctionMasterEntity.getFunctionStartDateTime() != null) {
					functionMasterResponseDto.setFunctionStartDateTime(
							eventFunctionMasterEntity.getFunctionStartDateTime().format(dateTimeFormatter));
				}
				if (eventFunctionMasterEntity.getFunctionEndDateTime() != null) {
					functionMasterResponseDto.setFunctionEndDateTime(
							eventFunctionMasterEntity.getFunctionEndDateTime().format(dateTimeFormatter));
				}

				functionMasterResponseDto.setEventId(eventId);
				eventFunctionMasterResponseDtos.add(functionMasterResponseDto);
			}
			return eventFunctionMasterResponseDtos;
		}

	}

	@Override
	public List<EventFunctionMasterResponseDto> getAllEventFunctionByEventId(List<Long> eventIds) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		List<EventFunctionMasterResponseDto> eventFunctionMasterResponseDtos = new ArrayList<>();

		for (Long eventId : eventIds) {
			Optional<EventMasterEntity> optEvent = eventMasterRepository.findByIdAndIsDeleteFalse(eventId);
			if (!optEvent.isPresent()) {
				return Collections.emptyList();
			}

			EventMasterEntity event = optEvent.get();
			List<EventFunctionMasterEntity> eventFunctionMasterEntities = eventFunctionMasterRepository
					.findAllByEventIdAndIsDeleteFalseOrderByFunctionStartDateTimeAscSortorderAsc(event.getId());
			if (eventFunctionMasterEntities.isEmpty()) {
				return Collections.emptyList();
			} else {
				for (EventFunctionMasterEntity eventFunctionMasterEntity : eventFunctionMasterEntities) {
					EventFunctionMasterResponseDto functionMasterResponseDto = eventFunctionMasterMapper
							.entityToResponse(eventFunctionMasterEntity);

					if (eventFunctionMasterEntity.getCreatedAt() != null) {
						functionMasterResponseDto
								.setCreatedAt(eventFunctionMasterEntity.getCreatedAt().format(dateFormatter));
					}
					if (eventFunctionMasterEntity.getFunctionStartDateTime() != null) {
						functionMasterResponseDto.setFunctionStartDateTime(
								eventFunctionMasterEntity.getFunctionStartDateTime().format(dateTimeFormatter));
					}
					if (eventFunctionMasterEntity.getFunctionEndDateTime() != null) {
						functionMasterResponseDto.setFunctionEndDateTime(
								eventFunctionMasterEntity.getFunctionEndDateTime().format(dateTimeFormatter));
					}

					functionMasterResponseDto.setEventId(eventId);
					eventFunctionMasterResponseDtos.add(functionMasterResponseDto);
				}
			}
		}
		return eventFunctionMasterResponseDtos;

	}

	@Override
	public List<EventFunctionMasterResponseDto> getEventFunctionById(Long eventFunctionId) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		Optional<EventFunctionMasterEntity> eventFunctionMasterEntities = eventFunctionMasterRepository
				.findByIdAndIsDeleteFalse(eventFunctionId);

		List<EventFunctionMasterResponseDto> eventFunctionMasterResponseDtos = new ArrayList<>();
		EventFunctionMasterEntity eventFunctionMasterEntity = eventFunctionMasterEntities.get();
		EventFunctionMasterResponseDto functionMasterResponseDto = eventFunctionMasterMapper
				.entityToResponse(eventFunctionMasterEntity);

		if (eventFunctionMasterEntity.getCreatedAt() != null) {
			functionMasterResponseDto.setCreatedAt(eventFunctionMasterEntity.getCreatedAt().format(dateFormatter));
		}
		if (eventFunctionMasterEntity.getFunctionStartDateTime() != null) {
			functionMasterResponseDto.setFunctionStartDateTime(
					eventFunctionMasterEntity.getFunctionStartDateTime().format(dateTimeFormatter));
		}
		if (eventFunctionMasterEntity.getFunctionEndDateTime() != null) {
			functionMasterResponseDto.setFunctionEndDateTime(
					eventFunctionMasterEntity.getFunctionEndDateTime().format(dateTimeFormatter));
		}

		functionMasterResponseDto.setEventId(eventFunctionMasterEntity.getId());
		eventFunctionMasterResponseDtos.add(functionMasterResponseDto);
		return eventFunctionMasterResponseDtos;
	}

	@Override
	public Boolean deleteEventFunctionById(Long id) {
		Boolean isDelete = false;
		if (eventFunctionMasterRepository.existsByIdAndIsDeleteFalse(id)) {

			Optional<EventFunctionMasterEntity> eventFunction = eventFunctionMasterRepository
					.findByIdAndIsDeleteFalse(id);
			EventFunctionMasterEntity entity = eventFunction.get();
			entity.setIsDelete(true);
			eventFunctionMasterRepository.save(entity);
			isDelete = true;

		}
		return isDelete;
	}

	@Override
	public Page<AllEventFunctionMasterResponseDto> getAllEventFunction(int page, int size, String search,Long userId) {

		int offset = (page - 1) * size;

		if (search != null && search.trim().isEmpty()) {
			search = null;
		}

		List<Object[]> rows = eventFunctionMasterRepository.findAllEventFunctionsNative(search, size, offset,userId);
		List<AllEventFunctionMasterResponseDto> dtoList = new ArrayList<>();

		for (Object[] row : rows) {

			AllEventFunctionMasterResponseDto dto = new AllEventFunctionMasterResponseDto(((Number) row[0]).longValue(),
					(String) row[1], (String) row[2], (String) row[3], String.valueOf(row[4]), String.valueOf(row[5]),
					((Number) row[6]).longValue(), (String) row[7], (String) row[8], (String) row[9],
					String.valueOf(row[10]), String.valueOf(row[11]),
					row[12] != null ? ((Number) row[12]).longValue() : null, (String) row[13], (String) row[14],
					(String) row[15], (String) row[16]);

			dtoList.add(dto);
		}

		long total = eventFunctionMasterRepository.countAllEventFunctions(search,userId);

		Pageable pageable = PageRequest.of(page - 1, size);

		return new PageImpl<>(dtoList, pageable, total);
	}

	@Override
	public List<EventFunctionFilterResponseDto> getFilteredEventFunctions(
	        EventFunctionFilterRequestDto request) {

	    List<Object[]> rows;

	    if (Boolean.TRUE.equals(request.getIsPriceFilterApplied())) {

	        rows = eventFunctionMasterRepository
	                .findFilteredEventFunctionsByPrice(
	                        request.getUserId(),
	                        request.getFunctionId(),
	                        request.getFromPrice(),
	                        request.getToPrice(),
	                        request.getMaxFunctionsCount());

	    } else {

	        rows = eventFunctionMasterRepository
	                .findFilteredEventFunctions(
	                        request.getUserId(),
	                        request.getFunctionId(),
	                        request.getMaxFunctionsCount());
	    }

	    if (rows == null || rows.isEmpty()) {
	        return Collections.emptyList();
	    }

	    List<EventFunctionFilterResponseDto> response = new ArrayList<>();

	    DateTimeFormatter dateFormatter =
	            DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    DateTimeFormatter dateTimeFormatter =
	            DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

	    for (Object[] row : rows) {

	        EventFunctionFilterResponseDto dto =
	                new EventFunctionFilterResponseDto();

	        dto.setEventFunctionId(
	                row[0] != null
	                        ? ((Number) row[0]).longValue()
	                        : null);

	        dto.setEventId(
	                row[1] != null
	                        ? ((Number) row[1]).longValue()
	                        : null);

	        dto.setUserId(
	                row[2] != null
	                        ? ((Number) row[2]).longValue()
	                        : null);

	        dto.setPartyId(
	                row[3] != null
	                        ? ((Number) row[3]).longValue()
	                        : null);

	        dto.setFunctionId(
	                row[4] != null
	                        ? ((Number) row[4]).longValue()
	                        : null);

	        dto.setPrice(
	                row[5] != null
	                        ? ((Number) row[5]).intValue()
	                        : null);

	        dto.setPartyNameEnglish(
	                row[6] != null
	                        ? row[6].toString()
	                        : null);

	        dto.setPartyNameHindi(
	                row[7] != null
	                        ? row[7].toString()
	                        : null);

	        dto.setPartyNameGujarati(
	                row[8] != null
	                        ? row[8].toString()
	                        : null);

	        dto.setFunctionNameEnglish(
	                row[9] != null
	                        ? row[9].toString()
	                        : null);

	        dto.setFunctionNameHindi(
	                row[10] != null
	                        ? row[10].toString()
	                        : null);

	        dto.setFunctionNameGujarati(
	                row[11] != null
	                        ? row[11].toString()
	                        : null);

	        // Event DateTime
	        if (row[12] != null) {
	            LocalDateTime eventDateTime;

	            if (row[12] instanceof java.sql.Timestamp) {
	                eventDateTime =
	                        ((java.sql.Timestamp) row[12]).toLocalDateTime();
	            } else {
	                eventDateTime = (LocalDateTime) row[12];
	            }

	            if (eventDateTime.toLocalTime()
	                    .equals(LocalTime.MIDNIGHT)) {
	                dto.setEventDateTime(
	                        eventDateTime.format(dateFormatter));
	            } else {
	                dto.setEventDateTime(
	                        eventDateTime.format(dateTimeFormatter));
	            }
	        }

	        // Function Start DateTime
	        if (row[13] != null) {
	            LocalDateTime functionStartDateTime;

	            if (row[13] instanceof java.sql.Timestamp) {
	                functionStartDateTime =
	                        ((java.sql.Timestamp) row[13]).toLocalDateTime();
	            } else {
	                functionStartDateTime =
	                        (LocalDateTime) row[13];
	            }

	            dto.setFunctionStartDateTime(
	                    functionStartDateTime.format(dateTimeFormatter));
	        }

	        // Function End DateTime
	        if (row[14] != null) {
	            LocalDateTime functionEndDateTime;

	            if (row[14] instanceof java.sql.Timestamp) {
	                functionEndDateTime =
	                        ((java.sql.Timestamp) row[14]).toLocalDateTime();
	            } else {
	                functionEndDateTime =
	                        (LocalDateTime) row[14];
	            }

	            dto.setFunctionEndDateTime(
	                    functionEndDateTime.format(dateTimeFormatter));
	        }

	        response.add(dto);
	    }

	    return response;
	}
}
