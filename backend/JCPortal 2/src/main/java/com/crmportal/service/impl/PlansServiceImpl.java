package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.PlanFeatureEntity;
import com.crmportal.entity.PlansEntity;
import com.crmportal.mapper.PlansFeatureMapper;
import com.crmportal.mapper.PlansMapper;
import com.crmportal.repository.PlansFeatureRepository;
import com.crmportal.repository.PlansRepository;
import com.crmportal.request.dto.PlanFeatureRequestDto;
import com.crmportal.request.dto.PlansRequestDto;
import com.crmportal.response.dto.PlanFeatureResponseDto;
import com.crmportal.response.dto.PlansResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.PlansService;

@Service
public class PlansServiceImpl implements PlansService {

	@Autowired
	PlansRepository plansRepository;
	
	@Autowired
	PlansMapper plansMapper;
	
	@Autowired
	CommonService commonService;
	
	@Autowired
	PlansFeatureMapper plansFeatureMapper;
	
	@Autowired
	PlansFeatureRepository plansFeatureRepository;
	
	@Override
	public PlansResponseDto addOrUpdatePlans(@Valid PlansRequestDto request, long id) {

	    PlansEntity entity;

	    if (id == -1) {
	        plansRepository.findByNameAndBillingCycleAndIsDeleteFalse(
	                request.getName(), request.getBillingCycle()
	        ).ifPresent(x -> {
	            throw new RuntimeException(
	                    "Plan with the name '" + request.getName() + "' already exists."
	            );
	        });

	        entity = plansMapper.requestToEntity(request);
	    } 
	    
	    else {
	        entity = plansRepository.findByIdAndIsDeleteFalse(id)
	                .orElseThrow(() ->
	                        new RuntimeException("Plan not found with id: " + id)
	                );

	        Optional<PlansEntity> dupCheck = plansRepository
	                .findByNameAndBillingCycleAndIdNotAndIsDeleteFalse(
	                        request.getName(),
	                        request.getBillingCycle(),
	                        id
	                );

	        if (!entity.getName().equalsIgnoreCase(request.getName()) && dupCheck.isPresent()) {
	            throw new RuntimeException(
	                    "Plan with the name '" + request.getName() + "' already exists."
	            );
	        }

	        entity.setName(request.getName());
	        entity.setPrice(BigDecimal.valueOf(request.getPrice()));
	        entity.setBillingCycle(request.getBillingCycle());
	        entity.setDescription(request.getDescription());
	        entity.setIsPopular(request.getIsPopular() != null ? request.getIsPopular() : Boolean.FALSE);
	        entity.setUpdatedAt(commonService.getCurrentDateTime());
	    }

	    entity.setOriginalPrice(BigDecimal.valueOf(request.getPrice()));
	    entity = plansRepository.save(entity);

	    List<PlanFeatureEntity> oldFeatures =
	            plansFeatureRepository.findAllByPlanAndIsDeleteFalse(entity);

	    oldFeatures.forEach(f -> {
	        f.setIsDelete(true);
	        plansFeatureRepository.save(f);
	    });

	    if (request.getFeatures() != null) {
	        for (PlanFeatureRequestDto featureRequest : request.getFeatures()) {
	            PlanFeatureEntity featureEntity = new PlanFeatureEntity();
	            featureEntity.setFeatureText(featureRequest.getFeatureText());
	            featureEntity.setPlan(entity);
	            featureEntity.setIsDelete(false);
	            plansFeatureRepository.save(featureEntity);
	        }
	    }

	    return plansMapper.entityToResponse(entity);
	}


