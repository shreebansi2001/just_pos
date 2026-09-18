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

import com.crmportal.request.dto.CaptainReceipeMasterRequestDto;
import com.crmportal.response.dto.CaptainReceipeMasterResponseDto;
import com.crmportal.service.CaptainReceipeMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/captain-receipe-master" })
@CrossOrigin(originPatterns = { "*" }, maxAge = 3600L)
public class CaptainReceipeMasterController {

	@Autowired
	CaptainReceipeMasterService captainReceipeMasterService;
	
	@PostMapping("/add-update")
	public ResponseEntity<?> addCaptainReceipeMaster(@Valid @RequestBody CaptainReceipeMasterRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			
			CaptainReceipeMasterResponseDto responseDto = captainReceipeMasterService.addOrUpdateCaptainReceipeMaster(request);
			
			if(responseDto == null) {
				response.put("msg", ConstantsPoc.CAPTAIN_RECEIPE_ADDED_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.CAPTAIN_RECEIPE_ADDED_SUCCESS);
				response.put("success", true);
			}
			return new ResponseEntity<>(response , HttpStatus.OK);
		}catch (RuntimeException e) {
		    response.put("success", false);
		    response.put("msg", e.getMessage());
		    return new ResponseEntity<>(response, HttpStatus.OK);
		}  catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getallbyuserid")
	public ResponseEntity<?> getAllCaptainReceipeByUserId(@RequestParam("userId") Long userId, @RequestParam(value = "status", required = false) Boolean status) {
		Map<String, Object> response = new HashMap<>();
		try {
			
			List<CaptainReceipeMasterResponseDto> responseDto = captainReceipeMasterService.getAllCaptainReceipeByUserId(userId, status);
			
			if(responseDto == null) {
				response.put("msg", ConstantsPoc.CAPTAIN_RECEIPE_FOUND_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.CAPTAIN_RECEIPE_FOUND_SUCCESS);
				response.put("success", true);
				response.put("data", responseDto);
			}
			return new ResponseEntity<>(response , HttpStatus.OK);
		}catch (RuntimeException e) {
		    response.put("success", false);
		    response.put("msg", e.getMessage());
		    return new ResponseEntity<>(response, HttpStatus.OK);
		}  catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getbyid")
	public ResponseEntity<?> getCaptainReceipeById(@RequestParam("id") Long id, 
			@RequestParam(value = "isSync", required = false) Boolean isSync) {
		Map<String, Object> response = new HashMap<>();
		try {
			
			CaptainReceipeMasterResponseDto responseDto = captainReceipeMasterService.getCaptainReceipeById(id, isSync);
			
			if(responseDto == null) {
				response.put("msg", ConstantsPoc.CAPTAIN_RECEIPE_FOUND_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.CAPTAIN_RECEIPE_FOUND_SUCCESS);
				response.put("success", true);
				response.put("data", responseDto);
			}
			return new ResponseEntity<>(response , HttpStatus.OK);
		}catch (RuntimeException e) {
		    response.put("success", false);
		    response.put("msg", e.getMessage());
		    return new ResponseEntity<>(response, HttpStatus.OK);
		}  catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/deletebyid")
	public ResponseEntity<?> deleteCaptainReceipeById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			
			Boolean isDeleted = captainReceipeMasterService.deleteCaptainReceipeById(id);
			
			if(!isDeleted) {
				response.put("msg", ConstantsPoc.CAPTAIN_RECEIPE_DELETE_SUCCESS);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.CAPTAIN_RECEIPE_DELETE_SUCCESS);
				response.put("success", true);
			}
			return new ResponseEntity<>(response , HttpStatus.OK);
		}catch (RuntimeException e) {
		    response.put("success", false);
		    response.put("msg", e.getMessage());
		    return new ResponseEntity<>(response, HttpStatus.OK);
		}  catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/updatestatusbyid")
	public ResponseEntity<?> updateCaptainReceipeStatusById(@RequestParam("id") Long id, @RequestParam("status") Boolean status) {
		Map<String, Object> response = new HashMap<>();
		try {
			
			Boolean isSuccess = captainReceipeMasterService.updateCaptainReceipeStatusById(id, status);
			
			if(!isSuccess) {
				response.put("msg", ConstantsPoc.CAPTAIN_RECEIPE_UPDATE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.CAPTAIN_RECEIPE_UPDATE_SUCCESS);
				response.put("success", true);
			}
			return new ResponseEntity<>(response , HttpStatus.OK);
		}catch (RuntimeException e) {
		    response.put("success", false);
		    response.put("msg", e.getMessage());
		    return new ResponseEntity<>(response, HttpStatus.OK);
		}  catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/syncallcaptainreceiperawmaterial")
	public ResponseEntity<?> syncAllCaptainReceipeRawMaterial(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			
			Boolean isSuccess = captainReceipeMasterService.syncAllCaptainReceipeRawMaterial(userId);
			
			if(isSuccess == null || !isSuccess) {
				response.put("msg", ConstantsPoc.CAPTAIN_RECEIPE_UPDATE_FAIL);
				response.put("success", isSuccess);
			}else {
				response.put("msg", ConstantsPoc.CAPTAIN_RECEIPE_UPDATE_SUCCESS);
				response.put("success", isSuccess);
			}
			return new ResponseEntity<>(response , HttpStatus.OK);
		}catch (RuntimeException e) {
		    response.put("success", false);
		    response.put("msg", e.getMessage());
		    return new ResponseEntity<>(response, HttpStatus.OK);
		}  catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
