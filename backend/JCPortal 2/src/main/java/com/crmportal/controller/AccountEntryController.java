package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.enums.AccountType;
import com.crmportal.enums.EntryType;
import com.crmportal.enums.PaymentMode;
import com.crmportal.request.dto.AccountEntryRequestDto;
import com.crmportal.response.dto.AccountEntryResponseDto;
import com.crmportal.service.AccountEntryService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/account-entry" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class AccountEntryController {

	@Autowired
	AccountEntryService accountEntryService;

	@GetMapping("/generateVoucherNo")
	public ResponseEntity<?> generateVoucherNo(@RequestParam("entryType") EntryType entryType,
			@RequestParam("accountType") AccountType accountType, @RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();

		try {
			String voucherNo = accountEntryService.generateVoucherNo(entryType, accountType, userId);

			if (voucherNo != null) {
				response.put("data", voucherNo);
				response.put("msg", ConstantsPoc.VOUCHER_NO_GENERATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.VOUCHER_NO_GENERATE_FAIL);
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

	@PostMapping("/add-update")
	public ResponseEntity<?> addOrUpdateEntry(@Valid @RequestBody AccountEntryRequestDto request,
			@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			AccountEntryResponseDto res = accountEntryService.addOrUpdateEntry(request, id);

			if (res != null) {
				response.put("data", res);
				response.put("msg", ConstantsPoc.ACCOUNT_ENTRY_ADDED_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.ACCOUNT_ENTRY_ADDED_FAIL);
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

	@GetMapping("/getallbytype")
	public ResponseEntity<?> getAllByType(@RequestParam("entryType") EntryType entryType,
			@RequestParam("accountType") AccountType accountType,
			@RequestParam(value = "paymentMode", required = false) PaymentMode paymentMode,
			@RequestParam(value = "cashTypeId", required = false) Long cashTypeId,
			@RequestParam(value = "bankAccountId", required = false) Long bankAccountId,
			@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();

		try {
			List<AccountEntryResponseDto> res = accountEntryService.getAllByType(entryType, accountType, paymentMode,
					cashTypeId, bankAccountId, startDate, endDate, userId);

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
	
	@GetMapping("/getbyid")
	public ResponseEntity<?> getById(@RequestParam("accountEntryId") Long accountEntryId) {
		Map<String, Object> response = new HashMap<>();

		try {
			AccountEntryResponseDto res = accountEntryService.getById(accountEntryId);
			
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
	
	@DeleteMapping("/deletebyid")
	public ResponseEntity<?> deleteById(@RequestParam("accountEntryId") Long accountEntryId) {
		Map<String, Object> response = new HashMap<>();

		try {
			Boolean isDeleted = accountEntryService.deleteById(accountEntryId);
			
			if (isDeleted) {
				response.put("data", isDeleted);
				response.put("msg", ConstantsPoc.ACCOUNT_ENTRY_DELETE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.ACCOUNT_ENTRY_DELETE_FAIL);
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
