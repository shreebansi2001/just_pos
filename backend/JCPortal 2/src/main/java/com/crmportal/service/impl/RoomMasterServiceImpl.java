package com.crmportal.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.RoomMaster;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.RoomMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.RoomMasterRequestDto;
import com.crmportal.response.dto.RoomMasterResponseDto;
import com.crmportal.service.RoomMasterService;

@Service
public class RoomMasterServiceImpl implements RoomMasterService {

    @Autowired
    private RoomMasterRepository roomMasterRepository;

    @Autowired
    private UserMasterRepository userMasterRepository;

    @Override
    public RoomMasterResponseDto addOrUpdateRoom(RoomMasterRequestDto request, Long id) {

        RoomMaster room;

        if (id != null && id > 0) {

            room = roomMasterRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Room not found"));

        } else {
            room = new RoomMaster();
        }

        UserMasterEntity user = userMasterRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        room.setNameEnglish(request.getNameEnglish());
        room.setNameHindi(request.getNameHindi());
        room.setNameGujarati(request.getNameGujarati());
        room.setPrice(request.getPrice());
        room.setIsActive(request.getIsActive());
        room.setUser(user);
        room.setUpdatedAt(LocalDateTime.now());

        room = roomMasterRepository.save(room);

        RoomMasterResponseDto response = new RoomMasterResponseDto();
        BeanUtils.copyProperties(room, response);
        response.setUserId(room.getUser().getId());

        return response;
    }

    @Override
    public List<RoomMasterResponseDto> getAllActiveRoomsByUserId(Long userId) {

        List<RoomMaster> rooms =
                roomMasterRepository.findByUser_IdAndIsDeleteFalseAndIsActiveTrue(userId);

        return rooms.stream().map(room -> {

            RoomMasterResponseDto dto = new RoomMasterResponseDto();

            BeanUtils.copyProperties(room, dto);

            dto.setUserId(room.getUser().getId());

            return dto;

        }).collect(Collectors.toList());
    }

    @Override
    public RoomMasterResponseDto getRoomById(Long roomId) {

        Optional<RoomMaster> roomOpt = roomMasterRepository.findById(roomId);

        if (!roomOpt.isPresent()) {
            return null;
        }

        RoomMaster room = roomOpt.get();

        RoomMasterResponseDto dto = new RoomMasterResponseDto();

        BeanUtils.copyProperties(room, dto);

        dto.setUserId(room.getUser().getId());

        return dto;
    }

    @Override
    public Boolean deleteRoomById(Long roomId) {

        Optional<RoomMaster> roomOpt = roomMasterRepository.findById(roomId);

        if (!roomOpt.isPresent()) {
            return false;
        }

        RoomMaster room = roomOpt.get();

        room.setIsDelete(true);

        roomMasterRepository.save(room);

        return true;
    }
}