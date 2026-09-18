package com.crmportal.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.AccountContactEntity;
import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.CashAccountEntity;
import com.crmportal.entity.CloseDateEntity;
import com.crmportal.entity.ExpenseDetailEntity;
import com.crmportal.entity.ExpenseEntity;
import com.crmportal.entity.IncomeExpenseTypeEntity;
import com.crmportal.entity.OfficeExpenseDocEntity;
import com.crmportal.entity.OfficeExpenseEntity;
import com.crmportal.entity.OfficeExpensePayoutHistoryEntity;
import com.crmportal.entity.TripExpensePayoutHistoryEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.AccountType;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.enums.PaymentMode;
import com.crmportal.mapper.ExpenseDetailMapper;
import com.crmportal.mapper.ExpenseMapper;
import com.crmportal.mapper.OfficeExpenseMapper;
import com.crmportal.mapper.OfficeExpensePayoutHistoryMapper;
import com.crmportal.mapper.TripExpensePayoutHistoryMapper;
import com.crmportal.repository.AccountContactRepository;
import com.crmportal.repository.BankDetailsRepository;
import com.crmportal.repository.CashAccountRepository;
import com.crmportal.repository.CloseDateRepository;
import com.crmportal.repository.ExpenseDetailRepository;
import com.crmportal.repository.ExpenseRepository;
import com.crmportal.repository.IncomeExpenseTypeRepository;
import com.crmportal.repository.OfficeExpenseDocRepository;
import com.crmportal.repository.OfficeExpensePayoutHistoryRepository;
import com.crmportal.repository.OfficeExpenseRepository;
import com.crmportal.repository.TripExpensePayoutHistoryRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.ExpenseDetailRequestDto;
import com.crmportal.request.dto.ExpenseRequestDto;
import com.crmportal.request.dto.OfficeExpensePayoutRequestDto;
import com.crmportal.request.dto.OfficeExpenseRequestDto;
import com.crmportal.request.dto.TripExpensePayoutRequestDto;
import com.crmportal.response.dto.AccountContactResponseDto;
import com.crmportal.response.dto.AllExpensesResponseDto;
import com.crmportal.response.dto.BankDetailsResponseDto;
import com.crmportal.response.dto.CashOpbResponseDto;
import com.crmportal.response.dto.CloseDateResponseDto;
import com.crmportal.response.dto.ExpenseDetailResponseDto;
import com.crmportal.response.dto.ExpenseResponseDto;
import com.crmportal.response.dto.ExpenseTypeAllData;
import com.crmportal.response.dto.InvoicePaymentHistoryResponseDto;
import com.crmportal.response.dto.MemberAllExpensesResponseDto;
import com.crmportal.response.dto.MemberResponseDto;
import com.crmportal.response.dto.OfficeExpenseDocResponseDto;
import com.crmportal.response.dto.OfficeExpensePayoutResponseDto;
import com.crmportal.response.dto.OfficeExpenseResponseDto;
import com.crmportal.response.dto.TripExpensePayoutHistoryResponseDto;
import com.crmportal.service.AccountContactService;
import com.crmportal.service.CommonService;
import com.crmportal.service.ExpenseService;
import com.crmportal.service.UserFileService;

@Service
public class ExpenseServiceImpl implements ExpenseService {

	@Autowired
	Environment environment;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	ExpenseMapper expenseMapper;

	@Autowired
	ExpenseRepository expenseRepository;

	@Autowired
	ExpenseDetailRepository expenseDetailRepository;

	@Autowired
	ExpenseDetailMapper expenseDetailMapper;

	@Autowired
	CommonService commonService;

	@Autowired
	UserFileService userFileService;

	@Autowired
	OfficeExpenseRepository officeExpenseRepository;

	@Autowired
	OfficeExpenseMapper officeExpenseMapper;

	@Autowired
	CloseDateRepository closeDateRepository;

	@Autowired
	TripExpensePayoutHistoryRepository tripExpensePayoutHistoryRepository;

	@Autowired
	CashAccountRepository cashAccountRepository;

	@Autowired
	TripExpensePayoutHistoryMapper tripExpensePayoutHistoryMapper;

	@Autowired
	BankDetailsRepository bankDetailsRepository;

	@Autowired
	OfficeExpensePayoutHistoryRepository officeExpensePayoutHistoryRepository;

	@Autowired
	OfficeExpensePayoutHistoryMapper officeExpensePayoutHistoryMapper;

	@Autowired
	AccountContactRepository accountContactRepository;

	@Autowired
	AccountContactService accountContactService;

	@Autowired
	IncomeExpenseTypeRepository incomeExpenseTypeRepository;

	@Autowired
	OfficeExpenseDocRepository officeExpenseDocRepository;

//	@Override
//	@Transactional
//	public ExpenseResponseDto addOrUpdateExpense(ExpenseRequestDto request) {
//
//		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//		if (request.getExpenseType() == null || request.getExpenseType().isEmpty()) {
//			throw new RuntimeException("Please add expense type. ");
//		}
//
//		UserMasterEntity userEntity = userMasterRepository.findById(request.getMemberId())
//				.orElseThrow(() -> new RuntimeException("User not found with id: " + request.getMemberId()));
//
//		ExpenseEntity expenseEntity;
//		if (request.getId() == -1) {
//			expenseEntity = expenseMapper.requestToEntity(request);
//			expenseEntity.setUser(userEntity);
//		} else {
//			expenseEntity = expenseRepository.findByIdAndIsDeleteFalse(request.getId())
//					.orElseThrow(() -> new RuntimeException("Expense not found with id: " + request.getId()));
//			expenseEntity = expenseMapper.updateEntityFromRequest(request, expenseEntity);
//			expenseEntity.setUpdatedAt(commonService.getCurrentDateTime());
//			expenseEntity.setUser(expenseEntity.getUser());
//		}
//
//		expenseEntity = expenseRepository.save(expenseEntity);
//
//		TripExpensePayoutHistoryEntity payout = null;
//		List<TripExpensePayoutHistoryEntity> history;
//		if (request.getPaidDate() != null && request.getPaidDate().trim().length() != 0) {
//			history = tripExpensePayoutHistoryRepository.findAllByExpenseAndIsDeleteFalse(expenseEntity).stream()
//					.map(payment -> {
//						payment.setIsDelete(true);
//						return payment;
//					}).collect(Collectors.toList());
//			tripExpensePayoutHistoryRepository.saveAll(history);
//
//			CashAccountEntity cashAccountEntity = cashAccountRepository
//					.findByUserIdAndIsPrimaryTrueAndIsDeleteFalse(Long.valueOf(1))
//					.orElseThrow(() -> new RuntimeException("Primary cash account not found."));
//
//			payout = new TripExpensePayoutHistoryEntity();
//			payout.setExpense(expenseEntity);
//			payout.setPaymentDate(LocalDate.parse(request.getPaidDate(), dateFormatter));
//			payout.setAmount(expenseEntity.getTotalAmount());
//			payout.setDueAmount(BigDecimal.ZERO);
//			payout.setPaymentMode(PaymentMode.CASH);
//			payout.setCashType(cashAccountEntity);
//			payout.setStatus("paid");
//			payout = tripExpensePayoutHistoryRepository.save(payout);
//		}
//
//		List<ExpenseDetailResponseDto> detailResponseDtos = new ArrayList<>();
//		List<ExpenseDetailRequestDto> expenseDetailRequestDtos = request.getDetailRequestDtos();
//		for (ExpenseDetailRequestDto dto : expenseDetailRequestDtos) {
//			ExpenseDetailEntity detailEntity;
//			if (dto.getId() == -1) {
//				detailEntity = expenseDetailMapper.requestToEntity(dto);
//			} else {
//				detailEntity = expenseDetailRepository.findByIdAndIsDeleteFalse(dto.getId())
//						.orElseThrow(() -> new RuntimeException("Expense detail not found with id: " + dto.getId()));
//				detailEntity = expenseDetailMapper.updateEntityFromRequest(dto, detailEntity);
//				detailEntity.setUpdatedAt(commonService.getCurrentDateTime());
//			}
//
//			detailEntity.setUser(userEntity);
//			detailEntity.setExpense(expenseEntity);
//			detailEntity = expenseDetailRepository.save(detailEntity);
//
//			if (dto.getFile() != null && !dto.getFile().isEmpty()) {
//				try {
//					userFileService.storeFile(userEntity.getId(), ModuleName.TRIPEXPENSEFILE.toString(),
//							detailEntity.getId(), FileType.RECEIPT.toString(), dto.getFile());
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//
//			ExpenseDetailResponseDto detailResponseDto = expenseDetailMapper.entityToResponse(detailEntity);
//			detailResponseDto.setExpenseId(detailEntity.getExpense().getId());
//			detailResponseDto.setUserId(detailEntity.getUser().getId());
//
//			detailResponseDtos.add(detailResponseDto);
//		}
//
//		history = tripExpensePayoutHistoryRepository.findAllByExpenseAndIsDeleteFalse(expenseEntity);
//
//		BigDecimal totalPaid = BigDecimal.ZERO;
//		BigDecimal remainingAmount = BigDecimal.ZERO;
//		LocalDateTime latestTime = null;
//		
//		List<TripExpensePayoutHistoryResponseDto> payoutList = new ArrayList<>();
//		for (TripExpensePayoutHistoryEntity entity : history) {
//		    payoutList.add(tripExpensePayoutHistoryMapper.entityToResponse(entity));
//
//		    if (entity.getAmount() != null) {
//		        totalPaid = totalPaid.add(entity.getAmount());
//		    }
//
//		    LocalDateTime currentTime = entity.getCreatedAt(); // or paymentDate if safer
//
//		    if (currentTime != null && (latestTime == null || currentTime.isAfter(latestTime))) {
//		        latestTime = currentTime;
//		        remainingAmount = entity.getDueAmount() != null
//		                ? entity.getDueAmount()
//		                : BigDecimal.ZERO;
//		    }
//		}
//
//		ExpenseResponseDto responseDto = expenseMapper.entityToResponse(expenseEntity);
//		responseDto.setMemberId(expenseEntity.getUser().getId());
//		responseDto.setDetailRequestDtos(detailResponseDtos);
//		responseDto.setPayoutHistory(payoutList);
//		responseDto.setUserName(expenseEntity.getUser().getFirstName() + " " + expenseEntity.getUser().getLastName());
//
//		responseDto.setPayoutAmount(totalPaid);
//		responseDto.setRemaingAmount(remainingAmount);
//		
//		return responseDto;
//	}

