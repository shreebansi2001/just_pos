package com.crmportal.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.enums.AccountType;
import com.crmportal.enums.PaymentMode;
import com.crmportal.request.dto.ReportMenuPlanningRequestDTO;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;
import com.crmportal.service.AccountLedgerReportService;
import com.crmportal.service.AdminTemplateModuleService;
import com.crmportal.service.AgencyBookingService;
import com.crmportal.service.CostingReportService;
import com.crmportal.service.CustomPackageService;
import com.crmportal.service.DecorationReportService;
import com.crmportal.service.EmployeeReportService;
import com.crmportal.service.EventAdvancePaymentService;
import com.crmportal.service.EventFunctionMenuAllocationService;
import com.crmportal.service.ExecutionReportService;
import com.crmportal.service.ExpenseReportService;
import com.crmportal.service.GuestSignatureReportService;
import com.crmportal.service.LaborReportService;
import com.crmportal.service.LeadReportService;
import com.crmportal.service.LogReportService;
import com.crmportal.service.MenuPackageReportService;
import com.crmportal.service.MenuPreparationService;
import com.crmportal.service.MenuRawMaterialMasterDataExportService;
import com.crmportal.service.NamePlateReportService;
import com.crmportal.service.PurchaseReportService;
import com.crmportal.service.QuotationReportService;
import com.crmportal.service.RawMaterialExcelService;
import com.crmportal.service.RawMaterialReportService;
import com.crmportal.service.ReportService;
import com.crmportal.service.SimpleReportService;
import com.crmportal.service.SuperAdminIncomeReportService;
import com.crmportal.service.SuperAdminInvoiceReportService;
import com.crmportal.service.TGBQuotationReportService;
import com.crmportal.service.WorkReportService;

@Service

public class ReportServiceImpl implements ReportService {

	@Autowired
	AdminTemplateModuleService adminTemplateModuleService;

	@Autowired
	MenuPreparationService menuPreparationService;

	@Autowired
	SimpleReportService simpleReportService;

	@Autowired
	LaborReportService laborReportService;

	@Autowired
	ExecutionReportService executionReportService;

	@Autowired
	QuotationReportService quotationReportService;

	@Autowired
	ChitthiReportService chitthiReportService;

	@Autowired
	RawMaterialReportService rawMaterialReportService;

	@Autowired
	EventFunctionMenuAllocationService eventFunctionMenuAllocationService;

	@Autowired
	NamePlateReportService namePlateReportService;

	@Autowired
	CostingReportService costingReportService;

	@Autowired
	EmployeeReportService employeeReportService;

	@Autowired
	PurchaseReportService purchaseReportService;

	@Autowired
	ExpenseReportService expenseReportService;

	@Autowired
	SuperAdminInvoiceReportService superAdminInvoiceReportService;

	@Autowired
	SuperAdminIncomeReportService superAdminIncomeReportService;

	@Autowired
	GuestSignatureReportService guestSignatureReportService;

	@Autowired
	AccountLedgerReportService accountLedgerReportService;

	@Autowired
	MenuRawMaterialMasterDataExportService menuRawMaterialMasterDataService;

	@Autowired
	CustomPackageService customPackageService;

	@Autowired
	LeadReportService leadReportService;

	@Autowired
	EventAdvancePaymentService eventAdvancePaymentService;

	@Autowired
	DecorationReportService decorationReportService;

	@Autowired
	TGBQuotationReportService tgbQuotationReportService;

	@Autowired
	MenuPackageReportService menuPackageReportService;

	@Autowired
	AgencyBookingService agencyBookingService;

	@Autowired
	RawMaterialExcelService rawMaterialExcelService;

	@Autowired
	WorkReportService workReportService;

