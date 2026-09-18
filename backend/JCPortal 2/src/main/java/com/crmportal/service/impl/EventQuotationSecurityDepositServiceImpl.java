package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.CashAccountEntity;
import com.crmportal.entity.EventFunctionQuotationPaymentEntity;
import com.crmportal.entity.EventQuotationSecurityDepositEntity;
import com.crmportal.enums.EntryType;
import com.crmportal.repository.BankDetailsRepository;
import com.crmportal.repository.CashAccountRepository;
import com.crmportal.repository.EventFunctionQuotationRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.EventQuotationSecurityDepositRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.EventQuotationSecurityDepositRequestDto;
import com.crmportal.response.dto.EventQuotationSecurityDepositResponseDto;
import com.crmportal.service.EventQuotationSecurityDepositService;

@Service
public class EventQuotationSecurityDepositServiceImpl implements EventQuotationSecurityDepositService {

	@Autowired
	EventMasterRepository eventMasterRepository;
	
	@Autowired
	EventFunctionQuotationRepository eventFunctionQuotationRepository;
	
	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Autowired
	EventQuotationSecurityDepositRepository eventQuotationSecurityDepositRepository;
	
	@Autowired
	BankDetailsRepository bankDetailsRepository;
	
	@Autowired
	CashAccountRepository cashAccountRepository;
	
	@Override
	@Transactional
	public EventQuotationSecurityDepositResponseDto addUpdateSecurityDeposit(
			@Valid EventQuotationSecurityDepositRequestDto request) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");
		
		Long eventId = request.getEventId();
		Long userId = request.getUserId();
		Long quotationId = request.getQuotationId();
		
		eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
				.orElseThrow(() -> new RuntimeException("Event not found with id : " + eventId));

		userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));

	    if (eventFunctionQuotationRepository.findByIdAndIsDeleteFalse(quotationId) == null) {
			throw new RuntimeException("Quotation not found with id : " + quotationId);
		}
	    
		boolean isBank = request.getBankAccountId() != null && request.getBankAccountId() != -1;
		boolean isCash = request.getCashAccountId() != null && request.getCashAccountId() != -1;
		
		if(isBank && isCash) {
			throw new RuntimeException("Please select either a Bank Account or a Cash Account, not both.");
		}
		
		if (!isBank && !isCash) {
		    throw new RuntimeException("Please select either a Bank Account or a Cash Account.");
		}
		
		EventQuotationSecurityDepositEntity entity = null;
		BigDecimal oldAmount = BigDecimal.ZERO;
	    BankDetailsEntity oldBank = null;
	    CashAccountEntity oldCash = null;
	    EntryType oldEntryType = null;
	    
		if(request.getId() != -1) {
			entity = eventQuotationSecurityDepositRepository.findByIdAndIsDeleteFalse(request.getId())
					.orElseThrow(() -> new RuntimeException("Security Deposit not found with id : " + request.getId()));
		
			oldAmount = entity.getAmount() != null ? entity.getAmount() : BigDecimal.ZERO;
	
			oldBank = getBank(entity.getBankAccountId());
			oldCash = getCashAccount(entity.getCashAccountId());
	
			oldEntryType = entity.getEntryType();
	
			entity.setUpdatedAt(LocalDateTime.now());
		}else {
			entity = new EventQuotationSecurityDepositEntity();
			
			entity.setEventId(eventId);
			entity.setQuotationId(quotationId);
			entity.setCreatedAt(LocalDateTime.now());
		}
	
		entity.setUserId(userId);
		entity.setPaymentMode(request.getPaymentMode());
		entity.setDescription(request.getDescription());
		entity.setAmount(request.getAmount());
		entity.setPaymentDateTime(
				request.getPaymentDateTime() != null ? LocalDateTime.parse(request.getPaymentDateTime(), dateTimeFormatter)
						: LocalDateTime.now());
		entity.setEntryType(request.getEntryType());
		
		if(isBank) {
			entity.setBankAccountId(request.getBankAccountId());
			entity.setCashAccountId(null);
		}else {
			entity.setCashAccountId(request.getCashAccountId());
			 entity.setBankAccountId(null);
		}
		
		EventQuotationSecurityDepositEntity saveDepositEntity = eventQuotationSecurityDepositRepository.save(entity);
		
	    BankDetailsEntity newBank = getBank(entity.getBankAccountId());
	    CashAccountEntity newCash = getCashAccount(entity.getCashAccountId());
	    
		if (request.getId() != -1) {
			if (oldEntryType == EntryType.RECEIPT) {
				updateAccountBalance(oldBank, oldCash, BigDecimal.ZERO, oldAmount);
			} else if (oldEntryType == EntryType.PAYMENT) {
				updateAccountBalance(oldBank, oldCash, oldAmount, BigDecimal.ZERO);
			}
		}
	
		if (entity.getEntryType() == EntryType.RECEIPT) {
			updateAccountBalance(newBank, newCash, entity.getAmount(), BigDecimal.ZERO);
		} else if (entity.getEntryType() == EntryType.PAYMENT) {
			updateAccountBalance(newBank, newCash, BigDecimal.ZERO, entity.getAmount());
		}
		
		return entityToResponse(saveDepositEntity);
	}
	
	@Override
	public List<EventQuotationSecurityDepositResponseDto> getAllSecurityDepositByEventId(Long eventId) {
		List<EventQuotationSecurityDepositEntity> listSecurityDeposits = eventQuotationSecurityDepositRepository.findAllByEventIdAndIsDeleteFalse(eventId);
		
		List<EventQuotationSecurityDepositResponseDto> response = listSecurityDeposits.stream()
				.map(sd -> entityToResponse(sd)).collect(Collectors.toList());
		
		return response;
	}
	
	@Override
	@Transactional
	public Boolean deleteSecurityDeposit(Long securityDepositId) {
		EventQuotationSecurityDepositEntity entity = eventQuotationSecurityDepositRepository
				.findByIdAndIsDeleteFalse(securityDepositId)
				.orElseThrow(() -> new RuntimeException("Security Deposit not found with id : " + securityDepositId));
		
		BigDecimal amount = entity.getAmount() != null ? entity.getAmount() : BigDecimal.ZERO;

		BankDetailsEntity bank = getBank(entity.getBankAccountId());

		CashAccountEntity cash = getCashAccount(entity.getCashAccountId());

		if (entity.getEntryType() == EntryType.RECEIPT) {
			updateAccountBalance(bank, cash, BigDecimal.ZERO, amount);
		} else if (entity.getEntryType() == EntryType.PAYMENT) {
			updateAccountBalance(bank, cash, amount, BigDecimal.ZERO);
		}
	    
		entity.setIsDelete(true);
		entity.setUpdatedAt(LocalDateTime.now());
		
		eventQuotationSecurityDepositRepository.save(entity);
		
		return true;
	}
	
	public BankDetailsEntity getBank(Long bankId) {
		if (bankId == null) {
			return null;
		}

		return bankDetailsRepository.findByIdAndIsDeleteFalse(bankId)
				.orElseThrow(() -> new RuntimeException("Bank Details not found with id : " + bankId));
	}

	public CashAccountEntity getCashAccount(Long cashAccountId) {
		if (cashAccountId == null) {
			return null;
		}

		return cashAccountRepository.findByIdAndIsDeleteFalse(cashAccountId)
				.orElseThrow(() -> new RuntimeException("Cash Account not found with id : " + cashAccountId));
	}
	
	public void updateAccountBalance(BankDetailsEntity bankDetailsEntity, CashAccountEntity cashAccountEntity,
			BigDecimal receivedAmount, BigDecimal payAmount) {

		BigDecimal received = receivedAmount != null ? receivedAmount : BigDecimal.ZERO;

		BigDecimal paid = payAmount != null ? payAmount : BigDecimal.ZERO;

		if (bankDetailsEntity != null) {

			bankDetailsEntity.setCurrentBalance(bankDetailsEntity.getCurrentBalance().add(received).subtract(paid));

			bankDetailsRepository.save(bankDetailsEntity);

		} else if (cashAccountEntity != null) {

			cashAccountEntity.setCurrentBalance(cashAccountEntity.getCurrentBalance().add(received).subtract(paid));

			cashAccountRepository.save(cashAccountEntity);
		}
	}
	
	public EventQuotationSecurityDepositResponseDto entityToResponse(EventQuotationSecurityDepositEntity entity) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");
		
		EventQuotationSecurityDepositResponseDto response = new EventQuotationSecurityDepositResponseDto();
		
		response.setId(entity.getId());
		response.setEventId(entity.getEventId());
		response.setQuotationId(entity.getQuotationId());
		response.setUserId(entity.getUserId());
		response.setCashAccountId(entity.getCashAccountId());
		response.setBankAccountId(entity.getBankAccountId());
		response.setDescription(entity.getDescription());
		response.setPaymentMode(entity.getPaymentMode());
		response.setAmount(entity.getAmount());
		response.setEntryType(entity.getEntryType());
		response.setPaymentDateTime(
				entity.getPaymentDateTime() != null ? entity.getPaymentDateTime().format(dateTimeFormatter) : null);
		response.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().format(dateTimeFormatter) : null);
		response.setUpdatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().format(dateTimeFormatter) : null);

		return response;
	}
}
