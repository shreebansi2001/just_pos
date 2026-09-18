package com.crmportal.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.crmportal.enums.AccountType;
import com.crmportal.enums.EntryType;
import com.crmportal.enums.PaymentMode;
import com.crmportal.request.NameplateRequestDto;
import com.crmportal.request.dto.ReportMenuPlanningRequestDTO;

@Service
public interface ReportService {

	String getMenuPlanningExclusiveReport(Long eventId, List<Integer> eventStatus, Long eventFunctionId,
			List<Long> agencyId, List<Long> itemId, int lang, String startDate, String endDate, String pageSize,
			List<Long> rawMaterialCatIds, List<Long> eventFunctionIds, Long partyId, List<Long> managerIds,
			Long adminTemplateModuleId, Long userid, ReportMenuPlanningRequestDTO request, HttpServletRequest re);

	String generateQuotationReport(Long eventId, HttpServletRequest re, int lang, Long userId, Integer isInvoice,
			Long adminTemplateModuleId, Integer isQrCode, Integer isTermsCond, Integer isAdvance,Integer isWithPrice,Integer isCompanyDetails, Boolean isDecore,Integer isCombo, Integer isOnePage,
			Integer isNotes, Long exclusiveThemeId, Long backOfficeId, Integer showLastPage);

	String getNamePlateReport(Long eventId, Long eventFunctionId, int lang, Long adminTemplateModuleId,
			Integer isCompanyDetails, Integer twoLanugage, Long userid, HttpServletRequest re, Integer numberOfColumns,
			Integer numberOfItemsPerPage, Long imageId);

	String generateChitthi(Long eventId, Long eventFunctionId, Long contactId, String type, Long userId,
			HttpServletRequest re);

	String generateEmployeeReportPerformance(Long employeeId, String startDate, String endDate, Integer lang,
			Long pipelineId, HttpServletRequest req, Long userId);


	String generatePurchaseOrderReport(Long poId, int lang, Long userId, HttpServletRequest re);

	String generateExpenseReport(Long userId, String type, Long expenseId, String startDate, String endDate,
			Long incomeExpenseTypeId, HttpServletRequest re, Long accountContactId);

	String generateInvoiceReport(Long invoiceId, HttpServletRequest re);

	String generateIncomeReport(String startDate, String endDate, AccountType accounType, PaymentMode paymentMode, 
			Long cashAccountId, Long bankAccountId, Long typeId, Long userId, HttpServletRequest re);

	String generateAccountLedgerReport(String startDate, String endDate, AccountType accountType,
			PaymentMode paymentMode, Long cashAccountId, Long bankAccountId, Long userId, HttpServletRequest req);

	String generateMenuRawMaterialMasterDataExcel(Long userId, HttpServletRequest request);

	String generateLeadReport(String startDate, String endDate, Long statusId, Long sourceId, String priority,
			List<Long> leadAssignId, Integer isCompanyDetails, HttpServletRequest req, Long userId);

	String generateAdvancePaymentReceipt(Long advancePaymentId, Long userId, Long eventId, HttpServletRequest re, Boolean isTermsCond, Long eventFunctionId);
	
	String generateFollowupReport(String startDate, String endDate, Long statusId, Long sourceId, String priority,
			List<Long> leadAssignId, Integer isCompanyDetails, HttpServletRequest req, Long userId);

}