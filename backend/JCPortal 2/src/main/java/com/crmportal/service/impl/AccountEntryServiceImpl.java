package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.crmportal.entity.AccountContactEntity;
import com.crmportal.entity.AccountEntryEntity;
import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.CashAccountEntity;
import com.crmportal.entity.IncomeExpenseTypeEntity;
import com.crmportal.entity.InvoiceEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.AccountType;
import com.crmportal.enums.EntryType;
import com.crmportal.enums.PaymentMode;
import com.crmportal.mapper.AccountEntryMapper;
import com.crmportal.mapper.BankDetailsMapper;
import com.crmportal.mapper.CashAccountMapper;
import com.crmportal.mapper.InvoiceMapper;
import com.crmportal.repository.AccountContactRepository;
import com.crmportal.repository.AccountEntryRepository;
import com.crmportal.repository.BankDetailsRepository;
import com.crmportal.repository.CashAccountRepository;
import com.crmportal.repository.IncomeExpenseTypeRepository;
import com.crmportal.repository.InvoiceEntityRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.AccountEntryRequestDto;
import com.crmportal.response.dto.AccountEntryResponseDto;
import com.crmportal.response.dto.BankDetailsResponseDto;
import com.crmportal.response.dto.CashOpbResponseDto;
import com.crmportal.response.dto.MonthWiseDataDto;
import com.crmportal.response.dto.PaymentDashboardDto;
import com.crmportal.response.dto.ReceiptDashboardDto;
import com.crmportal.service.AccountEntryService;

@Service
public class AccountEntryServiceImpl implements AccountEntryService {

	@Autowired
	AccountEntryRepository accountEntryRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	CashAccountRepository cashAccountRepository;

	@Autowired
	InvoiceEntityRepository invoiceRepository;

	@Autowired
	AccountEntryMapper accountEntryMapper;

	@Autowired
	BankDetailsRepository bankDetailsRepository;

	@Autowired
	InvoiceMapper invoiceMapper;

	@Autowired
	CashAccountMapper cashAccountMapper;

	@Autowired
	BankDetailsMapper bankDetailsMapper;
	
	@Autowired
	AccountContactRepository accountContactRepository;
	
	@Autowired
	IncomeExpenseTypeRepository incomeExpenseTypeRepository;

	@Override
	public String generateVoucherNo(EntryType entryType, AccountType accountType, Long userId) {
		String prefix = "";

		if (entryType == EntryType.PAYMENT) {
			prefix = (accountType == AccountType.CASH) ? "CP" : "BP";
		} else if (entryType == EntryType.RECEIPT) {
			prefix = (accountType == AccountType.CASH) ? "CR" : "BR";
		}

		String lastVoucher = accountEntryRepository.findLastVoucherByPrefix(prefix, userId);

		int nextNumber = 1;

		if (lastVoucher != null && lastVoucher.contains("-")) {
			try {
				String numberPart = lastVoucher.split("-")[1];
				nextNumber = Integer.parseInt(numberPart) + 1;
			} catch (Exception e) {
				nextNumber = 1;
			}
		}

		return prefix + "-" + String.format("%06d", nextNumber);
	}

