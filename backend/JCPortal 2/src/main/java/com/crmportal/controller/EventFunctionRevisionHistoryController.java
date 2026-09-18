package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.entity.EventFunctionRevisionHistoryEntity;
import com.crmportal.request.dto.EventFunctionRevisionHistoryRequestDto;
import com.crmportal.response.dto.EventFunctionRevisionHistoryResponseDto;
import com.crmportal.service.EventFunctionRevisionHistoryService;

@RestController
@RequestMapping({ "/v1/api/revision-history" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventFunctionRevisionHistoryController {

	@Autowired
	EventFunctionRevisionHistoryService eventFunctionRevisionHistoryService;

	@PostMapping("/addorupdate")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdate(
			@RequestBody EventFunctionRevisionHistoryRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = eventFunctionRevisionHistoryService.addOrUpdate(request);
			if (isSuccess) {
				response.put("msg", "History Created/Updated Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "History Created/Updated Failed");
				response.put("success", isSuccess);
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
	public ResponseEntity<Map<String, Object>> getAll(@RequestParam Long userId,
			@RequestParam(value = "eventId", required = false) Long eventId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventFunctionRevisionHistoryResponseDto> dtos = eventFunctionRevisionHistoryService.getAll(userId,
					eventId);
			if (dtos.isEmpty()) {
				response.put("msg", "Data Not Found");
				response.put("success", false);
			} else {
				Map<String, Object> historyResp = new HashMap<>();
				historyResp.put("History", dtos);
				response.put("data", historyResp);
				response.put("msg", "Data Found Successfully");
				response.put("success", true);
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

	@DeleteMapping("/delete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = eventFunctionRevisionHistoryService.deleteById(id);
			if (isSuccess) {
				response.put("msg", "History deleted Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "History Deleted Failed");
				response.put("success", isSuccess);
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
