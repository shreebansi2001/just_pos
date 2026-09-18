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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.GeneralFixRequestDto;
import com.crmportal.response.dto.GeneralFixRawResponseDto;
import com.crmportal.service.EventFunctionGeneralFixService;

@RestController
@RequestMapping({ "/v1/api/eventfunctiongeneral" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventFunctionGeneralFixController {

	@Autowired
	EventFunctionGeneralFixService eventFunctionGeneralFixService;

	@PostMapping("/add-update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addUpdate(@RequestBody GeneralFixRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = eventFunctionGeneralFixService.addUpdate(request);
			if (isSuccess) {
				response.put("success", isSuccess);
				response.put("msg", "EventFunction GeneralFix Added/Updated Successfully");
			} else {
				response.put("success", isSuccess);
				response.put("msg", "EventFunction GeneralFix Added/Updated Failed");
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

	@GetMapping("getallgeneralfixraw")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllGeneralFixRaw(@RequestParam("eventId") Long eventId,
			@RequestParam("eventFunctionIds") List<Long> eventFunctionIds, @RequestParam("rawCatIds") List<Long> rawCatIds) {
		Map<String, Object> response = new HashMap<>();
		try {
			GeneralFixRawResponseDto responseDto = eventFunctionGeneralFixService.getAllGeneralFixRaw(eventId,
					eventFunctionIds, rawCatIds);
			if (responseDto != null) {
				response.put("success", true);
				response.put("msg", "Data Found Successfully");
				response.put("data", responseDto);
			} else {
				response.put("success", false);
				response.put("msg", "Data Found Failed");
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
}
