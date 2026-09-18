package com.crmportal.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.ReportMenuPlanningRequestDTO;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;

@Service
public interface MenuPackageReportService {

	String generateMenuPackageReportType1(Integer lang, Long userid, AdminTemplateModuleResponseDto adminTemplate,
			ReportMenuPlanningRequestDTO request, HttpServletRequest re);

	String generateMenuPackageReportType2(Integer lang, Long userid, AdminTemplateModuleResponseDto adminTemplate,
			ReportMenuPlanningRequestDTO request, HttpServletRequest re);

}
