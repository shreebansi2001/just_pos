package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.CoupenEntity;
import com.crmportal.mapper.CoupenMasterMapper;
import com.crmportal.repository.CoupenMasterRepository;
import com.crmportal.request.dto.CoupenMasterRequestDto;
import com.crmportal.response.dto.CoupenMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.CoupenMasterService;

@Service
public class CoupenMasterServiceImpl implements CoupenMasterService {

	@Autowired
	CoupenMasterRepository coupenMasterRepository;
	
	@Autowired
	CoupenMasterMapper coupenMasterMapper;
	
	@Autowired
	CommonService commonService;
	
	@Override
	public CoupenMasterResponseDto addOrUpdateCoupenMaster(CoupenMasterRequestDto request, Long id) {
		
		CoupenEntity entity = new CoupenEntity();
		
		if(id == -1) {
			entity = coupenMasterMapper.requestToEntity(request);
		} else {
			CoupenEntity entityFromDb = coupenMasterRepository.findByIdAndIsDeleteFalse(id)
					.orElseThrow(() -> new RuntimeException("Coupen not found with id : " + id));
			entity = coupenMasterMapper.updateEntityFromRequest(request, entityFromDb);
			entity.setUpdatedAt(commonService.getCurrentDateTime());
		}
		
		coupenMasterRepository.save(entity);
		
		CoupenMasterResponseDto responseDto = coupenMasterMapper.entityToResponse(entity);
		
		return responseDto;
	}

	@Override
	public Boolean deleteCoupenMaster(Long id) {
		
		CoupenEntity entityFromDb = coupenMasterRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Coupen not found with id : " + id));
		
		entityFromDb.setIsDelete(true);
		entityFromDb.setUpdatedAt(commonService.getCurrentDateTime());
		
		coupenMasterRepository.save(entityFromDb);
		
		return true;
	}

	@Override
	public CoupenMasterResponseDto getCoupenMasterById(Long id) {
		CoupenEntity entityFromDb = coupenMasterRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Coupen not found with id : " + id));
		
		CoupenMasterResponseDto responseDto = coupenMasterMapper.entityToResponse(entityFromDb);
		
		return responseDto;
	}

	@Override
	public List<CoupenMasterResponseDto> getAllCoupenMaster() {

		List<CoupenMasterResponseDto> responseDtos = new ArrayList<>();
		
		List<CoupenEntity> entities = coupenMasterRepository.findByIsDeleteFalse();
		
		for (CoupenEntity coupenEntity : entities) {
			CoupenMasterResponseDto responseDto = coupenMasterMapper.entityToResponse(coupenEntity);
			responseDtos.add(responseDto);
		}
		
		return responseDtos;
	}

}
