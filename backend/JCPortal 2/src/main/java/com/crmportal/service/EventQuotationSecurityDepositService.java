package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.EventQuotationSecurityDepositRequestDto;
import com.crmportal.response.dto.EventQuotationSecurityDepositResponseDto;

@Service
public interface EventQuotationSecurityDepositService {

	EventQuotationSecurityDepositResponseDto addUpdateSecurityDeposit(
			@Valid EventQuotationSecurityDepositRequestDto request);

	Boolean deleteSecurityDeposit(Long securityDepositId);

	List<EventQuotationSecurityDepositResponseDto> getAllSecurityDepositByEventId(Long eventId);

}
