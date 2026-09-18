package com.crmportal.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.MenuPreparationRequestDto;
import com.crmportal.request.dto.ReportMenuPlanningRequestDTO;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;
import com.crmportal.response.dto.AiEventFunctionMenuResponseDto;
import com.crmportal.response.dto.EventAndFunctionWisePartyResponseDto;
import com.crmportal.response.dto.EventFunctionRawMaterialPermissionResponseDto;
import com.crmportal.response.dto.MenuItemPartyMasterResponseDto;
import com.crmportal.response.dto.MenuPreparationCombResponseDto;
import com.crmportal.response.dto.MenuPreparationResponseDto;
import com.crmportal.response.dto.SavedMenuPreparationResponseDto;

@Service
public interface MenuPreparationService {

	MenuPreparationResponseDto addOrUpdateMenuPreparation(@Valid MenuPreparationRequestDto request);

	MenuPreparationCombResponseDto getMenuPreparationItems(Integer pageNo, Integer totalRecord, Long menuCategoryId,
			Long eventFunctionId, String itemName, Long userId);

	Boolean deleteMenuPreparationItem(Long menuPreparationId, Long menuCategoryId, Long itemId);


	String generateExclusiveReportType1(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetail,
			Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate,
			ReportMenuPlanningRequestDTO request, Integer showAddOnLabel,
			Integer isAllItemTogether);

	String generateExclusiveReportType2(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate);

	String generateExclusiveReportType3(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate);

	String generateExclusiveReportType4(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetail, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request);

	String generateExclusiveReportType5(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate);

	String generateExclusiveReportType6(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate);

	String generateExclusiveReportType7(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Long adminTemplateModuleId, Long userId,
			AdminTemplateModuleResponseDto adminTemplate);

	String generateExclusiveReportType8(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetail, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request);

	List<MenuItemPartyMasterResponseDto> getSelectedMenuItemByEventFunctionid(Long eventId,Long eventFunctionId,List<Long> partyIds);

	List<EventAndFunctionWisePartyResponseDto> getAgencyByEventAndEventFunctionid(Long eventId, List<Long> eventFunctionIds, String type, Long userId);

	Boolean copyeventfunctionmenu(Long oldEventFunctionId, Long activeEventFunctionId);

	String generateExclusiveReportType9(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request, Integer showAddonLabel);

	String generateExclusiveReportType10(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request);
	
	String generateExclusiveReportType11(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetail,
			Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate,
			ReportMenuPlanningRequestDTO request);

	String generateExclusiveReportType12(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request);

	String generateExclusiveReportType1Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request, Integer showAddOnLabel,
			Integer isAllItemTogether);

	String generateExclusiveReportType8Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request);

	String generateExclusiveReportType4Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request);

	String generateExclusiveReportType10Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request);

	String generateExclusiveReportType11Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request);

	String generateExclusiveReportType12Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request);

	String generateExclusiveReportType9Docx(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request, Integer showAddOnLabel);

	String generateExclusiveReportType13(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long partyId, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request);

	Boolean updateEventMenuPreparationStatus(Long eventId, String status);

	String getMenuPreparationStatus(Long eventId);

	String generateExclusiveReportType14(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request, Integer isAddDecoration,
			Integer showAdditional, Integer isAddMenu, Integer withVendor);

//	String generateExclusiveReportType9(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
//			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
//			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
//			AdminTemplateModuleResponseDto adminTemplate);
	
	public List<AiEventFunctionMenuResponseDto> getAiMenuData(String eventFunctionIdsCsv) throws Exception;

	String generateExclusiveReportType15(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request);

	String generateExclusiveReportType16(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request);

	String generateExclusiveReportType17(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request);

	String generateExclusiveReportType18(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request);

	EventFunctionRawMaterialPermissionResponseDto getEventFunctionPermissionRawMaterial(Long eventId,
			Long eventFunctionId, Long userId);

	String generateMenuItemSlogan(Long menuItemId, Long userId);

	String updateMenuItemImage(Long menuItemId, Long userId, MultipartFile image);

	String generateExclusiveReportType15_1(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request);

	String generateExclusiveReportType21(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request, Integer showAddOnLabel);

	String generateExclusiveReportType22(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request, Integer showAddOnLabel);

	String generateExclusiveReportType23(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction,
			HttpServletRequest re, Integer lang, Integer isCompanyDetails, Long adminTemplateModuleId, Long userid,
			AdminTemplateModuleResponseDto adminTemplate, ReportMenuPlanningRequestDTO request);

	List<SavedMenuPreparationResponseDto> getAllMenuPreparationItems(Long eventId, Long eventFunctionId);
}
