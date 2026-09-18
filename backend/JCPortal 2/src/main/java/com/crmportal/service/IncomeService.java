package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.enums.AccountType;
import com.crmportal.enums.PaymentMode;
import com.crmportal.response.dto.IncomeListResponseDto;
import com.crmportal.response.dto.IncomeResponseDto;

@Service
public interface IncomeService {

	IncomeResponseDto getIncome(String startDate, String endDate, AccountType accountType, PaymentMode paymentMode, Long bankAccountId, Long cashAccountId, Long typeId, Long userId);

	List<IncomeListResponseDto> mapToIncomeResponseDto(List<Object[]> rows);

}
