package com.crmportal.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.SpecialNotesEntity;
import com.crmportal.entity.SpecialNotesImagesEntity;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.enums.PriorityType;
import com.crmportal.enums.StatusType;
import com.crmportal.repository.SpecialNotesImagesRepository;
import com.crmportal.repository.SpecialNotesRepository;
import com.crmportal.request.dto.SpecialNotesImagesRequestDto;
import com.crmportal.request.dto.SpecialNotesRequestDto;
import com.crmportal.response.dto.SpecialNotesImagesResponseDto;
import com.crmportal.response.dto.SpecialNotesResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.SpecialNotesService;
import com.crmportal.service.UserFileService;

@Service
public class SpecialNotesServiceImpl implements SpecialNotesService {

	@Autowired
	SpecialNotesRepository specialNotesRepository;

	@Autowired
	SpecialNotesImagesRepository specialNotesImagesRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	UserFileService userFileService;

	@Autowired
	Environment environment;

	@Override
	public Boolean addOrUpdate(SpecialNotesRequestDto request) {

		SpecialNotesEntity specialNotesEntity = null;
		if (request.getId() != null && request.getId() != -1) {
			specialNotesEntity = specialNotesRepository.findByIdAndIsDeleteFalse(request.getId())
					.orElseThrow(() -> new RuntimeException("Special Notes not found with id: " + request.getId()));
			specialNotesEntity.setStatus(StatusType.valueOf(request.getStatus()));
			specialNotesEntity.setUpdatedAt(commonService.getCurrentDateTime());
		} else {
			specialNotesEntity = new SpecialNotesEntity();
			specialNotesEntity.setStatus(StatusType.PENDING);
			specialNotesEntity.setCreatedAt(commonService.getCurrentDateTime());
		}
		specialNotesEntity.setDescription(request.getDescription());
		specialNotesEntity.setEventFunctionId(request.getEventFunctionId());
		specialNotesEntity.setEventId(request.getEventId());
		specialNotesEntity.setManagerId(request.getManagerId());
		specialNotesEntity.setName(request.getName());
		specialNotesEntity.setPriority(PriorityType.valueOf(request.getPriority()));
		specialNotesEntity.setRemarks(request.getRemarks());
		specialNotesEntity.setSequence(request.getSequence());
		specialNotesEntity.setUserId(request.getUserId());
		SpecialNotesEntity updatedEntity = specialNotesRepository.save(specialNotesEntity);
		saveSpecialNotesImages(request.getFiles(), updatedEntity);
		return true;

	}

	private void saveSpecialNotesImages(List<SpecialNotesImagesRequestDto> files, SpecialNotesEntity updatedEntity) {

		if (files == null || files.isEmpty())
			return;
		SpecialNotesImagesEntity uploadEntity = null;
		for (SpecialNotesImagesRequestDto file : files) {
			if (file.getId() == -1 || file.getId() == 0) {
				uploadEntity = new SpecialNotesImagesEntity();
			} else {
				uploadEntity = specialNotesImagesRepository.findById(file.getId())
						.orElseThrow(() -> new RuntimeException("Image Not Found"));
			}
			uploadEntity.setIsDelete(false);
			uploadEntity.setSpecialNotesId(updatedEntity.getId());
			uploadEntity = specialNotesImagesRepository.save(uploadEntity);
			if (file.getFile() != null && !file.getFile().isEmpty()) {
				try {
					userFileService.storeFile(updatedEntity.getUserId(), ModuleName.SPECIALNOTES.toString(),
							uploadEntity.getId(), FileType.IMAGE.toString(), file.getFile());
				} catch (IOException e) {

					throw new RuntimeException("Failed to store image", e);
				}
			}
		}
	}

	@Override
	public List<SpecialNotesResponseDto> getSpecialNotesByEventFunction(Long eventFunctionId, Long managerId,
			Long userId) {

		List<SpecialNotesEntity> entities = specialNotesRepository.getAllSpecialNotes(eventFunctionId, managerId,
				userId);
		List<SpecialNotesResponseDto> dtos = new ArrayList<>();
		for (SpecialNotesEntity specialNotesEntity : entities) {
			SpecialNotesResponseDto dto = mapEntityToDto(specialNotesEntity);
			dtos.add(dto);
		}
		return dtos;
	}

	private SpecialNotesResponseDto mapEntityToDto(SpecialNotesEntity specialNotesEntity) {

		SpecialNotesResponseDto dto = new SpecialNotesResponseDto();
		dto.setDescription(specialNotesEntity.getDescription());
		dto.setEventFunctionId(specialNotesEntity.getEventFunctionId());
		dto.setEventId(specialNotesEntity.getEventId());
		dto.setId(specialNotesEntity.getId());
		dto.setManagerId(specialNotesEntity.getManagerId());
		dto.setName(specialNotesEntity.getName());
		dto.setPriority(specialNotesEntity.getPriority().toString());
		dto.setRemarks(specialNotesEntity.getRemarks());
		dto.setSequence(specialNotesEntity.getSequence());
		dto.setStatus(specialNotesEntity.getStatus().toString());
		dto.setUserId(specialNotesEntity.getUserId());

		List<SpecialNotesImagesEntity> entities = specialNotesImagesRepository
				.findAllBySpecialNotesIdAndIsDeleteFalse(specialNotesEntity.getId());
		if (entities.isEmpty()) {
			dto.setFiles(new ArrayList<>());
		} else {
			List<SpecialNotesImagesResponseDto> files = new ArrayList<>();
			for (SpecialNotesImagesEntity specialNotesImagesEntity : entities) {
				SpecialNotesImagesResponseDto images = new SpecialNotesImagesResponseDto();
				images.setId(specialNotesImagesEntity.getId());
				images.setImagePath(environment.getProperty("app.image.url") + specialNotesImagesEntity.getImagePath());
				files.add(images);
			}
			dto.setFiles(files);
		}
		return dto;
	}

	@Override
	public Boolean deleteSpecialNotesImage(Long id) {

		if (specialNotesImagesRepository.existsById(id)) {
			specialNotesImagesRepository.deleteById(id);
		}

		return false;

	}

}