	@Override
	@Transactional
	public AccountEntryResponseDto addOrUpdateEntry(@Valid AccountEntryRequestDto request, Long id) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));

		AccountContactEntity accountContact = accountContactRepository.findByIdAndIsDeleteFalse(request.getAccountContactId())
					.orElseThrow(() -> new RuntimeException("Pary not found with id : " + request.getAccountContactId()));
		
		IncomeExpenseTypeEntity incomeExpenseType = incomeExpenseTypeRepository.findByTypeIdAndIsDeleteFalse(request.getIncomeExpenseTypeId())
				 	.orElseThrow(() -> new RuntimeException("Income/Expense type not found with id : " + request.getIncomeExpenseTypeId()));
		
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		CashAccountEntity cashAccountEntity = null;
		BankDetailsEntity bankDetailsEntity = null;

		boolean hasCash = request.getCashTypeId() != null && request.getCashTypeId() != -1;
		boolean hasBank = request.getBankAccountId() != null && request.getBankAccountId() != -1;

		if (hasCash && hasBank) {
			throw new RuntimeException("Only one of Cash or Bank should be provided.");
		}

		if (!hasCash && !hasBank) {
			throw new RuntimeException("Either Cash or Bank detail is required.");
		}

		if (hasCash) {
			cashAccountEntity = cashAccountRepository.findByIdAndIsDeleteFalse(request.getCashTypeId()).orElseThrow(
					() -> new RuntimeException("Cash Account not found with id : " + request.getCashTypeId()));
		}
		if (hasBank) {
			bankDetailsEntity = bankDetailsRepository.findByIdAndIsDeleteFalse(request.getBankAccountId()).orElseThrow(
					() -> new RuntimeException("Bank account not found with id : " + request.getBankAccountId()));
		}

		InvoiceEntity invoiceEntity = null;
		if (request.getInvoiceId() != null && request.getInvoiceId() != -1) {
			invoiceEntity = invoiceRepository.findByInvoiceIdAndIsDeleteFalse(request.getInvoiceId())
					.orElseThrow(() -> new RuntimeException("Invoice not found with id : " + request.getInvoiceId()));
		}
		System.out.println("voucher no : " + request.getVoucherNo());
		Optional<AccountEntryEntity> op = accountEntryRepository
				.findByVoucherNoAndUserIdAndIsDeleteFalse(request.getVoucherNo(), request.getUserId());

		AccountEntryEntity entity;

		if (id == -1) {
			System.out.println(op.isPresent());
			if (op.isPresent()) {
				throw new RuntimeException("Voucher no. already exist for this user.");
			}

			entity = accountEntryMapper.requestToEntity(request);
			entity.setAccountContactId(accountContact.getId());
		} else {
			entity = accountEntryRepository.findByIdAndIsDeleteFalse(id)
					.orElseThrow(() -> new RuntimeException("Account entry not found with id : " + id));

			if (op.isPresent() && !op.get().getId().equals(entity.getId())) {
				throw new RuntimeException("Voucher no. already for this user.");
			}

			BigDecimal oldAmount = entity.getAmount();
			EntryType oldType = entity.getEntryType();

			if (entity.getAccountType() == AccountType.CASH && entity.getCashType() != null) {
				CashAccountEntity oldCash = entity.getCashType();

				BigDecimal balance = Optional.ofNullable(oldCash.getCurrentBalance()).orElse(BigDecimal.ZERO);

				balance = (oldType == EntryType.PAYMENT) ? balance.add(oldAmount) : balance.subtract(oldAmount);

				oldCash.setCurrentBalance(balance);
				cashAccountRepository.save(oldCash);
			}

			if (entity.getAccountType() == AccountType.BANK && entity.getBankDetails() != null) {
				BankDetailsEntity oldBank = entity.getBankDetails();

				BigDecimal balance = Optional.ofNullable(oldBank.getCurrentBalance()).orElse(BigDecimal.ZERO);

				balance = (oldType == EntryType.PAYMENT) ? balance.add(oldAmount) : balance.subtract(oldAmount);

				oldBank.setCurrentBalance(balance);
				bankDetailsRepository.save(oldBank);
			}

			entity.setDate(LocalDate.parse(request.getDate(), dateFormatter));
			entity.setUpdatedAt(LocalDateTime.now());
			entity.setVoucherNo(request.getVoucherNo());
			entity.setAmount(request.getAmount());
			entity.setEntryType(request.getEntryType());
			entity.setPaymentMode(request.getPaymentMode());
			entity.setNotes(request.getNotes());
			entity.setAccountContactId(accountContact.getId());
			entity.setDate(LocalDate.parse(request.getDate(), dateFormatter));
		}
		entity.setIncomeExpenseType(incomeExpenseType);
		
		BigDecimal accountContactCurrBalance = accountContact.getCurrentBalance() != null
				? accountContact.getCurrentBalance()
						: BigDecimal.ZERO;
		
		if(request.getEntryType() == EntryType.RECEIPT) {
			accountContactCurrBalance = accountContactCurrBalance.add(request.getAmount());
		}else {
			accountContactCurrBalance = accountContactCurrBalance.subtract(request.getAmount());
		}
		
		accountContact.setCurrentBalance(accountContactCurrBalance);
		accountContactRepository.save(accountContact);

		if (hasCash) {
			BigDecimal cashCurrBalance = cashAccountEntity.getCurrentBalance() != null
					? cashAccountEntity.getCurrentBalance()
					: BigDecimal.ZERO;

			cashCurrBalance = (request.getEntryType() == EntryType.PAYMENT)
					? cashCurrBalance.subtract(request.getAmount())
					: cashCurrBalance.add(request.getAmount());

			cashAccountEntity.setCurrentBalance(cashCurrBalance);
			cashAccountRepository.save(cashAccountEntity);

			entity.setCashType(cashAccountEntity);
			entity.setBankDetails(null);
			entity.setAccountType(AccountType.CASH);
		} else {
			BigDecimal bankCurrBalance = bankDetailsEntity.getCurrentBalance() != null
					? bankDetailsEntity.getCurrentBalance()
					: BigDecimal.ZERO;

			bankCurrBalance = (request.getEntryType() == EntryType.PAYMENT)
					? bankCurrBalance.subtract(request.getAmount())
					: bankCurrBalance.add(request.getAmount());

			bankDetailsEntity.setCurrentBalance(bankCurrBalance);
			bankDetailsRepository.save(bankDetailsEntity);

			entity.setBankDetails(bankDetailsEntity);
			entity.setCashType(null);
			entity.setAccountType(AccountType.BANK);
		}

		if (invoiceEntity != null) {
			entity.setInvoice(invoiceEntity);
		} else {
			entity.setInvoice(null);
		}

		entity = accountEntryRepository.save(entity);

		AccountEntryResponseDto response = accountEntryMapper.entityToResponse(entity);
		response.setAccountContactId(entity.getAccountContactId());
		response.setIncomeExpenseTypeId(entity.getIncomeExpenseType().getTypeId());
		
		return response;
	}

	@Override
	public List<AccountEntryResponseDto> getAllByType(EntryType entryType, AccountType accountType,
			PaymentMode paymentMode, Long cashTypeId, Long bankAccountId, String startDate, String endDate,
			Long userId) {

		DateTimeFormatter dateFormtter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate, dateFormtter) : null;
		LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate, dateFormtter) : null;

		boolean hasCash = cashTypeId != null;
		boolean hasBank = bankAccountId != null;

		if (hasCash && hasBank) {
			throw new RuntimeException("Only one of Cash or Bank should be provided.");
		}

		if (!hasCash && !hasBank) {
			throw new RuntimeException("Either Cash or Bank detail is required.");
		}

		if (hasCash && cashTypeId != -1l) {
			cashAccountRepository.findByIdAndIsDeleteFalse(cashTypeId)
					.orElseThrow(() -> new RuntimeException("Cash Account not found with id : " + cashTypeId));
		}

		if (hasBank && bankAccountId != -1l) {
			bankDetailsRepository.findByIdAndIsDeleteFalse(bankAccountId)
					.orElseThrow(() -> new RuntimeException("Bank account not found with id : " + bankAccountId));
		}

