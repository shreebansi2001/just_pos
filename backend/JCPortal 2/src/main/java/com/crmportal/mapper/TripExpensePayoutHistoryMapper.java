package com.crmportal.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.crmportal.entity.TripExpensePayoutHistoryEntity;
import com.crmportal.repository.BankDetailsRepository;
import com.crmportal.repository.ExpenseRepository;
import com.crmportal.request.dto.TripExpensePayoutRequestDto;
import com.crmportal.response.dto.TripExpensePayoutHistoryResponseDto;

@Component
public class TripExpensePayoutHistoryMapper {

	@Autowired
	BankDetailsMapper bankDetailsMapper;
	
	@Autowired
    CashAccountMapper cashAccountMapper;
	
	@Autowired
	ExpenseRepository expenseRepository;
	
	@Autowired
	BankDetailsRepository bankDetailsRepository;
	
	public TripExpensePayoutHistoryEntity requestToEntity(TripExpensePayoutRequestDto request) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		TripExpensePayoutHistoryEntity entity = new TripExpensePayoutHistoryEntity();

	    entity.setAmount(request.getPayoutAmount());
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
	
	 public TripExpensePayoutHistoryResponseDto entityToResponse(TripExpensePayoutHistoryEntity entity) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");
		 
        if (entity == null) {
            return null;
        }

        return new TripExpensePayoutHistoryResponseDto(
                entity.getId(),
                entity.getExpense() != null ? entity.getExpense().getId() : null,
                bankDetailsMapper.entityToResponse(entity.getBankAccount()),
                entity.getPaymentDate().format(dateFormatter),
                entity.getTransactionId(),
                entity.getChequeNo(),
                entity.getAmount(),
                entity.getDueAmount(),
                entity.getPaymentMode(),
                cashAccountMapper.entityToResponse(entity.getCashType()),
                entity.getStatus(),
                entity.getDescription(),
                entity.getIsDelete(),
                entity.getCreatedAt() != null ? entity.getCreatedAt().format(dateTimeFormatter) : null,
                entity.getUpdatedAt() != null ? entity.getUpdatedAt().format(dateTimeFormatter) : null
        );
    }
	
}
