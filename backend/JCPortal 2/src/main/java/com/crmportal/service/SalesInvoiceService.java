package com.crmportal.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.SalesInvoiceRequestDto;
import com.crmportal.response.dto.SalesInvoiceResponseDto;

@Service
public interface SalesInvoiceService {

	SalesInvoiceResponseDto addUpdateSalesInvoice(SalesInvoiceRequestDto request);

	Map<String, Object> getSalesInvoiceByUserIdAndEventId(Long userId, Long eventId);

	Boolean deleteSalesInvoiceById(Long salesInvoiceid);

	Map<String, Object> getSalesInvoiceByUserIdAndEventId(Long eventId);

}
