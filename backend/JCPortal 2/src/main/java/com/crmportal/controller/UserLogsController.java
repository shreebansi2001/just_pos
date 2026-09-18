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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.entity.UserLogsEntity;
import com.crmportal.service.UserLogsService;
import com.crmportal.utility.ResponseUtils;

@RestController
@RequestMapping({ "v1/api/user-logs/" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class UserLogsController {

	@Autowired
	private UserLogsService userLogsService;

	@PostMapping("/saveLog")
	public ResponseEntity<Map<String, Object>> saveUserLog(@Valid @RequestBody UserLogsEntity request,
			HttpServletRequest httpServletRequest) {
		userLogsService.saveUserLogs(request.getUser(), request.getEventType(), request.getDescription(),
				httpServletRequest, request.getEventId());
		return ResponseEntity.ok(
				ResponseUtils.createSuccessRespones("Log Saved", "Logs Saved Successfully for " + request.getUser()));
	}

	@GetMapping("/getUserLogs")
	public ResponseEntity<Map<String, Object>> getUserLogs(@RequestParam(value = "user", required = false) String user,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "eventType", required = false) String eventType,
			@RequestParam(value = "eventId", required = false) Long eventId,@RequestParam("userId") Long userId) {
		return ResponseEntity.ok(userLogsService.getAllLogs(user, startDate, endDate, eventType, eventId,userId));
	}

	@GetMapping("/health")
	public ResponseEntity<String> healthCheck() {
		return ResponseEntity.ok("Server is running");
	}

	@GetMapping("/logout-notification")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> logOutUserLogs(@RequestParam(name = "email") String emailId,
			@RequestParam(name = "eventType") String eventTypes, @RequestParam(name = "eventId") Long eventId,
			HttpServletRequest httpServletRequest) {
		try {
			userLogsService.saveUserLogs(emailId, eventTypes, "No Description", httpServletRequest, eventId);
			return ResponseEntity
					.ok(ResponseUtils.createSuccessRespones(eventTypes + " saved", "Event Saved Succesfully"));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok(ResponseUtils.createFailedRespones(e.getLocalizedMessage()));
		}
	}

	@GetMapping("/inactiveuser")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getInActiveUser(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		try {
			String path = userLogsService.inactiveUserExcel(request);
			if (path == null || path.trim().isEmpty()) {
				response.put("msg", "File Generate Error");
				response.put("success", false);
			} else {
				response.put("data", path);
				response.put("msg", "File Generate Successfully");
				response.put("success", true);
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
