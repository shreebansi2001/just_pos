package com.crmportal.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.crmportal.entity.OfficeExpensePayoutHistoryEntity;
import com.crmportal.entity.TripExpensePayoutHistoryEntity;
import com.crmportal.request.dto.OfficeExpensePayoutRequestDto;
import com.crmportal.response.dto.OfficeExpensePayoutResponseDto;

@Component
public class OfficeExpensePayoutHistoryMapper {

	@Autowired
	OfficeExpenseMapper officeExpenseMapper;
	
	@Autowired
	BankDetailsMapper bankDetailsMapper;
	
	@Autowired
	CashAccountMapper cashAccountMapper;
	
	public OfficeExpensePayoutHistoryEntity requestToEntity(OfficeExpensePayoutRequestDto request) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		OfficeExpensePayoutHistoryEntity entity = new OfficeExpensePayoutHistoryEntity();

	    entity.setAmount(request.getAmount());
	    entity.setDueAmount(request.getDueAmount());
	    entity.setPaymentMode(request.getPaymentMode());
	    entity.setTransactionId(request.getTransactionId());
	    entity.setChequeNo(request.getChequeNo());
	    if (request.getPaymentDate() != null && !request.getPaymentDate().isEmpty()) {
	        entity.setPaymentDate(LocalDate.parse(request.getPaymentDate(), dateTimeFormatter));
	    }
	    entity.setDescription(request.getDescription());
	    
	    return entity;
	}
	
	public OfficeExpensePayoutResponseDto entityToResponse(OfficeExpensePayoutHistoryEntity entity) {

	    if (entity == null) {
	        return null;
	    }

	    OfficeExpensePayoutResponseDto dto = new OfficeExpensePayoutResponseDto();

	    dto.setId(entity.getId());

	    if (entity.getOfficeExpense() != null) {
	        dto.setOfficeExpense(officeExpenseMapper.entityToResponse(entity.getOfficeExpense()));
	    }

	    if (entity.getBankAccount() != null) {
	        dto.setBankAccount(bankDetailsMapper.entityToResponse(entity.getBankAccount()));
	    }

	    if (entity.getCashType() != null) {
	        dto.setCashType(cashAccountMapper.entityToResponse(entity.getCashType()));
	    }

	    dto.setPaymentDate(entity.getPaymentDate());
	    dto.setTransactionId(entity.getTransactionId());
	    dto.setChequeNo(entity.getChequeNo());
	    dto.setAmount(entity.getAmount());
	    dto.setDueAmount(entity.getDueAmount());
	    dto.setPaymentMode(entity.getPaymentMode());
	    dto.setStatus(entity.getStatus());
	    dto.setDescription(entity.getDescription());
	    dto.setIsDelete(entity.getIsDelete());

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	    if (entity.getCreatedAt() != null) {
	        dto.setCreatedAt(entity.getCreatedAt().format(formatter));
	    }

	    if (entity.getUpdatedAt() != null) {
	        dto.setUpdatedAt(entity.getUpdatedAt().format(formatter));
	    }

	    return dto;
	}
}
