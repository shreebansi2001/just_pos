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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.response.dto.LeadSourceResponseDto;
import com.crmportal.service.LeadSourceService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping("/v1/api/lead-source")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class LeadSourceController {

	@Autowired
	LeadSourceService leadSourceService;
	
	@PostMapping("/add")
	public ResponseEntity<?> addLeadSource(@RequestParam("name") String name,@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			LeadSourceResponseDto res = leadSourceService.addOrUpdateLeadSource(name, Long.valueOf(-1),userId);
			
			if(res == null) {
				response.put("msg", ConstantsPoc.LEAD_SOURCE_ADDED_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.LEAD_SOURCE_ADDED_SUCCESS);
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
	
	@PutMapping("/update")
	public ResponseEntity<?> updateLeadSource(@RequestParam("name") String name, @RequestParam("leadSourceId") Long leadSourceId,@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			LeadSourceResponseDto res = leadSourceService.addOrUpdateLeadSource(name, leadSourceId,userId);
			
			if(res == null) {
				response.put("msg", ConstantsPoc.LEAD_SOURCE_UPDATE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.LEAD_SOURCE_UPDATE_SUCCESS);
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
	
	@GetMapping("/getall")
	public ResponseEntity<?> getAllLeadSource(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<LeadSourceResponseDto> res = leadSourceService.getAllLeadSource(userId);
			
			if(res == null) {
				response.put("msg", ConstantsPoc.LEAD_SOURCE_FOUND_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.LEAD_SOURCE_FOUND_SUCCESS);
				response.put("success", true);
				response.put("data", res);
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
	
	@GetMapping("/getbyid")
	public ResponseEntity<?> getLeadSourceById(@RequestParam("leadSourceId") Long leadSourceId) {
		Map<String, Object> response = new HashMap<>();
		try {
			LeadSourceResponseDto res = leadSourceService.getLeadSourceById(leadSourceId);
			
			if(res == null) {
				response.put("msg", ConstantsPoc.LEAD_SOURCE_FOUND_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.LEAD_SOURCE_FOUND_SUCCESS);
				response.put("success", true);
				response.put("data", res);
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
	
	@DeleteMapping("/deletebyid")
	public ResponseEntity<?> deleteLeadSourceById(@RequestParam("leadSourceId") Long leadSourceId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean res = leadSourceService.deleteLeadSourceById(leadSourceId);
			
			if(res == null) {
				response.put("msg", ConstantsPoc.LEAD_SOURCE_DELETE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.LEAD_SOURCE_DELETE_SUCCESS);
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
