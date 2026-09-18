package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.UserGodownRequestDto;
import com.crmportal.response.dto.UserGodownResponseDto;
import com.crmportal.service.UserGodownService;
import com.crmportal.utility.ConstantsPoc;


@RestController
@RequestMapping({"/v1/api/godown"})
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class UserGodownController {
	
	@Autowired
	UserGodownService userGodownService;
	
	@PostMapping("/addorupdate")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addGodownMaster(@RequestBody UserGodownRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			UserGodownResponseDto userGodownResponseDto = userGodownService.addOrUserGodownMaster(request);
			
			if(userGodownResponseDto != null) {
				response.put("data", userGodownResponseDto);
				response.put("msg", "User Godown created / updated successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "User Godown is not created / updated.");
				response.put("success", false);
			}
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(RuntimeException e) { 
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/deleteById")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteGodownMasterById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		
		try {
			isSuccess = userGodownService.deleteGodownMasterById(id);
			
			if(isSuccess) {
				response.put("msg", "User Godown deleted successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "User Godown is not deleted.");
				response.put("success", false);
			}
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(RuntimeException e) { 
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getGodownMasterById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			UserGodownResponseDto userGodownResponseDto = userGodownService.getGodownMasterById(id);
			
			if(userGodownResponseDto != null) {
				response.put("data", userGodownResponseDto);
				response.put("msg", "User Godown found successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "User Godown is not found.");
				response.put("success", false);
			}
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(RuntimeException e) { 
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getall")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllGodownMaster(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			List<UserGodownResponseDto> userGodownResponseDto = userGodownService.getAllGodownMaster(userId);
			
			if(!userGodownResponseDto.isEmpty() || userGodownResponseDto != null) {
				response.put("data", userGodownResponseDto);
				response.put("msg", "User Godown found successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "User Godown is not found.");
				response.put("success", false);
			}
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(RuntimeException e) { 
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
