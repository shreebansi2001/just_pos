package com.crmportal.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

@Service
public interface ExecutionReportService {

	String menuReportWithQuantity(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			Integer withQty, Integer isUserDetails, Integer isPartyDetails, Integer is3Column);

	String rawMaterialwiseMenuItem(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			Integer isUserDetails, Integer isPartyDetails);

	String menuReportWithQuantityDocx(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			Integer isWithQty, Integer isCompanyDetails, Integer isPartyDetails, Integer is3Column);

	String menuReportWithQuantityRidhhiSidhhi(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer isWithQty, Integer isCompanyDetails, Integer isPartyDetails, Integer is3Column,
			Integer is5Column);

	String menuReportWithQuantityRidhhiSidhhiDocx(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer isWithQty, Integer isCompanyDetails, Integer isPartyDetails, Integer is3Column,
			Integer is5Column);

	String generateRawMaterialReport(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid);

}
