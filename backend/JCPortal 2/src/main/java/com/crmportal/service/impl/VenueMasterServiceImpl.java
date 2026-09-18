package com.crmportal.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.ContactCategoryMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.VenueMasterEntity;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.VenueMasterRepository;
import com.crmportal.request.dto.VenueMasterRequestDto;
import com.crmportal.response.dto.ContactCategoryMasterResponseDto;
import com.crmportal.response.dto.VenueMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.UserFileService;
import com.crmportal.service.VenueMasterService;
import com.crmportal.utility.DateUtils;

@Service
public class VenueMasterServiceImpl implements VenueMasterService {

	@Autowired
	VenueMasterRepository venueMasterRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	CommonService commonService;
	
	@Autowired
	UserFileServiceImpl userFileServiceImpl;

	@Autowired
	UserFileService userFileService;
	
	@Autowired
	Environment environment;
	
	@Override
	public VenueMasterResponseDto addOrUpdateVenue(@Valid VenueMasterRequestDto request, MultipartFile venueImg, Long id) {
		try {
			UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));
			Optional<VenueMasterEntity> existingName = venueMasterRepository
					.findByNameEnglishAndUserAndIsDeleteFalse(request.getNameEnglish(), user);
			
			VenueMasterEntity entity;
			if (id == -1) {
				if (existingName.isPresent()) {
					throw new RuntimeException("Venue with the name '" + request.getNameEnglish() + "' already exists.");
				}
				entity = new VenueMasterEntity();
				entity.setIsActive(true);
				entity.setIsDelete(false);
			} else {
				entity = venueMasterRepository.findByIdAndIsDeleteFalseAndUser(id, user);
	
				if (entity == null) {
					throw new RuntimeException("Venue Not Found");
				} else {
					if (!entity.getNameEnglish().equalsIgnoreCase(request.getNameEnglish()) && existingName.isPresent()) {
						throw new RuntimeException(
								"Venue with the name '" + request.getNameEnglish() + "' already exists.");
					}
				}
				entity.setUpdatedAt(commonService.getCurrentDateTime());
			}
	
			entity.setNameEnglish(request.getNameEnglish());
			entity.setNameGujarati(request.getNameGujarati());
			entity.setNameHindi(request.getNameHindi());
			entity.setUser(user);
			entity = venueMasterRepository.save(entity);
	
			if (venueImg != null && !venueImg.isEmpty()) {
				String fileType = "";
				
				if(id != -1) {
					userFileServiceImpl.deleteOldFile(entity.getImgPath());
					entity.setImgPath(null);
					
					entity = venueMasterRepository.save(entity);
				}
				
				String baseName = FilenameUtils.getBaseName(venueImg.getOriginalFilename());
				fileType = FileType.VENUE_IMG.toString();
	
				userFileService.storeFile(entity.getUser().getId(), ModuleName.EVENT_IMG.toString(), entity.getId(),
						fileType, venueImg);
			}
	
			VenueMasterResponseDto dto = setVenueRespose(entity);
			return dto;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to store report page file", e);
		}
	}

	private VenueMasterResponseDto setVenueRespose(VenueMasterEntity entity) {
		VenueMasterResponseDto dto = new VenueMasterResponseDto();
		dto.setId(entity.getId());
		dto.setIsActive(entity.getIsActive());
		dto.setIsDelete(entity.getIsDelete());
		dto.setNameEnglish(entity.getNameEnglish());
		dto.setNameGujarati(entity.getNameGujarati());
		dto.setNameHindi(entity.getNameHindi());
		dto.setUserId(entity.getUser().getId());
		dto.setCreatedAt(DateUtils.formatLocalDateTime(entity.getCreatedAt()));
		dto.setVenueImg(environment.getProperty("app.image.url") + entity.getImgPath());

		return dto;
	}

	@Override
	public List<VenueMasterResponseDto> getAllVenueByUser(Long userId, String venueName, Boolean isActive) {

		List<VenueMasterResponseDto> dtos = new ArrayList<>();
		List<VenueMasterEntity> entities = new ArrayList<>();

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		if (venueName == null || venueName.trim().isEmpty()) {
			if (isActive == null) {
				entities = venueMasterRepository.findAllByUserAndIsDeleteFalse(user);
			} else {
				entities = venueMasterRepository.findAllByUserAndIsActiveAndIsDeleteFalse(user, isActive);
			}
		} else {
			if (isActive == null) {
				entities = venueMasterRepository.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(venueName,
						user);
			} else {
				entities = venueMasterRepository
						.findByNameEnglishContainingIgnoreCaseAndUserAndIsActiveAndIsDeleteFalse(venueName, user,
								isActive);
			}
		}
		if (entities.isEmpty()) {
			return Collections.emptyList();
		}
		for (VenueMasterEntity venueMasterEntity : entities) {
			VenueMasterResponseDto dto = setVenueRespose(venueMasterEntity);
			dtos.add(dto);
		}
		return dtos;
	}

	@Override
	public VenueMasterResponseDto getVenueById(Long id) {
		VenueMasterEntity entity = venueMasterRepository.findByIdAndIsDeleteFalse(id);
		return setVenueRespose(entity);
	}

	@Override
	public Boolean updateStatus(Long id, Boolean isActive) {
		Boolean isSuccess = false;

		if (venueMasterRepository.existsById(id)) {
			VenueMasterEntity entity = venueMasterRepository.findByIdAndIsDeleteFalse(id);
			entity.setIsActive(isActive);
			venueMasterRepository.save(entity);
			isSuccess = true;
		}
		return isSuccess;
	}

	@Override
	public Boolean deleteVenue(Long id) {
		Boolean isSuccess = false;

		if (venueMasterRepository.existsByIdAndIsDeleteFalse(id)) {
			VenueMasterEntity entity = venueMasterRepository.findByIdAndIsDeleteFalse(id);
			entity.setIsDelete(true);
			venueMasterRepository.save(entity);
			isSuccess = true;
		}
		return isSuccess;
	}

}
