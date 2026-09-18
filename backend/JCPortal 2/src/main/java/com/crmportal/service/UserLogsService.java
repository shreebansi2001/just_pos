package com.crmportal.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface UserLogsService {

	void saveUserLogs(String user, String eventType, String description, HttpServletRequest httpServletRequest, Long eventId);

	Map<String, Object> getAllLogs(String user, String startDate, String endDate, String eventType, Long eventId, Long userId);

	String inactiveUserExcel(HttpServletRequest request);
}
