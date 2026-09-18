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

import com.crmportal.request.dto.InquiryRequestDto;
import com.crmportal.response.dto.InquiryResponseDto;
import com.crmportal.service.InquiryService;

@RestController
@RequestMapping({ "/v1/api/inquiry" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class InquiryController {

	@Autowired
	InquiryService inquiryService;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdateInquiry(@RequestBody InquiryRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			
			InquiryResponseDto responseDto = inquiryService.addOrUpdateInquiry(request);
			
			if(responseDto != null) {
				response.put("data", responseDto);
				response.put("success", true);
				response.put("msg", "Inquiry Created/Updated Successfully.");
			} else {
				response.put("success", false);
				response.put("msg", "Inquiry Creation/Updatation Successfully.");
			}
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
	
	@DeleteMapping("/deleteinquirybyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteInquiry(@RequestParam Long userId, 
			@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		
		try {
			isSuccess = inquiryService.deleteInquiryById(id, userId);
			
			if(isSuccess) {
				response.put("success", true);
				response.put("msg", "Inquiry Deleted Successfully.");
			} else {
				response.put("success", false);
				response.put("msg", "Inquiry Deletation Successfully.");
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
	
	@GetMapping("/getallinquiry")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllInquiry(@RequestParam Long userId, 
			@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			List<InquiryResponseDto> responseData = inquiryService.getAllInquiry(userId, startDate, endDate);
			
			if(responseData != null) {
				response.put("data", responseData);
				response.put("success", true);
				response.put("msg", "Inquiry Found Successfully.");
			} else {
				response.put("success", false);
				response.put("msg", "Inquiry Does Not Found.");
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
	
	@GetMapping("/getinquirybyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getInquiryById(@RequestParam Long userId, @RequestParam Long id) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			List<InquiryResponseDto> responseData = inquiryService.getInquiryById(userId, id);
			
			if(responseData != null) {
				response.put("data", responseData);
				response.put("success", true);
				response.put("msg", "Inquiry Found Successfully.");
			} else {
				response.put("success", false);
				response.put("msg", "Inquiry Does Not Found.");
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}
}