	@Override
	@Transactional
	public ExpenseResponseDto addOrUpdateExpense(ExpenseRequestDto request) {

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		if (request.getExpenseType() == null || request.getExpenseType().isEmpty()) {
			throw new RuntimeException("Please add expense type.");
		}

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));

		userMasterRepository.findByIdAndIsDeleteFalse(request.getAdminId())
				.orElseThrow(() -> new RuntimeException("Admin not found with id : " + request.getAdminId()));
		
		AccountContactEntity accountContactEntity = accountContactRepository
				.findByIdAndIsDeleteFalse(request.getAccountContactId()).orElseThrow(() -> new RuntimeException(
						"Account contact not found with id: " + request.getAccountContactId()));

		ExpenseEntity expenseEntity;
		if (request.getId() == -1) {
			expenseEntity = expenseMapper.requestToEntity(request);
		} else {
			expenseEntity = expenseRepository.findByIdAndIsDeleteFalse(request.getId())
					.orElseThrow(() -> new RuntimeException("Expense not found with id: " + request.getId()));

			expenseMapper.updateEntityFromRequest(request, expenseEntity);
			expenseEntity.setUpdatedAt(commonService.getCurrentDateTime());
		}
		expenseEntity.setUser(user);
		ExpenseEntity savedExpense = expenseRepository.save(expenseEntity);

		if (request.getPaidDate() != null && !request.getPaidDate().trim().isEmpty()) {

			List<TripExpensePayoutHistoryEntity> oldHistory = tripExpensePayoutHistoryRepository
					.findAllByExpenseAndIsDeleteFalse(expenseEntity);

			oldHistory.forEach(h -> h.setIsDelete(true));
			tripExpensePayoutHistoryRepository.saveAll(oldHistory);

			CashAccountEntity cashAccountEntity = cashAccountRepository.findByUserIdAndIsPrimaryTrueAndIsDeleteFalse(1L)
					.orElseThrow(() -> new RuntimeException("Primary cash account not found."));

			TripExpensePayoutHistoryEntity payout = new TripExpensePayoutHistoryEntity();
			payout.setExpense(expenseEntity);
			payout.setPaymentDate(LocalDate.parse(request.getPaidDate(), dateFormatter));
			payout.setAmount(expenseEntity.getTotalAmount());
			payout.setDueAmount(BigDecimal.ZERO);
			payout.setPaymentMode(PaymentMode.CASH);
			payout.setCashType(cashAccountEntity);
			payout.setDescription("");
			payout.setStatus("paid");

			tripExpensePayoutHistoryRepository.save(payout);

			accountContactEntity.setCurrentBalance(accountContactEntity.getCurrentBalance().add(payout.getAmount()));
			accountContactRepository.save(accountContactEntity);
		}

		List<ExpenseDetailResponseDto> detailResponseDtos = request.getDetailRequestDtos().stream().map(dto -> {

			ExpenseDetailEntity entity;

			if (dto.getId() == -1) {
				entity = expenseDetailMapper.requestToEntity(dto);
			} else {
				entity = expenseDetailRepository.findByIdAndIsDeleteFalse(dto.getId())
						.orElseThrow(() -> new RuntimeException("Expense detail not found with id: " + dto.getId()));

				expenseDetailMapper.updateEntityFromRequest(dto, entity);
				entity.setUpdatedAt(commonService.getCurrentDateTime());
			}

			entity.setUser(user);
			entity.setExpense(savedExpense);

			entity = expenseDetailRepository.save(entity);

			if (dto.getFile() != null && !dto.getFile().isEmpty()) {
				try {
					userFileService.storeFile(request.getAdminId(), ModuleName.TRIPEXPENSEFILE.toString(),
							entity.getId(), FileType.RECEIPT.toString(), dto.getFile());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			ExpenseDetailResponseDto response = expenseDetailMapper.entityToResponse(entity);
			response.setExpenseId(entity.getExpense().getId());
			response.setUserId(entity.getUser().getId());

			return response;

		}).collect(Collectors.toList());

		List<TripExpensePayoutHistoryEntity> history = tripExpensePayoutHistoryRepository
				.findAllByExpenseAndIsDeleteFalse(expenseEntity);

		BigDecimal totalPaid = history.stream().map(h -> h.getAmount() == null ? BigDecimal.ZERO : h.getAmount())
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		TripExpensePayoutHistoryEntity latest = history.stream().filter(h -> h.getCreatedAt() != null)
				.max(Comparator.comparing(TripExpensePayoutHistoryEntity::getCreatedAt)).orElse(null);

		BigDecimal remainingAmount = (latest != null && latest.getDueAmount() != null) ? latest.getDueAmount()
				: BigDecimal.ZERO;

		List<TripExpensePayoutHistoryResponseDto> payoutList = history.stream()
				.map(tripExpensePayoutHistoryMapper::entityToResponse).collect(Collectors.toList());

		String status = expenseRepository.getExpensePaidStatus(savedExpense.getId()).stream().findFirst()
				.orElse("pending");

		ExpenseResponseDto responseDto = expenseMapper.entityToResponse(expenseEntity);
		responseDto.setAccountContactId(accountContactEntity.getId());
		responseDto.setAccountContactName(accountContactEntity.getName());
		responseDto.setDetailRequestDtos(detailResponseDtos);
		responseDto.setPayoutHistory(payoutList);
		responseDto.setPayoutAmount(totalPaid);
		responseDto.setRemaingAmount(remainingAmount);
		responseDto.setStatus(status);

		return responseDto;
	}

	@Override
	public ExpenseResponseDto getByExpenseId(Long expenseId) {
		ExpenseEntity expenseEntity = expenseRepository.findByIdAndIsDeleteFalse(expenseId)
				.orElseThrow(() -> new RuntimeException("Expense not found with id: " + expenseId));

		List<ExpenseDetailResponseDto> detailResponseDtos = new ArrayList<>();
		List<ExpenseDetailEntity> detailEntities = expenseDetailRepository.findByExpenseIdAndIsDeleteFalse(expenseId);
		for (ExpenseDetailEntity expenseDetailEntity : detailEntities) {
			ExpenseDetailResponseDto detailResponseDto = expenseDetailMapper.entityToResponse(expenseDetailEntity);

			detailResponseDto.setExpenseId(expenseDetailEntity.getExpense().getId());
			detailResponseDto.setUserId(expenseDetailEntity.getUser().getId());
			detailResponseDto.setDocPath(environment.getProperty("app.image.url") + expenseDetailEntity.getDocPath());
			detailResponseDto.setExpenseDate(expenseDetailEntity.getExpenseDate() != null
					? commonService.dateTimeFormatted(expenseDetailEntity.getExpenseDate())
					: null);

			detailResponseDtos.add(detailResponseDto);
		}

		List<TripExpensePayoutHistoryEntity> history = tripExpensePayoutHistoryRepository
				.findAllByExpenseAndIsDeleteFalse(expenseEntity);

		BigDecimal totalPaid = history.stream().map(h -> h.getAmount() == null ? BigDecimal.ZERO : h.getAmount())
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		TripExpensePayoutHistoryEntity latest = history.stream().filter(h -> h.getCreatedAt() != null)
				.max(Comparator.comparing(TripExpensePayoutHistoryEntity::getCreatedAt)).orElse(null);

		BigDecimal remainingAmount = (latest != null && latest.getDueAmount() != null) ? latest.getDueAmount()
				: BigDecimal.ZERO;

		List<TripExpensePayoutHistoryResponseDto> payoutList = history.stream()
				.map(tripExpensePayoutHistoryMapper::entityToResponse).collect(Collectors.toList());

		String status = expenseRepository.getExpensePaidStatus(expenseId).stream().findFirst().orElse("pending");

		ExpenseResponseDto responseDto = expenseMapper.entityToResponse(expenseEntity);

		AccountContactEntity accountContactEntity = accountContactRepository
				.findByIdAndIsDeleteFalse(responseDto.getAccountContactId()).orElseThrow(() -> new RuntimeException(
						"Account contact not found with id : " + responseDto.getAccountContactId()));

		responseDto.setUserId(expenseEntity.getUser().getId());
		responseDto.setDetailRequestDtos(detailResponseDtos);
		responseDto.setFromDate(
				expenseEntity.getFromDate() != null ? commonService.dateTimeFormatted(expenseEntity.getFromDate())
						: null);
		responseDto.setToDate(
				expenseEntity.getToDate() != null ? commonService.dateTimeFormatted(expenseEntity.getToDate()) : null);
		responseDto.setDueDate(
				expenseEntity.getDueDate() != null ? commonService.dateTimeFormatted(expenseEntity.getDueDate())
						: null);
		responseDto.setPayoutHistory(payoutList);
		responseDto.setPayoutAmount(totalPaid);
		responseDto.setRemaingAmount(remainingAmount);
		responseDto.setStatus(status);
		responseDto.setAccountContactName(accountContactEntity.getName());

		return responseDto;
	}

//	@Override
//	public List<ExpenseResponseDto> getAllTripExpense(Long userId) {
//
//		List<ExpenseResponseDto> responseDtos = new ArrayList<>();
//		List<ExpenseEntity> entities = expenseRepository.findByUserIdAndIsDeleteFalse(userId);
//
//		for (ExpenseEntity entity : entities) {
//
//			List<ExpenseDetailResponseDto> detailResponseDtos = new ArrayList<>();
//			List<ExpenseDetailEntity> detailEntities = expenseDetailRepository
//					.findByExpenseIdAndIsDeleteFalse(entity.getId());
//			for (ExpenseDetailEntity expenseDetailEntity : detailEntities) {
//				ExpenseDetailResponseDto detailResponseDto = expenseDetailMapper.entityToResponse(expenseDetailEntity);
//				detailResponseDto.setExpenseId(expenseDetailEntity.getExpense().getId());
//				detailResponseDto.setUserId(expenseDetailEntity.getUser().getId());
//
//				detailResponseDtos.add(detailResponseDto);
//			}
//
//			List<TripExpensePayoutHistoryEntity> history = tripExpensePayoutHistoryRepository
//					.findAllByExpenseAndIsDeleteFalse(entity);
//			
//			List<TripExpensePayoutHistoryResponseDto> payoutList = history.stream()
//					.map(tripExpensePayoutHistoryMapper::entityToResponse).collect(Collectors.toList());
//			
//			BigDecimal totalPaid = history.stream().map(h -> h.getAmount() == null ? BigDecimal.ZERO : h.getAmount())
//					.reduce(BigDecimal.ZERO, BigDecimal::add);
//
//			TripExpensePayoutHistoryEntity latest = history.stream().filter(h -> h.getCreatedAt() != null)
//					.max(Comparator.comparing(TripExpensePayoutHistoryEntity::getCreatedAt)).orElse(null);
//
//			BigDecimal remainingAmount = (latest != null && latest.getDueAmount() != null) ? latest.getDueAmount()
//					: BigDecimal.ZERO;
//			
//			ExpenseResponseDto responseDto = expenseMapper.entityToResponse(entity);
//			responseDto.setMemberId(entity.getUser().getId());
//			responseDto.setDetailRequestDtos(detailResponseDtos);
//			responseDto.setPayoutHistory(payoutList);
//			responseDto.setPayoutAmount(totalPaid);
//			responseDto.setRemaingAmount(remainingAmount);
//			responseDto.setUserName(entity.getUser().getFirstName() + " " + entity.getUser().getLastName());
//
//			responseDtos.add(responseDto);
//		}
//
//		return responseDtos;
//	}

	@Override
	public List<ExpenseResponseDto> getAllTripExpense(Long userId) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));

		List<Object[]> rows = expenseRepository.getExpenseByUserId(userId, null, null, -1L);

		List<ExpenseResponseDto> response = mapExpenseResponse(rows);

