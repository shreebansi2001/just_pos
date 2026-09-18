package com.crmportal.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.StockTypeEntity;
import com.crmportal.entity.StockTypeRightsEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.StockTypeRepository;
import com.crmportal.repository.StockTypeRightsRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.StockTypeRequestDto;
import com.crmportal.response.dto.StockTypeResponseDto;
import com.crmportal.response.dto.StockTypeRightsDTO;
import com.crmportal.service.CommonService;
import com.crmportal.service.StockTypeService;

@Service
public class StockTypeServiceImpl implements StockTypeService {

	@Autowired
	private StockTypeRepository stockTypeRepository;

	@Autowired
	private UserMasterRepository userMasterRepository;

	@Autowired
	private CommonService commonService;

	@Autowired
	StockTypeRightsRepository stockTypeRightsRepository;

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Override
	public StockTypeResponseDto addOrUpdateStockType(@Valid StockTypeRequestDto request, Long id) {

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

		Optional<StockTypeEntity> duplicate = stockTypeRepository
				.findByNameEnglishAndUserAndIsDeleteFalse(request.getNameEnglish(), user);

		if (duplicate.isPresent() && (id == -1 || !duplicate.get().getId().equals(id))) {
			throw new RuntimeException("Stock Type already exists with name: " + request.getNameEnglish());
		}

		StockTypeEntity entity;

		if (id == -1) {
			entity = new StockTypeEntity();
			entity.setCreatedAt(commonService.getCurrentDateTime());
			entity.setIsActive(true);
			entity.setIsDelete(false);
		} else {
			entity = stockTypeRepository.findByIdAndUserAndIsDeleteFalse(id, user);
			if (entity == null) {
				throw new RuntimeException("Stock Type not found with id: " + id);
			}
			entity.setUpdatedAt(commonService.getCurrentDateTime());
		}

		// Set common fields
		entity.setNameEnglish(request.getNameEnglish());
		entity.setNameHindi(request.getNameHindi());
		entity.setNameGujarati(request.getNameGujarati());
		entity.setMainType(request.getMainType());
		entity.setUser(user);

		entity = stockTypeRepository.save(entity);

		return convertToDto(entity);
	}

	@Override
	public List<StockTypeResponseDto> getAllStockTypeByUserId(Long userId, Boolean isActive, Integer mainType) {

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		List<StockTypeEntity> entities;

		entities = stockTypeRepository.findStockTypes(user, isActive, mainType);
		/*
		 * entities = (isActive == null) ?
		 * stockTypeRepository.findAllByUserAndIsDeleteFalse(user) :
		 * stockTypeRepository.findAllByUserAndIsDeleteFalseAndIsActive(user, isActive);
		 */

		if (entities.isEmpty()) {
			return Collections.emptyList();
		}

		List<StockTypeResponseDto> responseList = new ArrayList<>();

		for (StockTypeEntity entity : entities) {
			responseList.add(convertToDto(entity));
		}

		return responseList;
	}

	@Override
	public StockTypeResponseDto getStockTypeById(Long id) {

		StockTypeEntity entity = stockTypeRepository.findByIdAndIsDeleteFalse(id);

		if (entity == null) {
			return null;
		}

		return convertToDto(entity);
	}

	@Override
	public Boolean deleteStockTypeById(Long id) {

		if (stockTypeRepository.existsByIdAndIsDeleteFalse(id)) {
			StockTypeEntity entity = stockTypeRepository.findByIdAndIsDeleteFalse(id);
			entity.setIsDelete(true);
			stockTypeRepository.save(entity);
			return true;
		}

		return false;
	}

	@Override
	public boolean updateStockTypeStatus(Long id, Boolean isActive) {

		if (stockTypeRepository.existsByIdAndIsDeleteFalse(id)) {
			StockTypeEntity entity = stockTypeRepository.findByIdAndIsDeleteFalse(id);
			entity.setIsActive(isActive);
			entity.setUpdatedAt(commonService.getCurrentDateTime());
			stockTypeRepository.save(entity);
			return true;
		}

		return false;
	}

	// ================= PRIVATE CONVERTER =================

	private StockTypeResponseDto convertToDto(StockTypeEntity entity) {

		StockTypeResponseDto dto = new StockTypeResponseDto();

		dto.setId(entity.getId());
		dto.setNameEnglish(entity.getNameEnglish());
		dto.setNameHindi(entity.getNameHindi());
		dto.setNameGujarati(entity.getNameGujarati());
		dto.setIsActive(entity.getIsActive());
		dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
		dto.setMainType(entity.getMainType());
		if (entity.getCreatedAt() != null) {
			dto.setCreatedAt(entity.getCreatedAt().format(formatter));
		}

		if (entity.getUpdatedAt() != null) {
			dto.setUpdatedAt(entity.getUpdatedAt().format(formatter));
		}

		return dto;
	}

	@Override
	@Transactional
	public Boolean stockTypeRights(Long userId, List<Long> stockTypeIds) {

		if (stockTypeIds.isEmpty()) {
			return false;
		} else {
			stockTypeRightsRepository.deleteAllByUserId(userId);
			List<StockTypeRightsEntity> entities = new ArrayList<>();
			for (Long id : stockTypeIds) {
				StockTypeRightsEntity entity = new StockTypeRightsEntity();
				entity.setStockTypeId(id);
				entity.setUserId(userId);
				entities.add(entity);
			}
			stockTypeRightsRepository.saveAll(entities);
			return true;
		}

	}

	@Override
	public List<StockTypeRightsDTO> getAllStockTypeRights(Long userId) {

		List<Object[]> results = stockTypeRightsRepository.getAllStockTypeRights(userId);

		return results.stream().map(row -> new StockTypeRightsDTO(((Number) row[0]).longValue(), (String) row[1]))
				.collect(Collectors.toList());
	}
}