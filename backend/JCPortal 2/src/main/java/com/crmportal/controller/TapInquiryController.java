package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.TapInquiryRequestDto;
import com.crmportal.response.dto.InquiryResponseDto;
import com.crmportal.response.dto.TapInquiryResponseDto;
import com.crmportal.service.TapInquiryService;

@RestController
@RequestMapping(value = "/v1/api/tap-inquiry")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class TapInquiryController {

	@Autowired
	TapInquiryService tapInquiryService;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addTapInquiry(@RequestBody TapInquiryRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = tapInquiryService.addTapInquiry(request);
			if (isSuccess) {
				response.put("success", isSuccess);
				response.put("msg", "Tap Inquiry Added Successfully");
			} else {
				response.put("success", isSuccess);
				response.put("msg", "Tap Inquiry Added Failed");
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
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllInquiry() {
		Map<String, Object> response = new HashMap<>();
		try {
			List<TapInquiryResponseDto> tapInquiries = tapInquiryService.getAllInquiry();
			if (!tapInquiries.isEmpty()) {
				response.put("success", true);
				response.put("data", tapInquiries);
				response.put("msg", "Data Found Successfully");
			} else {
				response.put("success", false);
				response.put("msg", "Data Found Failed");
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
