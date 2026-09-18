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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.LeadStatusRequestDto;
import com.crmportal.request.dto.LeadSubSourceRequestDto;
import com.crmportal.response.dto.LeadSourceResponseDto;
import com.crmportal.response.dto.LeadStatusResponseDto;
import com.crmportal.response.dto.LeadSubSourceResponseDto;
import com.crmportal.service.LeadSourceService;
import com.crmportal.service.LeadStatusService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping("/v1/api/lead-status")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class LeadStatusController {

	@Autowired
	LeadStatusService leadStatusService;
	
	@PostMapping("/add")
	public ResponseEntity<?> addLeadStatus(@Valid @RequestBody LeadStatusRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			LeadStatusResponseDto res = leadStatusService.addOrUpdateLeadStatus(request, Long.valueOf(-1));

			if (res == null) {
				response.put("msg", ConstantsPoc.LEAD_SUBSOURCE_ADDED_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.LEAD_SUBSOURCE_ADDED_SUCCESS);
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
	public ResponseEntity<?> updateLeadStatus(@Valid @RequestBody LeadStatusRequestDto request,
			@RequestParam("leadStatusId") Long leadStatusId) {
		Map<String, Object> response = new HashMap<>();
		try {
			LeadStatusResponseDto res = leadStatusService.addOrUpdateLeadStatus(request, leadStatusId);

			if (res == null) {
				response.put("msg", ConstantsPoc.LEAD_SUBSOURCE_UPDATE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.LEAD_SUBSOURCE_UPDATE_SUCCESS);
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
	public ResponseEntity<?> getAllLeadStatus(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<LeadStatusResponseDto> res = leadStatusService.getAllLeadStatus(userId);
			
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
	public ResponseEntity<?> getLeadStatusById(@RequestParam("leadStatusId") Long leadStatusId) {
		Map<String, Object> response = new HashMap<>();
		try {
			LeadStatusResponseDto res = leadStatusService.getLeadStatusById(leadStatusId);
			
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
	public ResponseEntity<?> deleteLeadStatusById(@RequestParam("leadStatusId") Long leadStatusId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean res = leadStatusService.deleteLeadStatusById(leadStatusId);
			
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
