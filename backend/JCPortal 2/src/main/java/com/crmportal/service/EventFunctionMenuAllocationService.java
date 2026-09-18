package com.crmportal.service;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.request.dto.EventFunctionMenuAllocationRequestDto;
import com.crmportal.request.dto.ItemWeightRateCalRequestDto;
import com.crmportal.request.dto.ReportMenuPlanningRequestDTO;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;
import com.crmportal.response.dto.EventFunctionMenuAllocationFullResponseDto;
import com.crmportal.response.dto.EventFunctionMenuAllocationFunctionFullResponseDto;
import com.crmportal.response.dto.EventFunctionMenuAllocationResponseDto;
import com.crmportal.response.dto.MenuAllocationAgencyWithItemsResponseDto;
import com.crmportal.response.dto.MenuAllocationItemRawMaterialResponseDto;

@Service
public interface EventFunctionMenuAllocationService {

	Boolean addOrUpdatedMenuAllocation(@Valid List<EventFunctionMenuAllocationRequestDto> request);

	List<EventFunctionMenuAllocationFullResponseDto> getMenuAllocation(Long eventId, Long eventFunctionId);

	List<EventFunctionMenuAllocationFunctionFullResponseDto> getMenuAllocation(Long eventId, Long eventFunctionId,
			String type);

	Boolean deleteMenuAllocationOrder(Long id);

	List<MenuAllocationItemRawMaterialResponseDto> getAllRawMaterial(Long menuItemId, EventFunctionMasterEntity entity,
			Integer pax);

	Boolean syncRawMaterialItemByEventFunctionId(Long id, Long eventId);

	Boolean removeAgencyByEventFunctionId(Long eventFunctionId, Long userId);

	List<MenuAllocationAgencyWithItemsResponseDto> getAgencyWithItemType(String type, Long eventId,
			Long eventFunctionId, String startDate, String endDate);

	List<MenuAllocationAgencyWithItemsResponseDto> getAgencyWithItemType(String type, Long eventId,
			Long eventFunctionId, List<Long> agencyId, List<Long> itemId, String startDate, String endDate);

	List<MenuAllocationAgencyWithItemsResponseDto> getAgencyWithItemType(String type, Long eventId,
			Long eventFunctionId, Long partyId);

	String generateOutsideAgencyReportType1(Long eventId, Long eventFunctionId, List<Long> agencyId, List<Long> itemId,
			Integer isCompanyDetails, String type, HttpServletRequest re, Integer lang, Long userid);

	String generateOutsideAgencyReportType2(Long eventId, Long eventFunctionId, List<Long> agencyId, List<Long> itemId,
			Integer isCompanyDetails, String type, String startDate, String endDate, HttpServletRequest re,
			Integer lang, Long userid, Integer isWithPrice, Integer isAgencyNextPage);

	String generateOutsideAgencyReportType3(Long eventId, Long eventFunctionId, List<Long> agencyId, List<Long> itemId,
			Integer isCompanyDetails, String type, String startDate, String endDate, Long partyId,
			HttpServletRequest re, Integer lang, Long userid, Integer withPrice);

	String generateChefAgencyReportType1(Long eventId, Long eventFunctionId, List<Long> agencyId, List<Long> itemId,
			Integer isCompanyDetails, String type, HttpServletRequest re, Integer lang, Long userid);

	String generateChefAgencyReportType2(Long eventId, Long eventFunctionId, List<Long> agencyId, List<Long> itemId,
			Integer isCompanyDetails, String type, String startDate, String endDate, HttpServletRequest re,
			Integer lang, Long userid, Integer isWithPrice, Integer isAgencyNextPage);

	String generateChefAgencyReportType3(Long eventId, Long eventFunctionId, Integer isCompanyDetails, String type,
			HttpServletRequest re, Integer lang, Long userid, List<Long> agencyId, List<Long> itemId);

	String generateChefAgencyReportType4(Long eventId, Long eventFunctionId, Integer isCompanyDetails, String type,
			HttpServletRequest re, Integer lang, Long userid, List<Long> agencyId, List<Long> itemId);

	String generateChefAgencyReportType8(Long eventId, Long eventFunctionId, List<Long> agencyId, List<Long> itemId,
			Integer isCompanyDetails, String type, String startDate, String endDate, Long partyId,
			HttpServletRequest re, Integer lang, Long userid, Integer withPrice);

	String generateMenuForHmReport(Long eventId, Integer isCompanyDetails, HttpServletRequest re, Integer lang,
			Long userid, Integer isPartyDetails);

	String profitAndLossReport(Long eventId, Integer isCompanyDetails, HttpServletRequest re, Integer lang,
			Long userid);

	String generateInsideAgencyReportType1(Long eventId, Long eventFunctionId, List<Long> agencyId, List<Long> itemId,
			Integer isCompanyDetails, String type, String startDate, String endDate, HttpServletRequest re,
			Integer lang, Long userid, Integer isWithPrice);

	BigDecimal calculateRate(ItemWeightRateCalRequestDto request);

	String generateChefAgencyReportType1Docx(Long eventId, Long eventFunctionId, List<Long> agencyId, List<Long> itemId,
			Integer isCompanyDetails, String type, HttpServletRequest re, Integer lang, Long userid);

	String generateOutsideAgencyReportType1Docx(Long eventId, Long eventFunctionId, List<Long> agencyId,
			List<Long> itemId, Integer isCompanyDetails, String type, HttpServletRequest re, Integer lang, Long userid);

	Boolean syncItemWiseRawmaterialByMenuItemId(Long eventFunctionid, Long eventId, Long menuItemId);

	String generateAttendanceReport(Long eventId, HttpServletRequest re);

	String generateInsideAgencyChithhiReportType1(Long eventId, Long eventFunctionId, List<Long> agencyId,
			List<Long> itemId, Integer isCompanyDetails, String type, HttpServletRequest re, Integer lang, Long useri);

	String generateItemOrderHistoryReport(Integer isCompanyDetails, String startDate, String endDate, List<Long> itemId,
			ReportMenuPlanningRequestDTO request, HttpServletRequest re, Long userId);
}
