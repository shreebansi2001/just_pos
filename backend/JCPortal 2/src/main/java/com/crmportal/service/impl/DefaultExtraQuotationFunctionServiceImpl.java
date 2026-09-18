package com.crmportal.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.DefaultExtraQuotationFunctionEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.DefaultExtraQuotationFunctionRepository;
import com.crmportal.repository.EventFunctionQuotationItemRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.DefaultExtraQuotationFunctionRequestDto;
import com.crmportal.response.dto.DefaultExtraQuotationFunctionResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.DefaultExtraQuotationFunctionService;

@Service
public class DefaultExtraQuotationFunctionServiceImpl implements DefaultExtraQuotationFunctionService {

	@Autowired
	DefaultExtraQuotationFunctionRepository defaultExtraQuotationFunctionRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	EventFunctionQuotationItemRepository eventFunctionQuotationItemRepository;

	@Override
	public Boolean addOrUpdate(DefaultExtraQuotationFunctionRequestDto request) {

		DefaultExtraQuotationFunctionEntity entity;

		UserMasterEntity userMasterEntity = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User Not Found"));

		boolean isDuplicate;

		if (request.getId() == -1) {
			isDuplicate = defaultExtraQuotationFunctionRepository
					.existsByNameIgnoreCaseAndUserIdAndIsDeleteFalse(request.getName(), userMasterEntity.getId());
		} else {
			isDuplicate = defaultExtraQuotationFunctionRepository
					.existsByNameIgnoreCaseAndUserIdAndIdNotAndIsDeleteFalse(request.getName(),
							userMasterEntity.getId(), request.getId());
		}

		if (isDuplicate) {
			throw new RuntimeException("Name already exists: " + request.getName());
		}

		if (request.getId() == -1) {
			entity = new DefaultExtraQuotationFunctionEntity();
			entity.setCreatedAt(commonService.getCurrentDateTime());
		} else {
			entity = defaultExtraQuotationFunctionRepository.findByIdAndIsDeleteFalse(request.getId());

			if (entity == null) {
				throw new RuntimeException("Record Not Found");
			}

			entity.setUpdatedAt(commonService.getCurrentDateTime());
		}

		entity.setIsActive(true);
		entity.setIsDelete(false);
		entity.setName(request.getName());
		entity.setNameGujarati(request.getNameGujarati());
		entity.setNameHindi(request.getNameHindi());
		entity.setUserId(userMasterEntity.getId());
		entity.setPrice(request.getPrice());
		defaultExtraQuotationFunctionRepository.save(entity);

		return true;
	}

	@Override
	public List<DefaultExtraQuotationFunctionResponseDto> getAll(Long userId, Boolean isActive) {

		List<DefaultExtraQuotationFunctionEntity> entities = defaultExtraQuotationFunctionRepository.getAll(userId,
				isActive);

		if (entities == null || entities.isEmpty()) {
			return Collections.emptyList();
		}

		return entities.stream()
				.map(entity -> new DefaultExtraQuotationFunctionResponseDto(entity.getId(), entity.getName(),
						entity.getIsActive(), entity.getUserId(), entity.getNameHindi(), entity.getNameGujarati(),entity.getPrice()))
				.collect(Collectors.toList());
	}

	@Override
	public Boolean deleteById(Long id) {

		DefaultExtraQuotationFunctionEntity entity = defaultExtraQuotationFunctionRepository
				.findByIdAndIsDeleteFalse(id);

		if (entity == null) {
			return false;
		}
		entity.setIsDelete(true);
		defaultExtraQuotationFunctionRepository.save(entity);

		return true;
	}
}
