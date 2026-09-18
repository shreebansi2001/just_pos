package com.crmportal.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public interface ExpenseReportService {

	String generateTripExpenseReport(Long userId, String type, Long expenseId, String startDate, String endDate, HttpServletRequest re, Long accountContactId);

	String generateOfficeExpenseReport(Long userId, Long incomeExpenseTypeId, String startDate, String endDate, HttpServletRequest re, Long accountContactId);

	String generateAllExpensesReport(Long userId, String startDate, String endDate, HttpServletRequest re, Long accountContactId);

}
