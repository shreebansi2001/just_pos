package com.crmportal.service;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.CashOpbRequestDto;
import com.crmportal.response.dto.CashOpbResponseDto;

@Service
public interface CashAccountService {

	CashOpbResponseDto addOrUpdateCashAccount(@Valid CashOpbRequestDto request, Long id);

	CashOpbResponseDto getById(Long id);

	List<CashOpbResponseDto> getAllByUserId(Long userId, Boolean isPrimary);

	Map<String, Object> deleteById(Long id);

}