	@Override
	public String generateQuotationReport(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isInvoice,
			Long adminTemplateModuleId, Integer isQrCode, Integer isTermsCond, Integer isAdvance, Integer isWithPrice,
			Integer isCompanyDetails, Boolean isDecore, Integer isCombo, Integer isOnePage, Integer isNotes,
			Long exclusiveThemeId, Long backOfficeId, Integer showLastPage) {

		AdminTemplateModuleResponseDto adminTemplate = adminTemplateModuleService
				.getAdminTemplateModuleById(adminTemplateModuleId);

		AdminTemplateModuleResponseDto exclusiveTheme = null;
		if (exclusiveThemeId != null && exclusiveThemeId != -1) {
			exclusiveTheme = adminTemplateModuleService.getAdminTemplateModuleById(exclusiveThemeId);
			System.out.println("Exclusive theme : " + exclusiveTheme.getTemplateMaster().toString());
		}

		AdminTemplateModuleResponseDto backOffice = null;
		if (backOfficeId != null && backOfficeId != -1) {
			backOffice = adminTemplateModuleService.getAdminTemplateModuleById(backOfficeId);
		}

		String file = "";
		if (adminTemplate.getTemplateModuleMaster().getNameEnglish().equalsIgnoreCase("Quotation Reports")) {
			if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
				file = quotationReportService.getQuotationReport1(eventId, re, lang, userId, 0, isQrCode, isTermsCond,
						isAdvance, isCompanyDetails, isDecore, isNotes, exclusiveTheme, backOffice, showLastPage);
			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 2")) {

				file = quotationReportService.getQuotationReport2(eventId, re, lang, userId, 0, isQrCode, isTermsCond,
						isAdvance, isWithPrice, isDecore);

			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 3")) {
				// TGB Type Quotation
//				file = quotationReportService.getQuotationReport3(eventId, re, lang, userId, 0, isQrCode, isTermsCond,
//						isAdvance, isWithPrice, isDecore, isCombo, isOnePage,isCompanyDetails);

				file = tgbQuotationReportService.getQuotationReport3(eventId, re, lang, userId, 0, isQrCode,
						isTermsCond, isAdvance, isWithPrice, isDecore, isCombo, isOnePage, isCompanyDetails);
			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 4")) {
				// Papaya Tree
				file = quotationReportService.generateInvoiceType3(eventId, re, lang, userId, isTermsCond, isAdvance,
						isDecore, isCompanyDetails, isQrCode, isInvoice);
			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 5")) {
				// Blue Leaf
				file = quotationReportService.generateInvoiceType4(eventId, re, lang, userId, isTermsCond, isAdvance,
						isDecore, isCompanyDetails, isQrCode, isInvoice);
			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 6")) {
				// legacy banquet
				file = quotationReportService.generateInvoiceType5(eventId, re, lang, userId, isTermsCond, isAdvance,
						isDecore, isCompanyDetails, isQrCode, isInvoice, adminTemplate);
			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 7")) {
				// South Avenue
				file = quotationReportService.generateEstimateOrInvoiceType7(eventId, re, lang, userId, isTermsCond,
						isAdvance, isCompanyDetails, isQrCode, isInvoice, adminTemplate.getTemplateMaster().getName());
			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 8")) {
				// papaya tree sp. (Static data)
				file = quotationReportService.generateInvoiceType8(eventId, re, lang, userId, isTermsCond, isAdvance,
						isDecore, isCompanyDetails, isQrCode, isInvoice);
			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 9")) {
				// Blue Leaf sp. (Static data)
				file = quotationReportService.generateInvoiceType9(eventId, re, lang, userId, isTermsCond, isAdvance,
						isDecore, isCompanyDetails, isQrCode, isInvoice);
			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 10")) {
				// Noda Caterers Invoice
				file = quotationReportService.generateInvoiceType10(eventId, re, lang, userId, isTermsCond, isAdvance,
						isDecore, isCompanyDetails, isQrCode, isInvoice);
			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 11")) {
				// Annapurna Caterers Invoice
				file = quotationReportService.generateInvoiceType11(eventId, re, lang, userId, isTermsCond, isAdvance, isInvoice, isWithPrice);
			}
		} else if (adminTemplate.getTemplateModuleMaster().getNameEnglish().equalsIgnoreCase("Invoice Reports")) {
			if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
				file = quotationReportService.getQuotationReport1(eventId, re, lang, userId, 1, isQrCode, isTermsCond,
						isAdvance, isCompanyDetails, isDecore, isNotes, exclusiveTheme, backOffice, showLastPage);
			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 2")) {
				file = quotationReportService.generateInvoiceType2(eventId, re, lang, userId, isTermsCond, isAdvance,
						isDecore);
			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 3")) {
				// Papaya Tree
				file = quotationReportService.generateInvoiceType3(eventId, re, lang, userId, isTermsCond, isAdvance,
						isDecore, isCompanyDetails, isQrCode, isInvoice);
			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 4")) {
				// Blue Leaf
				file = quotationReportService.generateInvoiceType4(eventId, re, lang, userId, isTermsCond, isAdvance,
						isDecore, isCompanyDetails, isQrCode, isInvoice);
			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 5")) {
//				legacy banquet
				file = quotationReportService.generateInvoiceType5(eventId, re, lang, userId, isTermsCond, isAdvance,
						isDecore, isCompanyDetails, isQrCode, isInvoice, adminTemplate);
			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 6")) {
				// South Avenue
				file = quotationReportService.generateEstimateOrInvoiceType7(eventId, re, lang, userId, isTermsCond,
						isAdvance, isCompanyDetails, isQrCode, isInvoice, adminTemplate.getTemplateMaster().getName());
			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 7")) {
				// Amoncar banquet
				file = quotationReportService.generateInvoiceType7(eventId, re, lang, userId, isTermsCond, isAdvance,
						isDecore, isCompanyDetails, isQrCode, isInvoice);
			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 8")) {
				// papaya tree sp. (Static data)
				file = quotationReportService.generateInvoiceType8(eventId, re, lang, userId, isTermsCond, isAdvance,
						isDecore, isCompanyDetails, isQrCode, isInvoice);
			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 9")) {
				// Blue Leaf sp. (Static data)
				file = quotationReportService.generateInvoiceType9(eventId, re, lang, userId, isTermsCond, isAdvance,
						isDecore, isCompanyDetails, isQrCode, isInvoice);
			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 10")) {
				// Noda Caterers Invoice
				file = quotationReportService.generateInvoiceType10(eventId, re, lang, userId, isTermsCond, isAdvance,
						isDecore, isCompanyDetails, isQrCode, isInvoice);
			} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 11")) {
				// Annapurna Caterers Invoice
				file = quotationReportService.generateInvoiceType11(eventId, re, lang, userId, isTermsCond, isAdvance, isInvoice, isWithPrice);
			}
		}

		return file;
	}

	@Override
	public String getMenuPlanningExclusiveReport(Long eventId, List<Integer> eventStatus, Long eventFunctionId,
			List<Long> agencyId, List<Long> itemId, int lang, String startDate, String endDate, String pageSize,
			List<Long> rawMaterialCatIds, List<Long> eventFunctionIds, Long partyId, List<Long> managerIds,
			Long adminTemplateModuleId, Long userid, ReportMenuPlanningRequestDTO request, HttpServletRequest re) {
		String file = "";
		try {
			AdminTemplateModuleResponseDto adminTemplate = adminTemplateModuleService
					.getAdminTemplateModuleById(adminTemplateModuleId);
			
			if (adminTemplate.getTemplateModuleMaster().getNameEnglish().equalsIgnoreCase("Simple Theme")) {

				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 2")) {
					System.out.println("type 2 calling");
					file = simpleReportService.getMenuPlanningSimpleReport2(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
							request.getIsItemInstruction(), request.getIsCompanyDetails(), re, lang, userid);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 4")) {
					System.out.println("type 4 calling");
					file = simpleReportService.getMenuPlanningSimpleReport4(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
							request.getIsItemInstruction(), request.getIsCompanyDetails(), re, lang, userid);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 5")) {
					System.out.println("type 5 calling");
					file = simpleReportService.remaingDataReport(request.getIsCompanyDetails(), re, lang, userid);
				}
			}

			else if (adminTemplate.getTemplateModuleMaster().getNameEnglish().equalsIgnoreCase("Back Office Theme")) {
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 3")) { // upcoming
					System.out.println("type 3 calling");
					file = simpleReportService.getMenuPlanningSimpleReport3(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
							request.getIsItemInstruction(), request.getIsCompanyLogo(), request.getIsCompanyDetails(),
							re, lang, userid, request.getIsPartyDetails());
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 5")) {
					System.out.println("type 5 calling");
					file = simpleReportService.getMenuPlanningSimpleReport5(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
							request.getIsItemInstruction(), request.getIsCompanyLogo(), request.getIsCompanyDetails(),
							re, lang, userid, request.getIsPartyDetails(), request.getIsTermsCond(),
							request.getIsExtraCharges(), request.getIsAdvancePayment());

				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 6")) {
					System.out.println("type 6 calling");
					file = simpleReportService.getMenuPlanningSimpleReport6(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
							request.getIsItemInstruction(), request.getIsCompanyDetails(), re, lang, userid,
							request.getIsPartyDetails(), request.getIsTermsCond(), request.getIsExtraCharges());
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 7")) {
					System.out.println("type 7 calling");
					file = simpleReportService.getMenuPlanningSimpleReport7(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
							request.getIsItemInstruction(), request.getIsCompanyDetails(), re, lang, userid,
							request.getIsPartyDetails(), request);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
					System.out.println("type 1 calling");
					file = simpleReportService.getMenuPlanningSimpleReport1(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
							request.getIsItemInstruction(), request.getIsCompanyDetails(), re, lang, userid,
							request.getIsPartyDetails(), request.getIsTermsCond(), request.getIsExtraCharges());
					
//					file = simpleReportService.getMenuPlanningSimpleReport1Docx(eventId, eventFunctionId,
//					request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
//					request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
//					request.getIsItemInstruction(), request.getIsCompanyDetails(), re, lang, userid,
//					request.getIsPartyDetails());
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 8")) {
					// sai caterers
					System.out.println("type 8 calling");
					file = simpleReportService.getMenuPlanningSimpleReport8(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
							request.getIsItemInstruction(), request.getIsCompanyDetails(), re, lang, userid,
							request.getIsPartyDetails(), adminTemplate);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 9")) {
					// riddhi siddhi caterers
					System.out.println("type 9 calling");
					if (request.getIsDoc() == 0) {
						file = simpleReportService.getMenuPlanningSimpleReport9(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
								request.getIsItemInstruction(), request.getIsCompanyDetails(), re, lang, userid,
								request.getIsPartyDetails(), adminTemplate, request.getIsHalfPax(),
								request.getIsFunctionNextPage());
					} else {
						file = simpleReportService.getMenuPlanningSimpleReport9Docx(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
								request.getIsItemInstruction(), request.getIsCompanyDetails(), re, lang, userid,
								request.getIsPartyDetails(), adminTemplate, request.getIsHalfPax(),
								request.getIsFunctionNextPage());
					}

				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 10")) {
					// tgb
					if (request.getIsDoc() == 0) {
						file = simpleReportService.getMenuPlanningSimpleReport10(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
								request.getIsItemInstruction(), request.getIsCompanyDetails(), re, lang, userid,
								request.getIsPartyDetails());
					} else {
						file = simpleReportService.getMenuPlanningSimpleReport10Docx(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
								request.getIsItemInstruction(), request.getIsCompanyDetails(), re, lang, userid,
								request.getIsPartyDetails());
					}
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 11")) {
					if (request.getIsDoc() == 0) {
						file = simpleReportService.getMenuPlanningSimpleReport11(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
								request.getIsItemInstruction(), request.getIsCompanyDetails(), re, lang, userid,
								request.getIsPartyDetails());
					} else {
						file = simpleReportService.getMenuPlanningSimpleReport11Docx(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
								request.getIsItemInstruction(), request.getIsCompanyDetails(), re, lang, userid,
								request.getIsPartyDetails());
					}
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 12")) {
					// Short Menu
					// Rinku bhai
					file = simpleReportService.getMenuPlanningSimpleReport12(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
							request.getIsItemInstruction(), request.getIsCompanyLogo(), request.getIsCompanyDetails(),
							re, lang, userid, request.getIsPartyDetails(), request);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 13")) {
					// Bhandari
					if (request.getIsDoc() != 1) {
						file = simpleReportService.getMenuPlanningSimpleReport13(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
								request.getIsItemInstruction(), request.getIsCompanyDetails(), re, lang, userid,
								request.getIsPartyDetails(), request.getIsFunctionNextPage(), request.getIsHalfPax());
					} else {
						file = simpleReportService.getMenuPlanningSimpleReport13Docx(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
								request.getIsItemInstruction(), request.getIsCompanyDetails(), re, lang, userid,
								request.getIsPartyDetails(), request.getIsFunctionNextPage(), request.getIsHalfPax());
					}
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 14")) {
					// Amoncar
					System.out.println("Back office type 14 calling");
					file = simpleReportService.getMenuPlanningSimpleReport14(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
							request.getIsItemInstruction(), request.getIsCompanyDetails(), re, lang, userid,
							request.getIsPartyDetails(), adminTemplate, request.getIsSignatureVisible(),
							request.getIsAddDecoration(), request.getIsTermsCond(), request.getIsNotes());
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 15")) {
					// Cuisine Caterers
					System.out.println("Back office type 15 calling");
					file = simpleReportService.getMenuPlanningSimpleReport15(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
							request.getIsItemInstruction(), request.getIsCompanyDetails(), re, lang, userid,
							adminTemplate);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 16")) {
					// KKC item order history report
					file = eventFunctionMenuAllocationService.generateItemOrderHistoryReport(
							request.getIsCompanyDetails(), startDate, endDate, request.getItemId(), request, re, userid);
				}
			}

			else if (adminTemplate.getTemplateModuleMaster().getNameEnglish()
					.equalsIgnoreCase("Kitchen Report Theme")) {
				System.out.println("kitchen report calling");
				file = simpleReportService.getMenuPlanningWithChefReport(eventId, eventFunctionId,
						request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
						request.getIsCategoryImage().intValue(), request.getIsItemSlogan(),
						request.getIsItemInstruction(), request.getIsCompanyDetails(), re, lang, userid,
						request.getIsPartyDetails());
			}