//		List<ExpenseEntity> expenses = expenseRepository.findByUserIdAndIsDeleteFalse(userId);

//		if (expenses.isEmpty()) {
//			return Collections.emptyList();
//		}

//		List<Long> expenseIds = expenses.stream().map(ExpenseEntity::getId).collect(Collectors.toList());
//
//		List<ExpenseDetailEntity> allDetails = expenseDetailRepository.findByExpenseIdInAndIsDeleteFalse(expenseIds);
//
//		List<TripExpensePayoutHistoryEntity> allHistory = tripExpensePayoutHistoryRepository
//				.findByExpenseIdInAndIsDeleteFalse(expenseIds);
//
//		Map<Long, List<ExpenseDetailEntity>> detailMap = allDetails.stream()
//				.collect(Collectors.groupingBy(d -> d.getExpense().getId()));
//
//		Map<Long, List<TripExpensePayoutHistoryEntity>> historyMap = allHistory.stream()
//				.collect(Collectors.groupingBy(h -> h.getExpense().getId()));
//
//		return expenses.stream().map(entity -> {
//
//			List<ExpenseDetailResponseDto> detailDtos = detailMap.getOrDefault(entity.getId(), Collections.emptyList())
//					.stream().map(d -> {
//						ExpenseDetailResponseDto dto = expenseDetailMapper.entityToResponse(d);
//						dto.setExpenseId(d.getExpense().getId());
//						dto.setUserId(d.getUser().getId());
//						dto.setDocPath(environment.getProperty("app.image.url") + d.getDocPath());
//						return dto;
//					}).collect(Collectors.toList());
//
//			List<TripExpensePayoutHistoryEntity> history = historyMap.getOrDefault(entity.getId(),
//					Collections.emptyList());
//
//			String status = expenseRepository.getExpensePaidStatus(entity.getId()).stream().findFirst().orElse("pending");
//			
//			List<TripExpensePayoutHistoryResponseDto> payoutList = history.stream()
//					.map(tripExpensePayoutHistoryMapper::entityToResponse).collect(Collectors.toList());
//
//			BigDecimal totalPaid = history.stream().map(h -> h.getAmount() == null ? BigDecimal.ZERO : h.getAmount())
//					.reduce(BigDecimal.ZERO, BigDecimal::add);
//
//			TripExpensePayoutHistoryEntity latest = history.stream().filter(h -> h.getCreatedAt() != null)
//					.max(Comparator.comparing(TripExpensePayoutHistoryEntity::getCreatedAt)).orElse(null);
//
//			BigDecimal remainingAmount = (latest != null && latest.getDueAmount() != null) ? latest.getDueAmount()
//					: BigDecimal.ZERO;
//
//			ExpenseResponseDto response = expenseMapper.entityToResponse(entity);
//
////			response.setMemberId(entity.getUser().getId());
////			response.setUserName(entity.getUser().getFirstName() + " " + entity.getUser().getLastName());
//			response.setDetailRequestDtos(detailDtos);
//			response.setPayoutHistory(payoutList);
//			response.setPayoutAmount(totalPaid);
//			response.setRemaingAmount(remainingAmount);
//			response.setStatus(status);
//
//			return response;
//
//		}).collect(Collectors.toList());

		return response;
	}

	@Override
	@Transactional
	public TripExpensePayoutHistoryResponseDto payoutExpense(TripExpensePayoutRequestDto payout) {

		ExpenseEntity expense = expenseRepository.findByIdAndIsDeleteFalse(payout.getExpenseId())
				.orElseThrow(() -> new RuntimeException("Expense not found with id: " + payout.getExpenseId()));

		CashAccountEntity cashAccountEntity = null;
		BankDetailsEntity bankDetailsEntity = null;
		BigDecimal newBalance = BigDecimal.ZERO;

		AccountContactEntity accountContact = accountContactRepository
				.findByIdAndIsDeleteFalse(expense.getAccountContactId())
				.orElseThrow(() -> new RuntimeException("Account contact not found for this expense."));

		if (AccountType.CASH == payout.getAccountType()) {
			cashAccountEntity = cashAccountRepository.findByIdAndIsDeleteFalse(payout.getCashAccountId()).orElseThrow(
					() -> new RuntimeException("Cash account not found with id : " + payout.getCashAccountId()));
		} else {
			bankDetailsEntity = bankDetailsRepository.findByIdAndIsDeleteFalse(payout.getBankAccountId()).orElseThrow(
					() -> new RuntimeException("Bank account not found with id : " + payout.getBankAccountId()));
		}

		BigDecimal oldAmount = BigDecimal.ZERO;

		TripExpensePayoutHistoryEntity entity = null;
		if (payout.getPayoutId() == -1) {
			entity = tripExpensePayoutHistoryMapper.requestToEntity(payout);
			entity.setExpense(expense);

			newBalance = AccountType.CASH == payout.getAccountType()
					? cashAccountEntity.getCurrentBalance().subtract(payout.getPayoutAmount())
					: bankDetailsEntity.getCurrentBalance().subtract(payout.getPayoutAmount());
		} else {
			entity = tripExpensePayoutHistoryRepository.findByIdAndIsDeleteFalse(payout.getPayoutId())
					.orElseThrow(() -> new RuntimeException("Payout not found for id : " + payout.getPayoutId()));

			oldAmount = entity.getAmount();

			entity.setTransactionId(payout.getTransactionId());
			entity.setChequeNo(payout.getChequeNo());
			entity.setAmount(payout.getPayoutAmount());
			entity.setDueAmount(payout.getDueAmount());
			entity.setPaymentMode(payout.getPaymentMode());
			entity.setDescription(payout.getDescription());

			newBalance = AccountType.CASH == payout.getAccountType()
					? cashAccountEntity.getCurrentBalance().add(oldAmount).subtract(payout.getPayoutAmount())
					: bankDetailsEntity.getCurrentBalance().add(oldAmount).subtract(payout.getPayoutAmount());
		}

		accountContact.setCurrentBalance(accountContact.getCurrentBalance().add(newBalance));
		accountContactRepository.save(accountContact);

		if (AccountType.CASH == payout.getAccountType()) {
			entity.setCashType(cashAccountEntity);
			entity.setBankAccount(null);
			cashAccountEntity.setCurrentBalance(newBalance);
			cashAccountRepository.save(cashAccountEntity);
		} else {
			entity.setBankAccount(bankDetailsEntity);
			entity.setCashType(null);
			bankDetailsEntity.setCurrentBalance(newBalance);
			bankDetailsRepository.save(bankDetailsEntity);
		}

		entity.setPaymentDate(LocalDate.parse(payout.getPaymentDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));

		BigDecimal totalPaidAmount = tripExpensePayoutHistoryRepository.getTotalPaidAmountByInvoice(expense);

		totalPaidAmount = totalPaidAmount.subtract(oldAmount);
		totalPaidAmount = totalPaidAmount.add(payout.getPayoutAmount());

		if (expense.getTotalAmount().compareTo(totalPaidAmount) == 0) {
			entity.setStatus("confirm");
		} else if (expense.getTotalAmount().compareTo(totalPaidAmount) > 0) {
			entity.setStatus("pending");
		} else {
			throw new RuntimeException("Paid amount exceeds invoice total.");
//			entity.setStatus("advanced");
		}

		entity = tripExpensePayoutHistoryRepository.save(entity);

		TripExpensePayoutHistoryResponseDto response = tripExpensePayoutHistoryMapper.entityToResponse(entity);

		return response;
	}

	@Override
	@Transactional
	public Boolean deleteTripPayout(Long payoutId) {
		TripExpensePayoutHistoryEntity payout = tripExpensePayoutHistoryRepository.findByIdAndIsDeleteFalse(payoutId)
				.orElseThrow(() -> new RuntimeException("Payout not found with id: " + payoutId));

		ExpenseEntity expense = payout.getExpense();
		BigDecimal amount = payout.getAmount();

		AccountContactEntity accountContact = accountContactRepository
				.findByIdAndIsDeleteFalse(expense.getAccountContactId())
				.orElseThrow(() -> new RuntimeException("Account contact not found for this payout."));

		accountContact.setCurrentBalance(accountContact.getCurrentBalance().subtract(amount));
		accountContactRepository.save(accountContact);

		if (payout.getCashType() != null) {
			CashAccountEntity cash = payout.getCashType();
			cash.setCurrentBalance(cash.getCurrentBalance().add(amount));
			cashAccountRepository.save(cash);
		} else if (payout.getBankAccount() != null) {
			BankDetailsEntity bank = payout.getBankAccount();
			bank.setCurrentBalance(bank.getCurrentBalance().add(amount));
			bankDetailsRepository.save(bank);
		} else {
			throw new RuntimeException("No payment source found (cash/bank)");
		}

		payout.setIsDelete(true);
		payout.setUpdatedAt(LocalDateTime.now());
		tripExpensePayoutHistoryRepository.save(payout);

		return true;
	}

	@Override
	public List<TripExpensePayoutHistoryResponseDto> getAllTripPayoutByExpenseId(Long expenseId) {
		ExpenseEntity expense = expenseRepository.findByIdAndIsDeleteFalse(expenseId)
				.orElseThrow(() -> new RuntimeException("Expense not found with id : " + expenseId));

		List<TripExpensePayoutHistoryEntity> payouts = tripExpensePayoutHistoryRepository
				.findAllByExpenseAndIsDeleteFalse(expense);

		return payouts.stream().map(tripExpensePayoutHistoryMapper::entityToResponse).collect(Collectors.toList());
	}

	@Override
	public TripExpensePayoutHistoryResponseDto getTripPayoutByPayoutId(Long payoutId) {
		TripExpensePayoutHistoryEntity entity = tripExpensePayoutHistoryRepository.findByIdAndIsDeleteFalse(payoutId)
				.orElseThrow(() -> new RuntimeException("Expense payout not found with id : " + payoutId));

		return tripExpensePayoutHistoryMapper.entityToResponse(entity);
	}

	@Override
	public Boolean deleteExpense(Long expenseId) {
		ExpenseEntity expenseEntity = expenseRepository.findByIdAndIsDeleteFalse(expenseId)
				.orElseThrow(() -> new RuntimeException("Expense not found with id: " + expenseId));

		expenseEntity.setIsDelete(true);
		expenseRepository.save(expenseEntity);

		List<ExpenseDetailEntity> detailEntities = expenseDetailRepository.findByExpenseIdAndIsDeleteFalse(expenseId);
		for (ExpenseDetailEntity expenseDetailEntity : detailEntities) {
			expenseDetailEntity.setIsDelete(true);
			expenseDetailRepository.save(expenseDetailEntity);
		}

		return true;

	}

	@Override
	@Transactional
	public OfficeExpenseResponseDto addOrUpdateOfficeExpense(OfficeExpenseRequestDto request) {

		if (request.getExpenseType() == null || request.getExpenseType().isEmpty()) {
			throw new RuntimeException("Please add expense type. ");
		}

		UserMasterEntity userEntity = userMasterRepository.findById(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

		userMasterRepository.findByIdAndIsDeleteFalse(request.getAdminId())
				.orElseThrow(() -> new RuntimeException("Admin not found with id : " + request.getAdminId()));

		IncomeExpenseTypeEntity incomeExpenseTypeEntity = incomeExpenseTypeRepository
				.findByTypeIdAndIsDeleteFalse(request.getIncomeExpenseTypeId()).orElseThrow(() -> new RuntimeException(
						"Income/Expense type is not found with id : " + request.getIncomeExpenseTypeId()));

		AccountContactEntity accountContactEntity = accountContactRepository
				.findByIdAndIsDeleteFalse(request.getAccountContactId()).orElseThrow(() -> new RuntimeException(
						"Account contact not found with id : " + request.getAccountContactId()));

		OfficeExpenseEntity officeExpenseEntity;
		if (request.getId() == -1) {
			officeExpenseEntity = officeExpenseMapper.requestToEntity(request);
		} else {
			officeExpenseEntity = officeExpenseRepository.findByIdAndIsDeleteFalse(request.getId())
					.orElseThrow(() -> new RuntimeException("Expense not found with id: " + request.getId()));
			officeExpenseEntity = officeExpenseMapper.updateEntityFromRequest(request, officeExpenseEntity);
			officeExpenseEntity.setUpdatedAt(commonService.getCurrentDateTime());
		}
		officeExpenseEntity.setIncomeExpenseType(incomeExpenseTypeEntity);

		if (request.getDueDate() != null) {
			officeExpenseEntity.setDueDate(commonService.dateTimeFormatted(request.getDueDate()));
		}

		officeExpenseEntity.setUser(userEntity);

		officeExpenseEntity = officeExpenseRepository.save(officeExpenseEntity);

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		if (request.getPaidDate() != null && request.getPaidDate().trim().length() != 0) {
			List<OfficeExpensePayoutHistoryEntity> oldHistory = officeExpensePayoutHistoryRepository
					.findAllByOfficeExpenseAndIsDeleteFalse(officeExpenseEntity);

			oldHistory.forEach(h -> h.setIsDelete(true));
			officeExpensePayoutHistoryRepository.saveAll(oldHistory);

			CashAccountEntity cashAccountEntity = cashAccountRepository.findByUserIdAndIsPrimaryTrueAndIsDeleteFalse(1L)
					.orElseThrow(() -> new RuntimeException("Primary cash account not found."));

			OfficeExpensePayoutHistoryEntity payout = new OfficeExpensePayoutHistoryEntity();
			payout.setOfficeExpense(officeExpenseEntity);
			payout.setPaymentDate(LocalDate.parse(request.getPaidDate(), dateFormatter));
			payout.setAmount(officeExpenseEntity.getExpenseAmount());
			payout.setDueAmount(BigDecimal.ZERO);
			payout.setPaymentMode(PaymentMode.CASH);
			payout.setCashType(cashAccountEntity);
			payout.setDescription("");
			payout.setStatus("paid");

			officeExpensePayoutHistoryRepository.save(payout);

			accountContactEntity.setCurrentBalance(accountContactEntity.getCurrentBalance().add(payout.getAmount()));
			accountContactRepository.save(accountContactEntity);
		}

		if (request.getDoc() != null && !request.getDoc().isEmpty()) {
			try {
				for (MultipartFile file : request.getDoc()) {
					OfficeExpenseDocEntity docEntity = new OfficeExpenseDocEntity();
					docEntity.setIsDelete(false);
					docEntity.setDocPath(null);
					docEntity.setOfficeExpense(officeExpenseEntity);
					docEntity = officeExpenseDocRepository.save(docEntity);
					userFileService.storeFile(request.getAdminId(), ModuleName.OFFICEEXPENSEFILE.toString(),
							docEntity.getId(), FileType.RECEIPT.toString(), file);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		List<OfficeExpensePayoutHistoryEntity> payoutHistory = officeExpensePayoutHistoryRepository
				.findAllByOfficeExpenseAndIsDeleteFalse(officeExpenseEntity);

		BigDecimal totalAmount = officeExpenseEntity.getExpenseAmount() != null ? officeExpenseEntity.getExpenseAmount()
				: BigDecimal.ZERO;
		BigDecimal paidAmount = payoutHistory.stream().map(OfficeExpensePayoutHistoryEntity::getAmount)
				.filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal unpaidAmount = totalAmount.subtract(paidAmount);

		String status = officeExpenseRepository.getExpensePaidStatus(officeExpenseEntity.getId()).stream().findFirst()
				.orElse("pending");

		OfficeExpenseResponseDto responseDto = officeExpenseMapper.entityToResponse(officeExpenseEntity);
		responseDto.setAccountContactId(accountContactEntity.getId());
		responseDto.setUserId(officeExpenseEntity.getUser().getId());
		responseDto.setAccountContactName(accountContactEntity.getName());
		responseDto.setPayoutHistory(payoutHistory.stream().map(officeExpensePayoutHistoryMapper::entityToResponse)
				.collect(Collectors.toList()));
		responseDto.setExpenseAmount(totalAmount);
		responseDto.setPayoutAmount(paidAmount);
		responseDto.setRemaingAmount(unpaidAmount);
		responseDto.setStatus(status);
		responseDto.setIncomeExpenseTypeId(officeExpenseEntity.getIncomeExpenseType().getTypeId());
		responseDto.setIncomeExpenseTypeName(officeExpenseEntity.getIncomeExpenseType().getName());

		return responseDto;
	}

	@Override
	public List<OfficeExpensePayoutResponseDto> getAllOfficePayoutByExpenseId(Long expenseId) {
		OfficeExpenseEntity expense = officeExpenseRepository.findByIdAndIsDeleteFalse(expenseId)
				.orElseThrow(() -> new RuntimeException("Expense not found with id : " + expenseId));

		List<OfficeExpensePayoutHistoryEntity> payouts = officeExpensePayoutHistoryRepository
				.findAllByOfficeExpenseAndIsDeleteFalse(expense);

		return payouts.stream().map(officeExpensePayoutHistoryMapper::entityToResponse).collect(Collectors.toList());
	}

	@Override
	public OfficeExpensePayoutResponseDto getOfficePayoutByPayoutId(Long payoutId) {
		OfficeExpensePayoutHistoryEntity expense = officeExpensePayoutHistoryRepository
				.findByIdAndIsDeleteFalse(payoutId)
				.orElseThrow(() -> new RuntimeException("Expense not found with id : " + payoutId));

		return officeExpensePayoutHistoryMapper.entityToResponse(expense);
	}

	@Override
	public Boolean deleteOfficeExpense(Long expenseId) {
		OfficeExpenseEntity officeExpenseEntity = officeExpenseRepository.findByIdAndIsDeleteFalse(expenseId)
				.orElseThrow(() -> new RuntimeException("Expense not found with id: " + expenseId));

		officeExpenseEntity.setIsDelete(true);
		officeExpenseRepository.save(officeExpenseEntity);

		return true;
	}

	@Override
	@Transactional
	public Boolean deleteOfficePayout(Long payoutId) {
		OfficeExpensePayoutHistoryEntity payout = officeExpensePayoutHistoryRepository
				.findByIdAndIsDeleteFalse(payoutId)
				.orElseThrow(() -> new RuntimeException("Payout not found with id: " + payoutId));

		OfficeExpenseEntity expense = payout.getOfficeExpense();
		BigDecimal amount = payout.getAmount();

		AccountContactEntity accountContact = accountContactRepository
				.findByIdAndIsDeleteFalse(expense.getAccountContactId())
				.orElseThrow(() -> new RuntimeException("Account contact not found for this payout."));

		accountContact.setCurrentBalance(accountContact.getCurrentBalance().subtract(amount));
		accountContactRepository.save(accountContact);

		if (payout.getCashType() != null) {
			CashAccountEntity cash = payout.getCashType();
			cash.setCurrentBalance(cash.getCurrentBalance().add(amount));
			cashAccountRepository.save(cash);
		} else if (payout.getBankAccount() != null) {
			BankDetailsEntity bank = payout.getBankAccount();
			bank.setCurrentBalance(bank.getCurrentBalance().add(amount));
			bankDetailsRepository.save(bank);
		} else {
			throw new RuntimeException("No payment source found (cash/bank)");
		}

		payout.setIsDelete(true);
		payout.setUpdatedAt(LocalDateTime.now());
		officeExpensePayoutHistoryRepository.save(payout);

		return true;
	}

	@Override
	@Transactional
	public OfficeExpensePayoutResponseDto payoutOfficeExpense(OfficeExpensePayoutRequestDto request) {
		OfficeExpenseEntity officeExpenseEntity = officeExpenseRepository
				.findByIdAndIsDeleteFalse(request.getOfficeExpenseId())
				.orElseThrow(() -> new RuntimeException("Expense not found with id: " + request.getOfficeExpenseId()));

		CashAccountEntity cashAccountEntity = null;
		BankDetailsEntity bankDetailsEntity = null;
		BigDecimal newBalance = BigDecimal.ZERO;

		AccountContactEntity accountContact = accountContactRepository
				.findByIdAndIsDeleteFalse(officeExpenseEntity.getAccountContactId())
				.orElseThrow(() -> new RuntimeException("Account contact not found for this expense."));

		if (AccountType.CASH == request.getAccountType()) {
			cashAccountEntity = cashAccountRepository.findByIdAndIsDeleteFalse(request.getCashAccountId()).orElseThrow(
					() -> new RuntimeException("Cash account not found with id : " + request.getCashAccountId()));
		} else {
			bankDetailsEntity = bankDetailsRepository.findByIdAndIsDeleteFalse(request.getBankAccountId()).orElseThrow(
					() -> new RuntimeException("Bank account not found with id : " + request.getBankAccountId()));
		}
		BigDecimal oldAmount = BigDecimal.ZERO;

		OfficeExpensePayoutHistoryEntity entity = null;
		if (request.getPayoutId() == -1) {
			entity = officeExpensePayoutHistoryMapper.requestToEntity(request);
			entity.setOfficeExpense(officeExpenseEntity);

			newBalance = AccountType.CASH == request.getAccountType()
					? cashAccountEntity.getCurrentBalance().subtract(request.getAmount())
					: bankDetailsEntity.getCurrentBalance().subtract(request.getAmount());
		} else {
			entity = officeExpensePayoutHistoryRepository.findByIdAndIsDeleteFalse(request.getPayoutId())
					.orElseThrow(() -> new RuntimeException("Payout not found for id : " + request.getPayoutId()));

			oldAmount = entity.getAmount();

			entity.setTransactionId(request.getTransactionId());
			entity.setChequeNo(request.getChequeNo());
			entity.setAmount(request.getAmount());
			entity.setDueAmount(request.getDueAmount());
			entity.setPaymentMode(request.getPaymentMode());
			entity.setDescription(request.getDescription());

			newBalance = AccountType.CASH == request.getAccountType()
					? cashAccountEntity.getCurrentBalance().add(oldAmount).subtract(request.getAmount())
					: bankDetailsEntity.getCurrentBalance().add(oldAmount).subtract(request.getAmount());
		}

		accountContact.setCurrentBalance(accountContact.getCurrentBalance().add(newBalance));
		accountContactRepository.save(accountContact);

		if (AccountType.CASH == request.getAccountType()) {
			entity.setCashType(cashAccountEntity);
			entity.setBankAccount(null);
			cashAccountEntity.setCurrentBalance(newBalance);
			cashAccountRepository.save(cashAccountEntity);
		} else {
			entity.setBankAccount(bankDetailsEntity);
			entity.setCashType(null);
			bankDetailsEntity.setCurrentBalance(newBalance);
			bankDetailsRepository.save(bankDetailsEntity);
		}

		entity.setPaymentDate(LocalDate.parse(request.getPaymentDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));

		BigDecimal totalPaidAmount = officeExpensePayoutHistoryRepository
				.getTotalPaidAmountByInvoice(officeExpenseEntity);

		totalPaidAmount = totalPaidAmount.subtract(oldAmount);
		totalPaidAmount = totalPaidAmount.add(request.getAmount());

		if (officeExpenseEntity.getExpenseAmount().compareTo(totalPaidAmount) == 0) {
			entity.setStatus("confirm");
		} else if (officeExpenseEntity.getExpenseAmount().compareTo(totalPaidAmount) > 0) {
			entity.setStatus("pending");
		} else {
			throw new RuntimeException("Paid amount exceeds invoice total.");
//			entity.setStatus("advanced");
		}

		entity = officeExpensePayoutHistoryRepository.save(entity);

		OfficeExpensePayoutResponseDto response = officeExpensePayoutHistoryMapper.entityToResponse(entity);

		return response;
	}

	@Override
	public List<OfficeExpenseResponseDto> getAllOfficeExpense(Long userId) {
//		List<OfficeExpenseResponseDto> responseDtos = new ArrayList<>();
//		List<OfficeExpenseEntity> entities = officeExpenseRepository.findByUserIdAndIsDeleteFalse(userId);

//		for (OfficeExpenseEntity entity : entities) {
//			List<OfficeExpensePayoutHistoryEntity> payoutHistory = officeExpensePayoutHistoryRepository
//					.findAllByOfficeExpenseAndIsDeleteFalse(entity);
//
//			BigDecimal totalAmount = entity.getExpenseAmount() != null ? entity.getExpenseAmount() : BigDecimal.ZERO;
//			BigDecimal paidAmount = payoutHistory.stream().map(OfficeExpensePayoutHistoryEntity::getAmount)
//					.filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
//			BigDecimal unpaidAmount = totalAmount.subtract(paidAmount);
//
//			AccountContactEntity accountContactEntity = accountContactRepository.findByIdAndIsDeleteFalse(entity.getAccountContactId())
//					.orElseThrow(() -> new RuntimeException("Account contact not found with id : " + entity.getAccountContactId()));
//			
//			OfficeExpenseResponseDto responseDto = officeExpenseMapper.entityToResponse(entity);
//			responseDto.setUserId(entity.getId());
//			responseDto.setPayoutHistory(payoutHistory.stream().map(officeExpensePayoutHistoryMapper::entityToResponse)
//					.collect(Collectors.toList()));
//			responseDto.setExpenseAmount(totalAmount);
//			responseDto.setPayoutAmount(paidAmount);
//			responseDto.setRemaingAmount(unpaidAmount);
//			responseDto.setAccountContactName(accountContactEntity.getName());
//			responseDto.setIncomeExpenseTypeId(entity.getIncomeExpenseType().getTypeId());
//			responseDto.setIncomeExpenseTypeName(entity.getIncomeExpenseType().getName());
//			
//			responseDtos.add(responseDto);
//		}

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));

		List<Object[]> rows = officeExpenseRepository.getOfficeExpenseData(user.getId(), null, null, null, -1l);

		List<OfficeExpenseResponseDto> responseDtos = mapOfficeExpenseResponse(rows);

		return responseDtos;
	}

	@Override
	public OfficeExpenseResponseDto getOfficeExpenseByExpenseId(Long expenseId) {

		OfficeExpenseEntity officeExpenseEntity = officeExpenseRepository.findByIdAndIsDeleteFalse(expenseId)
				.orElseThrow(() -> new RuntimeException("Expense not found with id: " + expenseId));

		List<OfficeExpensePayoutHistoryEntity> payoutHistory = officeExpensePayoutHistoryRepository
				.findAllByOfficeExpenseAndIsDeleteFalse(officeExpenseEntity);

		BigDecimal totalAmount = officeExpenseEntity.getExpenseAmount() != null ? officeExpenseEntity.getExpenseAmount()
				: BigDecimal.ZERO;
		BigDecimal paidAmount = payoutHistory.stream().map(OfficeExpensePayoutHistoryEntity::getAmount)
				.filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal unpaidAmount = totalAmount.subtract(paidAmount);

		String status = officeExpenseRepository.getExpensePaidStatus(officeExpenseEntity.getId()).stream().findFirst()
				.orElse("pending");

		OfficeExpenseResponseDto responseDto = officeExpenseMapper.entityToResponse(officeExpenseEntity);

		AccountContactEntity accountContactEntity = accountContactRepository
				.findByIdAndIsDeleteFalse(responseDto.getAccountContactId()).orElseThrow(() -> new RuntimeException(
						"Account contact not found with id : " + responseDto.getAccountContactId()));

		responseDto.setUserId(officeExpenseEntity.getUser().getId());
		responseDto.setFiles(new ArrayList<>());
		responseDto.setDueDate(officeExpenseEntity.getDueDate() != null
				? commonService.dateTimeFormatted(officeExpenseEntity.getDueDate())
				: null);
		responseDto.setExpenseDate(officeExpenseEntity.getExpenseDate() != null
				? commonService.dateTimeFormatted(officeExpenseEntity.getExpenseDate())
				: null);
		responseDto.setPayoutHistory(payoutHistory.stream().map(officeExpensePayoutHistoryMapper::entityToResponse)
				.collect(Collectors.toList()));
		responseDto.setExpenseAmount(totalAmount);
		responseDto.setPayoutAmount(paidAmount);
		responseDto.setRemaingAmount(unpaidAmount);
		responseDto.setAccountContactName(accountContactEntity.getName());
		responseDto.setRemarks(officeExpenseEntity.getRemarks());
		responseDto.setStatus(status);
		responseDto.setIncomeExpenseTypeId(officeExpenseEntity.getIncomeExpenseType().getTypeId());
		responseDto.setIncomeExpenseTypeName(officeExpenseEntity.getIncomeExpenseType().getName());
		List<OfficeExpenseDocEntity> entities = officeExpenseDocRepository
				.findAllByOfficeExpenseAndIsDeleteFalse(officeExpenseEntity);
		List<OfficeExpenseDocResponseDto> docResponseDtos = new ArrayList<>();
		for (OfficeExpenseDocEntity officeExpenseDocEntity : entities) {
			OfficeExpenseDocResponseDto docResponseDto = new OfficeExpenseDocResponseDto();
			docResponseDto.setId(officeExpenseDocEntity.getId());
			docResponseDto.setDocPath(environment.getProperty("app.image.url") + officeExpenseDocEntity.getDocPath());
			docResponseDtos.add(docResponseDto);
		}
		responseDto.setFiles(docResponseDtos);
		return responseDto;
	}

	@Override
	public Map<String, Object> getOfficeExpenseByExpenseType(Long incomeExpenseTypeId, Long userId, String startDate,
			String endDate, Long accountContactId) {
		Map<String, Object> map = new HashMap<>();

		LocalDateTime startDateFormated = startDate != null ? commonService.dateTimeFormatted(startDate) : null;
		LocalDateTime endDateFormated = endDate != null ? commonService.dateTimeFormatted2(endDate) : null;

		List<OfficeExpenseEntity> entities = new ArrayList<>();

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));

		if (incomeExpenseTypeId != null) {
			IncomeExpenseTypeEntity incomeExpenseType = incomeExpenseTypeRepository
					.findByTypeIdAndIsDeleteFalse(incomeExpenseTypeId).orElseThrow(() -> new RuntimeException(
							"Income/Expense type not found with id : " + incomeExpenseTypeId));
		}

//		if (userId == 1L) {
//			entities = officeExpenseRepository.getByExpenseTypeIgnoreCaseAndIsDeleteFalseAndExpenseDateBetween(
//					expenseType, startDateFormated, endDateFormated);
//		} else {
//			entities = officeExpenseRepository
//					.getByExpenseTypeIgnoreCaseAndUserIdAndIsDeleteFalseAndExpenseDateBetween(expenseType, userId,
//							startDateFormated, endDateFormated);
//		}

		List<Object[]> rows = officeExpenseRepository.getOfficeExpenseData(user.getId(), startDateFormated,
				endDateFormated, incomeExpenseTypeId, accountContactId);

		List<OfficeExpenseResponseDto> responseDtos = mapOfficeExpenseResponse(rows);

		BigDecimal totalExpense = BigDecimal.ZERO;
		BigDecimal remaingExpense = BigDecimal.ZERO;
		BigDecimal paidExpense = BigDecimal.ZERO;

		for (OfficeExpenseResponseDto dto : responseDtos) {
			if (dto != null) {
				totalExpense = totalExpense
						.add(dto.getExpenseAmount() != null ? dto.getExpenseAmount() : BigDecimal.ZERO);
				remaingExpense = remaingExpense
						.add(dto.getRemaingAmount() != null ? dto.getRemaingAmount() : BigDecimal.ZERO);
				paidExpense = paidExpense.add(dto.getPayoutAmount() != null ? dto.getPayoutAmount() : BigDecimal.ZERO);
			}
		}

//		for (OfficeExpenseEntity entity : entities) {
//			List<OfficeExpensePayoutHistoryEntity> payoutHistory = officeExpensePayoutHistoryRepository
//					.findAllByOfficeExpenseAndIsDeleteFalse(entity);
//
//			BigDecimal totalAmount = entity.getExpenseAmount() != null ? entity.getExpenseAmount() : BigDecimal.ZERO;
//			BigDecimal paidAmount = payoutHistory.stream().map(OfficeExpensePayoutHistoryEntity::getAmount)
//					.filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
//			BigDecimal unpaidAmount = totalAmount.subtract(paidAmount);
//
//			String status = officeExpenseRepository.getExpensePaidStatus(entity.getId()).stream().findFirst().orElse("pending");
//			
//			OfficeExpenseResponseDto responseDto = officeExpenseMapper.entityToResponse(entity);
//			
//			AccountContactEntity accountContactEntity = accountContactRepository.findByIdAndIsDeleteFalse(responseDto.getAccountContactId())
//					.orElseThrow(() -> new RuntimeException("Account contact not found with id : " + responseDto.getAccountContactId()));
//			
//			responseDto.setUserId(entity.getUser().getId());
//			responseDto.setPayoutHistory(payoutHistory.stream().map(officeExpensePayoutHistoryMapper::entityToResponse)
//					.collect(Collectors.toList()));
//			responseDto.setExpenseAmount(totalAmount);
//			responseDto.setPayoutAmount(paidAmount);
//			responseDto.setRemaingAmount(unpaidAmount);
//			responseDto.setAccountContactName(accountContactEntity.getName());
//			responseDto.setStatus(status);
//			responseDto.setIncomeExpenseTypeId(entity.getIncomeExpenseType().getTypeId());
//			responseDto.setIncomeExpenseTypeName(entity.getIncomeExpenseType().getName());
//
//			totalExpense = totalExpense.add(nullSafe(totalAmount));
//			remaingExpense = remaingExpense.add(nullSafe(unpaidAmount));
//			paidExpense = paidExpense.add(nullSafe(paidAmount));
//
//			responseDtos.add(responseDto);
//		}

		map.put("data", responseDtos);
		map.put("total_expense", totalExpense);
		map.put("remaing_expense", remaingExpense);
		map.put("paid_expense", paidExpense);

		return map;
	}

	@Override
	public Map<String, Object> getTripExpenseByExpenseType(String expenseType, Long userId, String startDate,
			String endDate, Long accountContactId) {
		Map<String, Object> map = new HashMap<>();

		LocalDateTime startDateFormated = startDate != null ? commonService.dateTimeFormatted(startDate) : null;
		LocalDateTime endDateFormated = endDate != null ? commonService.dateTimeFormatted2(endDate) : null;

//		if (userId == 1L) {
//			entities = expenseRepository.getByExpenseTypeIgnoreCaseAndIsDeleteFalseAndFromDateBetween(expenseType,
//					startDateFormated, endDateFormated);
//		} else {
//			entities = expenseRepository.getByExpenseTypeIgnoreCaseAndUserIdAndIsDeleteFalseAndFromDateBetween(
//					expenseType, userId, startDateFormated, endDateFormated);
//		}		

		List<Object[]> rows = expenseRepository.getExpenseByUserId(userId, startDateFormated, endDateFormated, accountContactId);

		List<ExpenseResponseDto> responseDtos = mapExpenseResponse(rows);

		BigDecimal totalExpense = BigDecimal.ZERO;
		BigDecimal remaingExpense = BigDecimal.ZERO;
		BigDecimal paidExpense = BigDecimal.ZERO;

		for (ExpenseResponseDto dto : responseDtos) {
			if (dto != null) {
				totalExpense = totalExpense.add(dto.getTotalAmount() != null ? dto.getTotalAmount() : BigDecimal.ZERO);
				remaingExpense = remaingExpense
						.add(dto.getRemaingAmount() != null ? dto.getRemaingAmount() : BigDecimal.ZERO);
				paidExpense = paidExpense.add(dto.getPayoutAmount() != null ? dto.getPayoutAmount() : BigDecimal.ZERO);
			}
		}

//		for (ExpenseEntity entity : entities) {
//			List<ExpenseDetailResponseDto> detailResponseDtos = new ArrayList<>();
//			List<ExpenseDetailEntity> detailEntities = expenseDetailRepository
//					.findByExpenseIdAndIsDeleteFalse(entity.getId());
//			for (ExpenseDetailEntity expenseDetailEntity : detailEntities) {
//				ExpenseDetailResponseDto detailResponseDto = expenseDetailMapper.entityToResponse(expenseDetailEntity);
//				detailResponseDto.setExpenseId(expenseDetailEntity.getExpense().getId());
//				detailResponseDto.setUserId(expenseDetailEntity.getUser().getId());
//				detailResponseDto
//						.setDocPath(environment.getProperty("app.image.url") + expenseDetailEntity.getDocPath());
//				detailResponseDtos.add(detailResponseDto);
//			}
//
//			List<TripExpensePayoutHistoryEntity> tripPayoutHistoryEntity = tripExpensePayoutHistoryRepository
//					.findAllByExpenseAndIsDeleteFalse(entity);
//
//			
//			BigDecimal totalAmount = entity.getTotalAmount();
//			BigDecimal totalPaidAmount = tripPayoutHistoryEntity.stream().map(TripExpensePayoutHistoryEntity::getAmount)
//					.filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
//			BigDecimal totalUnpaidAmount = totalAmount.subtract(totalExpense);
//
//			String status = expenseRepository.getExpensePaidStatus(entity.getId()).stream().findFirst().orElse("pending");
//			
//			ExpenseResponseDto responseDto = expenseMapper.entityToResponse(entity);
////			responseDto.setMemberId(entity.getUser().getId());
////			responseDto.setUserName(entity.getUser().getFirstName() + " " + entity.getUser().getLastName());
//			responseDto.setDetailRequestDtos(detailResponseDtos);
//			responseDto.setPayoutHistory(tripPayoutHistoryEntity.stream()
//					.map(tripExpensePayoutHistoryMapper::entityToResponse).collect(Collectors.toList()));
//			responseDto.setPayoutAmount(paidExpense);
//			responseDto.setRemaingAmount(remaingExpense);
//			responseDto.setStatus(status);
//
//			responseDtos.add(responseDto);
//			
//			totalExpense = totalExpense.add(nullSafe(totalAmount));
//			remaingExpense = remaingExpense.add(nullSafe(totalUnpaidAmount));
//			paidExpense = paidExpense.add(nullSafe(totalPaidAmount));
//		}

		map.put("data", responseDtos);
		map.put("total_expense", totalExpense);
		map.put("remaing_expense", remaingExpense);
		map.put("paid_expense", paidExpense);

		return map;
	}

	private BigDecimal nullSafe(BigDecimal value) {
		return value != null ? value : BigDecimal.ZERO;
	}

	@Override
	public Map<String, Object> addOrUpdateCloseDate(String startDate, String closeDate, Long userId) {

		Map<String, Object> response = new HashMap<>();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
		LocalDate closeLocalDate = LocalDate.parse(closeDate, formatter);

		LocalDateTime startDateTime = startLocalDate.atStartOfDay();
		LocalDateTime closeDateTime = closeLocalDate.atTime(23, 59, 59);

		// extract month & year
		Integer year = startLocalDate.getYear();
		Integer month = startLocalDate.getMonthValue();

		// optional: prevent duplicate month
		Optional<CloseDateEntity> existing = closeDateRepository.findByYearAndMonthAndUserId(year, month, userId);

		CloseDateEntity entity;

		if (!existing.isPresent()) {
			entity = new CloseDateEntity();
			entity.setYear(year);
			entity.setMonth(month);
		} else {
			entity = existing.get();
		}

		entity.setStartDate(startDateTime);
		entity.setCloseDate(closeDateTime);
		entity.setUserId(userId);

		entity = closeDateRepository.save(entity);

		response.put("startDate", entity.getStartDate());
		response.put("closeDate", entity.getCloseDate());
		response.put("year", entity.getYear());
		response.put("month", entity.getMonth());

		return response;
	}

	@Override
	public Map<String, Object> getCloseDate(Integer month, Integer year, Long userId) {
		Map<String, Object> response = new HashMap<>();

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		if (month != null && month != -1) {
			Optional<CloseDateEntity> optional = closeDateRepository.findByMonthAndYearAndUserId(month, year, userId);
			CloseDateEntity closeDate;

			if (optional.isPresent()) {

				closeDate = optional.get();

			} else {

				int prevMonth = (month == 1) ? 12 : month - 1;
				int prevYear = (month == 1) ? year - 1 : year;

				CloseDateEntity prev = closeDateRepository.findByMonthAndYearAndUserId(prevMonth, prevYear, userId)
						.orElseThrow(
								() -> new RuntimeException("Start & End date not found for this and previous month."));

				// new start = prev end + 1 day
				LocalDate newStartDate = prev.getCloseDate().toLocalDate().plusDays(1);

				// new end = start + 1 month - 1 day
				LocalDate newEndDate = newStartDate.plusMonths(1).minusDays(1);

				LocalDateTime startDate = newStartDate.atStartOfDay();
				LocalDateTime endDate = newEndDate.atTime(23, 59, 59);

				closeDate = new CloseDateEntity();
				closeDate.setStartDate(startDate);
				closeDate.setCloseDate(endDate);
				closeDate.setMonth(month);
				closeDate.setYear(year);

				closeDate = closeDateRepository.save(closeDate);

			}
			response.put("startDate",
					closeDate.getStartDate() != null ? closeDate.getStartDate().format(dateFormatter) : null);
			response.put("closeDate",
					closeDate.getCloseDate() != null ? closeDate.getCloseDate().format(dateFormatter) : null);
			response.put("month", closeDate.getMonth());
			response.put("year", closeDate.getYear());
		} else {
			List<CloseDateEntity> existingList = closeDateRepository.findAllByYearAndUserIdOrderByMonthAsc(year,
					userId);

			if (existingList.isEmpty()) {
				LocalDate startDate = LocalDate.of(year, 1, 1);

				for (int monthNo = 1; monthNo <= 12; monthNo++) {

					LocalDate endDate = startDate.plusMonths(1).minusDays(1);

					CloseDateEntity entity = new CloseDateEntity();
					entity.setMonth(monthNo);
					entity.setYear(year);
					entity.setUserId(userId);
					entity.setStartDate(startDate.atStartOfDay());
					entity.setCloseDate(endDate.atTime(23, 59, 59));

					closeDateRepository.save(entity);

					startDate = endDate.plusDays(1);
				}

				existingList = closeDateRepository.findAllByYearAndUserIdOrderByMonthAsc(year, userId);
			}

			LocalDateTime today = LocalDateTime.now();

			List<CloseDateResponseDto> res = existingList.stream().map(date -> {

				CloseDateResponseDto dto = new CloseDateResponseDto();
				dto.setCloseDateId(date.getCloseDateId());
				dto.setCloseDate(date.getCloseDate().format(dateFormatter));
				dto.setStartDate(date.getStartDate().format(dateFormatter));
				dto.setMonth(date.getMonth());
				dto.setYear(date.getYear());

				Boolean isActive = !today.isBefore(date.getStartDate()) && !today.isAfter(date.getCloseDate());

				dto.setIsActive(isActive);

				return dto;

			}).collect(Collectors.toList());

			response.put("dates", res);
		}

		return response;
	}

