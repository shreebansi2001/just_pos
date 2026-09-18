package com.crmportal.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public interface LaborReportService {
	String generateLaborChithhi(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid, Integer isUserDetails, List<Long> agencyId, Integer withPrice);

	String laborReport(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			Integer withQty, String startDate, String endDate, Integer isCompanyDetails, List<Long> agencyId, Integer withPrice, Integer isPartyDetails,
			Integer isAgencyNextPage);

	String generateLaborChithhiPageSizeA6(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer isUserDetails, List<Long> agencyId, Integer withPrice);

	String laborReportDateWiseWithPrice(HttpServletRequest re, int lang, Long userid, String startDate, String endDate,
			Integer isCompanyDetails, List<Long> agencyId, Long partyId);

	String laborReportDateWiseWithoutPrice(HttpServletRequest re, int lang, Long userid, String startDate, String endDate,
			Integer isCompanyDetails, List<Long> agencyId, Long partyId);

	String generateLaborChithhiPageSizeA5(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer isUserDetails, List<Long> agencyId, Integer withPrice);
}
