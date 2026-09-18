package com.crmportal.service;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public interface DashboardService {

	public Map<String, Object> userWiseDashboardData(Long userId);
	public Map<String, Object> userWiseDashboardCostingDataPieChart1(Long userId, String dateString);
	public Map<String, Object> userWiseSalesInvoiceDataPieChart2(Long userId, String dateString);
	public Map<String, Object> userWiseEventQuotationDataPieChart3(Long userId, String dateString);
	public Map<String, Object> getEventsByUserAndDateJson(Long userId, String startDate, String endDate);
	public Map<String, Object> getMostSellingItems(String startDate, String endDate,Long userId);
	public Map<String, Object> getSummery(Long userId);
}