//	@Override
//	public AllExpensesResponseDto getAllExpenses(Long userId, String startDate, String endDate) {
//
//		AllExpensesResponseDto response = new AllExpensesResponseDto();
//
//		Map<String, Object> trip = getTripExpenseByExpenseType("TRIP", userId, startDate, endDate);
//		Map<String, Object> employee = getOfficeExpenseByExpenseType("EMPLOYEES", userId, startDate, endDate);
//		Map<String, Object> office = getOfficeExpenseByExpenseType("OFFICE", userId, startDate, endDate);
//		Map<String, Object> server = getOfficeExpenseByExpenseType("SERVE", userId, startDate, endDate);
//		Map<String, Object> other = getOfficeExpenseByExpenseType("OTHER", userId, startDate, endDate);

	// -------- TOTALS --------
//		response.setTotalTripExpense(getAmount(trip, "total_expense"));
//		response.setTotalTripPaidAmount(getAmount(trip, "paid_expense"));
//		response.setTotalTripUnPaidAmount(getAmount(trip, "remaing_expense"));

//		response.setTotalEmployeeExpense(getAmount(employee, "total_expense"));
//		response.setTotalEmployeePaidAmount(getAmount(employee, "paid_expense"));
//		response.setTotalEmployeeUnPaidAmount(getAmount(employee, "remaing_expense"));
//
//		response.setTotalOfficeExpense(getAmount(office, "total_expense"));
//		response.setTotalOfficePaidAmount(getAmount(office, "paid_expense"));
//		response.setTotalOfficeUnPaidAmount(getAmount(office, "remaing_expense"));
//
//		response.setTotalServerExpense(getAmount(server, "total_expense"));
//		response.setTotalServerPaidAmount(getAmount(server, "paid_expense"));
//		response.setTotalServerUnPaidAmount(getAmount(server, "remaing_expense"));
//
//		response.setTotalOtherExpense(getAmount(other, "total_expense"));
//		response.setTotalOtherPaidAmount(getAmount(other, "paid_expense"));
//		response.setTotalOtherUnPaidAmount(getAmount(other, "remaing_expense"));

	// GRAND TOTAL
