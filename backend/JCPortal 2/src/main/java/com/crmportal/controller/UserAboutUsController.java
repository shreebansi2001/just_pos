package com.crmportal.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

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

import com.crmportal.request.dto.UserAboutUsRequestDto;
import com.crmportal.response.dto.UserAboutUsResponseDto;
import com.crmportal.service.UserAboutUsService;

@RestController
@RequestMapping({ "/v1/api/useraboutus" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class UserAboutUsController {

	@Autowired
	UserAboutUsService aboutUsService;

	@PostMapping("add-update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdateAboutUs(@Valid @RequestBody UserAboutUsRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			UserAboutUsResponseDto dto = aboutUsService.addOrUpdateAboutUs(request);
			if (dto != null) {
				response.put("msg", "AboutUs Added/Updated Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "AboutUs Added/Updated Failed");
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
	
	@GetMapping("/getbyuser")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getByUser(@RequestParam("userid") Long userId){
		Map<String, Object> response = new HashMap<>();
		try {
			UserAboutUsResponseDto dto = aboutUsService.getByUser(userId);
			if (dto != null) {
				response.put("data", dto);
				response.put("msg", "AboutUs Found Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "AboutUs Found Failed");
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
