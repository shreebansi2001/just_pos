package com.crmportal.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.ReportMenuPlanningRequestDTO;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;

@Service
public interface DecorationReportService {

	String generateDecoreReport(AdminTemplateModuleResponseDto adminTemplate, Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isItemImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyLogo, Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid,
			Integer isPartyDetails, Integer withOutBg, Integer withVendor, ReportMenuPlanningRequestDTO request);

	String generateDecoreReportDocx(AdminTemplateModuleResponseDto adminTemplate, Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isItemImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyLogo, Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid,
			Integer isPartyDetails,Integer withOutBg,Integer withVendor, ReportMenuPlanningRequestDTO request);

}
