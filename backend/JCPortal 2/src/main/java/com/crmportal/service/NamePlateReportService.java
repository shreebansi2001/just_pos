package com.crmportal.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.crmportal.request.NameplateRequestDto;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;

@Service
public interface NamePlateReportService {

	String counterNamePlateReport(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid);

	String mainStandyMenuReport(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid, Integer isCompanyDetails);

	String tableMenuReport(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid, Integer isCompanyDetails);

	String counterNamePlateTwoLanguageReport(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid);
	
	String customeNamePlateReportItem(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
			Long userid, Integer isCompanyDetails, Integer columns, Integer items);

	String counterNamePlateWithLogo(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate);

	String tableMenuReportWithBg(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate);

	String counterNamePlateWithLogo1(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate);

	String onePageCounterNamePlate(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang,
	        Long userid, Integer isCompanyDetails);

	String paarisoCounterNamePlate1(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate);

	String paarisoCounterNamePlate2(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate);

	String amoncarCounterNamePlate1(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate, Long imageId);

	String amoncarCounterNamePlate2(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate, Long imageId);

	String amoncarCounterNamePlate3(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate, Long imageId);

	String amoncarCounterNamePlate4(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate, Long imageId);

	String amoncarCounterNamePlate5(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate, Long imageId);

	String amoncarCounterNamePlate6(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate, Long imageId);

	String amoncarCounterNamePlate7(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			Integer isCompanyDetails, AdminTemplateModuleResponseDto adminTemplate, Long imageId);

	String rDevrajCounterNamePlate1(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			AdminTemplateModuleResponseDto adminTemplate);

	String rDevrajCounterNamePlate2(Long eventId, Long eventFunctionId, HttpServletRequest re, int lang, Long userid,
			AdminTemplateModuleResponseDto adminTemplate);

}
