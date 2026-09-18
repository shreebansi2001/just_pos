package com.crmportal.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.crmportal.enums.AccountType;
import com.crmportal.enums.PaymentMode;

@Service
public interface SuperAdminIncomeReportService {

	String generateIncomeReport(String startDate, String endDate, AccountType accountType, PaymentMode paymentMode,
			Long cashAccountId, Long bankAccountId, Long typeId, Long userId, HttpServletRequest re);
}