//		List<AccountEntryEntity> entities = accountEntryRepository.findAllByFilters(entryType, accountType, paymentMode,
//				cashTypeId, bankAccountId, start, end, userId);

		List<Object[]> data = accountEntryRepository.findAllDataByFilters(entryType.name(), accountType.name(),
				paymentMode != null ? paymentMode.name() : null, cashTypeId, bankAccountId, start.toString(), end.toString(), userId);

		List<AccountEntryResponseDto> response = mapToResponse(data);

		return response;
	}

	@Override
	public AccountEntryResponseDto getById(Long accountEntryId) {
		AccountEntryEntity entity = accountEntryRepository.findByIdAndIsDeleteFalse(accountEntryId)
				.orElseThrow(() -> new RuntimeException("Account entry not found with id : " + accountEntryId));

		AccountEntryResponseDto response = accountEntryMapper.entityToResponse(entity);
		response.setIncomeExpenseTypeId(entity.getIncomeExpenseType().getTypeId());
		response.setIncomeExpenseTypeName(entity.getIncomeExpenseType().getName());

		return response;
	}

	@Override
	@Transactional
	public Boolean deleteById(Long accountEntryId) {
		AccountEntryEntity entity = accountEntryRepository.findByIdAndIsDeleteFalse(accountEntryId)
				.orElseThrow(() -> new RuntimeException("Account entry not found with id : " + accountEntryId));

		entity.setIsDelete(true);
		entity.setUpdatedAt(LocalDateTime.now());

		entity = accountEntryRepository.save(entity);

		if (entity.getIsDelete()) {
			AccountContactEntity accountContact = accountContactRepository.findByIdAndIsDeleteFalse(entity.getId())
					.orElseThrow(() -> new RuntimeException("Account contact not found for this account entry."));
			
			BigDecimal amount = entity.getAmount();

			boolean isPayment = EntryType.PAYMENT == entity.getEntryType();

			BigDecimal accountContactCurrBalance = accountContact.getCurrentBalance() != null
					? accountContact.getCurrentBalance()
							: BigDecimal.ZERO;
			
			accountContact.setCurrentBalance(isPayment ? accountContactCurrBalance.subtract(amount) : accountContactCurrBalance.add(amount));
			accountContactRepository.save(accountContact);
			
			if (AccountType.CASH == entity.getAccountType()) {
				CashAccountEntity cash = entity.getCashType();

				BigDecimal updatedBalance = isPayment ? cash.getCurrentBalance().add(amount)
						: cash.getCurrentBalance().subtract(amount);

				cash.setCurrentBalance(updatedBalance);
				cashAccountRepository.save(cash);
			} else {
				BankDetailsEntity bank = entity.getBankDetails();

				BigDecimal updatedBalance = isPayment ? bank.getCurrentBalance().add(amount)
						: bank.getCurrentBalance().subtract(amount);

				bank.setCurrentBalance(updatedBalance);
				bankDetailsRepository.save(bank);
			}
			return true;
		}

		return false;
	}

	public List<AccountEntryResponseDto> mapToResponse(List<Object[]> result) {

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

		return result.stream().map(obj -> {

			AccountEntryResponseDto dto = new AccountEntryResponseDto();

			dto.setId(obj[0] != null ? ((Number) obj[0]).longValue() : null);
			dto.setUserId(obj[1] != null ? ((Number) obj[1]).longValue() : null);
			dto.setVoucherNo((String) obj[2]);

			dto.setAccountType(obj[3] != null ? AccountType.valueOf((String) obj[3]) : null);
			dto.setEntryType(obj[4] != null ? EntryType.valueOf((String) obj[4]) : null);
			dto.setPaymentMode(obj[5] != null ? PaymentMode.valueOf((String) obj[5]) : null);

			if (obj[6] != null) {
				CashAccountEntity cash = cashAccountRepository.findByIdAndIsDeleteFalse(((Number) obj[6]).longValue())
						.orElse(null);
				dto.setCashType(cash != null ?  cashAccountMapper.entityToResponse(cash) : null);
			}

			if (obj[7] != null) {
				BankDetailsEntity bank = bankDetailsRepository.findByIdAndIsDeleteFalse(((Number) obj[7]).longValue())
						.orElse(null);
				dto.setBankDetails(bank != null ? bankDetailsMapper.entityToResponse(bank) : null);
			}

			if (obj[8] != null) {
				if (obj[8] instanceof Date) {
					dto.setDate(((Date) obj[8]).toLocalDate().format(dateFormatter));
				} else {
					dto.setDate(((LocalDate) obj[8]).format(dateFormatter));
				}
			}

			dto.setAccountContactName((String) obj[9]);
			dto.setAmount((BigDecimal) obj[10]);
			dto.setNotes((String) obj[11]);

			if (obj[12] != null) {
				InvoiceEntity invoice = invoiceRepository
						.findByInvoiceIdAndIsDeleteFalse(((Number) obj[12]).longValue())
						.orElse(null);
				dto.setInvoice(invoice != null ? invoiceMapper.toResponse(invoice) : null);
			}

			dto.setReferenceNo((String) obj[13]);
			dto.setIsDelete((Boolean) obj[14]);

			if (obj[15] != null) {
				LocalDateTime created = obj[15] instanceof Timestamp ? ((Timestamp) obj[15]).toLocalDateTime()
						: (LocalDateTime) obj[15];

				dto.setCreatedAt(created.format(dateTimeFormatter));
			}

			if (obj[16] != null) {
				LocalDateTime updated = obj[16] instanceof Timestamp ? ((Timestamp) obj[16]).toLocalDateTime()
						: (LocalDateTime) obj[16];

				dto.setUpdatedAt(updated.format(dateTimeFormatter));
			}

			dto.setSource((String) obj[17]);

			dto.setAccountContactId(obj[18] != null ? ((Number) obj[18]).longValue() : null);
			dto.setIncomeExpenseTypeId(obj[19] != null ? ((Number) obj[19]).longValue() : null);
			dto.setIncomeExpenseTypeName(obj[20] != null ? (String)obj[20] : null);
			
			return dto;

		}).collect(Collectors.toList());
	}
	
}
