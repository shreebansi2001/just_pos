package com.crmportal.service;

import org.springframework.stereotype.Service;

import com.crmportal.enums.AccountType;
import com.crmportal.enums.PaymentMode;
import com.crmportal.response.dto.AccountLedgerDashboardResponseDto;

@Service
public interface AccountLedgerService {

	AccountLedgerDashboardResponseDto getAccountLedger(String startDate, String endDate, AccountType accountType,
			PaymentMode paymentMode, Long cashAccountId, Long bankAccountId, Long userId);

}
