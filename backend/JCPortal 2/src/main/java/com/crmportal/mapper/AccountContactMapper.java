package com.crmportal.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.crmportal.entity.AccountContactEntity;
import com.crmportal.request.dto.AccountContactRequestDto;
import com.crmportal.response.dto.AccountContactResponseDto;

@Component
public class AccountContactMapper {

	public AccountContactEntity requestToEntity(AccountContactRequestDto request) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		if (request == null) {
			return null;
		}

		AccountContactEntity entity = new AccountContactEntity();
		entity.setName(request.getName());
		entity.setOpeningBalance(request.getOpeningBalance());
		entity.setCurrentBalance(request.getCurrentBalance());
		entity.setOpeningDate(LocalDate.parse(request.getOpeningDate(), dateFormatter));
		entity.setEntryType(request.getEntryType());
		entity.setMemberId(request.getMemberId());
		entity.setUserId(request.getUserId());

		return entity;
	}

	public AccountContactResponseDto entityToResponse(AccountContactEntity entity) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");

		if (entity == null) {
			return null;
		}

		AccountContactResponseDto response = new AccountContactResponseDto();
		response.setAccountContactId(entity.getId());
		response.setName(entity.getName());
		response.setOpeningBalance(entity.getOpeningBalance());
		response.setCurrentBalance(entity.getCurrentBalance());
		response.setOpeningDate(entity.getOpeningDate().format(dateFormatter));
		response.setEntryType(entity.getEntryType());
		response.setMemberId(entity.getMemberId());
		response.setUserId(entity.getUserId());
		response.setIsDelete(entity.getIsDelete());
		response.setCreatedAt(entity.getCreatedAt().format(dateTimeFormatter));

		return response;
	}

	public AccountContactEntity updateEntity(AccountContactEntity entity, AccountContactRequestDto request) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		if (entity == null || request == null) {
			return null;
		}

		entity.setName(request.getName());
		entity.setOpeningBalance(request.getOpeningBalance());
		entity.setCurrentBalance(request.getCurrentBalance());
		entity.setOpeningDate(LocalDate.parse(request.getOpeningDate(), dateFormatter));
		entity.setEntryType(request.getEntryType());
		entity.setMemberId(request.getMemberId());
		entity.setUserId(request.getUserId());
		entity.setUpdatedAt(LocalDateTime.now());

		return entity;
	}
}
