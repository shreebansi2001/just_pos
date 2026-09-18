package com.crmportal.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public interface CostingReportService {

	String generateCostingReport(Long eventId, Long userid, int lang, Integer isCompanyDetails, HttpServletRequest re);

	String generateStoreIssueWiseCostingReport(Long eventId, Long userid, int lang, Integer isCompanyDetails,
			HttpServletRequest re);

}
