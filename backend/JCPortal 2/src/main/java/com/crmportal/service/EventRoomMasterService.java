package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.response.dto.EventRoomMasterResponseDto;

@Service
public interface EventRoomMasterService {

	List<EventRoomMasterResponseDto> getByEventId(Long eventId);

	Boolean deleteByEventId(Long eventId);

}
