package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.enums.AccountType;
import com.crmportal.enums.EntryType;
import com.crmportal.enums.PaymentMode;
import com.crmportal.request.dto.ReportMenuPlanningRequestDTO;
import com.crmportal.service.LogReportService;
import com.crmportal.service.ReportService;

@RestController
@RequestMapping({ "/v1/api/report" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class ReportController {

	@Autowired
	private ReportService reportService;
	
	@Autowired
	LogReportService logReportService;

	@PostMapping("/menu-planning-exclusive/")
	public ResponseEntity<?> getMenuPlanningExclusiveReport(@RequestParam("eventId") Long eventId,
			@RequestParam(value = "eventFunctionId") Long eventFunctionId, @RequestParam("lang") int lang,
			@RequestParam(value = "pageSize", required = false) String pageSize, @RequestParam("userId") Long userId,
			@RequestParam("adminTemplateModuleId") Long adminTemplateModuleId,
			@ModelAttribute ReportMenuPlanningRequestDTO request, HttpServletRequest re) {

		Map<String, Object> response = new HashMap<>();
		try {

			String reportPath = reportService.getMenuPlanningExclusiveReport(eventId, request.getEventStatus(),
					eventFunctionId, request.getAgencyId(), request.getItemId(), lang, request.getStartDate(),
					request.getEndDate(), pageSize, request.getRawMaterialCatIds(), request.getEventFunctionIds(),
					request.getPartyId(), request.getManagerIds(), adminTemplateModuleId, userId, request, re);

			if (reportPath != null && reportPath.trim().length() != 0
					&& (reportPath.startsWith("https") || reportPath.startsWith("http"))) {
				response.put("success", true);
				response.put("report_path", reportPath);
				response.put("msg", "Menu planning report fetched successfully");
			} else {
				response.put("success", false);
				response.put("msg", reportPath);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/generate-name-plate")
	public ResponseEntity<?> getMenuPlanningExclusiveReport(@RequestParam Long eventId,
			@RequestParam Long eventFunctionId, @RequestParam Long userId, @RequestParam int lang,
			@RequestParam(name = "isCompanyDetails", required = false) Integer isCompanyDetails,
			@RequestParam(name = "twoLanugage", required = false) Integer twoLanugage,
			@RequestParam(name = "numberOfColumns", required = false) Integer numberOfColumns,
			@RequestParam(name = "numberOfItemsPerPage", required = false) Integer numberOfItemsPerPage,
			@RequestParam(name = "imageId", required = false) Long imageId,
			@RequestParam Long adminTemplateModuleId, HttpServletRequest re) {

		Map<String, Object> response = new HashMap<>();
		try {
			String reportPath = reportService.getNamePlateReport(eventId, eventFunctionId, lang, adminTemplateModuleId,
					isCompanyDetails, twoLanugage, userId, re, numberOfColumns, numberOfItemsPerPage, imageId);

			if (reportPath != null && reportPath.trim().length() != 0) {
				response.put("success", true);
				response.put("report_path", reportPath);
				response.put("msg", "Name Plate report fetched successfully");
			} else {
				response.put("success", false);
				response.put("msg", "Name Plate report fetched failed");
			}

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/generate-chitthi")
	public ResponseEntity<?> generateChitthi(@RequestParam Long eventId, @RequestParam Long eventFunctionId,
			@RequestParam Long userId, @RequestParam Long contactId, @RequestParam String type, HttpServletRequest re) {

		Map<String, Object> response = new HashMap<>();
		try {
			String reportPath = reportService.generateChitthi(eventId, eventFunctionId, contactId, type, userId, re);

			if (reportPath != null && reportPath.trim().length() != 0) {
				response.put("success", true);
				response.put("report_path", reportPath);
				response.put("msg", "Name Plate report fetched successfully");
			} else {
				response.put("success", false);
				response.put("msg", "Name Plate report fetched failed");
			}

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/generate-employee-report")
	public ResponseEntity<?> generateEmployeeReport(@RequestParam("employeeId") Long employeeId,@RequestParam("userId") Long userId,
			@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
			@RequestParam("lang") Integer lang, @RequestParam("pipelineId") Long pipelineId, HttpServletRequest req) {
		Map<String, Object> response = new HashMap<>();
		try {
			String reportUrl = reportService.generateEmployeeReportPerformance(employeeId, startDate, endDate, lang,
					pipelineId, req,userId);

			if (reportUrl != null && reportUrl.trim().length() != 0) {
				response.put("success", true);
				response.put("report_path", reportUrl);
				response.put("msg", "Employee report fetched successfully");
			} else {
				response.put("success", false);
				response.put("msg", "Employee report fetched failed");
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/generate-expense-report")
	public ResponseEntity<?> generateExpenseReport(
			@RequestParam(value = "userId", required = false) Long userId,
			@RequestParam("type") String type,
			@RequestParam(value = "incomeExpenseTypeId", required = false) Long incomeExpenseTypeId,
			@RequestParam(value = "expenseId", required = false) Long expenseId,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, 
			HttpServletRequest re,
			@RequestParam(value = "accountContactId", required = false) Long accountContactId) {
		Map<String, Object> response = new HashMap<>();
		try {
			String reportUrl = reportService.generateExpenseReport(userId, type, expenseId, startDate, endDate, incomeExpenseTypeId, re, accountContactId);

			if (reportUrl != null && reportUrl.trim().length() != 0) {
				response.put("success", true);
				response.put("report_path", reportUrl);
				response.put("msg", "Expense Report generated successfully.");
			} else {
				response.put("success", false);
				response.put("msg", "Expense Report generated failed.");
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/generate-invoice-report")
	public ResponseEntity<?> generateInvoiceReport(@RequestParam("invoiceId") Long invoiceId, HttpServletRequest re) {
		Map<String, Object> response = new HashMap<>();
		try {
			String reportUrl = reportService.generateInvoiceReport(invoiceId, re);

			if (reportUrl != null && reportUrl.trim().length() != 0) {
				response.put("success", true);
				response.put("report_path", reportUrl);
				response.put("msg", "Expense Report generated successfully.");
			} else {
				response.put("success", false);
				response.put("msg", "Expense Report generated failed.");
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/generate-income-report")
	public ResponseEntity<?> generateIncomeReport(@RequestParam("startDate") String startDate, 
			@RequestParam("endDate") String endDate,
			@RequestParam(value = "accountType", required = false) AccountType accountType,
			@RequestParam(value = "paymentMode", required = false) PaymentMode paymentMode,
			@RequestParam(value = "cashAccountId", required = false) Long cashAccountId,
			@RequestParam(value = "bankAccountId", required = false) Long bankAccountId,
			@RequestParam(value = "typeId", required = false) Long typeId,
			@RequestParam(value = "userId") Long userId,
			HttpServletRequest re) {
		Map<String, Object> response = new HashMap<>();
		try {
			String reportUrl = reportService.generateIncomeReport(startDate, endDate, accountType, paymentMode, cashAccountId, 
					bankAccountId, typeId, userId, re);

			if (reportUrl != null && reportUrl.trim().length() != 0) {
				response.put("success", true);
				response.put("report_path", reportUrl);
				response.put("msg", "Expense Report generated successfully.");
			} else {
				response.put("success", false);
				response.put("msg", "Expense Report generated failed.");
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/generate-account-ledger-report")
	public ResponseEntity<?> generateAccountLedgerReport(
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam("accountType") AccountType accountType,
			@RequestParam(value = "paymentMode", required = false) PaymentMode paymentMode,
			@RequestParam(value = "cashAccountId", required = false) Long cashAccountId,
			@RequestParam(value = "bankAccountId", required = false) Long bankAccountId,
			@RequestParam("userId") Long userId, HttpServletRequest re) {
		Map<String, Object> response = new HashMap<>();
		try {
			String reportUrl = reportService.generateAccountLedgerReport(startDate, endDate, accountType,
					paymentMode, cashAccountId, bankAccountId, userId, re);

			if (reportUrl != null && reportUrl.trim().length() != 0) {
				response.put("success", true);
				response.put("report_path", reportUrl);
				response.put("msg", "Expense Report generated successfully.");
			} else {
				response.put("success", false);
				response.put("msg", "Expense Report generated failed.");
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/generate-user-menuitem-rawmaterial-excel")
	public ResponseEntity<?> generateMenuItemRawMaterialMasterDataExcel(
			@RequestParam("userId") Long userId, 
			HttpServletRequest re) {
		Map<String, Object> response = new HashMap<>();
		try {
			String reportUrl = reportService.generateMenuRawMaterialMasterDataExcel(userId, re);

			if (reportUrl != null && reportUrl.trim().length() != 0) {
				response.put("success", true);
				response.put("report_path", reportUrl);
				response.put("msg", "Excel generated successfully.");
			} else {
				response.put("success", false);
				response.put("msg", "Expense Report generated failed.");
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/generate-date-wise-log-report")
	public ResponseEntity<?> generateDatewiseLogReport(
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("userId") Long userId,
			HttpServletRequest re) {
		Map<String, Object> response = new HashMap<>();
		try {
			String reportUrl = logReportService.generateLogReport(userId, re, startDate, endDate);

			if (reportUrl != null && reportUrl.trim().length() != 0) {
				response.put("success", true);
				response.put("report_path", reportUrl);
				response.put("msg", "Report generated successfully.");
			} else {
				response.put("success", false);
				response.put("msg", "Report generated failed.");
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}