//		response.setTotalExpenses(sum(response.getTotalTripExpense(), response.getTotalEmployeeExpense(),
//				response.getTotalOfficeExpense(), response.getTotalServerExpense(), response.getTotalOtherExpense()));
//
//		response.setTotalPaidAmount(sum(response.getTotalTripPaidAmount(), response.getTotalEmployeePaidAmount(),
//				response.getTotalOfficePaidAmount(), response.getTotalServerPaidAmount(),
//				response.getTotalOtherPaidAmount()));
//
//		response.setTotalUnPaidAmount(sum(response.getTotalTripUnPaidAmount(), response.getTotalEmployeeUnPaidAmount(),
//				response.getTotalOfficeUnPaidAmount(), response.getTotalServerUnPaidAmount(),
//				response.getTotalOtherUnPaidAmount()));
//
//		// -------- MEMBER GROUPING --------
//		Map<Long, MemberAllExpensesResponseDto> memberMap = new HashMap<>();

	// TRIP
//		List<ExpenseResponseDto> tripList = (List<ExpenseResponseDto>) trip.get("data");

//		for (ExpenseResponseDto dto : tripList) {
////			MemberAllExpensesResponseDto member = getMember(memberMap, dto.getMemberId(), dto.getUserName());
//			MemberAllExpensesResponseDto member = null;
//
//			member.getTripExpenses().add(dto);
//
//			member.setTotalAmount(member.getTotalAmount().add(nullSafe(dto.getTotalAmount())));
//			member.setTotalPaidAmount(member.getTotalPaidAmount().add(nullSafe(dto.getPayoutAmount())));
//			member.setTotalUnPaidAmount(member.getTotalUnPaidAmount().add(nullSafe(dto.getRemaingAmount())));
//		}

	// EMPLOYEE
