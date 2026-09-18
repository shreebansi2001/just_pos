package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

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

import com.crmportal.request.dto.ManagerTaskMasterRequestDto;
import com.crmportal.response.dto.ManagerTaskMasterResponseDto;
import com.crmportal.service.ManagerTaskMasterService;

@RestController
@RequestMapping({ "/v1/api/managertask" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class ManagerTaskMasterController {

	@Autowired
	ManagerTaskMasterService managerTaskMasterService;

	@PostMapping("/add-update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdateManagerTask(
			@RequestBody List<ManagerTaskMasterRequestDto> request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = managerTaskMasterService.addOrUpdateManagerTask(request);
			if (isSuccess) {
				response.put("msg", "Manager Task Added/Updated Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "Manager Task Added/Updated Failed");
				response.put("success", isSuccess);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (EntityNotFoundException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", "Something went wrong");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getall")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllManagerTask(@RequestParam("userId") Long userId,
			@RequestParam(value = "type", required = false) String type) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<ManagerTaskMasterResponseDto> dtos = managerTaskMasterService.getAllManagerTask(userId,type);
			if (dtos.isEmpty()) {
				response.put("success", false);
				response.put("msg", "Data Not Found");
			} else {
				Map<String, Object> specialResp = new HashMap<>();
				specialResp.put("ManagerTasks", dtos);
				response.put("success", true);
				response.put("msg", "Data Found Successfully");
				response.put("data", specialResp);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (EntityNotFoundException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", "Something went wrong");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/delete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = managerTaskMasterService.deleteById(id);
			if (isSuccess) {
				response.put("msg", "Manager Task Deleted Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "Manager Task Deleted Failed");
				response.put("success", isSuccess);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (EntityNotFoundException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", "Something went wrong");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
