package com.crmportal.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.IncomeExpenseTypeEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.IncomeExpenseTypeMapper;
import com.crmportal.repository.IncomeExpenseTypeRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.IncomeExpenseTypeRequestDto;
import com.crmportal.response.dto.IncomeExpenseTypeResponseDto;
import com.crmportal.service.IncomeExpenseTypeService;

@Service
public class IncomeExpenseTypeServiceImpl implements IncomeExpenseTypeService {

	@Autowired
	IncomeExpenseTypeRepository incomeExpenseTypeRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	IncomeExpenseTypeMapper incomeExpenseTypeMapper;

	@Override
	public IncomeExpenseTypeResponseDto addUpdateIncomeExpenseType(@Valid IncomeExpenseTypeRequestDto request) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));

		if(!request.getType().equalsIgnoreCase("INCOME") && !request.getType().equalsIgnoreCase("EXPENSE")) {
			throw new RuntimeException("Invalid type. Allowed values are 'income' or 'expense'.");
		}
		
		IncomeExpenseTypeEntity entity = null;
		
		Optional<IncomeExpenseTypeEntity> op = incomeExpenseTypeRepository.findByNameAndUserIdAndIsDeleteFalse(request.getName(), request.getUserId());
		
		if (request.getTypeId() == -1) {
			if(op.isPresent()) {
				throw new RuntimeException("Income/Expense type is already exist with name : " + request.getName());
			}
			entity = incomeExpenseTypeMapper.requestToEntity(request);
		} else {
			entity = incomeExpenseTypeRepository.findByTypeIdAndIsDeleteFalse(request.getTypeId()).orElseThrow(
					() -> new RuntimeException("Income/Expense type is not found with id : " + request.getTypeId()));
			
			if(op.isPresent() && op.get().getTypeId() != entity.getTypeId()) {
				throw new RuntimeException("Income/Expense type is already exist with name : " + request.getName());
			}
			
			entity = incomeExpenseTypeMapper.updateEntity(entity, request);
		}
		
		entity = incomeExpenseTypeRepository.save(entity);

		IncomeExpenseTypeResponseDto response = incomeExpenseTypeMapper.entityToResponse(entity);
		
		return response;
	}
	
	@Override
	public List<IncomeExpenseTypeResponseDto> getAllByUserId(Long userId, String type) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));
		
		if(type != null) {
			if(!type.equalsIgnoreCase("INCOME") && !type.equalsIgnoreCase("EXPENSE")) {
				throw new RuntimeException("Invalid type. Allowed values are 'income' or 'expense'.");
			}
		}
		List<IncomeExpenseTypeEntity> entities = incomeExpenseTypeRepository.getAllByUserId(type != null ? type.toUpperCase() : null, userId);
		
		return incomeExpenseTypeMapper.entitiesToResponse(entities);
	}
	
	@Override
	public IncomeExpenseTypeResponseDto getById(Long id) {
		IncomeExpenseTypeEntity entity = incomeExpenseTypeRepository.findByTypeIdAndIsDeleteFalse(id).orElseThrow(
				() -> new RuntimeException("Income/Expense type is not found with id : " + id));
		
		return incomeExpenseTypeMapper.entityToResponse(entity);
	}
	
	@Override
	public Boolean deleteById(Long id) {
		IncomeExpenseTypeEntity entity = incomeExpenseTypeRepository.findByTypeIdAndIsDeleteFalse(id).orElseThrow(
				() -> new RuntimeException("Income/Expense type is not found with id : " + id));
		
		entity.setIsDelete(true);
		entity.setUpdatedAt(LocalDateTime.now());
		
		entity = incomeExpenseTypeRepository.save(entity);
		
		return true;
	}
	
}
