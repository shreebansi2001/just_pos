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

import com.crmportal.request.dto.EventGroundTaskRequestDto;
import com.crmportal.response.dto.EventGroundTaskResponseDto;
import com.crmportal.service.EventGroundTaskService;

@RestController
@RequestMapping({ "/v1/api/eventgroundtask" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventGroundTaskController {

	@Autowired
	EventGroundTaskService eventGroundTaskService;

	@PostMapping("/addorupdate")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdate(@RequestBody EventGroundTaskRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = eventGroundTaskService.addOrUpdate(request);
			if (isSuccess) {
				response.put("msg", "Data Added/Updated Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "Data Added/Updated Failed");
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
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAll(
			@RequestParam(value = "resourceType", required = false) String resourcseType,
			@RequestParam(value = "isActive", required = false) Boolean isActive,
			@RequestParam(value = "userId", required = false) Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventGroundTaskResponseDto> dtos = eventGroundTaskService.getAll(resourcseType, isActive, userId);
			if (dtos.isEmpty()) {
				response.put("msg", "Data Not Found");
				response.put("success", false);
			} else {
				Map<String, Object> respData = new HashMap<>();
				respData.put("TaskDetails", dtos);
				response.put("msg", "Data Found Successfully");
				response.put("success", true);
				response.put("data", respData);
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
	public ResponseEntity<Map<String, Object>> deleteTask(@RequestParam("id") Long id) {

		Map<String, Object> response = new HashMap<>();

		try {
			eventGroundTaskService.deleteTask(id);
			response.put("success", true);
			response.put("msg", "Task deleted successfully");
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

	@PutMapping("/update-status")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateStatus(@RequestParam("id") Long id,
			@RequestParam("isActive") Boolean isActive) {

		Map<String, Object> response = new HashMap<>();

		try {

			eventGroundTaskService.updateStatus(id, isActive);

			response.put("success", true);
			response.put("msg", "Status updated successfully");

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
