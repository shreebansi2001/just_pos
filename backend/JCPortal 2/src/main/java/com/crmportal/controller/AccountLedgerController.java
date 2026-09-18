package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.crmportal.enums.AccountType;
import com.crmportal.enums.PaymentMode;
import com.crmportal.response.dto.AccountLedgerDashboardResponseDto;
import com.crmportal.response.dto.AccountLedgerResponseDto;
import com.crmportal.service.AccountLedgerService;
import com.crmportal.utility.ConstantsPoc;

@Repository
@RequestMapping({ "/v1/api/account-ledger" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class AccountLedgerController {

	@Autowired
	AccountLedgerService accountLedgerService;

	@GetMapping("/")
	public ResponseEntity<?> getAccountLedger(@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("accountType") AccountType accountType,
			@RequestParam(value = "paymentMode", required = false) PaymentMode paymentMode,
			@RequestParam(value = "cashAccountId", required = false) Long cashAccountId,
			@RequestParam(value = "bankAccountId", required = false) Long bankAccountId,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();

		try {
			AccountLedgerDashboardResponseDto res = accountLedgerService.getAccountLedger(startDate, endDate, accountType,
					paymentMode, cashAccountId, bankAccountId, userId);

			if(res == null) {
				response.put("success", false);
				response.put("msg", ConstantsPoc.ACCOUNT_LEDGER_FOUND_FAIL);
			}else {
				response.put("data", res);
				response.put("success", true);
				response.put("msg", ConstantsPoc.ACCOUNT_LEDGER_FOUND_SUCCESS);
			}
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("msg", e.getLocalizedMessage());
			response.put("success", false);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("msg", e.getLocalizedMessage());
			response.put("success", false);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

}
