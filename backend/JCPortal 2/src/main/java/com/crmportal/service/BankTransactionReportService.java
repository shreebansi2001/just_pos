package com.crmportal.service;

import java.time.LocalDate;

import com.crmportal.response.dto.BankTransactionReportDTO;

public interface BankTransactionReportService {

	BankTransactionReportDTO getReport(
            Long bankId, LocalDate fromDate, LocalDate toDate);

    byte[] generatePdf(Long bankId, LocalDate fromDate, LocalDate toDate);
}