package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.VenueMasterRequestDto;
import com.crmportal.response.dto.VenueMasterResponseDto;

@Service
public interface VenueMasterService {

	VenueMasterResponseDto addOrUpdateVenue(@Valid VenueMasterRequestDto request, MultipartFile venueImg, Long id);

	List<VenueMasterResponseDto> getAllVenueByUser(Long userId, String venueName, Boolean isActive);

	VenueMasterResponseDto getVenueById(Long id);

	Boolean updateStatus(Long id, Boolean isActive);

	Boolean deleteVenue(Long id);

}
