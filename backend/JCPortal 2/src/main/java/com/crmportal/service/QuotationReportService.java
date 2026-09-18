package com.crmportal.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.crmportal.response.dto.AdminTemplateModuleResponseDto;

@Service
public interface QuotationReportService {

	String getQuotationReport1(Long eventId, HttpServletRequest re, int lang, Long userid, Integer isInvoice,
			Integer isQrCode, Integer isTermsCond, Integer isAdvance, Integer isCompanyDetails, Boolean isDecore, Integer isNotes,
			AdminTemplateModuleResponseDto exclusiveTheme, AdminTemplateModuleResponseDto backOffice, Integer showLastPage);

	String getQuotationReport2(Long eventId, HttpServletRequest re, int lang, Long userid, Integer isInvoice,
			Integer isQrCode, Integer isTermsCond, Integer isAdvance, Integer isWithPrice, Boolean isDecore);

	Boolean sendMailQuotationReport(Long eventId, HttpServletRequest re, int lang, Long userId, int i, Integer isQrCode,
			Integer isTermsCond, Integer isAdvance, Integer isCompanyDetails, Boolean isDecore, Integer isNotes,
			Long exclusiveThemeId, Long backOfficeId, Integer showLastPage);

	String generateInvoiceType2(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isTermsCond,
			Integer isAdvance, Boolean isDecore);

	String getQuotationReport3(Long eventId, HttpServletRequest re, int lang, Long userId, int i, Integer isQrCode,
			Integer isTermsCond, Integer isAdvance, Integer isWithPrice, Boolean isDecore, Integer isCombo,
			Integer isOnePage, Integer isCompanyDetails);

	String generateInvoiceType3(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isTermsCond,
			Integer isAdvance, Boolean isDecore, Integer isCompanyDetails, Integer isQrCode, Integer isInvoice);

	String generateInvoiceType4(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isTermsCond,
			Integer isAdvance, Boolean isDecore, Integer isCompanyDetails, Integer isQrCode, Integer isInvoice);

	String generateInvoiceType5(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isTermsCond,
			Integer isAdvance, Boolean isDecore, Integer isCompanyDetails, Integer isQrCode, Integer isInvoice,
			AdminTemplateModuleResponseDto adminTemplate);
	
	String generateEstimateOrInvoiceType7(Long eventId, HttpServletRequest re, int lang, Long userId,
			Integer isTermsCond, Integer isAdvance, Integer isCompanyDetails, Integer isQrCode, Integer isInvoice,
			String reportName);
	
	String generateInvoiceType7(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isTermsCond,
			Integer isAdvance, Boolean isDecore, Integer isCompanyDetails, Integer isQrCode, Integer isInvoice);

	String generateInvoiceType8(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isTermsCond,
			Integer isAdvance, Boolean isDecore, Integer isCompanyDetails, Integer isQrCode, Integer isInvoice);

	String generateInvoiceType9(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isTermsCond,
			Integer isAdvance, Boolean isDecore, Integer isCompanyDetails, Integer isQrCode, Integer isInvoice);

	String generateInvoiceType10(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isTermsCond,
			Integer isAdvance, Boolean isDecore, Integer isCompanyDetails, Integer isQrCode, Integer isInvoice);

	String generateInvoiceType11(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isTermsCond,
			Integer isAdvance, Integer isInvoice, Integer isWithPrice);

}
