package com.crmportal.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.EventFunctionQuotationRequestDto;
import com.crmportal.request.dto.EventInvoiceRequestDto;
import com.crmportal.response.dto.EventFunctionQuotationResponseDto;
import com.crmportal.response.dto.EventInvoiceResponseDto;

@Service
public interface EventInvoiceService {

	EventInvoiceResponseDto addOrUpdateEventInvoice(@Valid EventInvoiceRequestDto request,
			long parseLong);

	Boolean deleteByInvoiceItemId(Long invoiceItemId);

	EventInvoiceResponseDto getEventInvoiceByEventId(Long eventId,Boolean isDecore);

	List<EventInvoiceResponseDto> getEventInvoiceByUserId(Long userid, String startDate, String endDate);

	List<EventInvoiceResponseDto> getEventInvoiceByUserIdAndDateWise(Long userid, String startDate,
			String endDate, Boolean isVenue, Long id);

	String invoiceExcel(Long userid, String startDate, String endDate, HttpServletRequest request, Boolean isVenue, Long id);

	String getInvoiceCode(Long userId);

}
