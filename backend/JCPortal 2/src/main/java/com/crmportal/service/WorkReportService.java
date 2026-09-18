package com.crmportal.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public interface WorkReportService {

	String generateWorkReport(Long eventId, Long userid, HttpServletRequest re, int lang);

}
