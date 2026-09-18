package com.crmportal.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.UpdateVendorPaymentRequestDto;
import com.crmportal.request.dto.VendorPaymentRequestDto;
import com.crmportal.response.dto.AccountLadgerPartyResponseDto;
import com.crmportal.response.dto.AccountLadgerResponseDto;
import com.crmportal.response.dto.AccountLedgerFinalResponseDto;
import com.crmportal.response.dto.VendorPartyWiseEventResponseDto;
import com.crmportal.response.dto.VendorPartyWiseFinalEventResponseDto;
import com.crmportal.response.dto.VendorPaymentInvoiceResponseDto;
import com.crmportal.response.dto.VendorPaymentResponseDto;

@Service
public interface VendorPaymentService {

	VendorPaymentResponseDto getAllVendorPaymentByEventId(Long eventId, Boolean isLabour);

	Map<String, Object> getVendorPaymentByEventIdAndVendorId(Long eventId, Long vendorId, Long userId,
			Boolean isPayable, String vendorName);

	Boolean addOrUpdateVendorPaymentInvoice(VendorPaymentRequestDto request);

	Boolean deleteEventVendorPaymentInvoiceById(Long id);

	List<AccountLadgerPartyResponseDto> getAllParty(Long userid, Boolean isBookingParty, Boolean isAllStatus);

	VendorPartyWiseFinalEventResponseDto getAllEventByParty(Long partyId, Long userId);

	AccountLedgerFinalResponseDto getAccountLadger(Long partyId, String startDate, String endDate, Long userid, String vendorCat, String type);

	Boolean updateVendorPaymentInvoice(UpdateVendorPaymentRequestDto request);

	String accountLedgerPdf(Long partyId, String startDate, String endDate, Long userId, String vendorCat, HttpServletRequest request, String type);

	String accountLedgerExcel(Long partyId, String startDate, String endDate, Long userId, String vendorCat, HttpServletRequest request, String type);

	String generateVendorPaymentReport(Integer isCompanyDetails, Boolean isPayable, HttpServletRequest request, Long userId);

	String generatePaymentReceipt(Integer isCompanyDetails, HttpServletRequest request, Long vendorPayId,Long userId);

}
