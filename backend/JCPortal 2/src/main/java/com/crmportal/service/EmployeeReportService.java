package com.crmportal.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public interface EmployeeReportService {

	String employeePerformaceReport(Long employeeId, String startDate, String endDate, Integer lang, Long pipelineId,
			HttpServletRequest re, Long userId);
}