//			else if(adminTemplate.getTemplateModuleMaster().getNameEnglish().equalsIgnoreCase("Custome Package Theme")) {
//				file = customPackageService.generateCustomPackageReport(request.getCustomPackageId(), userid, re,adminTemplate.getId());
//			}

			else if (adminTemplate.getTemplateModuleMaster().getNameEnglish().equalsIgnoreCase("Raw Material Theme")) {
				System.err.println("innnn");
//				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
//					System.out.println("Raw Material type 1 calling");
//					file = rawMaterialReportService.generateRawaterialReportType1(eventId, re, request.getLang(),
//							adminTemplateModuleId, userid, adminTemplate, request.getIsCompanyLogo(),
//							request.getIsCompanyDetails(), request.getIsPartyDetails(), eventFunctionId);
//				} else 
				 if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 2")) {
					file = executionReportService.rawMaterialwiseMenuItem(eventId, eventFunctionId, re, lang, userid,
							request.getIsCompanyDetails(), request.getIsPartyDetails());
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 3")) {
					System.out.println("Raw Material type 3 calling");
					if (request.getIsExcel() != 1) {
						if (request.getIsCombo() != 1) {
							if (request.getIsWithPrice() == 1) {
								file = rawMaterialReportService.generateRawaterialReportType3(eventId, re,
										request.getLang(), adminTemplateModuleId, userid, adminTemplate,
										request.getIsCompanyDetails(), eventFunctionIds, rawMaterialCatIds,
										request.getIsPartyDetails(), request.getIsAddStoreIssue());
							} else {
								file = rawMaterialReportService.generateRawaterialReportType4(eventId, re,
										request.getLang(), adminTemplateModuleId, userid, adminTemplate,
										request.getIsCompanyDetails(), eventFunctionIds, rawMaterialCatIds,
										request.getIsPartyDetails(), request.getIsAddStoreIssue());
							}
						} else {
							if (request.getIsWithPrice() == 1) {
								file = rawMaterialReportService.generateRawaterialReportType1_2(eventId, re,
										request.getLang(), adminTemplateModuleId, userid, adminTemplate,
										request.getIsCompanyLogo(), request.getIsCompanyDetails(),
										request.getIsPartyDetails(), eventFunctionIds, rawMaterialCatIds,
										request.getIsAddStoreIssue());
							} else {
								file = rawMaterialReportService.generateRawaterialReportType1(eventId, re,
										request.getLang(), adminTemplateModuleId, userid, adminTemplate,
										request.getIsCompanyLogo(), request.getIsCompanyDetails(),
										request.getIsPartyDetails(), eventFunctionIds, rawMaterialCatIds,
										request.getIsAddStoreIssue());
							}
						}
					} else {
						file = rawMaterialExcelService.generateRawMaterialExcelType3(eventId, re, request.getLang(),
								userid, eventFunctionIds, rawMaterialCatIds, request.getIsCombo(),
								request.getIsWithPrice(), request.getIsAddStoreIssue());
					}
//				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 4")) {
//					System.out.println("Raw Material type 4 calling");
//					file = rawMaterialReportService.generateRawaterialReportType4(eventId, re, request.getLang(),
//							adminTemplateModuleId, userid, adminTemplate, request.getIsCompanyDetails(), eventFunctionId);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 5")) {
					System.out.println("Raw Material type 5 calling");
//					file = rawMaterialReportService.generateRawaterialReportType5(eventId, eventFunctionId, re, lang,
//							userid, request.getIsCompanyLogo(), request.getIsCompanyDetails(),
//							request.getIsPartyDetails());

					file = rawMaterialReportService.generateSupplireRawaterialReportType5(eventId, eventFunctionId, re,
							lang, userid, request.getIsCompanyLogo(), request.getIsCompanyDetails(),
							request.getIsPartyDetails(), agencyId);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 6")) {
					if (request.getIsWithPrice() == 1) {
						file = rawMaterialReportService.generateDateWiseRawMaterialReportWithPrice(
								request.getIsCompanyDetails(), startDate, endDate, re, request.getLang(), userid,
								rawMaterialCatIds);
					} else {
						file = rawMaterialReportService.generateDateWiseRawMaterialReportWithoutPrice(
								request.getIsCompanyDetails(), startDate, endDate, re, request.getLang(), userid,
								rawMaterialCatIds);
					}
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 7")) {
					file = rawMaterialReportService.generateRawaterialReportType7(eventId, re, request.getLang(),
							adminTemplateModuleId, userid, adminTemplate, request.getIsCompanyDetails(),
							eventFunctionIds, rawMaterialCatIds, request.getIsPartyDetails(), request.getIsWithPrice(),
							request.getIsCombo(), request.getIsWithQty());
				}
			}

			else if (adminTemplate.getTemplateModuleMaster().getNameEnglish()
					.equalsIgnoreCase("Menu Allocation Theme")) {

				// with or without qty
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 6")) {
					if (request.getIsDoc() != 1) {
						file = executionReportService.menuReportWithQuantity(eventId, eventFunctionId, re, lang, userid,
								request.getIsWithQty(), request.getIsCompanyDetails(), request.getIsPartyDetails(),
								request.getIs3Column());
					} else {
						file = executionReportService.menuReportWithQuantityDocx(eventId, eventFunctionId, re, lang,
								userid, request.getIsWithQty(), request.getIsCompanyDetails(),
								request.getIsPartyDetails(), request.getIs3Column());
					}
				}

				// ridhhi sidhhi
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 7")) {

					if (request.getIsDoc() != 1) {
						file = executionReportService.menuReportWithQuantityRidhhiSidhhi(eventId, eventFunctionId, re,
								lang, userid, request.getIsWithQty(), request.getIsCompanyDetails(),
								request.getIsPartyDetails(), request.getIs3Column(), request.getIs5Column());
					} else {
						file = executionReportService.menuReportWithQuantityRidhhiSidhhiDocx(eventId, eventFunctionId,
								re, lang, userid, request.getIsWithQty(), request.getIsCompanyDetails(),
								request.getIsPartyDetails(), request.getIs3Column(), request.getIs5Column());
					}

				}
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
					file = eventFunctionMenuAllocationService.generateMenuForHmReport(eventId,
							request.getIsCompanyDetails(), re, request.getLang(), userid, request.getIsPartyDetails());
				}

				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 2")) {
					file = simpleReportService.dishCountingReport(eventId, request.getIsCompanyDetails(), re, lang,
							userid, request.getIsPartyDetails());
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 3")) {
					file = simpleReportService.dishCountingReportSinglePage(eventId, request.getIsCompanyDetails(), re,
							lang, userid, eventFunctionId, request.getIsPartyDetails());
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 4")) {
					file = guestSignatureReportService.generateGuestSignatureReport(eventId, userid, lang,
							request.getIsCompanyDetails(), re);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 5")) {
					// samir lakhani
					file = executionReportService.generateRawMaterialReport(eventId, eventFunctionId, re, lang, userid);
				}

			} else if (adminTemplate.getTemplateModuleMaster().getNameEnglish().equalsIgnoreCase("Chef Agency Theme")) {

				// chef
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 3")) {
					System.out.println("chef chithhi report 1");
					if (request.getIsDoc() == 0) {
						file = eventFunctionMenuAllocationService.generateChefAgencyReportType1(eventId,
								eventFunctionId, agencyId, itemId, request.getIsCompanyDetails(), request.getType(), re,
								request.getLang(), userid);
					} else {
						file = eventFunctionMenuAllocationService.generateChefAgencyReportType1Docx(eventId,
								eventFunctionId, agencyId, itemId, request.getIsCompanyDetails(), request.getType(), re,
								request.getLang(), userid);
					}
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 4")) {
					System.out.println("chef chithhi report 2");
					file = eventFunctionMenuAllocationService.generateChefAgencyReportType2(eventId, eventFunctionId,
							agencyId, itemId, request.getIsCompanyDetails(), request.getType(), startDate, endDate, re,
							request.getLang(), userid, request.getIsWithPrice(), request.getIsAgencyNextPage());
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 5")) {
					System.out.println("chef chithhi report 3");
					file = eventFunctionMenuAllocationService.generateChefAgencyReportType3(eventId, eventFunctionId,
							request.getIsCompanyDetails(), request.getType(), re, request.getLang(), userid,
							request.getAgencyId(), request.getItemId());
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 7")) {
					System.out.println("chef chithhi report 4");
					file = eventFunctionMenuAllocationService.generateChefAgencyReportType4(eventId, eventFunctionId,
							request.getIsCompanyDetails(), request.getType(), re, request.getLang(), userid,
							request.getAgencyId(), request.getItemId());
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 8")) {
					// remaining
					// date wise with & without price
					file = eventFunctionMenuAllocationService.generateChefAgencyReportType8(eventId, eventFunctionId,
							agencyId, itemId, request.getIsCompanyDetails(), request.getType(), startDate, endDate,
							partyId, re, request.getLang(), userid, request.getIsWithPrice());
				}
			} else if (adminTemplate.getTemplateModuleMaster().getNameEnglish()
					.equalsIgnoreCase("Inside Agency Theme")) {
				// inside
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
					System.out.println("Inside report 1");
					file = eventFunctionMenuAllocationService.generateInsideAgencyReportType1(eventId, eventFunctionId,
							agencyId, itemId, request.getIsCompanyDetails(), request.getType(), startDate, endDate, re,
							request.getLang(), userid, request.getIsWithPrice());
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 2")) {
					file = eventFunctionMenuAllocationService.generateInsideAgencyChithhiReportType1(eventId,
							eventFunctionId, agencyId, itemId, request.getIsCompanyDetails(), request.getType(), re,
							request.getLang(), userid);
				}
			}

			else if (adminTemplate.getTemplateModuleMaster().getNameEnglish()
					.equalsIgnoreCase("Outside Agency Theme")) {
				// outside
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
					System.out.println("Type 1 calling...");
					if (request.getIsDoc() == 0) {
						file = eventFunctionMenuAllocationService.generateOutsideAgencyReportType1(eventId,
								eventFunctionId, agencyId, itemId, request.getIsCompanyDetails(), request.getType(), re,
								request.getLang(), userid);
					} else {
						file = eventFunctionMenuAllocationService.generateOutsideAgencyReportType1Docx(eventId,
								eventFunctionId, agencyId, itemId, request.getIsCompanyDetails(), request.getType(), re,
								request.getLang(), userid);
					}
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 2")) {
					System.out.println("Type 2 calling...");
					file = eventFunctionMenuAllocationService.generateOutsideAgencyReportType2(eventId, eventFunctionId,
							agencyId, itemId, request.getIsCompanyDetails(), request.getType(), startDate, endDate, re,
							request.getLang(), userid, request.getIsWithPrice(), request.getIsAgencyNextPage());
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 3")) {
					System.out.println("Type 3 calling...");
					// remaining
					file = eventFunctionMenuAllocationService.generateOutsideAgencyReportType3(eventId, eventFunctionId,
							agencyId, itemId, request.getIsCompanyDetails(), request.getType(), startDate, endDate,
							partyId, re, request.getLang(), userid, request.getIsWithPrice());
				}

			}

			else if (adminTemplate.getTemplateModuleMaster().getNameEnglish().equalsIgnoreCase("Labour Agency Theme")) {
				// labor(agency)
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 5")) {
					if (pageSize.equalsIgnoreCase("A6")) {
						System.out.println("A6");
						file = laborReportService.generateLaborChithhiPageSizeA6(eventId, eventFunctionId, re, lang,
								userid, request.getIsCompanyDetails(), agencyId, request.getIsWithPrice());
					} else if (pageSize.equalsIgnoreCase("A4")) {
						System.out.println("in A4");
						file = laborReportService.generateLaborChithhi(eventId, eventFunctionId, re, lang, userid,
								request.getIsCompanyDetails(), agencyId, request.getIsWithPrice());
					} else {
						System.out.println("in A5");
						file = laborReportService.generateLaborChithhiPageSizeA5(eventId, eventFunctionId, re, lang,
								userid, request.getIsCompanyDetails(), agencyId, request.getIsWithPrice());
					}
				}
				// labour report (agency)
				else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 7")) {
					file = laborReportService.laborReport(eventId, eventFunctionId, re, lang, userid,
							request.getIsWithQty(), startDate, endDate, request.getIsCompanyDetails(), agencyId,
							request.getIsWithPrice(), request.getIsPartyDetails(), request.getIsAgencyNextPage());
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 8")) {
					// remaining
					if (request.getIsWithPrice() == 1) {
						file = laborReportService.laborReportDateWiseWithPrice(re, lang, userid, startDate, endDate,
								request.getIsCompanyDetails(), agencyId, partyId);
					} else {
						System.out.println("labour without price");
						file = laborReportService.laborReportDateWiseWithoutPrice(re, lang, userid, startDate, endDate,
								request.getIsCompanyDetails(), agencyId, partyId);
					}
				}

			} else if (adminTemplate.getTemplateModuleMaster().getNameEnglish().equalsIgnoreCase("Exclusive Theme")) {
				System.out.println(adminTemplate.getTemplateMappingResponseDto());
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
					System.out.println("-------type 1 calling");
					if (request.getIsDoc() == 0) {
						file = menuPreparationService.generateExclusiveReportType1(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(),
								re, request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
								adminTemplate, request, request.getShowAddOnLabel(), request.getIsAllItemTogether());
					} else {
						file = menuPreparationService.generateExclusiveReportType1Docx(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(),
								re, request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
								adminTemplate, request, request.getShowAddOnLabel(), request.getIsAllItemTogether());
					}
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 8")) {
					System.out.println("-------type 8 calling");
					if (request.getIsDoc() == 0) {
						file = menuPreparationService.generateExclusiveReportType8(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(),
								re, request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
								adminTemplate, request);
					} else {
						file = menuPreparationService.generateExclusiveReportType8Docx(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(),
								re, request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
								adminTemplate, request);
					}
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 4")) {
					System.out.println("type 4 calling");
					if (request.getIsDoc() == 0) {
						file = menuPreparationService.generateExclusiveReportType4(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(),
								re, request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
								adminTemplate, request);
					} else {
						file = menuPreparationService.generateExclusiveReportType4Docx(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(),
								re, request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
								adminTemplate, request);
					}
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 9")) {
					System.out.println("type 9 calling");
					if (request.getIsDoc() == 0) {
						file = menuPreparationService.generateExclusiveReportType9(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(),
								re, request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
								adminTemplate, request, request.getShowAddOnLabel());
					} else {
						file = menuPreparationService.generateExclusiveReportType9Docx(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(),
								re, request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
								adminTemplate, request, request.getShowAddOnLabel());
					}
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 10")) {
					System.out.println("type 10 calling");
					if (request.getIsDoc() == 0) {
						file = menuPreparationService.generateExclusiveReportType10(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(),
								re, request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
								adminTemplate, request);
					} else {
						file = menuPreparationService.generateExclusiveReportType10Docx(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(),
								re, request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
								adminTemplate, request);
					}
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 11")) {
					System.out.println("-------type 11 calling");
					if (request.getIsDoc() == 0) {
						file = menuPreparationService.generateExclusiveReportType11(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(),
								re, request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
								adminTemplate, request);
					} else {
						file = menuPreparationService.generateExclusiveReportType11Docx(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(),
								re, request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
								adminTemplate, request);
					}
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 12")) {
					System.out.println("-------type 12 calling");
					if (request.getIsDoc() == 0) {
						file = menuPreparationService.generateExclusiveReportType12(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(),
								re, request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
								adminTemplate, request);
					} else {
						file = menuPreparationService.generateExclusiveReportType12Docx(eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(),
								re, request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
								adminTemplate, request);
					}
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 13")) {
					file = menuPreparationService.generateExclusiveReportType13(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(), re,
							request.getLang(), request.getIsCompanyDetails(), request.getPartyId(),
							adminTemplateModuleId, userid, adminTemplate, request);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 14")) {
					System.out.println("Type 14 calling..");
					// TGB
					file = menuPreparationService.generateExclusiveReportType14(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(), re,
							request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
							adminTemplate, request, request.getIsAddDecoration(), request.getShowAdditional(),
							request.getIsAddMenu(),request.getWithVendor());
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 15")) {
					// Aroma Report ()
					System.out.println("type 15 calling");
//					file = menuPreparationService.generateExclusiveReportType15(eventId, eventFunctionId,
//							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
//							request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(), re,
//							request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
//							adminTemplate, request);

					file = menuPreparationService.generateExclusiveReportType15_1(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(), re,
							request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
							adminTemplate, request);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 16")) {
					// Aroma Special
					System.out.println("type 16 calling");
					file = menuPreparationService.generateExclusiveReportType16(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(), re,
							request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
							adminTemplate, request);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 17")) {
					// Samir Lakhani Hospitality
					System.out.println("type 17 calling");
					file = menuPreparationService.generateExclusiveReportType17(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(), re,
							request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
							adminTemplate, request);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 18")) {
					// South Avenue
					file = menuPreparationService.generateExclusiveReportType18(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(), re,
							request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
							adminTemplate, request);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 19")) {
					// Aroma Menu Package Report
					file = menuPackageReportService.generateMenuPackageReportType1(request.getLang(), userid,
							adminTemplate, request, re);
//					file = menuPackageReportService.generateMenuPackageReportType2(request.getLang(), userid, adminTemplate,
//							request, re);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 20")) {
					// Aroma Menu Package Report
					file = menuPackageReportService.generateMenuPackageReportType2(request.getLang(), userid,
							adminTemplate, request, re);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 21")) {
					// Amoncar Sp.
					file = menuPreparationService.generateExclusiveReportType21(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(), re,
							request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
							adminTemplate, request, request.getShowAddOnLabel());
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 22")) {
					// Amoncar Sp.
					file = menuPreparationService.generateExclusiveReportType22(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(), re,
							request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
							adminTemplate, request, request.getShowAddOnLabel());
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 23")) {
					// Samir Lakhani Theme 2
					file = menuPreparationService.generateExclusiveReportType23(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(), re,
							request.getLang(), request.getIsCompanyDetails(), adminTemplateModuleId, userid,
							adminTemplate, request);	
				}

				// up comming
				else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 2")) {
					System.out.println("type 2 calling");
					file = menuPreparationService.generateExclusiveReportType2(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(), re,
							request.getLang(), adminTemplateModuleId, userid, adminTemplate);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 3")) {
					System.out.println("type 3 calling");
					file = menuPreparationService.generateExclusiveReportType3(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(), re,
							request.getLang(), adminTemplateModuleId, userid, adminTemplate);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 5")) {
					System.out.println("type 5 calling");
					file = menuPreparationService.generateExclusiveReportType5(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(), re,
							request.getLang(), adminTemplateModuleId, userid, adminTemplate);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 6")) {
					System.out.println("type 6 calling");
					file = menuPreparationService.generateExclusiveReportType6(eventId, eventFunctionId,
							request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
							request.getIsCategoryImage(), request.getIsItemSlogan(), request.getIsItemInstruction(), re,
							request.getLang(), adminTemplateModuleId, userid, adminTemplate);
				}

			} else if (adminTemplate.getTemplateModuleMaster().getNameEnglish().equalsIgnoreCase("General Fix Theme")) {
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
					System.out.println("type 1 calling");
					if (request.getIsCombo() != 1) {
						file = rawMaterialReportService.generalFixReport(eventId, userid, eventFunctionId,
								rawMaterialCatIds, lang, request.getIsWithQty(), request.getIsCompanyDetails(), re);
					} else {
						file = rawMaterialReportService.generalFixReportComboReport(eventId, userid, eventFunctionId,
								rawMaterialCatIds, lang, request.getIsWithQty(), request.getIsCompanyDetails(), re);

					}
				}
			} else if (adminTemplate.getTemplateModuleMaster().getNameEnglish()
					.equalsIgnoreCase("Crockert Cutlery Theme")) {
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
					System.out.println("type 1 calling");
					file = rawMaterialReportService.crockeryCutleryReport2(eventId, userid, eventFunctionIds, lang,
							request.getIsWithQty(), request.getIsCompanyDetails(), re, request.getIsCompanyLogo(),
							request.getIsPartyDetails());
				}
			} else if (adminTemplate.getTemplateModuleMaster().getNameEnglish()
					.equalsIgnoreCase("Costing Report Theme")) {
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
					System.out.println("type 1 calling");
					if (request.getStoreIssueWise() == 0) {
						file = costingReportService.generateCostingReport(eventId, userid, lang,
								request.getIsCompanyDetails(), re);
					} else {
						file = costingReportService.generateStoreIssueWiseCostingReport(eventId, userid, lang,
								request.getIsCompanyDetails(), re);
					}
				}
			}
