package com.crmportal.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public interface TGBQuotationReportService {

	String getQuotationReport3(Long eventId, HttpServletRequest re, int lang, Long userId, int i, Integer isQrCode,
			Integer isTermsCond, Integer isAdvance, Integer isWithPrice, Boolean isDecore, Integer isCombo,
			Integer isOnePage, Integer isCompanyDetails);

}
