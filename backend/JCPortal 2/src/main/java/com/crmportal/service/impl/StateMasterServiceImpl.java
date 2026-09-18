package com.crmportal.service.impl;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.CountryMasterEntity;
import com.crmportal.entity.StateMasterEntity;
import com.crmportal.mapper.StateMasterMappper;
import com.crmportal.repository.CountryMasterRepository;
import com.crmportal.repository.StateMasterRepository;
import com.crmportal.request.dto.StateMasterRequestDto;
import com.crmportal.response.dto.StateMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.StateMasterService;

@Service
public class StateMasterServiceImpl implements StateMasterService {

	@Autowired
	StateMasterMappper stateMasterMappper;

	@Autowired
	StateMasterRepository stateMasterRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	CountryMasterRepository countryMasterRepository;
	
	@Override
	public StateMasterResponseDto addOrUpdateStateMaster(StateMasterRequestDto request, long id) {
		StateMasterEntity entity = null;
		Optional<StateMasterEntity> existingName = stateMasterRepository.findByNameAndIsDeleteFalse(request.getName());
		CountryMasterEntity country = countryMasterRepository.findByIdAndIsDeleteFalse(request.getCountryId())
				.orElseThrow(() -> new RuntimeException("Country not found with id: " + request.getCountryId()));
		if (id == -1) {
			if (existingName.isPresent()) {
				throw new RuntimeException("State with the name '" + request.getName() + "' already exists.");
			}
			entity = stateMasterMappper.requestToEntity(request);
			entity.setCountry(country);
		} else {
			Optional<StateMasterEntity> stateMasterEntity = stateMasterRepository.findByIdAndIsDeleteFalse(id);
			if (!stateMasterEntity.isPresent()) {
				throw new RuntimeException("State not found with id: " + id);
			} else {
				entity = stateMasterEntity.get();
				if (!entity.getName().equalsIgnoreCase(request.getName()) && existingName.isPresent()) {
					throw new RuntimeException("State with the name '" + request.getName() + "' already exists.");
				} else {
					entity.setName(request.getName());
					entity.setUpdatedAt(commonService.getCurrentDateTime());
					entity.setCountry(country);
				}
			}
		}
		entity = stateMasterRepository.save(entity);
		StateMasterResponseDto responseDto = stateMasterMappper.entityToResponse(entity);
		return responseDto;
	}

	@Override
	public List<StateMasterResponseDto> getAllStateMaster(String stateName) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    List<StateMasterEntity> stateMasterEntities = new ArrayList<>();

	    if (stateName == null || stateName.trim().isEmpty()) {
	        stateMasterEntities = stateMasterRepository.findAllByIsDeleteFalse();
	    } else {
	        stateMasterEntities = stateMasterRepository.findByNameContainingIgnoreCaseAndIsDeleteFalse(stateName);
	    }

	    if (stateMasterEntities.isEmpty()) {
	        return Collections.emptyList();
	    }

	    List<StateMasterResponseDto> stateMasterResponseDtos = new ArrayList<>();
	    for (StateMasterEntity stateMasterEntity : stateMasterEntities) {
	        StateMasterResponseDto responseDto = stateMasterMappper.entityToResponse(stateMasterEntity);

	        if (stateMasterEntity.getCreatedAt() != null) {
	            responseDto.setCreatedAt(stateMasterEntity.getCreatedAt().format(formatter));
	        }
	        if (stateMasterEntity.getCountry() != null && stateMasterEntity.getCountry().getCreatedAt() != null) {
	            responseDto.getCountry().setCreatedAt(stateMasterEntity.getCountry().getCreatedAt().format(formatter));
	        }

	        stateMasterResponseDtos.add(responseDto);
	    }

