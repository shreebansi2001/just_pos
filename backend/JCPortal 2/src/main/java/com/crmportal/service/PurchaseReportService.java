package com.crmportal.service;
 
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
 
@Service
public interface PurchaseReportService {
    String generatePurchaseOrderReport(Long poId, int lang, Long userId, HttpServletRequest re);

	String generateDatewisePurchaseReport(Long userId, String startDate, String endDate,
			HttpServletRequest re, Integer isCompanyDetails, Integer isWithPrice);
}
 