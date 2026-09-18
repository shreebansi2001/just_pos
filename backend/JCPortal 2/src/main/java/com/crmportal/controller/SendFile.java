package com.crmportal.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.response.dto.AdminTemplateModuleResponseDto;
import com.crmportal.service.AdminTemplateModuleService;
import com.crmportal.service.QuotationReportService;

@RestController
@RequestMapping({ "/v1/api/sendFile" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class SendFile {

	@Autowired
	QuotationReportService quotationReportService;
	
	@Autowired
	AdminTemplateModuleService adminTemplateModuleService;
	
	@GetMapping("/sendQuotation")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> sendQuotayion(@RequestParam Long eventId, 
			@RequestParam int lang, @RequestParam Long userId, @RequestParam(value = "isQrCode", required = false) Integer isQrCode,@RequestParam(value = "isTermsCond", required = false) Integer isTermsCond,@RequestParam(value = "isAdvance", required = false) Integer isAdvance,@RequestParam(value = "isCompanyDetails", required = false) Integer isCompanyDetails,
			HttpServletRequest re,@RequestParam(value = "isDecore", defaultValue  = "false") Boolean isDecore,
			@RequestParam("isNotes") Integer isNotes,
			@RequestParam(value = "exclusiveThemeId", required = false) Long exclusiveThemeId,
			@RequestParam(value = "backOfficeId", required = false) Long backOfficeId,
			@RequestParam(value = "showLastPage", required = false) Integer showLastPage) {
		Map<String, Object> response = new HashMap<>();
		
		Boolean isSent = false;
		
		try {

			isSent = quotationReportService.sendMailQuotationReport(eventId, re, lang, userId, 1, isQrCode != null ? isQrCode : 0,isTermsCond != null ? isTermsCond : 0, isAdvance != null ? isAdvance : 0, isCompanyDetails != null ? isCompanyDetails : 0,isDecore, isNotes, exclusiveThemeId, backOfficeId, showLastPage);

			if (isSent ) {
				response.put("success", true);
				response.put("msg", "Mail send successfully.");
			} else {
				response.put("success", false);
				response.put("msg", "Mail is note sent.");
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
