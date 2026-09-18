package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.CrockeryCutleryEntity;
import com.crmportal.entity.RawMaterialCategoryMasterEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.CrockeryCutleryMapper;
import com.crmportal.repository.CrockeryCutleryRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.RawMaterialCategoryMasterRepository;
import com.crmportal.request.dto.CrockeryCutleryRequestDto;
import com.crmportal.response.dto.CrockeryCutleryResponseDto;
import com.crmportal.service.CrockeryCutleryService;

@Service
public class CrockeryCutleryServiceImpl implements CrockeryCutleryService {

	@Autowired
	CrockeryCutleryRepository crockeryCutleryRepository;

	@Autowired
	CrockeryCutleryMapper crockeryCutleryMapper;
	
	@Autowired
	RawMaterialMasterRepository rawMaterialMasterRepository;
	
	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Autowired
	RawMaterialCategoryMasterRepository rawMaterialCategoryMasterRepository;
	
	@Override
	public List<CrockeryCutleryResponseDto> getByRawMaterialCatId(Long rawMaterialCatId, Long userId) {
		
		List<CrockeryCutleryResponseDto> responseDtos = new ArrayList<>();
		
		List<CrockeryCutleryEntity> entities = crockeryCutleryRepository.findByRawMaterialCategoryIdAndUserIdAndIsDeleteFalse(rawMaterialCatId, userId);
		
		for (CrockeryCutleryEntity entity : entities) {
			CrockeryCutleryResponseDto dto = crockeryCutleryMapper.entityToResponse(entity);
			dto.setRawMaterialCategoryId(entity.getRawMaterialCategory().getId());
			dto.setRawMaterialId(entity.getRawMaterial().getId());
			dto.setUserId(entity.getUser().getId());
			
			responseDtos.add(dto);
		}
		
		return responseDtos;
	}

	@Override
	public List<CrockeryCutleryResponseDto> addUpdateCrockeryCutlery(
			List<CrockeryCutleryRequestDto> crockeryCutleries) {
		
		List<CrockeryCutleryResponseDto> responseDtos = new ArrayList<>();
		
		for (CrockeryCutleryRequestDto dto : crockeryCutleries) {
			UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(dto.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));
			
			RawMaterialCategoryMasterEntity rawMaterialCategoryMasterEntity = rawMaterialCategoryMasterRepository.findByIdAndIsDeleteFalse(dto.getRawMaterialCategoryId())
					.orElseThrow(() -> new RuntimeException("Raw material category not found with id : " + dto.getRawMaterialCategoryId()));
			
			RawMaterialMasterEntity rawMaterialMasterEntity = Optional.ofNullable(rawMaterialMasterRepository.findByIdAndIsDeleteFalse(dto.getRawMaterialId()))
					.orElseThrow(() -> new RuntimeException("Raw material category not found with id : " + dto.getRawMaterialId()));
		
			CrockeryCutleryEntity entity = new CrockeryCutleryEntity();
			
			if(dto.getId() == -1) {
				entity = crockeryCutleryMapper.requestToEntity(dto);
				entity.setRawMaterialCategory(rawMaterialCategoryMasterEntity);
				entity.setRawMaterial(rawMaterialMasterEntity);
				entity.setUser(user);
			} else {
				entity = crockeryCutleryRepository.findByIdAndIsDeleteFalse(dto.getId())
						.orElseThrow(() -> new RuntimeException("Crockery cutlery not found with id : " + dto.getId()));
				
				entity = crockeryCutleryMapper.updateEntityFromRequest(entity, dto);
				entity.setRawMaterialCategory(rawMaterialCategoryMasterEntity);
				entity.setUser(user);
				entity.setRawMaterial(rawMaterialMasterEntity);
			}
			entity = crockeryCutleryRepository.save(entity);
			
			CrockeryCutleryResponseDto responseDto = crockeryCutleryMapper.entityToResponse(entity);
			responseDto.setRawMaterialCategoryId(entity.getRawMaterialCategory().getId());
			responseDto.setRawMaterialId(entity.getRawMaterial().getId());
			
			responseDtos.add(responseDto);
		}
		
		return responseDtos;
	}

}
