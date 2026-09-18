package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.response.dto.EventOverViewResponseDto;
import com.crmportal.service.EventMasterService;

@RestController
@RequestMapping({ "/v1/api/eventoverview" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventOverViewController {

	@Autowired
	EventMasterService eventMasterService;

	@GetMapping("/geteventoverview")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getEventOverview(@RequestParam("partyId") Long partyId,
			@RequestParam("eventId") Long eventId, @RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventOverViewResponseDto dto = eventMasterService.getEventOverview(partyId, eventId, userId);
			return null;
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
}
