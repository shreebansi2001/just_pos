package com.crmportal.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.AdminTemplateModuleRequestDTO;
import com.crmportal.request.dto.ReportMenuPlanningRequestDTO;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;

@Service
public interface SimpleReportService {

	String getMenuPlanningSimpleReport1(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails, Integer isTermsCondition, Integer isExtraCharges);

	String getMenuPlanningSimpleReport2(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid);

	String getMenuPlanningSimpleReport3(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isUserLogo, Integer isUserDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails);

	String getMenuPlanningSimpleReport4(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid);

	String getMenuPlanningSimpleReport5(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isUserLogo, Integer isUserDetails, HttpServletRequest re, int lang, Long userid,
			Integer isPartyDetails, Integer isTermsCondition, Integer isExtraCharges, Integer isAdvancePayment);

	String getMenuPlanningSimpleReport6(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails, Integer isTermsCondition, Integer isExtraCharges);

	String getMenuPlanningSimpleReport7(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails, ReportMenuPlanningRequestDTO request);

	String remaingDataReport(Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid);

	String dishCountingReport(Long eventId, Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails);

	String orderSummaryReport(String startDate, String endDate, List<Integer> eventStatus, Integer isCompanyDetails,
			HttpServletRequest re, int lang, Long userid, List<Long> managerIds, Long partyId, Integer isContactNoVisible);

	String dishCountingReportSinglePage(Long eventId, Integer isCompanyDetails, HttpServletRequest re, int lang,
			Long userid, Long eventFunctionId, Integer isPartyDetails);
	
	String getMenuPlanningWithChefReport(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails);

	String getMenuPlanningSimpleReport8(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, int intValue, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails,
			AdminTemplateModuleResponseDto adminTemplate);

	String getMenuPlanningSimpleReport9(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, int intValue, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails,
			AdminTemplateModuleResponseDto adminTemplate, Integer isHalfPax, Integer isFunctionNextPage);

//	String getMenuPlanningSimpleReport1Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
//			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
//			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails);
	
	String getMenuPlanningSimpleReport9Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, int intValue, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails,
			AdminTemplateModuleResponseDto adminTemplate, Integer isHalfPax, Integer isFunctionNextPage);

	String getMenuPlanningSimpleReport10(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails);

	String getMenuPlanningSimpleReport10Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, int intValue, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails);

	String getMenuPlanningSimpleReport11(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails);

	String getMenuPlanningSimpleReport11Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails);

	String getMenuPlanningSimpleReport12(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, int intValue, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyLogo, Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid,
			Integer isPartyDetails, ReportMenuPlanningRequestDTO request);

	String getMenuPlanningSimpleReport13(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Integer isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails,
			Integer isFunctionNextPage, Integer isHalfPax);

	String getMenuPlanningSimpleReport13Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, int intValue, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails,
			Integer isFunctionNextPage, Integer isHalfPax);

	String orderSummaryReport2(String startDate, String endDate, List<Integer> eventStatus, Integer isCompanyDetails,
			HttpServletRequest re, int lang, Long userid, List<Long> managerIds, Long partyId);

	String getMenuPlanningSimpleReport14(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, int intValue, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid, Integer isPartyDetails,
			AdminTemplateModuleResponseDto adminTemplate, Integer isSignatureVisible, Integer isAddDecoration,
			Integer isTermsCond, Integer isNotes);
	
	String orderSummaryReport3(String startDate, String endDate, List<Integer> eventStatus, Integer isCompanyDetails,
			HttpServletRequest re, int lang, Long userid, List<Long> managerIds, Long partyId);

	String getMenuPlanningSimpleReport15(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, int categoryImage, Integer isItemSlogan, Integer isItemInstruction,
			Integer isCompanyDetails, HttpServletRequest re, int lang, Long userid,
			AdminTemplateModuleResponseDto adminTemplate);
}
