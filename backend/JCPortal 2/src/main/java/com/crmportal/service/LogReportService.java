package com.crmportal.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public interface LogReportService {

	String generateLogReport(Long userid, HttpServletRequest re, String startDate, String endDate);

}
