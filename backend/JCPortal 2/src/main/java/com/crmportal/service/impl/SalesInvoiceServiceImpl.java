package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.CashAccountEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.SalesInvoiceEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.AccountType;
import com.crmportal.mapper.SalesInvoiceMapper;
import com.crmportal.repository.BankDetailsRepository;
import com.crmportal.repository.CashAccountRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.SalesInvoiceRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.SalesInvoiceRequestDto;
import com.crmportal.response.dto.BankDetailsResponseDto;
import com.crmportal.response.dto.SalesInvoiceResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.SalesInvoiceService;


@Service
public class SalesInvoiceServiceImpl implements SalesInvoiceService {

	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Autowired
	EventMasterRepository eventMasterRepository;
	
	@Autowired
	CommonService commonService;
	
	@Autowired
	SalesInvoiceRepository salesInvoiceRepository;
	
	@Autowired
	BankDetailsRepository bankDetailsRepository;
	
	@Autowired
	SalesInvoiceMapper salesInvoiceMapper;
	
	@Autowired
	CashAccountRepository cashAccountRepository;
	
	@Override
	@Transactional
	public SalesInvoiceResponseDto addUpdateSalesInvoice(SalesInvoiceRequestDto request) {
		
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));
		
		EventMasterEntity event = eventMasterRepository.findByIdAndIsDeleteFalse(request.getEventId())
				.orElseThrow(() -> new RuntimeException("Event not found with id : " + request.getEventId()));
		
		BankDetailsEntity bank = null;
		
		CashAccountEntity cash = null;
		
		SalesInvoiceEntity entity = new SalesInvoiceEntity();

		boolean isNew = request.getId() == -1;

		if(request.getId() == -1) {
			entity = salesInvoiceMapper.requestToEntity(request);
		} else {
			Optional<SalesInvoiceEntity> optEntity = salesInvoiceRepository.findByIdAndIsDeleteFalse(request.getId());
			if(!optEntity.isPresent()) {
				throw new RuntimeException("Bank detail not found with id : " + request.getId());
			}
			
			BigDecimal oldAmount = entity.getTotalAmount() != null ? entity.getTotalAmount() : BigDecimal.ZERO;
			
			entity = salesInvoiceMapper.updateEntityFromRequest(optEntity.get(), request);
			entity.setUpdatedAt(commonService.getCurrentDateTime());
			
	        BigDecimal difference = request.getTotalAmount().subtract(oldAmount);
	        updateAccountBalance(request, user.getId(), difference, entity);
		}
		
		if (isNew) {
			updateAccountBalance(request, user.getId(), request.getTotalAmount(), entity);
		}
		 
		if(request.getDueAmount().compareTo(BigDecimal.ZERO) == 0) {
			entity.setStatus("confirm");
		} else {
			entity.setStatus("pending");
		}
		
		entity.setPaymentDate(commonService.dateTimeFormatted(request.getPaymentDate()));
		entity.setUser(user);
		entity.setEvent(event);
		entity = salesInvoiceRepository.save(entity);
		
		SalesInvoiceResponseDto responseDto = salesInvoiceMapper.entityToResponse(entity);
		responseDto.setUserId(entity.getUser().getId());
		responseDto.setEventId(entity.getEvent().getId());
		
		return responseDto;
	}

	@Override
	public Map<String, Object> getSalesInvoiceByUserIdAndEventId(Long userId, Long eventId) {
		
		Map<String, Object> response = new HashMap<>();
		
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));
		
		EventMasterEntity event = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
				.orElseThrow(() -> new RuntimeException("Event not found with id : " + eventId));
		
		BigDecimal totalDueAmount = BigDecimal.ZERO;
		
		List<SalesInvoiceResponseDto> responseDtos = new ArrayList<>();

		List<SalesInvoiceEntity> entities = salesInvoiceRepository.findByUserIdAndEventIdAndIsDeleteFalseOrderById(userId, eventId);
		
		for (SalesInvoiceEntity entity : entities) {
			SalesInvoiceResponseDto responseDto = salesInvoiceMapper.entityToResponse(entity);
			responseDto.setStatus(entity.getStatus());
			responseDto.setUserId(entity.getUser().getId());
			responseDto.setEventId(entity.getEvent().getId());
			
			totalDueAmount = entity.getDueAmount();
			
			responseDtos.add(responseDto);
		}

		response.put("due_amount", totalDueAmount);
		response.put("data", responseDtos);
		
		return response;
	}

	@Override
	public Map<String, Object> getSalesInvoiceByUserIdAndEventId(Long eventId) {
		
		Map<String, Object> response = new HashMap<>();
		
		EventMasterEntity event = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
				.orElseThrow(() -> new RuntimeException("Event not found with id : " + eventId));
		
		BigDecimal totalDueAmount = BigDecimal.ZERO;
		
		List<SalesInvoiceResponseDto> responseDtos = new ArrayList<>();

		List<SalesInvoiceEntity> entities = salesInvoiceRepository.findByEventIdAndIsDeleteFalseOrderById(eventId);
		
		for (SalesInvoiceEntity entity : entities) {
			SalesInvoiceResponseDto responseDto = salesInvoiceMapper.entityToResponse(entity);
			responseDto.setStatus(entity.getStatus());
			responseDto.setUserId(entity.getUser().getId());
			responseDto.setEventId(entity.getEvent().getId());
			
			totalDueAmount = entity.getDueAmount();
			
			responseDtos.add(responseDto);
		}

		response.put("due_amount", totalDueAmount);
		response.put("data", responseDtos);
		
		return response;
	}

	@Override
	@Transactional
	public Boolean deleteSalesInvoiceById(Long salesInvoiceid) {
		
		SalesInvoiceEntity entity = salesInvoiceRepository.findByIdAndIsDeleteFalse(salesInvoiceid)
				.orElseThrow(() -> new RuntimeException("Sales Invoice not found with id : " + salesInvoiceid));
		
		BigDecimal amount = entity.getTotalAmount() != null ? entity.getTotalAmount() : BigDecimal.ZERO;
		
	    if (entity.getBankId() != null && entity.getBankId() != 0) {

	        BankDetailsEntity bank = bankDetailsRepository
	                .findByIdAndIsDeleteFalse(entity.getBankId())
	                .orElseThrow(() -> new RuntimeException("Bank not found with id : " + entity.getBankId()));

	        bank.setCurrentBalance(bank.getCurrentBalance().subtract(amount));
	        bankDetailsRepository.save(bank);

	    } else if (entity.getCashId() != null && entity.getCashId() != 0) {

	        CashAccountEntity cash = cashAccountRepository
	                .findByIdAndIsDeleteFalse(entity.getCashId())
	                .orElseThrow(() -> new RuntimeException("Cash not found with id : " + entity.getCashId()));

	        cash.setCurrentBalance(cash.getCurrentBalance().subtract(amount));
	        cashAccountRepository.save(cash);
	    }
	    
		entity.setIsDelete(true);
		salesInvoiceRepository.save(entity);
		
		return true;
	}
	
	private void updateAccountBalance(SalesInvoiceRequestDto request, Long userId, BigDecimal amount,
			SalesInvoiceEntity entity) {

		if (request.getAccountType() == AccountType.BANK) {

			BankDetailsEntity bank = bankDetailsRepository
					.findByIdAndUserIdAndIsDeleteFalse(request.getBankId(), userId)
					.orElseThrow(() -> new RuntimeException("Bank not found with id : " + request.getBankId()));

			bank.setCurrentBalance(bank.getCurrentBalance().add(amount));
			bankDetailsRepository.save(bank);

			entity.setBankId(bank.getId());
			entity.setCashId(0l);

		} else {

			CashAccountEntity cash = cashAccountRepository.findByIdAndIsDeleteFalse(request.getCashId())
					.orElseThrow(() -> new RuntimeException("Cash not found with id : " + request.getCashId()));

			cash.setCurrentBalance(cash.getCurrentBalance().add(amount));
			cashAccountRepository.save(cash);

			entity.setCashId(cash.getId());
			entity.setBankId(0l);
		}
	}

}
