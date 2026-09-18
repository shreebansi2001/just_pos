package com.crmportal.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.ExtraPaymentEntity;
import com.crmportal.repository.ExtraPaymentMasterRepository;
import com.crmportal.request.dto.ExtraPaymentRequestDto;
import com.crmportal.response.dto.ExtraPaymentMasterResponseDto;
import com.crmportal.service.ExtraPaymentMasterService;

@Service
public class ExtraPaymentMasterServiceImpl implements ExtraPaymentMasterService {

	@Autowired
	ExtraPaymentMasterRepository extraPaymentMasterRepository;

	@Override
	public Boolean addOrUpdate(@Valid ExtraPaymentRequestDto request) {
		Boolean isSuccess = false;
		ExtraPaymentEntity entity = null;
		if (request.getId() == -1) {
			entity = new ExtraPaymentEntity();
		} else {
			entity = extraPaymentMasterRepository.findByIdAndIsDeleteFalse(request.getId());
			if (entity == null) {
				throw new RuntimeException("ExtraPayment not found with id: " + request.getId());
			}
		}
		entity.setDescription(request.getDescription());
		entity.setPrice(request.getPrice());
		entity.setName(request.getName());
		entity = extraPaymentMasterRepository.save(entity);
		if (entity != null) {
			isSuccess = true;
		}
		return isSuccess;
	}

	@Override
	public List<ExtraPaymentMasterResponseDto> getAll() {
		List<ExtraPaymentMasterResponseDto> dtos = new ArrayList<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		List<ExtraPaymentEntity> entities = extraPaymentMasterRepository.findAllByIsDeleteFalse();
		if (entities.isEmpty()) {
			return Collections.emptyList();
		} else {
			for (ExtraPaymentEntity extraPaymentEntity : entities) {
				ExtraPaymentMasterResponseDto dto = new ExtraPaymentMasterResponseDto();
				dto.setId(extraPaymentEntity.getId());
				dto.setDescription(extraPaymentEntity.getDescription());
				dto.setName(extraPaymentEntity.getName());
				dto.setPrice(extraPaymentEntity.getPrice());
				dto.setIsDelete(extraPaymentEntity.getIsDelete());
				dto.setCreatedAt(extraPaymentEntity.getCreatedAt().format(formatter));
				dtos.add(dto);
			}
		}
		return dtos;
	}

	@Override
	public ExtraPaymentMasterResponseDto getById(Long id) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		ExtraPaymentEntity extraPaymentEntity = extraPaymentMasterRepository.findByIdAndIsDeleteFalse(id);
		if (extraPaymentEntity == null) {
			return null;
		} else {
			ExtraPaymentMasterResponseDto dto = new ExtraPaymentMasterResponseDto();
			dto.setDescription(extraPaymentEntity.getDescription());
			dto.setId(extraPaymentEntity.getId());
			dto.setName(extraPaymentEntity.getName());
			dto.setPrice(extraPaymentEntity.getPrice());
			dto.setIsDelete(extraPaymentEntity.getIsDelete());
			dto.setCreatedAt(extraPaymentEntity.getCreatedAt().format(formatter));
			return dto;
		}
	}

	@Override
	public Boolean deleteById(Long id) {

		Boolean isSuccess = false;
		if (extraPaymentMasterRepository.existsByIdAndIsDeleteFalse(id)) {
			ExtraPaymentEntity entity = extraPaymentMasterRepository.findByIdAndIsDeleteFalse(id);
			entity.setIsDelete(true);
			extraPaymentMasterRepository.save(entity);
			isSuccess = true;
		}
		return isSuccess;
	}

}
