package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.UserTermsAndConditionRequestDto;
import com.crmportal.response.dto.UserTermsAndConditionResponseDto;
import com.crmportal.service.UserTermsAndConditionService;

@RestController
@RequestMapping({ "/v1/api/termscondition" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class UserTermsAndConditionController {

	@Autowired
	UserTermsAndConditionService userTermsAndConditionService;

	@PostMapping("/add-update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addUpdate(@RequestBody UserTermsAndConditionRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = userTermsAndConditionService.addUpdate(request);
			if (isSuccess) {
				response.put("msg", "Terms & Condition Add/Updated Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "Terms & Condition Add/Updated Failed");
				response.put("success", isSuccess);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getall")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAll(@RequestParam("userId") Long userId,
			@RequestParam(value = "moduleName", required = false) String moduleName,
			@RequestParam(value = "isActive", required = false) Boolean isActive) {

		Map<String, Object> response = new HashMap<>();

		try {
			Map<String, Object> result = userTermsAndConditionService.getAll(userId, moduleName, isActive);

			response.put("data", result);
			response.put("msg", "Data Fetch Successfully");
			response.put("success", true);

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/delete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> delete(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = userTermsAndConditionService.delete(id);
			if (isSuccess) {
				response.put("msg", "Terms & Condition deleted Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "Terms & Condition deleted Failed");
				response.put("success", isSuccess);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/isActive")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> isActive(@RequestParam("isActive") Boolean isActive,
			@RequestParam("id") Long id) {

		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = userTermsAndConditionService.isActive(id, isActive);
			if (isSuccess) {
				response.put("msg", "Terms & Condition Updated Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "Terms & Condition Updated Failed");
				response.put("success", isSuccess);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
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
