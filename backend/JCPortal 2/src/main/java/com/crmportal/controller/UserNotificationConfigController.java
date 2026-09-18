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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.repository.UserNotificationConfigRepository;
import com.crmportal.request.dto.UpdateUserNotificationRequestDto;
import com.crmportal.request.dto.UserUpgradedModulePaymentRequestDto;
import com.crmportal.response.dto.UserNotificationConfigResponseDto;
import com.crmportal.service.UserNotificationConfigService;

@RestController
@RequestMapping({ "/v1/api/usernotificationconfig" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class UserNotificationConfigController {

	@Autowired
	UserNotificationConfigService userNotificationConfigService;

	@PostMapping("/addusernotification")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addUserNotification(
			@RequestBody UserUpgradedModulePaymentRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = userNotificationConfigService.addUserNotification(request);
			if (isSuccess) {
				response.put("msg", "User Notification Add/Updated successfully");
				response.put("success", true);
			} else {
				response.put("msg", "User Notification Add/Updated failed");
				response.put("success", false);
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
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/updateusernotification")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateUserNotification(
			@RequestBody UpdateUserNotificationRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = userNotificationConfigService.updateUserNotification(request);
			if (isSuccess) {
				response.put("msg", "User Notification Updated Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "User Notification Updated Failed");
				response.put("success", false);
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

	@PutMapping("/isActive")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> isActive(@RequestParam("userId") Long userId,
			@RequestParam("isActive") Boolean isActive, @RequestParam("otp") String otp,
			@RequestParam("moduleId") List<Long> moduleId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = userNotificationConfigService.isActive(userId, isActive, otp, moduleId);
			if (isSuccess) {
				response.put("msg", "User Notification Actived/DeActived Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "User Notification Actived/DeActived Failed");
				response.put("success", false);
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

	@GetMapping("/getallbyuser")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllByUser(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<UserNotificationConfigResponseDto> dtos = userNotificationConfigService.getAllByUser(userId);
			if (dtos.isEmpty()) {
				response.put("msg", "Data Found Failed");
				response.put("success", false);
			} else {
				Map<String, Object> resp = new HashMap<>();
				resp.put("userNotifications", dtos);
				response.put("data", resp);
				response.put("msg", "Data Found Failed");
				response.put("success", false);
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
