package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.AmountTransferEntity;
import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.CashAccountEntity;
import com.crmportal.enums.AccountType;
import com.crmportal.mapper.AmountTransferMapper;
import com.crmportal.repository.AmountTransferRepository;
import com.crmportal.repository.BankDetailsRepository;
import com.crmportal.repository.CashAccountRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.AmountTransferRequestDto;
import com.crmportal.response.dto.AmountTransferResponseDto;
import com.crmportal.service.AmountTransferService;

@Service
public class AmountTransferServiceImpl implements AmountTransferService {

	@Autowired
	CashAccountRepository cashAccountRepository;
	
	@Autowired
	BankDetailsRepository bankDetailsRepository;
	
	@Autowired
	AmountTransferRepository amountTransferRepository;
	
	@Autowired
	AmountTransferMapper amountTransferMapper;
	
	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Override
	@Transactional
	public AmountTransferResponseDto addUpdateAmountTransfer(AmountTransferRequestDto request) {
		userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));
		
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
	    if (request.getFromAccountId().equals(request.getToAccountId())
	            && request.getFromType() == request.getToType()) {
	        throw new RuntimeException("From and To account cannot be same");
	    }

	    AmountTransferEntity entity = null;

	    if (request.getId() != -1) {
	        entity = amountTransferRepository.findByIdAndUserIdAndIsDeleteFalse(request.getId(), request.getUserId())
	                .orElseThrow(() -> new RuntimeException("Transfer not found"));

	        reverseTransaction(entity);
	    } else {
	        entity = new AmountTransferEntity();
	    }

	    Object fromAccount = getAccount(request.getFromType(), request.getFromAccountId());
	    Object toAccount = getAccount(request.getToType(), request.getToAccountId());

	    BigDecimal amount = request.getAmount();

	    if (request.getFromType() == AccountType.CASH) {
	        CashAccountEntity cash = (CashAccountEntity) fromAccount;
	        cash.setCurrentBalance(cash.getCurrentBalance().subtract(amount));
	        cashAccountRepository.save(cash);
	    } else {
	        BankDetailsEntity bank = (BankDetailsEntity) fromAccount;
	        bank.setCurrentBalance(bank.getCurrentBalance().subtract(amount));
	        bankDetailsRepository.save(bank);
	    }

	    if (request.getToType() == AccountType.CASH) {
	        CashAccountEntity cash = (CashAccountEntity) toAccount;
	        cash.setCurrentBalance(cash.getCurrentBalance().add(amount));
	        cashAccountRepository.save(cash);
	    } else {
	        BankDetailsEntity bank = (BankDetailsEntity) toAccount;
	        bank.setCurrentBalance(bank.getCurrentBalance().add(amount));
	        bankDetailsRepository.save(bank);
	    }

	    entity.setFromType(request.getFromType());
	    entity.setToType(request.getToType());
	    entity.setFromAccountId(request.getFromAccountId());
	    entity.setToAccountId(request.getToAccountId());
	    entity.setAmount(amount);
	    entity.setDate(LocalDate.parse(request.getDate(), dateFormatter));
	    entity.setNotes(request.getNotes());
	    entity.setUpdatedAt(LocalDateTime.now());
	    entity.setUserId(request.getUserId());

	    amountTransferRepository.save(entity);

	    return amountTransferMapper.mapToResponse(entity);
	}
	
	@Override
	public List<AmountTransferResponseDto> getAllTransfer(String startDate, String endDate, Long userId) {
		userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));
		
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    LocalDate start = (startDate != null && !startDate.isEmpty())
	            ? LocalDate.parse(startDate, formatter)
	            : null;

	    LocalDate end = (endDate != null && !endDate.isEmpty())
	            ? LocalDate.parse(endDate, formatter)
	            : null;

	    List<AmountTransferEntity> list = amountTransferRepository.getAllTransfers(start, end, userId);

	    return list.stream().map(amountTransferMapper::mapToResponse).collect(Collectors.toList());
	}
	
	@Override
	public AmountTransferResponseDto getTransferById(Long id, Long userId) {
		userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));
		
	    AmountTransferEntity entity = amountTransferRepository
	            .findByIdAndUserIdAndIsDeleteFalse(id, userId)
	            .orElseThrow(() -> new RuntimeException("Transfer not found"));

	    return amountTransferMapper.mapToResponse(entity);
	}
	
	@Override
	@Transactional
	public Boolean deleteTransferById(Long id) {

	    AmountTransferEntity entity = amountTransferRepository
	            .findByIdAndIsDeleteFalse(id)
	            .orElseThrow(() -> new RuntimeException("Transfer not found"));

	    BigDecimal amount = entity.getAmount();

	    if (entity.getFromType() == AccountType.CASH) {
	        CashAccountEntity cash = cashAccountRepository
	                .findById(entity.getFromAccountId())
	                .orElseThrow(() -> new RuntimeException("Cash account not found"));

	        cash.setCurrentBalance(cash.getCurrentBalance().add(amount));
	        cashAccountRepository.save(cash);
	    } else {
	        BankDetailsEntity bank = bankDetailsRepository
	                .findById(entity.getFromAccountId())
	                .orElseThrow(() -> new RuntimeException("Bank account not found"));

	        bank.setCurrentBalance(bank.getCurrentBalance().add(amount));
	        bankDetailsRepository.save(bank);
	    }

	    if (entity.getToType() == AccountType.CASH) {
	        CashAccountEntity cash = cashAccountRepository
	                .findById(entity.getToAccountId())
	                .orElseThrow(() -> new RuntimeException("Cash account not found"));

	        cash.setCurrentBalance(cash.getCurrentBalance().subtract(amount));
	        cashAccountRepository.save(cash);
	    } else {
	        BankDetailsEntity bank = bankDetailsRepository
	                .findById(entity.getToAccountId())
	                .orElseThrow(() -> new RuntimeException("Bank account not found"));

	        bank.setCurrentBalance(bank.getCurrentBalance().subtract(amount));
	        bankDetailsRepository.save(bank);
	    }

	    entity.setIsDelete(true);
	    entity.setUpdatedAt(LocalDateTime.now());

	    amountTransferRepository.save(entity);

	    return true;
	}
	
	private void reverseTransaction(AmountTransferEntity entity) {

	    BigDecimal amount = entity.getAmount();

	    if (entity.getFromType() == AccountType.CASH) {
	        CashAccountEntity cash = cashAccountRepository
	                .findById(entity.getFromAccountId())
	                .orElseThrow(() -> new RuntimeException("Cash not found"));

	        cash.setCurrentBalance(cash.getCurrentBalance().add(amount));
	        cashAccountRepository.save(cash);
	    } else {
	        BankDetailsEntity bank = bankDetailsRepository
	                .findById(entity.getFromAccountId())
	                .orElseThrow(() -> new RuntimeException("Bank not found"));

	        bank.setCurrentBalance(bank.getCurrentBalance().add(amount));
	        bankDetailsRepository.save(bank);
	    }

	    if (entity.getToType() == AccountType.CASH) {
	        CashAccountEntity cash = cashAccountRepository
	                .findById(entity.getToAccountId())
	                .orElseThrow(() -> new RuntimeException("Cash not found"));

	        cash.setCurrentBalance(cash.getCurrentBalance().subtract(amount));
	        cashAccountRepository.save(cash);
	    } else {
	        BankDetailsEntity bank = bankDetailsRepository
	                .findById(entity.getToAccountId())
	                .orElseThrow(() -> new RuntimeException("Bank not found"));

	        bank.setCurrentBalance(bank.getCurrentBalance().subtract(amount));
	        bankDetailsRepository.save(bank);
	    }
	}
	
	private Object getAccount(AccountType type, Long id) {

	    if (type == AccountType.CASH) {
	        return cashAccountRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Cash account not found"));
	    } else {
	        return bankDetailsRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Bank account not found"));
	    }
	}
}
