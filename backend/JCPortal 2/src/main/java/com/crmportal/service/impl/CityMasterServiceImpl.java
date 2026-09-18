package com.crmportal.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.CityMasterEntity;
import com.crmportal.entity.StateMasterEntity;
import com.crmportal.mapper.CityMasterMapper;
import com.crmportal.repository.CityMasterRepository;
import com.crmportal.repository.StateMasterRepository;
import com.crmportal.request.dto.CityMasterRequestDto;
import com.crmportal.response.dto.CityMasterResponseDto;
import com.crmportal.response.dto.StateMasterResponseDto;
import com.crmportal.service.CityMasterService;
import com.crmportal.service.CommonService;

@Service
public class CityMasterServiceImpl implements CityMasterService {

	@Autowired
	CityMasterRepository cityMasterRepository;

	@Autowired
	CityMasterMapper cityMasterMapper;

	@Autowired
	CommonService commonService;

	@Autowired
	StateMasterRepository stateMasterRepository;

	@Override
	public CityMasterResponseDto addOrUpdateCityMaster(@Valid CityMasterRequestDto request, long id) {
		CityMasterEntity entity = null;
		Optional<CityMasterEntity> existingName = cityMasterRepository
				.findByNameAndStateIdAndIsDeleteFalse(request.getName(), request.getStateId());
		StateMasterEntity state = stateMasterRepository.findByIdAndIsDeleteFalse(request.getStateId())
				.orElseThrow(() -> new RuntimeException("State not found with id: " + request.getStateId()));

		if (id == -1) {
			if (existingName.isPresent()) {
				throw new RuntimeException("City with the name '" + request.getName() + "' already exists.");
			}
			entity = cityMasterMapper.requestToEntity(request);
			entity.setState(state);
		} else {
			Optional<CityMasterEntity> entities = cityMasterRepository.findByIdAndIsDeleteFalse(id);
			if (!entities.isPresent()) {
				throw new RuntimeException("City not found with id: " + id);
			} else {
				entity = entities.get();
				if (!entity.getName().equalsIgnoreCase(request.getName()) && existingName.isPresent()) {
					throw new RuntimeException("City with the name '" + request.getName() + "' already exists.");
				}
				entity.setUpdatedAt(commonService.getCurrentDateTime());
				entity.setName(request.getName());
				entity.setState(state);
			}
		}
		entity = cityMasterRepository.save(entity);
		CityMasterResponseDto responseDto = cityMasterMapper.entityToResponse(entity);
		return responseDto;
	}

	@Override
	public List<CityMasterResponseDto> getAllCityMaster() {
		List<CityMasterResponseDto> cityMasterResponseDtos = new ArrayList<>();
		List<CityMasterEntity> entities = cityMasterRepository.findAllByIsDeleteFalse();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		if (entities.isEmpty()) {
			return Collections.emptyList();
		} else {
			for (CityMasterEntity cityMasterEntity : entities) {
				CityMasterResponseDto responseDto = cityMasterMapper.entityToResponse(cityMasterEntity);
				responseDto.setCreatedAt(cityMasterEntity.getCreatedAt().format(formatter));
				responseDto.getState().setCreatedAt(cityMasterEntity.getState().getCreatedAt().format(formatter));
				cityMasterResponseDtos.add(responseDto);
			}
			return cityMasterResponseDtos;
		}
	}

	@Override
	public CityMasterResponseDto getCityMasterById(Long id) {
		Optional<CityMasterEntity> entities = cityMasterRepository.findByIdAndIsDeleteFalse(id);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		if (!entities.isPresent()) {
			return null;
		} else {
			CityMasterEntity cityMasterEntity = entities.get();
			CityMasterResponseDto responseDto = cityMasterMapper.entityToResponse(cityMasterEntity);
			responseDto.setCreatedAt(cityMasterEntity.getCreatedAt().format(formatter));
			responseDto.getState().setCreatedAt(cityMasterEntity.getState().getCreatedAt().format(formatter));
			return responseDto;
		}
	}

	@Override
	public List<CityMasterResponseDto> getCityMasterByStateId(Long stateId, String cityName) {
	    List<CityMasterEntity> cityEntities = new ArrayList<>();

	    if (stateId == null && (cityName == null || cityName.trim().isEmpty())) {
	        return Collections.emptyList();
	    }

	    if (stateId != null && (cityName == null || cityName.trim().isEmpty())) {
	        StateMasterEntity state = stateMasterRepository.findByIdAndIsDeleteFalse(stateId)
	                .orElseThrow(() -> new RuntimeException("State not found with id: " + stateId));
	        cityEntities = cityMasterRepository.findByStateAndIsDeleteFalse(state);

	    } else if (cityName != null && stateId == null) {
	        cityEntities = cityMasterRepository.findByNameContainingIgnoreCaseAndIsDeleteFalse(cityName);

	    } else if (stateId != null && cityName != null && !cityName.trim().isEmpty()) {
	        StateMasterEntity state = stateMasterRepository.findByIdAndIsDeleteFalse(stateId)
	                .orElseThrow(() -> new RuntimeException("State not found with id: " + stateId));
	        cityEntities = cityMasterRepository.findByNameContainingIgnoreCaseAndStateAndIsDeleteFalse(cityName,state);
	    }

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    List<CityMasterResponseDto> responseDtos = new ArrayList<>();
	    for (CityMasterEntity city : cityEntities) {
	        CityMasterResponseDto dto = cityMasterMapper.entityToResponse(city);

	        if (city.getCreatedAt() != null) {
	            dto.setCreatedAt(city.getCreatedAt().format(formatter));
	        }
	        if (city.getState() != null && city.getState().getCreatedAt() != null) {
	            dto.getState().setCreatedAt(city.getState().getCreatedAt().format(formatter));
	        }

	        responseDtos.add(dto);
	    }

	    return responseDtos;
	}

	@Override
	public List<CityMasterResponseDto> getCityNameWithSearch(String cityName) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		if (cityName == null || cityName.trim().equals("")) {
			return Collections.emptyList();
		}
		List<CityMasterEntity> cityMasterEntities = cityMasterRepository.findByNameContainingIgnoreCaseAndIsDeleteFalse(cityName);
		List<CityMasterResponseDto> cityMasterResponseDtos = new ArrayList<>();

		if (cityMasterEntities.isEmpty()) {
			return Collections.emptyList();
		} else {
			for (CityMasterEntity cityMasterEntity : cityMasterEntities) {
				CityMasterResponseDto responseDto = cityMasterMapper.entityToResponse(cityMasterEntity);

				if (cityMasterEntity.getCreatedAt() != null) {
					responseDto.setCreatedAt(cityMasterEntity.getCreatedAt().format(formatter));
				}

				cityMasterResponseDtos.add(responseDto);
			}
			return cityMasterResponseDtos;
		}

	}

	@Override
	public Boolean deleteCityMasterById(Long id) {
		if (!cityMasterRepository.existsByIdAndIsDeleteFalse(id)) {
			return false;
		}

		Optional<CityMasterEntity> optionalEntity = cityMasterRepository.findByIdAndIsDeleteFalse(id);
		if (!optionalEntity.isPresent()) {
			return false;
		}

		CityMasterEntity cityMasterEntity = optionalEntity.get();
		cityMasterEntity.setIsDelete(true);
		cityMasterRepository.save(cityMasterEntity);
		return true;
	}

}
