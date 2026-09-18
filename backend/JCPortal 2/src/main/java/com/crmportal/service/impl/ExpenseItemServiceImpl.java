package com.crmportal.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.ExpenseItemEntity;
import com.crmportal.entity.ExpenseManagementEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.RoleMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.EventMasterMapper;
import com.crmportal.mapper.ExpenseItemMapper;
import com.crmportal.mapper.ExpenseManagementMapper;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.ExpenseItemRepository;
import com.crmportal.repository.ExpenseManagementRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.ExpenseItemRequestDto;
import com.crmportal.response.dto.EventMasterResponseDto;
import com.crmportal.response.dto.ExpenseItemResponseDto;
import com.crmportal.response.dto.ExpenseManagementResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.ExpenseItemService;

@Service
public class ExpenseItemServiceImpl implements ExpenseItemService {

	@Autowired
	ExpenseItemRepository expenseItemRepository;

	@Autowired
	PartyMasterRepository partyMasterRepository;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	ExpenseManagementRepository expenseManagementRepository;

	@Autowired
	ExpenseItemMapper expenseItemMapper;

	@Autowired
	CommonService commonService;

	@Autowired
	EventMasterMapper eventMasterMapper;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	ExpenseManagementMapper expenseManagementMapper;

	@Override
	public ExpenseItemResponseDto addOrUpdateExpenseItem(ExpenseItemRequestDto request, Long expenseItemId) {

		ExpenseItemEntity entity;

		Optional<EventMasterEntity> eventOptional = eventMasterRepository
				.findByIdAndIsDeleteFalse(request.getEventId());
		if (!eventOptional.isPresent()) {
			throw new RuntimeException("Event not found with id: " + request.getEventId());
		}
		EventMasterEntity eventMasterEntity = eventOptional.get();

		PartyMasterEntity supplier = null;
		if (request.getSupplierId() != null) {
			supplier = partyMasterRepository.findByIdAndIsDeleteFalse(request.getSupplierId()).orElse(null);
		}

		Optional<ExpenseManagementEntity> expenseOptional = expenseManagementRepository
				.findByExpenseIdAndIsDeleteFalse(request.getExpenseId());
		if (!eventOptional.isPresent()) {
			throw new RuntimeException("Expense not found with id: " + request.getExpenseId());
		}
		ExpenseManagementEntity expenseManagementEntity = expenseOptional.get();

		Optional<UserMasterEntity> userOptional = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId());
		if (!userOptional.isPresent()) {
			throw new RuntimeException("User not found with id: " + request.getUserId());
		}
		UserMasterEntity userMasterEntity = userOptional.get();

		if (expenseItemId == -1) {
			entity = expenseItemMapper.requestToEntity(request);
		} else {
			Optional<ExpenseItemEntity> optional = expenseItemRepository
					.findByExpenseItemIdAndIsDeleteFalse(expenseItemId);
			if (!optional.isPresent()) {
				throw new RuntimeException("Expense not found with id " + expenseItemId);
			}
			entity = optional.get();
			entity.setUpdatedAt(commonService.getCurrentDateTime());
			expenseItemMapper.updateEntityFromRequest(request, entity);
		}
		entity.setSupplierId(supplier.getId());
		entity.setExpense(expenseManagementEntity);
		entity.setEvent(eventMasterEntity);
		entity.setUser(userMasterEntity);
		entity = expenseItemRepository.save(entity);

		ExpenseItemResponseDto responseDto = expenseItemMapper.entityToResponse(entity);
		ExpenseManagementResponseDto expenseManagementResponseDto = expenseManagementMapper
				.entityToResponse(expenseManagementEntity);

		responseDto.setExpense(expenseManagementResponseDto);
		responseDto.setUserId(entity.getUser().getId());

