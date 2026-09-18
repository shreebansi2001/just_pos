package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.AccountContactEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.EntryType;
import com.crmportal.mapper.AccountContactMapper;
import com.crmportal.repository.AccountContactRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.AccountContactRequestDto;
import com.crmportal.response.dto.AccountContactResponseDto;
import com.crmportal.service.AccountContactService;

@Service
public class AccountContactServiceImpl implements AccountContactService {

	@Autowired
	AccountContactRepository accountContactRepository;

	@Autowired
	AccountContactMapper accountContactMapper;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Override
	public AccountContactResponseDto addOrUpdateAccountContact(@Valid AccountContactRequestDto request) {
		AccountContactEntity entity = null;

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));

		Optional<AccountContactEntity> op = accountContactRepository
				.findByNameAndUserIdAndIsDeleteFalse(request.getName(), request.getUserId());

		if (request.getAccountContactId() == -1) {
			if (op.isPresent()) {
				throw new RuntimeException("Account contact name is already exist.");
			}
			entity = accountContactMapper.requestToEntity(request);
		} else {
			entity = accountContactRepository.findByIdAndIsDeleteFalse(request.getAccountContactId()).orElseThrow(
					() -> new RuntimeException("Account contact not found with id : " + request.getAccountContactId()));

			if (op.isPresent() && op.get().getId() != entity.getId()) {
				throw new RuntimeException("Account contact name is already exist.");
			}

			entity = accountContactMapper.updateEntity(entity, request);
		}

		entity = accountContactRepository.save(entity);

		AccountContactResponseDto response = accountContactMapper.entityToResponse(entity);

		return response;
	}

	@Override
	public List<AccountContactResponseDto> getAllAccountContactByUserId(Long userId) {
		List<Object[]> rows = accountContactRepository.findAccountContactsByUser(userId);

		return mapToResponse(rows);
	}

	@Override
	public AccountContactResponseDto getAccountContactById(Long accountContactId) {
		AccountContactEntity entity = accountContactRepository.findByIdAndIsDeleteFalse(accountContactId)
				.orElseThrow(() -> new RuntimeException("Account contact not found id : " + accountContactId));

		return accountContactMapper.entityToResponse(entity);
	}

	@Override
	public Boolean deleteAccountContactById(Long accountContactId) {
		AccountContactEntity entity = accountContactRepository.findByIdAndIsDeleteFalse(accountContactId)
				.orElseThrow(() -> new RuntimeException("Account contact not found id : " + accountContactId));

		entity.setIsDelete(true);
		entity.setUpdatedAt(LocalDateTime.now());

		accountContactRepository.save(entity);

		return true;
	}

	private List<AccountContactResponseDto> mapToResponse(List<Object[]> rows) {
		List<AccountContactResponseDto> list = new ArrayList<>();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");

		for (Object[] row : rows) {
			AccountContactResponseDto dto = new AccountContactResponseDto();

			dto.setAccountContactId(row[0] != null ? ((Number) row[0]).longValue() : null);
			dto.setName((String) row[1]);
			dto.setOpeningBalance((BigDecimal) row[2]);
			dto.setCurrentBalance((BigDecimal) row[3]);

			dto.setOpeningDate(row[4] != null ? toLocalDate(row[4]).format(dateFormatter) : null);

			dto.setEntryType(row[5] != null ? EntryType.valueOf(row[5].toString()) : null);

			dto.setUserId(row[6] != null ? ((Number) row[6]).longValue() : null);
			dto.setMemberId(row[7] != null ? ((Number) row[7]).longValue() : null);

			dto.setIsDelete(row[8] != null ? (Boolean) row[8] : null);

			dto.setCreatedAt(row[9] != null ? formatDateTime(row[9], dateTimeFormatter) : null);
			dto.setUpdatedAt(row[10] != null ? formatDateTime(row[10], dateTimeFormatter) : null);

			list.add(dto);
		}

		return list;
	}
	
	private LocalDate toLocalDate(Object obj) {
	    if (obj == null) return null;

	    if (obj instanceof LocalDate) return (LocalDate) obj;

	    if (obj instanceof java.sql.Date)
	        return ((java.sql.Date) obj).toLocalDate();

	    if (obj instanceof java.sql.Timestamp)
	        return ((java.sql.Timestamp) obj).toLocalDateTime().toLocalDate();

	    return LocalDate.parse(obj.toString());
	}
	
	private String formatDateTime(Object obj, DateTimeFormatter formatter) {
	    if (obj == null) return null;

	    LocalDateTime dateTime = null;

	    if (obj instanceof LocalDateTime) {
	        dateTime = (LocalDateTime) obj;

	    } else if (obj instanceof java.sql.Timestamp) {
	        dateTime = ((java.sql.Timestamp) obj).toLocalDateTime();

	    } else if (obj instanceof java.util.Date) {
	        dateTime = new java.sql.Timestamp(((java.util.Date) obj).getTime()).toLocalDateTime();

	    } else {
	        dateTime = LocalDateTime.parse(obj.toString().replace(" ", "T"));
	    }

	    return dateTime.format(formatter);
	}
	
}
