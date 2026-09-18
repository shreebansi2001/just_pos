package com.crmportal.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventRoomMasterEntity;
import com.crmportal.repository.EventRoomMasterRepository;
import com.crmportal.response.dto.EventRoomMasterResponseDto;
import com.crmportal.service.EventRoomMasterService;

@Service
public class EventRoomMasterServiceImpl implements EventRoomMasterService {

	@Autowired
	private EventRoomMasterRepository eventRoomMasterRepository;

	@Override
	public List<EventRoomMasterResponseDto> getByEventId(Long eventId) {

		List<EventRoomMasterEntity> entities =
				eventRoomMasterRepository.findByEvent_Id(eventId);

		return entities.stream().map(entity -> {

			EventRoomMasterResponseDto dto =
					new EventRoomMasterResponseDto();

			dto.setId(entity.getId());

			dto.setEventId(entity.getEvent().getId());

			dto.setRoomId(entity.getRoom().getId());

			dto.setRoomNameEnglish(entity.getRoom().getNameEnglish());

			dto.setRoomNameHindi(entity.getRoom().getNameHindi());

			dto.setRoomNameGujarati(entity.getRoom().getNameGujarati());

			dto.setPrice(entity.getPrice());

			dto.setQty(entity.getQty());

			dto.setTotal(entity.getTotal());

			dto.setBookingdate(entity.getBookingdate());
			
			dto.setBookingcheckoutdate(entity.getBookingcheckoutdate());

			return dto;

		}).collect(Collectors.toList());
	}

	@Override
	public Boolean deleteByEventId(Long eventId) {
		eventRoomMasterRepository.deleteByEventId(eventId);
		return true;
	}
}
