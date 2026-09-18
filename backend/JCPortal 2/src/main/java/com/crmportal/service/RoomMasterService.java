package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.RoomMasterRequestDto;
import com.crmportal.response.dto.RoomMasterResponseDto;

@Service
public interface RoomMasterService {

	    RoomMasterResponseDto addOrUpdateRoom(RoomMasterRequestDto request, Long id);

	    List<RoomMasterResponseDto> getAllActiveRoomsByUserId(Long userId);

	    RoomMasterResponseDto getRoomById(Long roomId);

	    Boolean deleteRoomById(Long roomId);

}
