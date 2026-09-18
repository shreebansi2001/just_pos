package com.crmportal.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public interface SuperAdminDashboardService {

	public Map<String, Object> planWiseTotal();
	
	public Map<String, Object> getTotalUserAndPlanData();
	
	public Map<String, Object> getMonthWisePlanTotal(Long planId, String startDate, String endDate);
	
	public Map<String, Object> getUsersDetailsBetweenDates(String startDate, String endDate);
	
	public Map<String, Object> getChartData(String startDate, String endDate, Long planId);

	public Map<String, Object> getInvoiceData();
}
