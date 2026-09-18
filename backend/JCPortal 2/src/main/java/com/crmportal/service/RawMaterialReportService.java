package com.crmportal.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.crmportal.response.dto.AdminTemplateModuleResponseDto;

public interface RawMaterialReportService {

	String generateRawaterialReportType1(Long eventId, HttpServletRequest re, Integer lang, Long adminTemplateModuleId,
			Long userid, AdminTemplateModuleResponseDto adminTemplate,
			Integer isCompanyLogo, Integer isCompanyDetails,Integer isPartyDetails, List<Long> eventFunctionIds, List<Long> rawMaterialCatIds,
			Integer isAddStoreIssue);
	
	String generateRawaterialReportType1_2(Long eventId, HttpServletRequest re, Integer lang, Long adminTemplateModuleId,
			Long userid, AdminTemplateModuleResponseDto adminTemplate,
			Integer isCompanyLogo, Integer isCompanyDetails,Integer isPartyDetails, List<Long> eventFunctionIds, List<Long> rawMaterialCatIds,
			Integer isAddStoreIssue);
	
	String generateRawaterialReportType3(Long eventId, HttpServletRequest re, Integer lang, Long adminTemplateModuleId,
			Long userid, AdminTemplateModuleResponseDto adminTemplate,
			Integer isCompanyDetails, List<Long> eventFunctionIds, List<Long> rawMaterialCatIds, Integer isPartyDetails,
			Integer isAddStoreIssue);
	
	String generateRawaterialReportType4(Long eventId, HttpServletRequest re, Integer lang, Long adminTemplateModuleId,
			Long userid, AdminTemplateModuleResponseDto adminTemplate,
			Integer isCompanyDetails, List<Long> eventFunctionIds, List<Long> rawMaterialCatIds, Integer isPartyDetails,
			Integer isAddStoreIssue);

	String generateRawaterialReportType5(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid,
			Integer isCompanyLogo, Integer isCompanyDetails,Integer isPartyDetails);

	String generalFixReport(Long eventId, Long userid, Long eventFunctionId, List<Long> rawMaterialCatIds,
			int lang, Integer isWithQty, Integer isCompanyDetails, HttpServletRequest re);

	String crockeryCutleryReport(Long eventId, Long userid, Long eventFunctionId, int lang, Integer isWithQty,
			Integer isCompanyDetails, HttpServletRequest re, Integer isCompanyLogo, Integer isPartyDetails);

	String crockeryCutleryReport2(Long eventId, Long userid, List<Long> eventFunctionId, int lang, Integer isWithQty,
			Integer isCompanyDetails, HttpServletRequest re, Integer isCompanyLogo, Integer isPartyDetails);

	String generateSupplireRawaterialReportType5(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer isCompanyLogo, Integer isCompanyDetails, Integer isPartyDetails, List<Long> agencyId);

	String generateDateWiseRawMaterialReportWithPrice(Integer isCompanyDetails, String startDate, String endDate,
			HttpServletRequest re, Integer lang, Long userid, List<Long> rawMaterialCatIds);

	String generateDateWiseRawMaterialReportWithoutPrice(Integer isCompanyDetails, String startDate, String endDate,
			HttpServletRequest re, Integer lang, Long userid, List<Long> rawMaterialCatIds);

	String generalFixReportComboReport(Long eventId, Long userid, Long eventFunctionId, List<Long> rawMaterialCatIds,
			int lang, Integer isWithQty, Integer isCompanyDetails, HttpServletRequest re);

	String generateRawaterialReportType7(Long eventId, HttpServletRequest re, Integer lang, Long adminTemplateModuleId,
			Long userid, AdminTemplateModuleResponseDto adminTemplate, Integer isCompanyDetails,
			List<Long> eventFunctionIds, List<Long> rawMaterialCatIds, Integer isPartyDetails, Integer isWithPrice,
			Integer isCombo, Integer isWithQty);

}
