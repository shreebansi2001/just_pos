package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.GuestSignatureRequestDto;
import com.crmportal.request.dto.ReportConfigurationRequestDto;
import com.crmportal.response.dto.GuestSignatureResponseDto;
import com.crmportal.response.dto.ReportConfigurationResponseDto;
import com.crmportal.service.GuestSignatureReportService;
import com.crmportal.service.GuestSignatureService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping("/v1/api/guest-signature")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class GuestSignatureController {

	@Autowired
	GuestSignatureService guestSignatureService;
	
	@Autowired
	GuestSignatureReportService guestSignatureReportService;
	
	@PostMapping("/add-update")
	public ResponseEntity<?> addOrUpdateGuestSignature(@Valid @RequestBody List<GuestSignatureRequestDto> request, @RequestParam("eventId") Long eventId, 
			@RequestParam("eventFunctionId") Long eventFunctionId) {
		Map<String, Object> response = new HashMap<>();
		try {

			List<GuestSignatureResponseDto> responseDto = guestSignatureService.addOrUpdateGuestSignature(request, eventId, eventFunctionId);
			
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.GUEST_SIGNATURE_ADD_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.GUEST_SIGNATURE_ADD_SUCCESS);
				response.put("success", true);
				response.put("data", responseDto);
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
	
	@GetMapping("/getAllbyEventAndEventFunction")
	public ResponseEntity<?> getAllByEventAndEventFunction(@RequestParam("eventId") Long eventId, @RequestParam("eventFunctionId") Long eventFunctionId) {
		Map<String, Object> response = new HashMap<>();

		try {
			List<GuestSignatureResponseDto> res = guestSignatureService.getAllByEventAndEventFunction(eventId, eventFunctionId);

			if (res == null) {
				response.put("msg", ConstantsPoc.GUEST_SIGNATURE_FOUND_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.GUEST_SIGNATURE_FOUND_SUCCESS);
				response.put("success", true);
				response.put("data", res);
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
	
	@GetMapping("/generate-guest-signature-report")
	public ResponseEntity<Map<String, Object>> generateGuestSignatureReport(@RequestParam("eventId") Long eventId, @RequestParam("userId") Long userId,
			@RequestParam("isCompanyDetails") Integer isCompanyDetails, HttpServletRequest req) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			String url = guestSignatureReportService.generateGuestSignatureReport2(eventId, userId, isCompanyDetails, req);
			
			if(url != null) {
				response.put("success", true);
				response.put("msg", ConstantsPoc.GUEST_SIGNATURE_REPORT_GENERATE_SUCCESS);
				response.put("data", url);
			} else {
				response.put("success", false);
				response.put("msg", ConstantsPoc.GUEST_SIGNATURE_REPORT_GENERATE_FAIL);
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
