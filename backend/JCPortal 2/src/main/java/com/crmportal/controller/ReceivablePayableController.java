package com.crmportal.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.enums.EntryType;
import com.crmportal.service.ReceivablePayableService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping("/v1/api/recievale-payable")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class ReceivablePayableController {

	@Autowired
	ReceivablePayableService receivablePayableService;
	
	@GetMapping("/getallmonthwise")
	public ResponseEntity<?> getByMonthWise(@RequestParam("entryType") EntryType entryType,
			@RequestParam(value = "startDate", required = false) String startDate, 
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();

		try {
			Map<String, Object> res = receivablePayableService.getByMonthWise(entryType, startDate, endDate, userId);

			if (res != null) {
				response.put("data", res);
				response.put("msg", ConstantsPoc.ACCOUNT_ENTRY_FOUND_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.ACCOUNT_ENTRY_FOUND_FAIL);
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