//		addOfficeExpenseToMember(memberMap, employee, "EMPLOYEES");

	// OFFICE
//		addOfficeExpenseToMember(memberMap, office, "OFFICE");

	// OTHER
//		addOfficeExpenseToMember(memberMap, other, "OTHER");

	// SERVER (GLOBAL ONLY)
//		response.setServerExpenses((List<OfficeExpenseResponseDto>) server.get("data"));

//		response.setMembers(new ArrayList<>(memberMap.values()));

//		return response;
//	}

	@Override
	public AllExpensesResponseDto getAllExpenses(Long userId, String startDate, String endDate, Long accountContactId) {

		AllExpensesResponseDto response = new AllExpensesResponseDto();

		response.setTotalTripExpense(BigDecimal.ZERO);
		response.setTotalTripPaidAmount(BigDecimal.ZERO);
		response.setTotalTripUnPaidAmount(BigDecimal.ZERO);

		response.setTotalOfficeExpense(BigDecimal.ZERO);
		response.setTotalOfficePaidAmount(BigDecimal.ZERO);
		response.setTotalOfficeUnPaidAmount(BigDecimal.ZERO);

		response.setTotalExpenses(BigDecimal.ZERO);
		response.setTotalPaidAmount(BigDecimal.ZERO);
		response.setTotalUnPaidAmount(BigDecimal.ZERO);

		response.setTripExpenses(new ArrayList<>());
		response.setExpenses(new ArrayList<>());

		Map<String, Object> tripRes = getTripExpenseByExpenseType("TRIP", userId, startDate, endDate, accountContactId);

		List<ExpenseResponseDto> tripList = (List<ExpenseResponseDto>) tripRes.get("data");

		response.setTripExpenses(tripList);

		response.setTotalTripExpense(getAmount(tripRes, "total_expense"));
		response.setTotalTripPaidAmount(getAmount(tripRes, "paid_expense"));
		response.setTotalTripUnPaidAmount(getAmount(tripRes, "remaing_expense"));

		List<IncomeExpenseTypeEntity> expenseTypes = incomeExpenseTypeRepository
				.findAllByTypeAndUserIdAndIsDeleteFalse("EXPENSE", userId);

		List<ExpenseTypeAllData> expenseTypeList = new ArrayList<>();

		BigDecimal officeTotal = BigDecimal.ZERO;
		BigDecimal officePaid = BigDecimal.ZERO;
		BigDecimal officeRemaining = BigDecimal.ZERO;

		for (IncomeExpenseTypeEntity type : expenseTypes) {
			Map<String, Object> officeRes = getOfficeExpenseByExpenseType(type.getTypeId(), userId, startDate, endDate, accountContactId);
			List<OfficeExpenseResponseDto> officeList = (List<OfficeExpenseResponseDto>) officeRes.get("data");

			ExpenseTypeAllData typeData = new ExpenseTypeAllData();
			typeData.setTypeId(type.getTypeId());
			typeData.setTypeName(type.getName());
			typeData.setExpenses(officeList);

			BigDecimal total = getAmount(officeRes, "total_expense");
			BigDecimal paid = getAmount(officeRes, "paid_expense");
			BigDecimal remaining = getAmount(officeRes, "remaining_expense");

			typeData.setTotalExpense(total);
			typeData.setTotalPaidExpense(paid);
			typeData.setTotalUnpaidExpense(remaining);

			expenseTypeList.add(typeData);

			officeTotal = officeTotal.add(total);
			officePaid = officePaid.add(paid);
			officeRemaining = officeRemaining.add(remaining);
		}

		response.setExpenses(expenseTypeList);

		response.setTotalOfficeExpense(officeTotal);
		response.setTotalOfficePaidAmount(officePaid);
		response.setTotalOfficeUnPaidAmount(officeRemaining);

		response.setTotalExpenses(response.getTotalTripExpense().add(response.getTotalOfficeExpense()));
		response.setTotalPaidAmount(response.getTotalTripPaidAmount().add(response.getTotalOfficePaidAmount()));
		response.setTotalUnPaidAmount(response.getTotalTripUnPaidAmount().add(response.getTotalOfficeUnPaidAmount()));

		return response;
	}

	@Override
	public List<MemberResponseDto> getAllMembers(Long userId) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));

		Long roleId = user.getUserBasicDetails().getRole().getId();

		UserMasterEntity effectiveUser;

		if (roleId == 1 || roleId == 2) {
			effectiveUser = user;
		} else {
			effectiveUser = userMasterRepository.findByIdAndIsDeleteFalse(user.getClientId())
					.orElseThrow(() -> new RuntimeException("Client not found with id : " + user.getClientId()));
		}

		Long effectiveRoleId = effectiveUser.getUserBasicDetails().getRole().getId();

		List<Object[]> data = accountContactRepository.getAllMemberData(effectiveUser.getId(), effectiveRoleId);

		return mapMembers(data);
	}

	private BigDecimal getAmount(Map<String, Object> map, String key) {
		if (map == null || map.get(key) == null) {
			return BigDecimal.ZERO;
		}
		return new BigDecimal(map.get(key).toString());
	}

	private BigDecimal sum(BigDecimal... values) {
		BigDecimal total = BigDecimal.ZERO;
		for (BigDecimal val : values) {
			if (val != null) {
				total = total.add(val);
			}
		}
		return total;
	}

	private List<MemberResponseDto> mapMembers(List<Object[]> rows) {

		List<MemberResponseDto> list = new ArrayList<>();

		for (Object[] row : rows) {

			MemberResponseDto dto = new MemberResponseDto();

			dto.setMemberId(row[0] != null ? ((Number) row[0]).longValue() : null);
			dto.setName(row[1] != null ? row[1].toString() : null);
			dto.setPhone(row[2] != null ? row[2].toString() : null);
			dto.setMemberType(row[3] != null ? row[3].toString() : null);

			list.add(dto);
		}

		return list;
	}

	private void addOfficeExpenseToMember(Map<Long, MemberAllExpensesResponseDto> memberMap, Map<String, Object> map,
			String type) {

		List<OfficeExpenseResponseDto> list = (List<OfficeExpenseResponseDto>) map.get("data");

		for (OfficeExpenseResponseDto dto : list) {

			MemberAllExpensesResponseDto member = getMember(memberMap, dto.getUserId(), dto.getAccountContactName());

			if ("EMPLOYEES".equals(type)) {
				member.getEmployeeExpenses().add(dto);
			} else if ("OFFICE".equals(type)) {
				member.getOfficeExpenses().add(dto);
			} else if ("OTHER".equals(type)) {
				member.getOtherExpenses().add(dto);
			}

			member.setTotalAmount(member.getTotalAmount().add(nullSafe(dto.getExpenseAmount())));

			member.setTotalPaidAmount(member.getTotalPaidAmount().add(nullSafe(dto.getPayoutAmount())));

			member.setTotalUnPaidAmount(member.getTotalUnPaidAmount().add(nullSafe(dto.getRemaingAmount())));
		}
	}

	private MemberAllExpensesResponseDto getMember(Map<Long, MemberAllExpensesResponseDto> map, Long userId,
			String userName) {

		if (!map.containsKey(userId)) {
			MemberAllExpensesResponseDto dto = new MemberAllExpensesResponseDto();

			dto.setMemberId(userId);
			dto.setMemberName(userName);

			dto.setTotalAmount(BigDecimal.ZERO);
			dto.setTotalPaidAmount(BigDecimal.ZERO);
			dto.setTotalUnPaidAmount(BigDecimal.ZERO);

			dto.setTripExpenses(new ArrayList<>());
			dto.setEmployeeExpenses(new ArrayList<>());
			dto.setOfficeExpenses(new ArrayList<>());
			dto.setOtherExpenses(new ArrayList<>());

			map.put(userId, dto);
		}
		return map.get(userId);
	}

	private List<ExpenseResponseDto> mapExpenseResponse(List<Object[]> rows) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

		Map<Long, ExpenseResponseDto> expenseMap = new LinkedHashMap<>();

		for (Object[] row : rows) {
			Long expenseId = row[0] != null ? ((Number) row[0]).longValue() : null;

			ExpenseResponseDto expenseDto = expenseMap.get(expenseId);

			if (expenseDto == null) {
				expenseDto = new ExpenseResponseDto();

				expenseDto.setId(expenseId);
				expenseDto.setTitle((String) row[1]);
				expenseDto.setFromCityId(row[2] != null ? ((Number) row[2]).longValue() : null);
				expenseDto.setToCityId(row[3] != null ? ((Number) row[3]).longValue() : null);
				expenseDto.setFromDate(formateDate(row[4], dateFormatter));
				expenseDto.setToDate(formateDate(row[5], dateFormatter));
				expenseDto.setDueDate(formateDate(row[6], dateFormatter));
				expenseDto.setPaidDate(formateDate(row[7], dateFormatter));
				expenseDto.setTotalAmount(row[8] != null ? (BigDecimal) row[8] : BigDecimal.ZERO);
				expenseDto.setPayoutAmount(BigDecimal.ZERO);
				expenseDto.setRemaingAmount(BigDecimal.ZERO);

				expenseDto.setExpenseType((String) row[9]);
				expenseDto.setRemark((String) row[10]);
				expenseDto.setAccountContactId(row[11] != null ? ((Number) row[11]).longValue() : null);
				expenseDto.setAccountContactName((String) row[12]);
				expenseDto.setUserId(row[13] != null ? ((Number) row[13]).longValue() : null);

				expenseDto.setDetailRequestDtos(new LinkedList<>());
				expenseDto.setPayoutHistory(new LinkedList<>());

				expenseMap.put(expenseId, expenseDto);
			}

			if (row[14] != null) {
				Long detailId = ((Number) row[14]).longValue();
				boolean exists = expenseDto.getDetailRequestDtos().stream().anyMatch(d -> d.getId().equals(detailId));

				if (!exists) {
					ExpenseDetailResponseDto detailDto = new ExpenseDetailResponseDto();

					detailDto.setId(detailId);
					detailDto.setExpenseDate(formateDate(row[15], dateFormatter));

					detailDto.setPerticular((String) row[16]);
					detailDto.setPaymentMode((String) row[17]);
					detailDto.setAmount(row[18] != null ? (BigDecimal) row[18] : BigDecimal.ZERO);
					detailDto.setRemarks((String) row[19]);
					detailDto.setKm(row[20] != null ? ((Number) row[20]).longValue() : null);
					detailDto.setDocPath((String) row[21]);
					detailDto.setExpenseId(expenseId);
					detailDto.setAccountContactId(expenseDto.getAccountContactId());
					detailDto.setUserId(expenseDto.getUserId());

					expenseDto.getDetailRequestDtos().add(detailDto);
				}
			}

			if (row[22] != null) {
				Long payoutId = ((Number) row[22]).longValue();

				boolean exists = expenseDto.getPayoutHistory().stream().anyMatch(p -> p.getId().equals(payoutId));

				if (!exists) {
					TripExpensePayoutHistoryResponseDto payoutDto = new TripExpensePayoutHistoryResponseDto();

					payoutDto.setId(payoutId);
					payoutDto.setTripExpenseId(expenseId);

					if (row[23] != null) {
						BankDetailsResponseDto bank = new BankDetailsResponseDto();
						bank.setId(((Number) row[23]).longValue());
						payoutDto.setBankAccount(bank);
					}

					payoutDto.setPaymentDate(formateDate(row[24], dateFormatter));
					payoutDto.setTransactionId((String) row[25]);
					payoutDto.setChequeNo((String) row[26]);
					payoutDto.setAmount(row[27] != null ? (BigDecimal) row[27] : BigDecimal.ZERO);
					payoutDto.setDueAmount(row[28] != null ? (BigDecimal) row[28] : BigDecimal.ZERO);
					payoutDto.setPaymentMode(row[29] != null ? PaymentMode.valueOf(row[29].toString()) : null);

					if (row[30] != null) {
						CashOpbResponseDto cash = new CashOpbResponseDto();
						cash.setId(((Number) row[30]).longValue());
						payoutDto.setCashType(cash);
					}

					payoutDto.setStatus((String) row[31]);
					payoutDto.setDescription((String) row[32]);
					payoutDto.setIsDelete(row[33] != null ? (Boolean) row[33] : false);
					payoutDto.setCreatedAt(row[34] != null ? toLocalDateTime(row[34]).format(dateTimeFormatter) : null);
					payoutDto.setUpdatedAt(row[35] != null ? toLocalDateTime(row[35]).format(dateTimeFormatter) : null);
					expenseDto.getPayoutHistory().add(payoutDto);

					expenseDto.setPayoutAmount(expenseDto.getPayoutAmount().add(payoutDto.getAmount()));
				}
			}

			expenseDto.setRemaingAmount(expenseDto.getTotalAmount().subtract(expenseDto.getPayoutAmount()));
			expenseDto.setStatus(expenseDto.getPayoutHistory().size() != 0
					? expenseDto.getPayoutHistory().get(expenseDto.getPayoutHistory().size() - 1).getStatus()
					: "pending");
		}

		return new ArrayList<>(expenseMap.values());
	}

	public List<OfficeExpenseResponseDto> mapOfficeExpenseResponse(List<Object[]> rows) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

		Map<Long, OfficeExpenseResponseDto> expenseMap = new LinkedHashMap<>();

		for (Object[] row : rows) {

			Long officeExpenseId = row[0] != null ? ((Number) row[0]).longValue() : null;

			OfficeExpenseResponseDto dto = expenseMap.get(officeExpenseId);

			if (dto == null) {
				dto = new OfficeExpenseResponseDto();

				dto.setId(officeExpenseId);
				dto.setTitle((String) row[1]);
				dto.setExpenseAmount(row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO);
				dto.setExpenseDate(formateDate(row[3], dateFormatter));
				dto.setPaidDate(formateDate(row[4], dateFormatter));
				dto.setPaymentMode(row[5] != null ? row[5].toString() : null);
				dto.setRemarks((String) row[6]);
				dto.setAccountContactId(row[7] != null ? ((Number) row[7]).longValue() : null);
				dto.setUserId(row[8] != null ? ((Number) row[8]).longValue() : null);
				dto.setAccountContactName((String) row[9]);
				OfficeExpenseEntity officeExpenseEntity = officeExpenseRepository
						.findByIdAndIsDeleteFalse(officeExpenseId)
						.orElseThrow(() -> new RuntimeException("Office Expense Not Found"));
				List<OfficeExpenseDocEntity> entities = officeExpenseDocRepository
						.findAllByOfficeExpenseAndIsDeleteFalse(officeExpenseEntity);
				List<OfficeExpenseDocResponseDto> docResponseDtos = new ArrayList<>();
				for (OfficeExpenseDocEntity officeExpenseDocEntity : entities) {
					OfficeExpenseDocResponseDto docResponseDto = new OfficeExpenseDocResponseDto();
					docResponseDto.setId(officeExpenseDocEntity.getId());
					docResponseDto
							.setDocPath(environment.getProperty("app.image.url") + officeExpenseDocEntity.getDocPath());
					docResponseDtos.add(docResponseDto);
				}

				dto.setFiles(docResponseDtos);
				dto.setIncomeExpenseTypeId(row[11] != null ? ((Number) row[11]).longValue() : null);
				dto.setIncomeExpenseTypeName((String) row[12]);

				dto.setPayoutHistory(new ArrayList<>());

				dto.setPayoutAmount(BigDecimal.ZERO);
				dto.setRemaingAmount(dto.getExpenseAmount());

				expenseMap.put(officeExpenseId, dto);
			}

			if (row[13] != null) {
				Long payoutId = ((Number) row[13]).longValue();

				boolean exists = dto.getPayoutHistory().stream().anyMatch(p -> p.getId().equals(payoutId));

				if (!exists) {
					OfficeExpensePayoutResponseDto payout = new OfficeExpensePayoutResponseDto();

					payout.setId(payoutId);

					if (row[14] != null) {
						BankDetailsResponseDto bank = new BankDetailsResponseDto();
						bank.setId(((Number) row[14]).longValue());
						payout.setBankAccount(bank);
					}

					payout.setPaymentDate(row[15] != null ? ((Date) row[15]).toLocalDate() : null);
					payout.setTransactionId((String) row[16]);
					payout.setChequeNo((String) row[17]);
					payout.setAmount(row[18] != null ? (BigDecimal) row[18] : BigDecimal.ZERO);
					payout.setDueAmount(row[19] != null ? (BigDecimal) row[19] : BigDecimal.ZERO);
					payout.setPaymentMode(row[20] != null ? PaymentMode.valueOf(row[20].toString()) : null);

					if (row[21] != null) {
						CashOpbResponseDto cash = new CashOpbResponseDto();
						cash.setId(((Number) row[21]).longValue());
						payout.setCashType(cash);
					}

					payout.setStatus((String) row[22]);
					payout.setDescription((String) row[23]);
					payout.setIsDelete(row[24] != null ? (Boolean) row[24] : false);
					payout.setCreatedAt(row[25] != null ? toLocalDateTime(row[25]).format(dateTimeFormatter) : null);
					payout.setUpdatedAt(row[26] != null ? toLocalDateTime(row[26]).format(dateTimeFormatter) : null);

					dto.getPayoutHistory().add(payout);

					dto.setPayoutAmount(dto.getPayoutAmount().add(payout.getAmount()));
				}
				dto.setRemaingAmount(dto.getExpenseAmount().subtract(dto.getPayoutAmount()));
				dto.setStatus(dto.getPayoutHistory().size() != 0
						? dto.getPayoutHistory().get(dto.getPayoutHistory().size() - 1).getStatus()
						: "pending");
			}
		}

		return new ArrayList<>(expenseMap.values());
	}

	private String formateDate(Object date, DateTimeFormatter formatter) {
		if (date != null) {

			if (date instanceof java.sql.Timestamp) {
				return ((java.sql.Timestamp) date).toLocalDateTime().toLocalDate().format(formatter);

			} else if (date instanceof java.sql.Date) {
				return ((java.sql.Date) date).toLocalDate().format(formatter);

			} else if (date instanceof LocalDateTime) {
				return ((LocalDateTime) date).toLocalDate().format(formatter);

			} else if (date instanceof LocalDate) {
				return ((LocalDate) date).format(formatter);
			}
		}

		return null;
	}

	public static LocalDateTime toLocalDateTime(Object obj) {
		if (obj == null)
			return null;

		if (obj instanceof LocalDateTime) {
			return (LocalDateTime) obj;
		}

		if (obj instanceof Timestamp) {
			return ((Timestamp) obj).toLocalDateTime();
		}

		if (obj instanceof java.util.Date) {
			return new Timestamp(((java.util.Date) obj).getTime()).toLocalDateTime();
		}

		throw new IllegalArgumentException("Unsupported type: " + obj.getClass());
	}

	@Override
	public Boolean deleteOfficeExpenseFile(Long id) {
		if (officeExpenseDocRepository.existsByIdAndIsDeleteFalse(id)) {
			OfficeExpenseDocEntity expenseDocEntity = officeExpenseDocRepository.findByIdAndIsDeleteFalse(id)
					.orElseThrow(() -> new RuntimeException("File Not Found"));
			expenseDocEntity.setIsDelete(true);
			officeExpenseDocRepository.save(expenseDocEntity);
			return true;
		}
		return false;
	}

}
