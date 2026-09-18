package com.crmportal.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public interface AgencyBookingService {

	String generateAgencyBookingReport(Long eventId, Long userId, HttpServletRequest re, Integer lang);

}
