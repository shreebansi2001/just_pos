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

import com.crmportal.request.dto.CoupenMasterRequestDto;
import com.crmportal.request.dto.LeadMasterRequestDto;
import com.crmportal.request.dto.LeadMasterResponseDto;
import com.crmportal.response.dto.CoupenMasterResponseDto;
import com.crmportal.service.CoupenMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/coupenmaster" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class CoupenMasterController {
	
	@Autowired
	CoupenMasterService coupenMasterService;
	
	@PostMapping("/addorupdate")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdateCoupenMaster(@RequestBody CoupenMasterRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			CoupenMasterResponseDto coupenMasterResponseDto = coupenMasterService.addOrUpdateCoupenMaster(request, request.getId());
			
			if(coupenMasterResponseDto != null) {
				response.put("data", coupenMasterResponseDto);
				response.put("msg", "Coupne Master created/updated successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Coupne Master is not created/updated.");
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
	
	@DeleteMapping("/delete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteCoupenMaster(@RequestParam Long id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		
		try {
			isSuccess = coupenMasterService.deleteCoupenMaster(id);
			if(isSuccess) {
				response.put("msg", "Coupne Master deleted successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Coupne Master is not deleted.");
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
	public ResponseEntity<Map<String, Object>> getCoupenMasterById(@RequestParam Long id) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			CoupenMasterResponseDto coupenMasterResponseDto = coupenMasterService.getCoupenMasterById(id);
			
			if(coupenMasterResponseDto != null) {
				response.put("data", coupenMasterResponseDto);
				response.put("msg", "Coupne Master found successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Coupne Master is not found.");
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
	public ResponseEntity<Map<String, Object>> getAllCoupenMaster() {
		Map<String, Object> response = new HashMap<>();
		
		try {
			List<CoupenMasterResponseDto> coupenMasterResponseDtos = coupenMasterService.getAllCoupenMaster();
			
			if(coupenMasterResponseDtos != null) {
				response.put("data", coupenMasterResponseDtos);
				response.put("msg", "Coupne Master found successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Coupne Master is not found.");
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
