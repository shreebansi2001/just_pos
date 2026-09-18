package com.crmportal.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.EventFunctionQuotationRequestDto;
import com.crmportal.response.dto.EventFunctionQuotationItemsResponseDto;
import com.crmportal.response.dto.EventFunctionQuotationResponseDto;

@Service
public interface EventFunctionQuotationService {

	EventFunctionQuotationResponseDto addOrUpdateEventFunctoinQuotation(@Valid EventFunctionQuotationRequestDto request,
			long parseLong);

	Boolean deleteByQuotationItemId(Long quotationItemId);

	List<EventFunctionQuotationResponseDto> getEventFunctionQuotationByUserId(Long userid, Boolean isDecore);

	List<EventFunctionQuotationResponseDto> getEventFunctionQuotationByUserIdAndDateWise(Long userid, String startDate,
			String endDate, Boolean isVenue, Long id, Boolean isDecore);

	EventFunctionQuotationResponseDto getEventFunctionQuotationByEventId(Long eventId, Integer isCopyToInvoice, Boolean isDecore);

	List<EventFunctionQuotationItemsResponseDto> getDefaultExtraFunction(Long quotationId, Boolean isOn);

	Boolean lockQuotation(Long quotationId, Boolean isLock);

	String quotationExcel(Long userid, String startDate, String endDate,HttpServletRequest request, Boolean isVenue, Long id, Boolean isDecore);

	Boolean deleteQuotationPayment(Long id);

}
