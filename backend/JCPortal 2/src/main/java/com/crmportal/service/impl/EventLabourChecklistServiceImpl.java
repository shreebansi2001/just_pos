package com.crmportal.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.ContactCategoryMasterEntity;
import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventLabourChecklistEntity;
import com.crmportal.entity.EventLabourChecklistPhotosEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.ShiftEntity;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.repository.ContactCategoryMasterRepository;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventLabourCheckListImagesRepository;
import com.crmportal.repository.EventLabourCheckListRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.ShiftRepository;
import com.crmportal.request.dto.EventLabourCheckListImagesRequestDto;
import com.crmportal.request.dto.EventLabourCheckListMainRequestDto;
import com.crmportal.request.dto.EventLabourCheckListRequestDto;
import com.crmportal.response.dto.EventLabourCheckListDataResponse;
import com.crmportal.response.dto.EventLabourCheckListImagesResponseDto;
import com.crmportal.response.dto.EventLabourCheckListResponseDto;
import com.crmportal.service.EventLabourChecklistService;
import com.crmportal.service.UserFileService;

@Service
public class EventLabourChecklistServiceImpl implements EventLabourChecklistService {

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	EventFunctionMasterRepository eventFunctionMasterRepository;

	@Autowired
	ContactCategoryMasterRepository categoryMasterRepository;

	@Autowired
	PartyMasterRepository partyMasterRepository;

	@Autowired
	ShiftRepository shiftRepository;

	@Autowired
	EventLabourCheckListImagesRepository eventLabourCheckListImagesRepository;

	@Autowired
	EventLabourCheckListRepository eventLabourCheckListRepository;

	@Autowired
	UserFileService userFileService;

	@Autowired
	Environment environment;

