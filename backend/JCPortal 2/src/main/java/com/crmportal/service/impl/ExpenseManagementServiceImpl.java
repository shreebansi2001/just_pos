package com.crmportal.service.impl;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.plugins.Docket;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.ExpenseManagementEntity;
import com.crmportal.entity.LeadMasterEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.RoleMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.EExpense;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.mapper.EventMasterMapper;
import com.crmportal.mapper.ExpenseManagementMapper;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.ExpenseItemRepository;
import com.crmportal.repository.ExpenseManagementRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.RoleMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.ExpenseManagementRequestDto;
import com.crmportal.response.dto.EventMasterResponseDto;
import com.crmportal.response.dto.ExpenseManagementResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.ExpenseManagementService;
import com.crmportal.service.UserFileService;
import com.crmportal.service.UserMasterService;


@Service
public class ExpenseManagementServiceImpl implements ExpenseManagementService  {

	@Autowired
	ExpenseManagementMapper expenseManagementMapper;
	
	@Autowired
	ExpenseManagementRepository  expenseManagementRepository;
	
	@Autowired
	CommonService commonService;
	
	@Autowired
	PartyMasterRepository partyMasterRepository;
	
	@Autowired
	RoleMasterRepository  roleMasterRepository;
	
	@Autowired
	EventMasterRepository eventMasterRepository;
	
	@Autowired
	EventMasterMapper eventMasterMapper;
	
	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Autowired
	ExpenseItemRepository expenseItemRepository;

	@Autowired
	UserFileService userFileService;
   
	@Override
	public ExpenseManagementResponseDto addOrUpdateExpenseMaster(ExpenseManagementRequestDto request) {
		
		ExpenseManagementEntity entity;
		
		Optional<PartyMasterEntity> partyOptional = null;
		Optional<UserMasterEntity> userManagerOptional = null;
		PartyMasterEntity partyMasterEntity = null;
		UserMasterEntity userManagerMasterEntity = null;
		
		if(request.getUserType().toString().trim().equalsIgnoreCase("MANAGER")) {
			userManagerOptional = userMasterRepository.findByIdAndIsDeleteFalse(request.getManagerId());
	        if (!userManagerOptional.isPresent()) {
	            throw new RuntimeException("Manager not found with id: " + request.getManagerId());
	        }
	        userManagerMasterEntity = userManagerOptional.get();
		} else {
			partyOptional = partyMasterRepository.findByIdAndIsDeleteFalse(request.getPartyId());
	        if(!partyOptional.isPresent()) {
	        	throw new RuntimeException("Party not found with id: " + request.getPartyId());
	        }
	        partyMasterEntity = partyOptional.get();     
		}
		
		
        RoleMasterEntity roleMasterEntity = null;
        Optional<RoleMasterEntity> roleOptional = roleMasterRepository.findByIdAndIsDeleteFalse(request.getRoleId());
        if(!roleOptional.isPresent()) {
        	 System.out.println("Role not found with id: " + request.getRoleId());
        } else {
        	roleMasterEntity = roleOptional.get();
        }
        
        
        Optional<EventMasterEntity> eventOptional = eventMasterRepository.findByIdAndIsDeleteFalse(request.getEventId());
        if(!eventOptional.isPresent()) {
        	throw new RuntimeException("Event not found with id: " + request.getEventId());
        }
        EventMasterEntity eventMasterEntity = eventOptional.get();
        
        Optional<UserMasterEntity> userOptional = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId());
        if(!userOptional.isPresent()) {
        	throw new RuntimeException("User not found with id: " + request.getUserId());
        }
        UserMasterEntity userMasterEntity = userOptional.get();
        
		if(request.getExpenseId() == -1) {
			entity = expenseManagementMapper.requestToEntity(request);
		} else {
			Optional<ExpenseManagementEntity> optional = expenseManagementRepository.findByExpenseIdAndIsDeleteFalse(request.getExpenseId());
			if(!optional.isPresent()) {
				throw new RuntimeException("Expense not found with id " + request.getExpenseId());
			}
			entity = optional.get();
			entity.setUpdatedAt(commonService.getCurrentDateTime());
			expenseManagementMapper.updateEntityFromRequest(request, entity);
		}
		
