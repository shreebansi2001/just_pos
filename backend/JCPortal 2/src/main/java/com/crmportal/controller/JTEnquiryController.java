package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.enums.JTEnquiryServiceType;
import com.crmportal.request.dto.JTEnquiryRequestDto;
import com.crmportal.response.dto.JTEnquiryResponseDto;
import com.crmportal.service.JTEnquiryService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/jt-enquiry" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class JTEnquiryController {

	@Autowired
	JTEnquiryService jtEnquiryService;
	
	@PostMapping("/add-update")
	public ResponseEntity<Map<String, Object>> addOrUpdateEnquiry(@Valid @RequestBody JTEnquiryRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			JTEnquiryResponseDto responseDto = jtEnquiryService.addOrUpdateEnquiry(request);
			
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.ENQUIRY_ADDED_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.ENQUIRY_ADDED_FAIL);
				response.put("success", false);
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
	
	@GetMapping("/getAll")
	public ResponseEntity<Map<String, Object>> getAllEnquiry(
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "type") JTEnquiryServiceType type
	) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<JTEnquiryResponseDto> responseDto = jtEnquiryService.getAllEnquiry(startDate, endDate, type);
			
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.ENQUIRY_FOUND_SUCCESS);
				response.put("success", true);
				response.put("data", responseDto);
			} else {
				response.put("msg", ConstantsPoc.ENQUIRY_FOUND_FAIL);
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			e.printStackTrace();
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
	
	@GetMapping("/getById")
	public ResponseEntity<Map<String, Object>> getAllEnquiry(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			JTEnquiryResponseDto responseDto = jtEnquiryService.getEnquiryById(id);
			
			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", ConstantsPoc.ENQUIRY_FOUND_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.ENQUIRY_FOUND_FAIL);
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			e.printStackTrace();
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
