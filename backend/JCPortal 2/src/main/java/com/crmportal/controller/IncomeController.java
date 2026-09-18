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

import com.crmportal.enums.AccountType;
import com.crmportal.enums.PaymentMode;
import com.crmportal.response.dto.IncomeResponseDto;
import com.crmportal.service.IncomeService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({"/v1/api/income"})
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class IncomeController {

	@Autowired
	IncomeService incomeService;
	
	@GetMapping("/get")
	public ResponseEntity<?> getIncome(
			@RequestParam(value = "startDate", required = false) String startDate, 
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "accountType", required = false) AccountType accountType,
			@RequestParam(value = "paymentMode", required = false) PaymentMode paymentMode,
			@RequestParam(value = "bankAccountId", required = false) Long bankAccountId,
			@RequestParam(value = "cashAccountId", required = false) Long cashAccountId,
			@RequestParam(value = "typeId", required = false) Long typeId,
			@RequestParam(value = "userId", required = false) Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			IncomeResponseDto res = incomeService.getIncome(startDate, endDate, accountType, paymentMode, bankAccountId, cashAccountId, typeId, userId); 

			if (res == null) {
				response.put("msg", ConstantsPoc.INCOME_FOUND_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.INCOME_FOUND_SUCCESS);
				response.put("success", true);
				response.put("data", res);
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
