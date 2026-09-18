package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.EventFunctionManagerTaskMainRequestDto;
import com.crmportal.response.dto.EventFunctionManagerTaskSummaryResponseDto;
import com.crmportal.response.dto.EventFunctionWiseManagerTaskResponseDto;
import com.crmportal.response.dto.ManagerTaskSummaryResponseDto;
import com.crmportal.service.EventFunctionManagerTaskService;

@RestController
@RequestMapping({ "/v1/api/eventfunction-managertask" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventFunctionManagerTaskController {

	@Autowired
	EventFunctionManagerTaskService eventFunctionManagerTaskService;

	@PostMapping(value = "/add-update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdate(
			@ModelAttribute EventFunctionManagerTaskMainRequestDto request, @RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {

			Boolean isSuccess = eventFunctionManagerTaskService.addOrUpdate(request, userId);
			if (isSuccess) {
				response.put("msg", "Event Function Manager Task Add/Update Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "Event Function Manager Task Add/Update Failed");
				response.put("success", isSuccess);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/geteventfunctionmanagertask")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getEventFunctionManagerTask(
			@RequestParam("eventFunctionId") Long eventFunctionId, @RequestParam("managerId") Long managerId,
			@RequestParam(value = "type", required = false) String type) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventFunctionManagerTaskSummaryResponseDto dtos = eventFunctionManagerTaskService
					.getEventFunctionManagerTask(eventFunctionId, managerId, type);
			if (dtos == null) {
				response.put("msg", "Data Not Found");
				response.put("success", false);
			} else {
				Map<String, Object> taskResp = new HashMap<>();
				taskResp.put("ManagerTasks", dtos);
				response.put("data", taskResp);
				response.put("msg", "Data Found Successfully");
				response.put("success", true);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getmanagertasksummary")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getManagerTaskSummary(@RequestParam("managerId") Long managerId,
			@RequestParam("eventFunctionId") Long eventFunctionId) {
		Map<String, Object> response = new HashMap<>();
		try {
			ManagerTaskSummaryResponseDto dto = eventFunctionManagerTaskService.getManagerTaskSummary(managerId,
					eventFunctionId);
			if (dto != null) {
				response.put("msg", "Data Found Successfully");
				response.put("success", true);
				Map<String, Object> managerResp = new HashMap<>();
				managerResp.put("ManagerTaskSummary", dto);
				response.put("data", managerResp);
			} else {
				response.put("msg", "Data Found Failed");
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getallfunctionwisemanagertask")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllFunctionWiseManagerTask(@RequestParam("eventId") Long eventId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventFunctionWiseManagerTaskResponseDto> dto = eventFunctionManagerTaskService
					.getAllFunctionWiseManagerTask(eventId);
			if (dto != null) {
				response.put("msg", "Data Found Successfully");
				response.put("success", true);
				Map<String, Object> managerResp = new HashMap<>();
				managerResp.put("ManagerTasks", dto);
				response.put("data", managerResp);
			} else {
				response.put("msg", "Data Found Failed");
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/deletemanagertaskimage")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteManagerTaskImage(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isDelete = eventFunctionManagerTaskService.deleteManagerTaskImage(id);
			if (isDelete) {
				response.put("msg", "Manager Task Image Deleted Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Manager Task Image Deleted Failed");
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