//			else if (adminTemplate.getTemplateModuleMaster().getNameEnglish().equalsIgnoreCase("Menu For HM")) {
//				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
//					file = eventFunctionMenuAllocationService.generateMenuForHmReport(eventId,
//							request.getIsCompanyDetails(), re, request.getLang(), userid);
//				}
//			} 
			else if (adminTemplate.getTemplateModuleMaster().getNameEnglish().equalsIgnoreCase("Profit And Loss")) {
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
					file = eventFunctionMenuAllocationService.profitAndLossReport(eventId,
							request.getIsCompanyDetails(), re, request.getLang(), userid);
				}
			}
//			else if (adminTemplate.getTemplateModuleMaster().getNameEnglish().equalsIgnoreCase("Dish Counting")) {
//				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
//					file = simpleReportService.dishCountingReport(eventId, request.getIsCompanyDetails(), re, lang,
//							userid);
//				}
//			} 
			else if (adminTemplate.getTemplateModuleMaster().getNameEnglish().equalsIgnoreCase("Order Summary Theme")) {
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
					System.out.println("order summary type 1");
					file = simpleReportService.orderSummaryReport(startDate, endDate, eventStatus,
							request.getIsCompanyDetails(), re, lang, userid, managerIds, partyId,
							request.getIsContactNoVisible());
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 2")) {
					System.out.println("order summary type 2");
					// tgb
					file = simpleReportService.orderSummaryReport2(startDate, endDate, eventStatus,
							request.getIsCompanyDetails(), re, lang, userid, managerIds, partyId);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 3")) {
					System.out.println("order summary type 3");
					// transporation plus total show
					file = simpleReportService.orderSummaryReport3(startDate, endDate, eventStatus,
							request.getIsCompanyDetails(), re, lang, userid, managerIds, partyId);
				}

			} else if (adminTemplate.getTemplateModuleMaster().getNameEnglish().equalsIgnoreCase("Lead Module")) {
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
					System.out.println("Lead module type 1");
					file = leadReportService.generateLeadReport(startDate, endDate, request.getStatusId(),
							request.getSourceId(), request.getPriority(), request.getLeadAssignId(),
							request.getIsCompanyDetails(), re, userid);
				}
				//follow up
				else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 2")) {
					System.out.println("Lead module type 2 for followup");
					file = leadReportService.generateFollowupReport(startDate, endDate, request.getStatusId(),
							request.getSourceId(), request.getPriority(), request.getLeadAssignId(),
							request.getIsCompanyDetails(), re, userid);
						}
			} else if (adminTemplate.getTemplateModuleMaster().getNameEnglish().equalsIgnoreCase("Decoration Theme")) {
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
					System.out.println("Decoration module type 1");
					if (request.getIsDoc() == 0) {
						file = decorationReportService.generateDecoreReport(adminTemplate,eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsItemImage().intValue(), request.getIsItemSlogan(),
								request.getIsItemInstruction(), request.getIsCompanyLogo(),
								request.getIsCompanyDetails(), re, lang, userid, request.getIsPartyDetails(),request.getWithOutBg(),request.getWithVendor(),request);
					} else {
						file = decorationReportService.generateDecoreReportDocx(adminTemplate,eventId, eventFunctionId,
								request.getIsCategorySlogan(), request.getIsCategoryInstruction(),
								request.getIsItemImage().intValue(), request.getIsItemSlogan(),
								request.getIsItemInstruction(), request.getIsCompanyLogo(),
								request.getIsCompanyDetails(), re, lang, userid, request.getIsPartyDetails(),request.getWithOutBg(),request.getWithVendor(),request);
					}
				}
			} else if (adminTemplate.getTemplateModuleMaster().getNameEnglish().equalsIgnoreCase("Attendance Theme")) {
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
					file = eventFunctionMenuAllocationService.generateAttendanceReport(eventId, re);
				}
			} else if (adminTemplate.getTemplateModuleMaster().getNameEnglish()
					.equalsIgnoreCase("Agency Booking Theme")) {
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
					file = agencyBookingService.generateAgencyBookingReport(eventId, userid, re, lang);
				}
			} else if (adminTemplate.getTemplateModuleMaster().getNameEnglish().equalsIgnoreCase("Work Report Theme")) {
				// nice caterers
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
					file = workReportService.generateWorkReport(eventId, userid, re, lang);
				}
			}

		} catch (

		RuntimeException e) {
			e.printStackTrace();
			throw new RuntimeException("Event Menu Report generation failed: No data found.", e);
		}
		return file;
	}

	@Override
	public String getNamePlateReport(Long eventId, Long eventFunctionId, int lang, Long adminTemplateModuleId,
			Integer isCompanyDetails, Integer twoLanugage, Long userid, HttpServletRequest re, Integer numberOfColumns,
			Integer numberOfItemsPerPage, Long imageId) {
		String file = "";
		try {
			AdminTemplateModuleResponseDto adminTemplate = adminTemplateModuleService
					.getAdminTemplateModuleById(adminTemplateModuleId);

			if (adminTemplate.getTemplateModuleMaster().getNameEnglish().equalsIgnoreCase("Name Plate Theme")) {
				if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 1")) {
					System.out.println("type 1 calling nameplate");
					if (twoLanugage == 1) {
						file = namePlateReportService.counterNamePlateTwoLanguageReport(eventId, eventFunctionId, re,
								lang, userid);
					} else {
						file = namePlateReportService.customeNamePlateReportItem(eventId, eventFunctionId, re, lang,
								userid, isCompanyDetails, numberOfColumns, numberOfItemsPerPage);
					}
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 2")) {
					System.out.println("type 2 calling nameplate");
					file = namePlateReportService.mainStandyMenuReport(eventId, eventFunctionId, re, lang, userid,
							isCompanyDetails);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 3")) {
					System.out.println("type 3 calling nameplate");
					file = namePlateReportService.tableMenuReport(eventId, eventFunctionId, re, lang, userid,
							isCompanyDetails);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 4")) {
					System.out.println("type 4 calling nameplate");

				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 5")) {
					System.out.println("type 5 calling nameplate");
					file = namePlateReportService.counterNamePlateWithLogo(eventId, eventFunctionId, re, lang, userid,
							isCompanyDetails, adminTemplate);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 6")) {
					System.out.println("type 6 calling nameplate");
					file = namePlateReportService.tableMenuReportWithBg(eventId, eventFunctionId, re, lang, userid,
							isCompanyDetails, adminTemplate);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 7")) {
					file = namePlateReportService.counterNamePlateWithLogo1(eventId, eventFunctionId, re, lang, userid,
							isCompanyDetails, adminTemplate);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 8")) {
					// RIDDHI SIDDHI NAME PLATE
					file = namePlateReportService.onePageCounterNamePlate(eventId, eventFunctionId, re, lang, userid,
							isCompanyDetails);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 9")) {
					// PAARISO NAME PLATE
					file = namePlateReportService.paarisoCounterNamePlate1(eventId, eventFunctionId, re, lang, userid,
							isCompanyDetails, adminTemplate);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 10")) {
					// PAARISO NAME PLATE
					file = namePlateReportService.paarisoCounterNamePlate2(eventId, eventFunctionId, re, lang, userid,
							isCompanyDetails, adminTemplate);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 11")) {
					// Amoncar (A4 Page)
					file = namePlateReportService.amoncarCounterNamePlate1(eventId, eventFunctionId, re, lang, userid,
							isCompanyDetails, adminTemplate, imageId);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 12")) {
					// Amoncar (A4 Page 2 X 2)
					file = namePlateReportService.amoncarCounterNamePlate2(eventId, eventFunctionId, re, lang, userid,
							isCompanyDetails, adminTemplate, imageId);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 13")) {
					// Amoncar (A4 Page Tent 4 X 2)
					file = namePlateReportService.amoncarCounterNamePlate3(eventId, eventFunctionId, re, lang, userid,
							isCompanyDetails, adminTemplate, imageId);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 14")) {
					// Amoncar (A3 Page)
					file = namePlateReportService.amoncarCounterNamePlate4(eventId, eventFunctionId, re, lang, userid,
							isCompanyDetails, adminTemplate, imageId);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 15")) {
					// Amoncar (A3 Page 3 X 3)
					file = namePlateReportService.amoncarCounterNamePlate5(eventId, eventFunctionId, re, lang, userid,
							isCompanyDetails, adminTemplate, imageId);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 16")) {
					// Amoncar (A3 Page Tent 6 X 3)
					file = namePlateReportService.amoncarCounterNamePlate6(eventId, eventFunctionId, re, lang, userid,
							isCompanyDetails, adminTemplate, imageId);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 17")) {
					// Amoncar (A3 Page 2 X 2)
					file = namePlateReportService.amoncarCounterNamePlate7(eventId, eventFunctionId, re, lang, userid,
							isCompanyDetails, adminTemplate, imageId);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 18")) {
					// R devraj
					file = namePlateReportService.rDevrajCounterNamePlate1(eventId, eventFunctionId, re, lang, userid, adminTemplate);
				} else if (adminTemplate.getTemplateMappingResponseDto().getNameEnglish().equalsIgnoreCase("Type 19")) {
					// R devraj
					file = namePlateReportService.rDevrajCounterNamePlate2(eventId, eventFunctionId, re, lang, userid, adminTemplate);
				}
			}
		} catch (

		RuntimeException e) {
			e.printStackTrace();
			throw new RuntimeException("Event Menu Report generation failed: No data found.", e);
		}
		return file;
	}

	@Override
	public String generateChitthi(Long eventId, Long eventFunctionId, Long contactId, String type, Long userId,
			HttpServletRequest re) {
		String file = "";
		try {
			file = chitthiReportService.generateChitthiReport(eventId, eventFunctionId, re, 0, userId, 1, contactId,
					type);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new RuntimeException("Event Menu Report generation failed: No data found.", e);
		}
		return file;
	}

	@Override
	public String generateEmployeeReportPerformance(Long employeeId, String startDate, String endDate, Integer lang,
			Long pipelineId, HttpServletRequest req, Long userId) {
		String file = "";
		try {
			file = employeeReportService.employeePerformaceReport(employeeId, startDate, endDate, lang, pipelineId, req,
					userId);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new RuntimeException("Employee Report generation failed: No data found.", e);
		}
		return file;
	}

	@Override
	public String generatePurchaseOrderReport(Long poId, int lang, Long userId, HttpServletRequest re) {
		String file = "";
		try {
			file = purchaseReportService.generatePurchaseOrderReport(poId, lang, userId, re);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new RuntimeException("Purchase Order Report generation failed: " + e.getMessage(), e);
		}
		return file;
	}

	@Override
	public String generateExpenseReport(Long userId, String type, Long expenseId, String startDate, String endDate,
			Long incomeExpenseTypeId, HttpServletRequest re, Long accountContactId) {
		String file = "";
		try {
			if (!type.equalsIgnoreCase("ALL")) {
				if (type.equalsIgnoreCase("trip")) {
					file = expenseReportService.generateTripExpenseReport(userId, type, expenseId, startDate, endDate,
							re, accountContactId);
				} else {
					file = expenseReportService.generateOfficeExpenseReport(userId, incomeExpenseTypeId, startDate,
							endDate, re, accountContactId);
				}
			} else {
				file = expenseReportService.generateAllExpensesReport(userId, startDate, endDate, re, accountContactId);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new RuntimeException("Expense Report generation failed: No data found.", e);
		}
		return file;
	}

	@Override
	public String generateInvoiceReport(Long invoiceId, HttpServletRequest re) {
		String file = "";
		try {
			file = superAdminInvoiceReportService.generateInvoiceReport(invoiceId, re);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new RuntimeException("Expense Report generation failed: No data found.", e);
		}
		return file;
	}

	@Override
	public String generateIncomeReport(String startDate, String endDate, AccountType accountType,
			PaymentMode paymentMode, Long cashAccountId, Long bankAccountId, Long typeId, Long userId,
			HttpServletRequest re) {
		String file = "";
		try {
			file = superAdminIncomeReportService.generateIncomeReport(startDate, endDate, accountType, paymentMode,
					cashAccountId, bankAccountId, typeId, userId, re);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new RuntimeException("Expense Report generation failed: No data found.", e);
		}
		return file;
	}

	@Override
	public String generateAccountLedgerReport(String startDate, String endDate, AccountType accountType,
			PaymentMode paymentMode, Long cashAccountId, Long bankAccountId, Long userId, HttpServletRequest req) {
		String file = "";
		try {
			file = accountLedgerReportService.generateAccountLedgerReport(startDate, endDate, accountType, paymentMode,
					cashAccountId, bankAccountId, userId, req);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new RuntimeException("Expense Report generation failed: No data found.", e);
		}
		return file;

	}

	@Override
	public String generateMenuRawMaterialMasterDataExcel(Long userId, HttpServletRequest request) {
		String file = "";
		try {
			file = menuRawMaterialMasterDataService.generateMenuRawMaterialMasterDataExcel(userId, request);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new RuntimeException("Data failed to export.", e);
		}
		return file;
	}

	@Override
	public String generateLeadReport(String startDate, String endDate, Long statusId, Long sourceId, String priority,
			List<Long> leadAssignId, Integer isCompanyDetails, HttpServletRequest req, Long userId) {
		String file = "";
		try {
			file = leadReportService.generateLeadReport(startDate, endDate, statusId, sourceId, priority, leadAssignId,
					isCompanyDetails, req, userId);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new RuntimeException("Lead Report generation failed: No data found.", e);
		}
		return file;
	}

	@Override
	public String generateAdvancePaymentReceipt(Long advancePaymentId, Long userId, Long eventId, HttpServletRequest re,
			Boolean isTermsCond, Long eventFunctionId) {
		String file = "";
		try {
			file = eventAdvancePaymentService.generateReport(advancePaymentId, userId, eventId, re, isTermsCond,
					eventFunctionId);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new RuntimeException("Lead Report generation failed: No data found.", e);
		}
		return file;
	}

	@Override
	public String generateFollowupReport(String startDate, String endDate, Long statusId, Long sourceId, String priority,
			List<Long> leadAssignId, Integer isCompanyDetails, HttpServletRequest req, Long userId) {
		String file = "";
		try {
			file = leadReportService.generateFollowupReport(startDate, endDate, statusId, sourceId, priority, leadAssignId,
					isCompanyDetails, req, userId);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new RuntimeException("Lead Report generation failed: No data found.", e);
		}
		return file;
	}
}
