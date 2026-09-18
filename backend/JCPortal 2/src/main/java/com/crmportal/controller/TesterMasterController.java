package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.TesterMasterRequestDto;
import com.crmportal.response.dto.TesterMasterResponseDto;
import com.crmportal.service.TesterMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/tester-master" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class TesterMasterController {

	@Autowired
	TesterMasterService testerMasterService;
	
	@PostMapping("/add-update")
	public ResponseEntity<?> addOrUpdateTesterMaster(@Valid @RequestBody TesterMasterRequestDto request,
			BindingResult bindingResult) {
		Map<String, Object> response = new HashMap<>();
		try {
			if (bindingResult.hasErrors()) {
			    Map<String, String> errors = new HashMap<>();
			    bindingResult.getFieldErrors().forEach(error ->
			            errors.put(error.getField(), error.getDefaultMessage())
			    );
			    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
			} else {
				TesterMasterResponseDto res = testerMasterService.addOrUpdateTesterMaster(request);
				
				if (res == null) {
					response.put("success", false);
					response.put("msg", ConstantsPoc.TESTER_MASTER_CREATE_FAIL);
				} else {
					response.put("data", res);
					response.put("success", true);
					response.put("msg", ConstantsPoc.TESTER_MASTER_CREATE_SUCCESS);
				}
				return ResponseEntity.status(HttpStatus.OK).body(response);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("msg", e.getLocalizedMessage());
			response.put("success", false);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("msg", e.getLocalizedMessage());
			response.put("success", false);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	@GetMapping("/getall")
	public ResponseEntity<?> getAllTesterMaster(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<TesterMasterResponseDto> res = testerMasterService.getAllTesterMaster(userId);
			
			if (res == null) {
				response.put("success", false);
				response.put("msg", ConstantsPoc.TESTER_MASTER_FOUND_FAIL);
			} else {
				response.put("data", res);
				response.put("success", true);
				response.put("msg", ConstantsPoc.TESTER_MASTER_FOUND_SUCCESS);
			}
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("msg", e.getLocalizedMessage());
			response.put("success", false);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("msg", e.getLocalizedMessage());
			response.put("success", false);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	@GetMapping("/getbyid")
	public ResponseEntity<?> getTesterMasterById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			TesterMasterResponseDto res = testerMasterService.getTesterMasterById(id);
		
			if (res == null) {
				response.put("success", false);
				response.put("msg", ConstantsPoc.TESTER_MASTER_FOUND_FAIL);
			} else {
				response.put("data", res);
				response.put("success", true);
				response.put("msg", ConstantsPoc.TESTER_MASTER_FOUND_SUCCESS);
			}
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("msg", e.getLocalizedMessage());
			response.put("success", false);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("msg", e.getLocalizedMessage());
			response.put("success", false);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	@DeleteMapping("/deletebyid")
	public ResponseEntity<?> deleteTesterMasterById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = testerMasterService.deleteTesterMasterById(id);
		
			if (isSuccess == null || !isSuccess) {
				response.put("success", false);
				response.put("msg", ConstantsPoc.TESTER_MASTER_DELETE_FAIL);
			} else {
				response.put("success", true);
				response.put("msg", ConstantsPoc.TESTER_MASTER_DELETE_SUCCESS);
			}
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("msg", e.getLocalizedMessage());
			response.put("success", false);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("msg", e.getLocalizedMessage());
			response.put("success", false);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
