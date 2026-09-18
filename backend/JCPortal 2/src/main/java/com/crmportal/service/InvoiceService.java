package com.crmportal.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.crmportal.entity.InvoicePaymentHistoryEntity;
import com.crmportal.request.dto.InvoicePaymentHistoryRequestDto;
import com.crmportal.request.dto.InvoiceRequestDTO;
import com.crmportal.response.dto.AdminInvoiceResponseDto;
import com.crmportal.response.dto.AllInvoiceResponseDto;
import com.crmportal.response.dto.InvoicePaymentHistoryResponseDto;
import com.crmportal.response.dto.InvoiceResponseDto;
import com.crmportal.response.dto.InvoiceWisePaymentHistoryResponseDto;
import com.crmportal.response.dto.SuperAdminInvoiceResponseDto;

public interface InvoiceService {

	AllInvoiceResponseDto getAllAdminInvoice(String startDate, String endDate, Long planId, Long customerId);

	SuperAdminInvoiceResponseDto getAdminInvoiceById(Long id);

	SuperAdminInvoiceResponseDto addOrUpdateInvoice(InvoiceRequestDTO invoiceRequestDTO, Long invoiceId) throws IOException;

	Boolean deleteInvoiceById(Long invoiceId);
	
	String generateInvoiceCode();

	InvoicePaymentHistoryResponseDto recordPayment(@Valid InvoicePaymentHistoryRequestDto request, Long invoicePaymentHistoryId);

	InvoiceWisePaymentHistoryResponseDto getPaymentHistoryByInvoiceId(Long invoiceId);

	InvoicePaymentHistoryResponseDto getPaymentDetailByInvoicePaymentHistoryId(Long invoicePaymentHistoryId);

	Boolean deleteInvoicePaymentDetailsByHistoryId(Long invoicePaymentHistoryId, Long invoiceId);

}