		entity.setParty(partyMasterEntity);
		entity.setRole(roleMasterEntity);
		entity.setEvent(eventMasterEntity);
		entity.setUser(userMasterEntity);
		entity.setManager(userManagerMasterEntity);
		entity = expenseManagementRepository.save(entity);
			
		if(request.getUserType().equals("MANAGER") && request.getFile() != null && !request.getFile().isEmpty()) {
			try {
				userFileService.storeFile(entity.getExpenseId(), ModuleName.USEREXPENSE.toString(),entity.getExpenseId(), FileType.IMAGE.toString(), request.getFile());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
			
		return entityToResponse(entity, eventMasterEntity);
	}

	@Override
	public List<ExpenseManagementResponseDto> getAllExpenses(Long eventId, Long userId) {
		List<ExpenseManagementResponseDto> responseDtos = new ArrayList<>();
		
		List<ExpenseManagementEntity> entities = expenseManagementRepository.findAllByEventIdAndUserIdAndIsDeleteFalse(eventId, userId);
		for (ExpenseManagementEntity expenseManagementEntity : entities) {
			ExpenseManagementResponseDto responseDto = entityToResponse(expenseManagementEntity, expenseManagementEntity.getEvent());
		
			responseDtos.add(responseDto);
		}
		
		return responseDtos;
	}

	@Override
	public Map<String, Object> getExpensesUserType(String userType, Long eventId, Long userId) {
		Map<String, Object> response = new HashMap<>();
		
		List<ExpenseManagementResponseDto> responseDtos = new ArrayList<>();
		EExpense eUserType = EExpense.valueOf(userType.toUpperCase());
		
		List<ExpenseManagementEntity> entities = expenseManagementRepository.findByUserTypeAndEventIdAndUserIdAndIsDeleteFalse(eUserType, eventId, userId);
		for (ExpenseManagementEntity expenseManagementEntity : entities) {
			ExpenseManagementResponseDto responseDto = entityToResponse(expenseManagementEntity, expenseManagementEntity.getEvent());
			
			responseDtos.add(responseDto);
		}
		
		Long totalExpense = expenseManagementRepository.findTotalExpenseByUserType(userType, eventId, userId);
		response.put("total_given_amount", totalExpense);
		
		
		if(userType.equalsIgnoreCase("MANAGER")) {
			Long totalUsedAmount = expenseItemRepository.findTotalManagerUsedAmount(eventId, userId);
			
			totalUsedAmount = totalUsedAmount == null ? Long.valueOf(0) : totalUsedAmount;
			
			response.put("total_used_amount", totalUsedAmount);
			
			Long remainingAmount = Long.valueOf(0);
			if(totalUsedAmount != null) {				
				remainingAmount = totalExpense - totalUsedAmount;
			}
			response.put("remaining_amount", remainingAmount);
		}
		
		response.put("data", responseDtos);
		
		return response;
	}

	@Override
	public ExpenseManagementResponseDto getExpensesById(Long expenseId) {
		
		Optional<ExpenseManagementEntity> entityOptional = expenseManagementRepository.findByExpenseIdAndIsDeleteFalse(expenseId);
		if(!entityOptional.isPresent()) {
			return null;
		}
		
		ExpenseManagementEntity entity = entityOptional.get();
		
		ExpenseManagementResponseDto responseDto = entityToResponse(entity, entity.getEvent());
		
		return responseDto;
	}

	@Override
	public Boolean deleteExpensesById(Long expenseId) {
		
		Optional<ExpenseManagementEntity> entityOptional = expenseManagementRepository.findByExpenseIdAndIsDeleteFalse(expenseId);
		if(!entityOptional.isPresent()) {
			return false;
		}
		
		ExpenseManagementEntity entity = entityOptional.get();
		entity.setIsDelete(true);
		expenseManagementRepository.save(entity);
		
		return true;
		
	}
	
	public ExpenseManagementResponseDto entityToResponse(ExpenseManagementEntity entity, EventMasterEntity eventMasterEntity) {

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
    	
    	if(entity.getDocPath() != null) {
    		dto.setDocPath(entity.getDocPath());
    	}
    	
    	if(entity.getDocument() != null) {
    		dto.setDocument(entity.getDocument());
    	}
    	
    	if(entity.getDate() != null) {
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

	    if(entity.getManager() != null) {
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