	@Override
	public List<PlansResponseDto> getAllPlans() {
	    List<PlansEntity> entities = plansRepository.findAllByIsDeleteFalseAndExcludeUnlimitedPlan();
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    
	    if (entities.isEmpty()) {
	        return Collections.emptyList();
	    }

	    List<PlansResponseDto> responseDtos = new ArrayList<>();
	    for (PlansEntity entity : entities) {
	        PlansResponseDto responseDto = plansMapper.entityToResponse(entity);
	        List<PlanFeatureEntity> featureEntity = plansFeatureRepository.findAllByPlanAndIsDeleteFalse(entity);
	        List<PlanFeatureResponseDto> planFeatureResponseDtos = new ArrayList<>();
	        
	        for (PlanFeatureEntity feature : featureEntity) {
	            PlanFeatureResponseDto featureResponseDto = plansFeatureMapper.entityToResponse(feature);
	            featureResponseDto.setCreatedAt(feature.getCreatedAt().format(formatter));
	            planFeatureResponseDtos.add(featureResponseDto);
	        }
	        
	        responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
	        responseDto.setFeatures(planFeatureResponseDtos);
	        responseDtos.add(responseDto);
	    }

	    return responseDtos;
	}

	@Override
	public PlansResponseDto getPlansById(Long id) {
	    Optional<PlansEntity> entities = plansRepository.findByIdAndIsDeleteFalse(id);
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    if (!entities.isPresent()) {
	        return null;
	    }

	    PlansEntity entity = entities.get();
	    PlansResponseDto responseDto = plansMapper.entityToResponse(entity);

	    List<PlanFeatureEntity> featureEntities = plansFeatureRepository.findAllByPlanAndIsDeleteFalse(entity);
	    
	    if (!featureEntities.isEmpty()) {
	        List<PlanFeatureResponseDto> planFeatureResponseDtos = new ArrayList<>();
	        for (PlanFeatureEntity featureEntity : featureEntities) {
	            PlanFeatureResponseDto planFeatureResponseDto = plansFeatureMapper.entityToResponse(featureEntity);
	            planFeatureResponseDto.setCreatedAt(featureEntity.getCreatedAt().format(formatter));
	            planFeatureResponseDtos.add(planFeatureResponseDto);
	        }
	        responseDto.setFeatures(planFeatureResponseDtos);
	    } else {
	        responseDto.setFeatures(Collections.emptyList());
	    }

	    responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
	    return responseDto;
	}


	@Override
	public Boolean deletePlanById(Long id) {
	    if (!plansRepository.existsByIdAndIsDeleteFalse(id)) {
	        return false;
	    }

	    Optional<PlansEntity> optionalEntity = plansRepository.findByIdAndIsDeleteFalse(id);
	    if (!optionalEntity.isPresent()) {
	        return false;
	    }

	    PlansEntity plansEntity = optionalEntity.get();
	    plansEntity.setIsDelete(true);
	    List<PlanFeatureEntity> planFeatureEntities = plansFeatureRepository.findAllByPlanAndIsDeleteFalse(plansEntity);

	    if (planFeatureEntities != null && !planFeatureEntities.isEmpty()) {
	    	planFeatureEntities.forEach(feature -> feature.setIsDelete(true));
	        plansFeatureRepository.saveAll(planFeatureEntities);
	    }

	    plansRepository.save(plansEntity);
	    return true;
	}

	@Override
	public List<PlansResponseDto> getAllPlansByBillingCycle(String billingCycle) {
		List<PlansEntity> entities = plansRepository.findAllByBillingCycleAndIsDeleteFalse(billingCycle);
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    
	    if (entities.isEmpty()) {
	        return Collections.emptyList();
	    }

	    List<PlansResponseDto> responseDtos = new ArrayList<>();
	    for (PlansEntity entity : entities) {
	        PlansResponseDto responseDto = plansMapper.entityToResponse(entity);
	        List<PlanFeatureEntity> featureEntity = plansFeatureRepository.findAllByPlanAndIsDeleteFalse(entity);
	        List<PlanFeatureResponseDto> planFeatureResponseDtos = new ArrayList<>();
	        
	        for (PlanFeatureEntity feature : featureEntity) {
	            PlanFeatureResponseDto featureResponseDto = plansFeatureMapper.entityToResponse(feature);
	            featureResponseDto.setCreatedAt(feature.getCreatedAt().format(formatter));
	            planFeatureResponseDtos.add(featureResponseDto);
	        }
	        
	        responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
	        responseDto.setFeatures(planFeatureResponseDtos);
	        responseDtos.add(responseDto);
	    }

	    return responseDtos;
	}

	

}