		return responseDto;
	}

	@Override
	public Boolean deleteExpenseItem(Long expenseItemId) {

		Optional<ExpenseItemEntity> entityOptional = expenseItemRepository
				.findByExpenseItemIdAndIsDeleteFalse(expenseItemId);
		if (!entityOptional.isPresent()) {
			return false;
		}
		ExpenseItemEntity entity = entityOptional.get();
		entity.setIsDelete(true);
		expenseItemRepository.save(entity);

		return true;
	}

	@Override
	public List<ExpenseItemResponseDto> getExpenseItemByExpenseAndEvent(Long expenseId, Long eventId) {

		List<ExpenseItemEntity> entities = expenseItemRepository
				.findByExpense_ExpenseIdAndEventIdAndIsDeleteFalse(expenseId, eventId);

		// Collect supplierIds
		Set<Long> supplierIds = entities.stream().map(ExpenseItemEntity::getSupplierId).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		// Fetch all suppliers in one query
		Map<Long, String> supplierMap = partyMasterRepository.findAllByIdInAndIsDeleteFalse(supplierIds).stream()
				.collect(Collectors.toMap(PartyMasterEntity::getId, PartyMasterEntity::getNameEnglish));

		// Map response
		return entities.stream().map(entity -> {

			ExpenseItemResponseDto dto = expenseItemMapper.entityToResponse(entity);

			dto.setEventId(entity.getEvent().getId());
			dto.setExpense(entityToResponse(entity.getExpense(), entity.getExpense().getEvent()));
			dto.setUserId(entity.getUser().getId());
			dto.setSupplierId(entity.getSupplierId());

			dto.setSupplierName(supplierMap.get(entity.getSupplierId()));

			return dto;

		}).collect(Collectors.toList());
	}

	public ExpenseManagementResponseDto entityToResponse(ExpenseManagementEntity entity,
			EventMasterEntity eventMasterEntity) {

		EventMasterResponseDto eventMasterResponseDto = eventMasterMapper.entityToResponse(eventMasterEntity);

		ExpenseManagementResponseDto dto = new ExpenseManagementResponseDto();

		dto.setExpenseId(entity.getExpenseId());
		dto.setName(entity.getName());
		dto.setAmount(entity.getAmount());
		dto.setMobileNo(entity.getMobileNo());
		dto.setPaymentType(entity.getPaymentType());
		dto.setRemark(entity.getRemark());
		dto.setDescription(entity.getDescription());
		dto.setCountryCode(entity.getCountryCode());
		dto.setGstin(entity.getGstin());
		dto.setBuildingAddress(entity.getBuildingAddress());
		dto.setArea(entity.getArea());
		dto.setPincode(entity.getPincode());
		dto.setCity(entity.getCity());
		dto.setState(entity.getState());
		dto.setUserRole(entity.getUserType());

		dto.setCreatedAt(entity.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

		dto.setEvent(eventMasterResponseDto);
		dto.setDate(null);
		dto.setRoleId(null);
		dto.setRoleName(null);
		dto.setPartyId(null);
		dto.setPartyNameEnglish(null);
		dto.setPartyNameHindi(null);
		dto.setPartyNameGujarati(null);
		dto.setManagerId(null);
		dto.setManagerFirstname(null);
		dto.setManagerLastname(null);
		dto.setDocument(null);
		dto.setDocPath(null);

		if (entity.getDocPath() != null) {
			dto.setDocPath(entity.getDocPath());
		}

		if (entity.getDocument() != null) {
			dto.setDocument(entity.getDocument());
		}

		if (entity.getDate() != null) {
			dto.setDate(entity.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		}

		if (entity.getRole() != null) {
			dto.setRoleId(entity.getRole().getId());
			dto.setRoleName(entity.getRole().getName());
		}

		if (entity.getParty() != null) {
			dto.setPartyId(entity.getParty().getId());
			dto.setPartyNameEnglish(entity.getParty().getNameEnglish());
			dto.setPartyNameHindi(entity.getParty().getNameHindi());
			dto.setPartyNameGujarati(entity.getParty().getNameGujarati());
		}

		if (entity.getManager() != null) {
			dto.setManagerId(entity.getManager().getId());
			dto.setManagerFirstname(entity.getManager().getFirstName());
			dto.setManagerLastname(entity.getManager().getLastName());
		}

		if (entity.getUser() != null) {
			dto.setUserId(entity.getUser().getId());
		}

		return dto;
	}

}
