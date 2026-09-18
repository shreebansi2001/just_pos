package com.crmportal.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.crmportal.entity.IncomeExpenseTypeEntity;
import com.crmportal.request.dto.IncomeExpenseTypeRequestDto;
import com.crmportal.response.dto.IncomeExpenseTypeResponseDto;

@Component
public class IncomeExpenseTypeMapper {

	public IncomeExpenseTypeEntity requestToEntity(IncomeExpenseTypeRequestDto request) {
		if(request == null) {
			return null;
		}
		
		IncomeExpenseTypeEntity entity = new IncomeExpenseTypeEntity();
		entity.setName(request.getName());
		entity.setType(request.getType().toUpperCase());
		entity.setUserId(request.getUserId());
		
		return entity;
	}
	
	public IncomeExpenseTypeEntity updateEntity(IncomeExpenseTypeEntity entity, IncomeExpenseTypeRequestDto request) {
		if(entity == null || request == null) {
			return null;
		}
		
		entity.setName(request.getName());
		entity.setType(request.getType().toUpperCase());
		entity.setUserId(request.getUserId());
		entity.setUpdatedAt(LocalDateTime.now());

		return entity;
	}
	
	public IncomeExpenseTypeResponseDto entityToResponse(IncomeExpenseTypeEntity entity) {
		if(entity == null) {
			return null;
		}

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		
		IncomeExpenseTypeResponseDto response = new IncomeExpenseTypeResponseDto();
		response.setTypeId(entity.getTypeId());
		response.setName(entity.getName());
		response.setType(entity.getType());
		response.setUserId(entity.getUserId());
		response.setCreatedAt(entity.getCreatedAt().format(dateTimeFormatter));
		response.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().format(dateTimeFormatter) : null);
		
		return response;
	}
	
	public List<IncomeExpenseTypeResponseDto> entitiesToResponse(List<IncomeExpenseTypeEntity> entities) {
		return entities.stream().map(entity -> entityToResponse(entity)).collect(Collectors.toList());
	}
}
