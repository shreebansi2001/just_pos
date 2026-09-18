package com.crmportal.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold.RangeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.RoleMasterEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UnitRangeEntity;
import com.crmportal.entity.UnitStepwiseRangeEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.ERangeType;
import com.crmportal.mapper.UnitMasterMapper;
import com.crmportal.repository.UnitMasterRepository;
import com.crmportal.repository.UnitRangeRepository;
import com.crmportal.repository.UnitStepwiseRangeRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.UnitMasterRequestDto;
import com.crmportal.request.dto.UnitRangeRequestDto;
import com.crmportal.response.dto.ParentUnitResponseDto;
import com.crmportal.response.dto.RoleMasterResponseDto;
import com.crmportal.response.dto.UnitChildDto;
import com.crmportal.response.dto.UnitHierarchyDto;
import com.crmportal.response.dto.UnitMasterResponseDto;
import com.crmportal.response.dto.UnitRangeResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.DatabasePlanningService;
import com.crmportal.service.UnitMasterService;

@Service
public class UnitMasterServiceImpl implements UnitMasterService {

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	UnitMasterRepository unitMasterRepository;

	@Autowired
	UnitMasterMapper unitMasterMapper;

	@Autowired
	CommonService commonService;

	@Autowired
	UnitRangeRepository unitRangeRepository;

	@Autowired
	UnitStepwiseRangeRepository stepwiseRangeRepository;

	@Autowired
	DatabasePlanningService databasePlanningService;

	@Override
	public UnitMasterResponseDto addOrUpdateUnitMaster(@Valid UnitMasterRequestDto request, long id) {
		UnitMasterEntity entity = null;
		Optional<UserMasterEntity> userOptional = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId());
		if (!userOptional.isPresent()) {
			throw new RuntimeException("User not found with id: " + request.getUserId());
		}
		UserMasterEntity user = userOptional.get();

		Optional<UnitMasterEntity> existing = unitMasterRepository
				.findByNameEnglishAndUserAndIsDeleteFalse(request.getNameEnglish(), user);
		if (id == -1) {
			if (existing.isPresent()) {
				throw new RuntimeException("Unit with the name '" + request.getNameEnglish() + "' already exists.");
			}
			entity = unitMasterMapper.requestToEntity(request);
			entity.setUuid(databasePlanningService.getOrCreateUserUuid(user));
		} else {
			Optional<UnitMasterEntity> unitMaster = unitMasterRepository.findByIdAndIsDeleteFalse(id);
			if (!unitMaster.isPresent()) {
				throw new RuntimeException("Unit not found with id: " + id);
			} else {
				entity = unitMaster.get();
				if (!entity.getNameEnglish().equalsIgnoreCase(request.getNameEnglish()) && existing.isPresent()) {
					throw new RuntimeException("Unit with the name '" + request.getNameEnglish() + "' already exists.");
				}
				entity = unitMasterMapper.updateEntityFromRequest(request, entity);
				entity.setUpdatedAt(commonService.getCurrentDateTime());
			}
		}
		if (request.getParent_unit_id() == null || request.getParent_unit_id() == 0) {
			entity.setParentUnit(null);
		} else {
			Optional<UnitMasterEntity> unitMaster = unitMasterRepository
					.findByIdAndIsDeleteFalse(request.getParent_unit_id());
			if (!unitMaster.isPresent()) {
				throw new RuntimeException("Parent Unit not found with id: " + id);
			} else {
				UnitMasterEntity unitMasterEntity = unitMaster.get();
				entity.setParentUnit(unitMasterEntity);
			}
		}
		entity.setUser(user);

		entity = unitMasterRepository.save(entity);
		List<UnitRangeEntity> unitRangeEntities = unitRangeRepository.findAllByUnitAndIsDeleteFalse(entity);
		if (!unitRangeEntities.isEmpty()) {
			unitRangeRepository.deleteAll(unitRangeEntities);
		}

		UnitStepwiseRangeEntity stepwiseRangeEntities = stepwiseRangeRepository.findByUnitAndIsDeleteFalse(entity);
		if (stepwiseRangeEntities != null) {
			stepwiseRangeRepository.delete(stepwiseRangeRepository.findByUnitAndIsDeleteFalse(entity));
		}

