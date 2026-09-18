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

import com.crmportal.request.dto.LeadSubSourceRequestDto;
import com.crmportal.response.dto.LeadSourceAllDataResponseDto;
import com.crmportal.response.dto.LeadSubSourceResponseDto;
import com.crmportal.service.LeadSubSourceService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping("/v1/api/lead-subsource")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class LeadSubSourceController {

	@Autowired
	LeadSubSourceService leadSubSourceService;

	@PostMapping("/add")
	public ResponseEntity<?> addLeadSubSource(@Valid @RequestBody LeadSubSourceRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			LeadSubSourceResponseDto res = leadSubSourceService.addOrUpdateLeadSubSource(request, Long.valueOf(-1));

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
	public ResponseEntity<?> updateLeadSubSource(@Valid @RequestBody LeadSubSourceRequestDto request,
			@RequestParam("leadSourceId") Long leadSourceId) {
		Map<String, Object> response = new HashMap<>();
		try {
			LeadSubSourceResponseDto res = leadSubSourceService.addOrUpdateLeadSubSource(request, leadSourceId);

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
	public ResponseEntity<?> getAllLeadSubSource(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<LeadSubSourceResponseDto> res = leadSubSourceService.getAllLeadSubSource(userId);

			if (res == null) {
				response.put("msg", ConstantsPoc.LEAD_SUBSOURCE_FOUND_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.LEAD_SUBSOURCE_FOUND_SUCCESS);
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
	public ResponseEntity<?> getLeadSubSourceById(@RequestParam("subSourceId") Long subSourceId) {
		Map<String, Object> response = new HashMap<>();
		try {
			LeadSubSourceResponseDto res = leadSubSourceService.getLeadSubSourceById(subSourceId);

			if (res == null) {
				response.put("msg", ConstantsPoc.LEAD_SUBSOURCE_FOUND_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.LEAD_SUBSOURCE_FOUND_SUCCESS);
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

	@GetMapping("/getallbysourceid")
	public ResponseEntity<?> getAllByLeadSourceId(@RequestParam("leadSourceId") Long leadSourceId) {
		Map<String, Object> response = new HashMap<>();
		try {
			LeadSourceAllDataResponseDto res = leadSubSourceService.getAllByLeadSourceId(leadSourceId);

			if (res == null) {
				response.put("msg", ConstantsPoc.LEAD_SUBSOURCE_FOUND_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.LEAD_SUBSOURCE_FOUND_SUCCESS);
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
			Boolean res = leadSubSourceService.deleteLeadSubSourceById(leadSourceId);

			if (res == null) {
				response.put("msg", ConstantsPoc.LEAD_SUBSOURCE_DELETE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.LEAD_SUBSOURCE_DELETE_SUCCESS);
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