	private static final DateTimeFormatter CREATE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a",
			Locale.ENGLISH);

	@Override
	@Transactional
	public Boolean addOrUpdate(EventLabourCheckListMainRequestDto request, Long userId) {

		if (request == null) {
			return false;
		}
		if (request.getLabourCheckList() == null || request.getLabourCheckList().isEmpty()) {
			return false;
		}
		for (EventLabourCheckListRequestDto req : request.getLabourCheckList()) {

			EventMasterEntity eventMaster = eventMasterRepository.findByIdAndIsDeleteFalse(req.getEventId())
					.orElseThrow(() -> new RuntimeException("Event not found: " + req.getEventId()));

			EventFunctionMasterEntity functionEntity = eventFunctionMasterRepository
					.findByIdAndIsDeleteFalse(req.getEventFunctionId())
					.orElseThrow(() -> new RuntimeException("Function not found: " + req.getEventFunctionId()));

			ContactCategoryMasterEntity categoryMaster = categoryMasterRepository
					.findByIdAndIsDeleteFalse(req.getContactCatId())
					.orElseThrow(() -> new RuntimeException("Category not found: " + req.getContactCatId()));

			PartyMasterEntity partyMaster = partyMasterRepository.findByIdAndIsDeleteFalse(req.getVendorId())
					.orElseThrow(() -> new RuntimeException("Vendor not found: " + req.getVendorId()));

			ShiftEntity shiftEntity = shiftRepository.findByIdAndIsDeleteFalse(req.getShiftId())
					.orElseThrow(() -> new RuntimeException("Shift not found: " + req.getShiftId()));

			EventLabourChecklistEntity entity = (req.getId() == null || req.getId() == -1 || req.getId() == 0)
					? new EventLabourChecklistEntity()
					: eventLabourCheckListRepository.findByIdAndIsDeleteFalse(req.getId())
							.orElseThrow(() -> new RuntimeException("Checklist not found: " + req.getId()));

			entity.setContact(partyMaster);
			entity.setContactCategory(categoryMaster);
			entity.setEvent(eventMaster);
			entity.setEventFunction(functionEntity);
			entity.setInQty(req.getInQty());
			entity.setInTime(req.getInTime());
			entity.setIsDelete(false);
			entity.setIsStatus(req.getIsStatus());

			if (req.getLabordatetime() != null && !req.getLabordatetime().isEmpty()) {
				entity.setLabordatetime(LocalDateTime.parse(req.getLabordatetime(), CREATE_DATE_FORMATTER));
			}

			entity.setOutTime(req.getOutTime());
			entity.setShiftId(shiftEntity.getId());
			entity.setTotalQty(req.getTotalQty());
			entity.setIsEditable(req.getIsEditable());
			entity.setUserId(userId);

			EventLabourChecklistEntity savedEntity = eventLabourCheckListRepository.save(entity);

			if (req.getFiles() != null && !req.getFiles().isEmpty()) {
				for (EventLabourCheckListImagesRequestDto file : req.getFiles()) {
					try {
						userFileService.storeFile(savedEntity.getId(), ModuleName.CHECKLIST.toString(), file.getId(),
								FileType.LABOURFILE.toString(), file.getFile());
					} catch (IOException e) {
						throw new RuntimeException("File upload failed", e);
					}
				}
			}
		}

		return true;
	}

	@Override
	public List<EventLabourCheckListResponseDto> getAllChecklist(Long eventId, Long eventFunctionId, Long userId) {

		List<Object[]> checklistData = eventLabourCheckListRepository.getChecklistData(eventId, eventFunctionId,
				userId);

		List<Object[]> laborData = eventLabourCheckListRepository.getEventLaborData(eventId, eventFunctionId, userId);

		Map<String, Object[]> existingKeys = new HashMap<>();

		for (Object[] row : checklistData) {
			String key = buildKey(row[3], row[5], row[14]);
			existingKeys.put(key, row);
		}

		List<Object[]> merged = new ArrayList<>(checklistData);

		for (Object[] row : laborData) {

			String key = buildKey(row[2], row[4], row[9]);

			if (!existingKeys.containsKey(key)) {

				Object[] newRow = new Object[17];

				newRow[0] = null;
				newRow[1] = row[0];
				newRow[2] = row[1];
				newRow[3] = row[2];
				newRow[4] = row[3];
				newRow[5] = row[4];
				newRow[6] = row[5];
				newRow[7] = row[6];
				newRow[8] = row[7];
				newRow[9] = row[8];
				newRow[10] = 0;
				newRow[11] = toBoolean(false);
				newRow[12] = "";
				newRow[13] = "";
				newRow[14] = row[9];
				newRow[15] = toBoolean(true);
				newRow[16] = 0;

				merged.add(newRow);
			}
		}

		List<Long> ids = new ArrayList<>();
		for (Object[] row : merged) {
			if (row[0] != null) {
				ids.add(((Number) row[0]).longValue());
			}
		}

		Map<Long, List<EventLabourCheckListImagesResponseDto>> imageMap = new HashMap<>();

		if (!ids.isEmpty()) {
			List<EventLabourChecklistPhotosEntity> images = eventLabourCheckListImagesRepository.findImages(ids);

			for (EventLabourChecklistPhotosEntity img : images) {
				imageMap.computeIfAbsent(img.getEventLabourCheckListId(), k -> new ArrayList<>())
						.add(new EventLabourCheckListImagesResponseDto(img.getId(), buildImageUrl(img.getImagePath())));
			}
		}

		Map<String, List<EventLabourCheckListDataResponse>> shiftMap = new LinkedHashMap<>();

		for (Object[] row : merged) {

			Long checklistId = row[0] != null ? ((Number) row[0]).longValue() : null;

			String formattedDateTime = formatDate(row[14]);

			EventLabourCheckListDataResponse dto = new EventLabourCheckListDataResponse(checklistId,
					row[1] == null ? null : ((Number) row[1]).longValue(), ((Number) row[2]).longValue(),
					row[3] != null ? ((Number) row[3]).longValue() : null, (String) row[4],
					row[5] != null ? ((Number) row[5]).longValue() : null, (String) row[6],
					row[7] != null ? ((Number) row[7]).longValue() : null, (String) row[8],
					row[9] != null ? ((Number) row[9]).intValue() : 0,
					row[10] != null ? ((Number) row[10]).intValue() : 0,
							toBoolean(row[11]),
					(String) row[12], (String) row[13], formattedDateTime,
					toBoolean(row[15]),
					checklistId != null ? imageMap.getOrDefault(checklistId, Collections.emptyList())
							: Collections.emptyList());

			String shiftName = (String) row[8];

			shiftMap.computeIfAbsent(shiftName, k -> new ArrayList<>()).add(dto);
		}

		List<EventLabourCheckListResponseDto> response = new ArrayList<>();

		for (Map.Entry<String, List<EventLabourCheckListDataResponse>> entry : shiftMap.entrySet()) {
			response.add(new EventLabourCheckListResponseDto(entry.getKey(), entry.getValue()));
		}

		return response;
	}

	private boolean toBoolean(Object value) {
	    if (value == null) return false;

	    if (value instanceof Boolean) {
	        return (Boolean) value;
	    }

	    if (value instanceof Number) {
	        return ((Number) value).intValue() == 1;
	    }

	    return false;
	}
	
	private String buildKey(Object categoryId, Object partyId, Object dateTime) {
		return (categoryId == null ? "0" : categoryId.toString()) + "_" + (partyId == null ? "0" : partyId.toString())
				+ "_" + (dateTime == null ? "0" : dateTime.toString());
	}

	private String buildImageUrl(String imagePath) {
		if (imagePath == null || imagePath.isEmpty()) {
			return "";
		}
		return environment.getProperty("app.image.url") + imagePath;
	}

	private String formatDate(Object dateObj) {
		if (dateObj == null)
			return null;

		if (dateObj instanceof java.sql.Timestamp) {
			return ((java.sql.Timestamp) dateObj).toLocalDateTime().format(CREATE_DATE_FORMATTER);
		}

		if (dateObj instanceof java.time.LocalDateTime) {
			return ((java.time.LocalDateTime) dateObj).format(CREATE_DATE_FORMATTER);
		}

		return dateObj.toString();
	}
}
