package com.crmportal.mapper;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.crmportal.entity.AmountTransferEntity;
import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.CashAccountEntity;
import com.crmportal.enums.AccountType;
import com.crmportal.repository.BankDetailsRepository;
import com.crmportal.repository.CashAccountRepository;
import com.crmportal.response.dto.AmountTransferResponseDto;

@Component
public class AmountTransferMapper {

	@Autowired
	CashAccountRepository cashAccountRepository;

	@Autowired
	BankDetailsRepository bankDetailsRepository;

	public AmountTransferResponseDto mapToResponse(AmountTransferEntity entity) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	            
	    String fromName = "";
	    String toName = "";

	    if (entity.getFromType() == AccountType.CASH) {
	        fromName = cashAccountRepository.findById(entity.getFromAccountId())
	                .map(CashAccountEntity::getAccountName).orElse("");
	    } else {
	        fromName = bankDetailsRepository.findById(entity.getFromAccountId())
	                .map(BankDetailsEntity::getBankName).orElse("");
	    }

	    if (entity.getToType() == AccountType.CASH) {
	        toName = cashAccountRepository.findById(entity.getToAccountId())
	                .map(CashAccountEntity::getAccountName).orElse("");
	    } else {
	        toName = bankDetailsRepository.findById(entity.getToAccountId())
	                .map(BankDetailsEntity::getBankName).orElse("");
	    }

	    return new AmountTransferResponseDto(
	            entity.getId(),
	            entity.getFromType(),
	            entity.getToType(),
	            entity.getFromAccountId(),
	            fromName,
	            entity.getToAccountId(),
	            toName,
	            entity.getAmount(),
	            entity.getDate().format(dateFormatter).toString(),
	            entity.getNotes(),
	            entity.getIsDelete(),
	            entity.getCreatedAt() != null ? entity.getCreatedAt().format(dateTimeFormatter).toString() : null,
	            entity.getUpdatedAt() != null ? entity.getUpdatedAt().format(dateTimeFormatter).toString() : null,
	            entity.getUserId()
	    );
	}
}
