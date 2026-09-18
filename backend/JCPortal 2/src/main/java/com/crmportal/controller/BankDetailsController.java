package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import com.crmportal.enums.FileType;
import com.crmportal.request.dto.BankDetailsRequestDto;
import com.crmportal.response.dto.BankDetailsResponseDto;
import com.crmportal.service.BankDetailsService;

@RestController
@RequestMapping({ "/v1/api/bankdetails" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class BankDetailsController {

	@Autowired
	BankDetailsService bankDetailsService;
	
	@PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addUpdateBankDetails(@ModelAttribute BankDetailsRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			BankDetailsResponseDto bankDetailsResponseDto = bankDetailsService.addUpdateBankDetails(request);
			
			if(bankDetailsResponseDto != null) {
				response.put("data", bankDetailsResponseDto);
				response.put("msg", "Bank details added successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Bank details is not added.");
				response.put("success", false);
			}
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(RuntimeException e) { 
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getbyuserid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getBankDetailsByUserId(@RequestParam Long userId) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			List<BankDetailsResponseDto> bankDetailsResponseDtos = bankDetailsService.getBankDetailsByUserId(userId);
			
			if(bankDetailsResponseDtos != null) {
				response.put("data", bankDetailsResponseDtos);
				response.put("msg", "Bank details added successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Bank details is not added.");
				response.put("success", false);
			}
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(RuntimeException e) { 
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/get")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getBankDetails(@RequestParam(value = "id", required = false) Long id) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			List<BankDetailsResponseDto> responseDtos = bankDetailsService.getBankDetails(id);
			
			if(responseDtos != null && !responseDtos.isEmpty()) {
				response.put("data", responseDtos);
				response.put("msg", "Bank details added successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Bank details is not added.");
				response.put("success", false);
			}
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(RuntimeException e) { 
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/deletebyid")
	public ResponseEntity<?> deleteBankDetailById(@RequestParam("bankAccountId") Long bankAccountId) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			Boolean isSuccess = bankDetailsService.deleteBankAccountById(bankAccountId);
			
			if(isSuccess) {
				response.put("msg", "Bank details deleted successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Bank details deleted failed.");
				response.put("success", false);
			}
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(RuntimeException e) { 
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
