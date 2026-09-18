package com.crmportal.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.CountryMasterEntity;
import com.crmportal.mapper.CountryMasterMapper;
import com.crmportal.repository.CountryMasterRepository;
import com.crmportal.request.dto.CountryMasterRequestDto;
import com.crmportal.response.dto.CountryMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.CountryMasterService;

@Service
public class CountryMasterServiceImpl implements CountryMasterService {

    @Autowired
    private CountryMasterRepository countryMasterRepository;

    @Autowired
    private CountryMasterMapper countryMasterMapper;

    @Autowired
    private CommonService commonService;

    @Override
    public CountryMasterResponseDto addOrUpdateCountryMaster(@Valid CountryMasterRequestDto request, long id) {
        CountryMasterEntity entity = null;

        Optional<CountryMasterEntity> existingName =
                countryMasterRepository.findByNameAndIsDeleteFalse(request.getName());

        Optional<CountryMasterEntity> existingCode =
                countryMasterRepository.findByCodeAndIsDeleteFalse(request.getCode());

        if (id == -1) {
            if (existingName.isPresent()) {
                throw new RuntimeException("Country with the name '" + request.getName() + "' already exists.");
            }
            if (existingCode.isPresent()) {
                throw new RuntimeException("Country with the code '" + request.getCode() + "' already exists.");
            }
            entity = countryMasterMapper.requestToEntity(request);
        } else {
            Optional<CountryMasterEntity> optionalEntity = countryMasterRepository.findByIdAndIsDeleteFalse(id);
            if (!optionalEntity.isPresent()) {
                throw new RuntimeException("Country not found with id: " + id);
            }
            entity = optionalEntity.get();

            if (!entity.getName().equalsIgnoreCase(request.getName()) && existingName.isPresent()) {
                throw new RuntimeException("Country with the name '" + request.getName() + "' already exists.");
            }
            
            if (!entity.getCode().equalsIgnoreCase(request.getCode()) && existingCode.isPresent()) {
                throw new RuntimeException("Country with the code '" + request.getCode() + "' already exists.");
            }

            entity.setUpdatedAt(commonService.getCurrentDateTime());
            entity.setName(request.getName());
            entity.setCode(request.getCode());
        }

        entity = countryMasterRepository.save(entity);
        return countryMasterMapper.entityToResponse(entity);
    }

    @Override
    public List<CountryMasterResponseDto> getAllCountryMaster(String countryName) {
        List<CountryMasterEntity> entities = new ArrayList<>();

        if (countryName == null || countryName.trim().isEmpty()) {
            entities = countryMasterRepository.findAllByIsDeleteFalse();
        } else {
            entities = countryMasterRepository.findByNameContainingIgnoreCaseAndIsDeleteFalse(countryName);
        }

        if (entities.isEmpty()) {
            return Collections.emptyList();
        }

        List<CountryMasterResponseDto> responseDtos = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (CountryMasterEntity entity : entities) {
            CountryMasterResponseDto dto = countryMasterMapper.entityToResponse(entity);

            if (entity.getCreatedAt() != null) {
                dto.setCreatedAt(entity.getCreatedAt().format(formatter));
            }
            responseDtos.add(dto);
        }

        return responseDtos;
    }

    @Override
    public CountryMasterResponseDto getCountryMasterById(Long id) {
        Optional<CountryMasterEntity> optionalEntity = countryMasterRepository.findByIdAndIsDeleteFalse(id);
        if (!optionalEntity.isPresent()) {
            return null;
        }

        CountryMasterEntity entity = optionalEntity.get();
        CountryMasterResponseDto responseDto = countryMasterMapper.entityToResponse(entity);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (entity.getCreatedAt() != null) {
            responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
        }

        return responseDto;
    }

    @Override
    public List<CountryMasterResponseDto> getCountryNameWithSearch(String countryName) {
        if (countryName == null || countryName.trim().equals("")) {
            return Collections.emptyList();
        }

        List<CountryMasterEntity> entities = countryMasterRepository.findByNameContainingIgnoreCaseAndIsDeleteFalse(countryName);
        if (entities.isEmpty()) {
            return Collections.emptyList();
        }

        List<CountryMasterResponseDto> responseDtos = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (CountryMasterEntity entity : entities) {
            CountryMasterResponseDto dto = countryMasterMapper.entityToResponse(entity);

            if (entity.getCreatedAt() != null) {
                dto.setCreatedAt(entity.getCreatedAt().format(formatter));
            }
            responseDtos.add(dto);
        }

        return responseDtos;
    }

    @Override
    public Boolean deleteCountryMasterById(Long id) {
        if (!countryMasterRepository.existsByIdAndIsDeleteFalse(id)) {
            return false;
        }

        Optional<CountryMasterEntity> optionalEntity = countryMasterRepository.findByIdAndIsDeleteFalse(id);
        if (!optionalEntity.isPresent()) {
            return false;
        }

        CountryMasterEntity entity = optionalEntity.get();
        entity.setIsDelete(true);
        countryMasterRepository.save(entity);
        return true;
    }
}
