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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.EventTermsAndConditionRequestDto;
import com.crmportal.response.dto.EventTermsAndConditionResponseDto;
import com.crmportal.service.EventTermsAndConditionService;

@RestController
@RequestMapping({"/v1/api/event-terms-and-condition"})
@CrossOrigin(origins = {"*"}, maxAge = 3600L)
public class EventTermsAndConditionController {

	@Autowired
	EventTermsAndConditionService eventTermsAndConditionService;
	
	@PostMapping("/add-update")
	public ResponseEntity<?> addOrUpdateEventTermsAndCondition(@RequestBody EventTermsAndConditionRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventTermsAndConditionResponseDto responseDto = eventTermsAndConditionService.addOrUpdateTermsAndCondition(request);

			if (responseDto != null) {
				response.put("success", true);
				response.put("msg", "Terms And Condition added successfully.");
			} else {
				response.put("success", false);
				response.put("msg", "Terms And Condition added failed.");
			}

			return ResponseEntity.ok(response);

		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	@GetMapping("/getbyeventId")
	public ResponseEntity<?> getAllByEventId(@RequestParam("eventId") Long eventId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventTermsAndConditionResponseDto> responseDto = eventTermsAndConditionService.getByEventId(eventId);

			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("success", true);
				response.put("msg", "Terms and condition found successfully.");
			} else {
				response.put("success", false);
				response.put("msg", "Terms and condition found failed");
			}

			return ResponseEntity.ok(response);

		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
