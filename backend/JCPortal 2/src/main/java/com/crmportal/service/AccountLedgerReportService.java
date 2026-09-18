package com.crmportal.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.crmportal.enums.AccountType;
import com.crmportal.enums.EntryType;
import com.crmportal.enums.PaymentMode;

@Service
public interface AccountLedgerReportService {

	String generateAccountLedgerReport(String startDate, String endDate, AccountType accountType,
			PaymentMode paymentMode, Long cashAccountId, Long bankAccountId, Long userId, HttpServletRequest req);

}