	    return stateMasterResponseDtos;
	}

	@Override
	public StateMasterResponseDto getStateMasterById(Long id) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		Optional<StateMasterEntity> stateMasterEntities = stateMasterRepository.findByIdAndIsDeleteFalse(id);
		
		if (!stateMasterEntities.isPresent()) {
			return null;
		} else {
			
				StateMasterEntity stateMasterEntity = stateMasterEntities.get();
				StateMasterResponseDto responseDto = stateMasterMappper.entityToResponse(stateMasterEntity);
				if (stateMasterEntity.getCreatedAt() != null) {
					responseDto.setCreatedAt(stateMasterEntity.getCreatedAt().format(formatter));
				}
				responseDto.getCountry().setCreatedAt(stateMasterEntity.getCountry().getCreatedAt().format(formatter));
			return responseDto;
		}
	}

	@Override
	public List<StateMasterResponseDto> getStateNameWithSearch(String stateName) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		if (stateName == null || stateName.trim().equals("")) {
	        return Collections.emptyList();
	    }
		List<StateMasterEntity> stateMasterEntities = stateMasterRepository.findByNameContainingIgnoreCaseAndIsDeleteFalse(stateName);
		List<StateMasterResponseDto> stateMasterResponseDtos = new ArrayList<>();

		if (stateMasterEntities.isEmpty()) {
			return Collections.emptyList();
		} else {
			for (StateMasterEntity stateMasterEntity : stateMasterEntities) {
				StateMasterResponseDto responseDto = stateMasterMappper.entityToResponse(stateMasterEntity);

				if (stateMasterEntity.getCreatedAt() != null) {
					responseDto.setCreatedAt(stateMasterEntity.getCreatedAt().format(formatter));
				}
				responseDto.getCountry().setCreatedAt(stateMasterEntity.getCountry().getCreatedAt().format(formatter));
				stateMasterResponseDtos.add(responseDto);
			}
			return stateMasterResponseDtos;
		}
	}

	@Override
	public Boolean deleteStateMasterById(Long id) {
		 if (!stateMasterRepository.existsByIdAndIsDeleteFalse(id)) {
		        return false;
		    }

		    Optional<StateMasterEntity> optionalEntity = stateMasterRepository.findByIdAndIsDeleteFalse(id);
		    if (!optionalEntity.isPresent()) {
		        return false;
		    }

		    StateMasterEntity stateMasterEntity = optionalEntity.get();
		    stateMasterEntity.setIsDelete(true);
		    stateMasterRepository.save(stateMasterEntity);
		    return true;
	}

	@Override
	public List<StateMasterResponseDto> getStateMasterByCountryId(Long countryId, String stateName) {
	    List<StateMasterEntity> stateEntities = new ArrayList<>();

	    if (countryId == null && (stateName == null || stateName.trim().isEmpty())) {
	        return Collections.emptyList();
	    }

	    if (countryId != null && (stateName == null || stateName.trim().isEmpty())) {
	        CountryMasterEntity country = countryMasterRepository.findByIdAndIsDeleteFalse(countryId)
	                .orElseThrow(() -> new RuntimeException("Country not found with id: " + countryId));
	        stateEntities = stateMasterRepository.findByCountryAndIsDeleteFalse(country);

	    } else if (stateName != null && countryId == null) {
	        stateEntities = stateMasterRepository.findByNameContainingIgnoreCaseAndIsDeleteFalse(stateName);

	    } else if (countryId != null && stateName != null && !stateName.trim().isEmpty()) {
	        CountryMasterEntity country = countryMasterRepository.findByIdAndIsDeleteFalse(countryId)
	                .orElseThrow(() -> new RuntimeException("Country not found with id: " + countryId));
	        stateEntities = stateMasterRepository.findByNameContainingIgnoreCaseAndCountryAndIsDeleteFalse(stateName, country);
	    }

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    List<StateMasterResponseDto> responseDtos = new ArrayList<>();
	    for (StateMasterEntity state : stateEntities) {
	        StateMasterResponseDto dto = stateMasterMappper.entityToResponse(state);

	        if (state.getCreatedAt() != null) {
	            dto.setCreatedAt(state.getCreatedAt().format(formatter));
	        }
	        if (state.getCountry() != null && state.getCountry().getCreatedAt() != null) {
	            dto.getCountry().setCreatedAt(state.getCountry().getCreatedAt().format(formatter));
	        }

	        responseDtos.add(dto);
	    }

	    return responseDtos;
	}



}
