package com.crmportal.controller;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.EventFoodTestingGenerateLinkRequestDto;
import com.crmportal.request.dto.EventFoodTestingGenerateLinkResponseDto;
import com.crmportal.request.dto.EventFoodTestingRequestDto;
import com.crmportal.request.dto.VerifyLinkRequestDto;
import com.crmportal.response.dto.EventFoodTestingFinalMenuResponseDto;
import com.crmportal.response.dto.EventFoodTestingResponseDto;
import com.crmportal.response.dto.EventFunctionTesterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventFoodTestingService;
import com.crmportal.utility.ConstantsPoc;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping({ "/v1/api/event-food-testing" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventFoodTestingController {

	@Autowired
	EventFoodTestingService eventFoodTestingService;
	
	@Autowired
	CommonService commonService;
	
	@PostMapping("/add-update")
	public ResponseEntity<?> addOrUpdateFoodTestingMenu(@RequestBody EventFoodTestingRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventFoodTestingResponseDto responseDto = eventFoodTestingService.addOrUpdateFoodTestingMenu(request);

			if (responseDto == null) {
				response.put("msg", ConstantsPoc.MENU_PALNNING_MASTER_ADD_FAILED);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.MENU_PALNNING_MASTER_ADD_SUCCESS);
				response.put("success", true);
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

	@PostMapping("/getMenu")
	public ResponseEntity<?> getMenuByTesterId(@RequestBody VerifyLinkRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			
			EventFoodTestingFinalMenuResponseDto responseDto = eventFoodTestingService.getMenuByTesterId(request);
			
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.MENU_PALNNING_MASTER_FOUND_FAILED);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.MENU_PALNNING_MASTER_FOUND_SUCCESS);
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
	
	@PostMapping("/generate-link")
	public ResponseEntity<?> generateLink(@RequestBody EventFoodTestingGenerateLinkRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "Link generated successfully");
			EventFoodTestingGenerateLinkResponseDto resp = eventFoodTestingService.generateLink(request);
			response.put("data", resp);

			List<String> set = new ArrayList<>(
					Arrays.asList(valueOrEmpty(resp.getTesterName()), valueOrEmpty(resp.getEventNo()),
							valueOrEmpty(resp.getEventName()), valueOrEmpty(resp.getFunctionName()),
							valueOrEmpty(resp.getFunctionDate()), "NA", 
							valueOrEmpty(resp.getTesterName()), valueOrEmpty(resp.getTesterContactNo()),
							valueOrEmpty(resp.getShareUrl()), valueOrEmpty(resp.getAccessCode())));

			try {
				commonService.sendWhatsappMsg("event_menu_link_access", resp.getTesterContactNo(), set, null, false,resp.getUserId());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}
	
	@GetMapping("/getall")
	public ResponseEntity<?> getAllEventFunctionTester(@RequestParam("eventId") Long eventId, @RequestParam("eventFunctionId") Long eventFunctionId) {
		Map<String, Object> response = new HashMap<>();
		try {
			
			List<EventFunctionTesterResponseDto> responseDto = eventFoodTestingService.getAllEventFunctionTester(eventId, eventFunctionId);
			
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.MENU_PALNNING_MASTER_FOUND_FAILED);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.MENU_PALNNING_MASTER_FOUND_SUCCESS);
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
	
	private static String valueOrEmpty(String value) {
		return value == null ? "NA" : value;
	}
}
