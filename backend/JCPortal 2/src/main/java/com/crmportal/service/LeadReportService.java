package com.crmportal.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public interface LeadReportService {

	String generateLeadReport(String startDate, String endDate, Long stageId ,Long sourceId, String priority,List<Long> leadAssignId,Integer isCompanyDetails,
			HttpServletRequest re, Long userId);
	
	String generateFollowupReport(String startDate, String endDate, Long stageId ,Long sourceId, String priority,List<Long> leadAssignId,Integer isCompanyDetails,
			HttpServletRequest re, Long userId);
	
}
