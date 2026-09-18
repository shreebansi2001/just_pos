package com.crmportal.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public interface GuestSignatureReportService {

	String generateGuestSignatureReport(Long eventId, Long userid, Integer lang, Integer isCompanyDetails, HttpServletRequest re);

	String generateGuestSignatureReport2(Long eventId, Long userId, Integer isCompanyDetails, HttpServletRequest re);
}
