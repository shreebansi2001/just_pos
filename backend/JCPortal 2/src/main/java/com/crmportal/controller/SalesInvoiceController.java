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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.BankDetailsRequestDto;
import com.crmportal.request.dto.SalesInvoiceRequestDto;
import com.crmportal.response.dto.BankDetailsResponseDto;
import com.crmportal.response.dto.SalesInvoiceResponseDto;
import com.crmportal.service.SalesInvoiceService;

@RestController
@RequestMapping({ "/v1/api/salesinvoice" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class SalesInvoiceController {

	@Autowired
	SalesInvoiceService salesInvoiceService;
	
	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addUpdateSalesInvoice(@RequestBody SalesInvoiceRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			SalesInvoiceResponseDto salesInvoiceResponseDto = salesInvoiceService.addUpdateSalesInvoice(request);
			
			if(salesInvoiceResponseDto != null) {
				response.put("data", salesInvoiceResponseDto);
				response.put("msg", "Sales Invoice added successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Sales Invoice is not added.");
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
	
	@DeleteMapping("/delete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addUpdateSalesInvoice(@RequestParam Long salesInvoiceid) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			Boolean isSuccess = salesInvoiceService.deleteSalesInvoiceById(salesInvoiceid);
			
			if(isSuccess) {
				response.put("msg", "Data deleted successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Data is not deleted.");
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
	
	@GetMapping("/getbyeventidanduserid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getSalesInvoice(@RequestParam Long userId,
			@RequestParam Long eventId) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			Map<String, Object> responseDtos = salesInvoiceService.getSalesInvoiceByUserIdAndEventId(userId, eventId);
			
			if(responseDtos != null) {
				response.put("data", responseDtos);
				response.put("msg", "Sales Invoice found successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Sales Invoice is not found.");
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
