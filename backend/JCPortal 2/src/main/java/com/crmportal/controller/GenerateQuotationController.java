package com.crmportal.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.ReportMenuPlanningRequestDTO;
import com.crmportal.service.ReportService;

@RestController
@RequestMapping({ "/v1/api/quotationreport" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class GenerateQuotationController {

	@Autowired
	ReportService reportService;

	@PostMapping("/generatequotation")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> generateQuotation(@RequestParam("eventId") Long eventId,
			@RequestParam("lang") int lang, @RequestParam("userId") Long userId,
			@RequestParam("isInvoice") Integer isInvoice, HttpServletRequest re,
			@RequestParam("adminTemplateModuleId") Long adminTemplateModuleId,
			@RequestParam(value = "isQrCode", required = false) Integer isQrCode,
			@RequestParam(value = "isTermsCond", required = false) Integer isTermsCond,
			@RequestParam(value = "isAdvance", required = false) Integer isAdvance,
			@RequestParam(value = "isWithPrice", required = false) Integer isWithPrice,
			@RequestParam(value = "isCompanyDetails", required = false) Integer isCompanyDetails,
			@RequestParam(value = "isDecore", defaultValue = "false") Boolean isDecore,
			@RequestParam(value = "isCombo", required = false) Integer isCombo,
			@RequestParam(value = "isOnePage", required = false) Integer isOnePage,
			@RequestParam(value = "isNotes", required = false) Integer isNotes,
			@RequestParam(value = "exclusiveThemeId", required = false) Long exclusiveThemeId,
			@RequestParam(value = "backOfficeId", required = false) Long backOfficeId,
			@RequestParam(value = "showLastPage", required = false) Integer showLastPage) {

		Map<String, Object> response = new HashMap<>();

		try {

			String reportPath = reportService.generateQuotationReport(eventId, re, lang, userId, isInvoice,
					adminTemplateModuleId, isQrCode != null ? isQrCode : 0, isTermsCond != null ? isTermsCond : 0,
					isAdvance != null ? isAdvance : 0, isWithPrice != null ? isWithPrice : 0,
					isCompanyDetails != null ? isCompanyDetails : 0, isDecore, isCombo != null ? isCombo : 0,isOnePage != null ? isOnePage : 0,
					isNotes, exclusiveThemeId, backOfficeId, showLastPage);

			if (reportPath != null && reportPath.trim().length() != 0) {
				response.put("success", true);
				response.put("report_path", reportPath);
				response.put("msg", "Menu planning report fetched successfully");
			} else {
				response.put("success", false);
				response.put("msg", "Menu planning report fetched failed");
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