		saveRanges(entity, request);

		UnitMasterResponseDto responseDto = unitMasterMapper.entityToResponse(entity);
		return responseDto;
	}

	private void saveRanges(UnitMasterEntity entity, @Valid UnitMasterRequestDto request) {
		// RANGE or PRECISION RANGE
		if (entity.getRangeType() == ERangeType.RANGE || entity.getRangeType() == ERangeType.PRECISION) {

			for (UnitRangeRequestDto r : request.getRanges()) {
				UnitRangeEntity ur = new UnitRangeEntity();
				ur.setUnit(entity);
				ur.setRangeType(entity.getRangeType());
				ur.setMinValue(r.getMinValue());
				ur.setMaxValue(r.getMaxValue());
				ur.setUuid(entity.getUuid());
				ur.setRoundOffValue(r.getRoundOffValue());
				unitRangeRepository.save(ur);
			}
		}
		// STEP WISE RANGE
		else if (entity.getRangeType() == ERangeType.STEPWISE) {

			UnitStepwiseRangeEntity sw = new UnitStepwiseRangeEntity();
			sw.setUnit(entity);
			sw.setStepValue(request.getStepValue());
			sw.setUuid(entity.getUuid());
			stepwiseRangeRepository.save(sw);
		}
	}

	@Override
	public List<UnitMasterResponseDto> getAllByUserId(Long userId, String unitName, Boolean isActive) {

		Optional<UserMasterEntity> userOpt = userMasterRepository.findByIdAndIsDeleteFalse(userId);
		if (!userOpt.isPresent()) {
			return Collections.emptyList();
		}
		UserMasterEntity user = userOpt.get();

		List<UnitMasterEntity> entities = fetchUnits(user, unitName, isActive);

		if (entities.isEmpty()) {
			return Collections.emptyList();
		}

		return entities.stream().map(entity -> buildUnitResponse(entity, userId)).collect(Collectors.toList());
	}

	@Override
	public UnitMasterResponseDto getById(Long id) {
		Optional<UnitMasterEntity> entityOp = unitMasterRepository.findByIdAndIsDeleteFalse(id);

		return entityOp.map(entity -> buildUnitResponse(entity, entity.getUser().getId())).orElse(null);
	}

	private List<UnitMasterEntity> fetchUnits(UserMasterEntity user, String name, Boolean isActive) {

		boolean emptyName = (name == null || name.trim().isEmpty());

		if (emptyName && isActive == null) {
			return unitMasterRepository.findAllByUserAndIsDeleteFalse(user);
		}
		if (emptyName) {
			return unitMasterRepository.findByUserAndIsActiveAndIsDeleteFalse(user, isActive);
		}
		if (isActive == null) {
			return unitMasterRepository.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(name, user);
		}

		return unitMasterRepository.findByNameEnglishContainingIgnoreCaseAndUserAndIsActiveAndIsDeleteFalse(name, user,
				isActive);
	}

	private UnitMasterResponseDto buildUnitResponse(UnitMasterEntity entity, Long userId) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		UnitMasterResponseDto dto = unitMasterMapper.entityToResponse(entity);

		if (entity.getParentUnit() == null) {
			dto.setParentUnit(null);
		} else {
			Optional<UnitMasterEntity> unitMaster = unitMasterRepository
					.findByIdAndIsDeleteFalse(entity.getParentUnit().getId());
			if (!unitMaster.isPresent()) {
				throw new RuntimeException("Parent Unit not found with id: " + entity.getParentUnit().getId());
			} else {
				UnitMasterEntity unitMasterEntity = unitMaster.get();
				ParentUnitResponseDto unitResponseDto = unitMasterMapper.entityToNewResponse(unitMasterEntity);
				dto.setParentUnit(unitResponseDto);
			}
		}

		if (entity.getCreatedAt() != null) {
			dto.setCreatedAt(entity.getCreatedAt().format(formatter));
		}

		dto.setUserId(userId);
		dto.setRangeType(entity.getRangeType());

		if (entity.getRangeType() != null) {
			switch (entity.getRangeType()) {
			case STEPWISE:
				setStepwiseRange(entity, dto);
				break;
			case RANGE:
			case PRECISION:
				setRanges(entity, dto);
				break;
			default:
				break;
			}
		}
		return dto;
	}

	private void setStepwiseRange(UnitMasterEntity entity, UnitMasterResponseDto dto) {
		UnitStepwiseRangeEntity step = stepwiseRangeRepository.findByUnitAndIsDeleteFalse(entity);
		if (step != null) {
			dto.setStepValue(step.getStepValue());
			dto.setStepRangeId(step.getId());
		}
	}

	private void setRanges(UnitMasterEntity entity, UnitMasterResponseDto dto) {

		List<UnitRangeEntity> ranges = unitRangeRepository.findAllByUnitAndIsDeleteFalse(entity);

		List<UnitRangeResponseDto> responseList = ranges.stream().map(range -> {
			UnitRangeResponseDto r = new UnitRangeResponseDto();
			r.setId(range.getId());
			r.setMaxValue(range.getMaxValue());
			r.setMinValue(range.getMinValue());
			r.setRoundOffValue(range.getRoundOffValue());
			return r;
		}).collect(Collectors.toList());

		dto.setRanges(responseList);
	}

	@Override
	public Boolean deleteById(Long id) {
		if (unitMasterRepository.existsByIdAndIsDeleteFalse(id)) {
			Optional<UnitMasterEntity> entity = unitMasterRepository.findByIdAndIsDeleteFalse(id);
			UnitMasterEntity masterEntity = entity.get();
			masterEntity.setIsDelete(true);
			unitMasterRepository.save(masterEntity);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Boolean updateStatusById(Long id, Boolean isActive) {
		if (unitMasterRepository.existsByIdAndIsDeleteFalse(id)) {
			Optional<UnitMasterEntity> entity = unitMasterRepository.findByIdAndIsDeleteFalse(id);
			UnitMasterEntity masterEntity = entity.get();
			masterEntity.setIsActive(isActive);
			unitMasterRepository.save(masterEntity);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public UnitHierarchyDto getParentUnitsWithChildren(Long unitId) {

		List<UnitMasterEntity> result = unitMasterRepository.findParentWithChildren(unitId);

		if (result.isEmpty()) {
			return null;
		}

		// Find the requested unit
		UnitMasterEntity requestedUnit = result.stream().filter(u -> u.getId().equals(unitId)).findFirst().orElse(null);

		if (requestedUnit == null) {
			return null;
		}

		// CASE 1: requested unit is parent
		if (Boolean.TRUE.equals(requestedUnit.getIsParentUnit())) {
			return mapParentWithChildren(requestedUnit);
		}

		// CASE 2: requested unit has a parent
		if (requestedUnit.getParentUnit() != null) {
			return mapParentWithChildren(requestedUnit.getParentUnit());
		}

		// CASE 3: standalone unit
		return mapUnitOnly(requestedUnit);
	}
	
	
	private UnitHierarchyDto mapParentWithChildren(UnitMasterEntity parent) {

	    UnitHierarchyDto dto = new UnitHierarchyDto(
	            parent.getId(),
	            parent.getNameEnglish(),
	            parent.getNameHindi(),
	            parent.getNameGujarati(),
	            parent.getSymbolEnglish(),
	            parent.getSymbolHindi(),
	            parent.getSymbolGujarati(),
	            parent.getChildren().stream()
	                    .filter(c -> Boolean.FALSE.equals(c.getIsDelete())
	                            && Boolean.TRUE.equals(c.getIsActive()))
	                    .map(c -> new UnitChildDto(
	                            c.getId(),
	                            c.getNameEnglish(),
	                            c.getNameHindi(),
	                            c.getNameGujarati(),
	                            c.getSymbolEnglish(),
	                            c.getSymbolHindi(),
	                            c.getSymbolGujarati(),
	                            c.getEquivalentValue()
	                    ))
	                    .collect(java.util.stream.Collectors.toList())
	    );

	    return dto;
	}


	
	private UnitHierarchyDto mapUnitOnly(UnitMasterEntity unit) {

	    return new UnitHierarchyDto(
	            unit.getId(),
	            unit.getNameEnglish(),
	            unit.getNameHindi(),
	            unit.getNameGujarati(),
	            unit.getSymbolEnglish(),
	            unit.getSymbolHindi(),
	            unit.getSymbolGujarati(),
	            Collections.emptyList()
	    );
	}


}
