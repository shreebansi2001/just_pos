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

import com.crmportal.request.dto.AccountContactRequestDto;
import com.crmportal.response.dto.AccountContactResponseDto;
import com.crmportal.service.AccountContactService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/account-contact" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class AccountContactController {

	@Autowired
	AccountContactService accountContactService;

	@PostMapping("/add-update")
	public ResponseEntity<?> addOrUpdateAccountContact(@Valid @RequestBody AccountContactRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			AccountContactResponseDto res = accountContactService.addOrUpdateAccountContact(request);

			if (res == null) {
				response.put("success", false);
				response.put("msg", ConstantsPoc.ACCOUNT_CONTACT_CREATE_FAIL);
			} else {
				response.put("data", res);
				response.put("success", true);
				response.put("msg", ConstantsPoc.ACCOUNT_CONTACT_CREATE_SUCCESS);
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

	@GetMapping("/getall")
	public ResponseEntity<?> getAllAccountContact(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<AccountContactResponseDto> res = accountContactService.getAllAccountContactByUserId(userId);

			if (res == null) {
				response.put("success", false);
				response.put("msg", ConstantsPoc.ACCOUNT_CONTACT_FOUND_FAIL);
			} else {
				response.put("data", res);
				response.put("success", true);
				response.put("msg", ConstantsPoc.ACCOUNT_CONTACT_FOUND_SUCCESS);
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

	@GetMapping("/getbyid")
	public ResponseEntity<?> getAccountContactById(@RequestParam("accountContactId") Long accountContactId) {
		Map<String, Object> response = new HashMap<>();
		try {
			AccountContactResponseDto res = accountContactService.getAccountContactById(accountContactId);

			if (res == null) {
				response.put("success", false);
				response.put("msg", ConstantsPoc.ACCOUNT_CONTACT_FOUND_FAIL);
			} else {
				response.put("data", res);
				response.put("success", true);
				response.put("msg", ConstantsPoc.ACCOUNT_CONTACT_FOUND_SUCCESS);
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

	@DeleteMapping("/deletebyid")
	public ResponseEntity<?> deleteAccountContactById(@RequestParam("accountContactId") Long accountContactId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = accountContactService.deleteAccountContactById(accountContactId);

			if (!isSuccess) {
				response.put("success", false);
				response.put("msg", ConstantsPoc.ACCOUNT_CONTACT_DELETE_FAIL);
			} else {
				response.put("success", true);
				response.put("msg", ConstantsPoc.ACCOUNT_CONTACT_DELETE_SUCCESS);
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
