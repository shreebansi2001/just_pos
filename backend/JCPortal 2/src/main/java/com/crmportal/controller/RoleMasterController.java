package com.crmportal.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.RoleMasterRequestDto;
import com.crmportal.response.dto.RoleMasterResponseDto;
import com.crmportal.service.RoleMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({"/v1/api/rolemaster"})
@CrossOrigin(
		   origins = {"*"},
		   maxAge = 3600L
		)
public class RoleMasterController {

	@Autowired
	RoleMasterService roleMasterService;
	
	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addRoleMaster(@Valid @RequestBody RoleMasterRequestDto request){
		Map<String, Object> response = new HashMap<>();
		try {
			RoleMasterResponseDto responseDto = roleMasterService.addOrUpdateRoleMaster(request , Long.parseLong("-1"));
			if(responseDto != null) {
				response.put("msg",ConstantsPoc.ROLES_CREATE_SUCCESS);
				response.put("success", true);
			}else {
				response.put("msg",ConstantsPoc.ROLES_CREATE_FAIL);
				response.put("success", false);
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
	
	
	@PutMapping("/update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateRoleMaster(@Valid @RequestBody RoleMasterRequestDto request,@RequestParam("id") Long id){
		Map<String, Object> response = new HashMap<>();
		try {
			RoleMasterResponseDto responseDto = roleMasterService.addOrUpdateRoleMaster(request , id);
			if(responseDto != null) {
				response.put("msg",ConstantsPoc.ROLES_UPDATE_SUCCESS);
				response.put("success", true);
			}else {
				response.put("msg",ConstantsPoc.ROLES_UPDATE_FAIL);
				response.put("success", false);
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
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllRoleMasterByUserId(@RequestParam(value = "userId") Long userId, @RequestParam(value = "roleName", required = false) String roleName){
		Map<String, Object> response = new HashMap<>();
		try {
			List<RoleMasterResponseDto> responseDtos = roleMasterService.getAllRoleMaster(userId,roleName);
			if(responseDtos.isEmpty()) {
				response.put("msg", ConstantsPoc.ROLE_NOT_FOUND);
				response.put("success", false);
			}else {
				Map<String, Object> roleResp = new HashMap<>();
				roleResp.put("Role Details", responseDtos);
				response.put("data", roleResp);
				response.put("msg", ConstantsPoc.ROLE_FOUND_SUCCESS);
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
	
	
	@GetMapping("/gebyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getRoleMasterById(@RequestParam("id") Long id){
		Map<String, Object> response = new HashMap<>();
		try {
			RoleMasterResponseDto responseDto = roleMasterService.getRoleMasterById(id);
			if(responseDto == null) {
				response.put("msg", ConstantsPoc.ROLE_NOT_FOUND);
				response.put("success", false);
			}else {
				Map<String, Object> roleResp = new HashMap<>();
				List<RoleMasterResponseDto> responseDtos = new ArrayList<>();
				responseDtos.add(responseDto);
				roleResp.put("Role Details", responseDtos);
				response.put("data", roleResp);
				response.put("msg", ConstantsPoc.ROLE_FOUND_SUCCESS);
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
	
	
	@DeleteMapping("deletebyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteRoleMasterById(@RequestParam("id") Long id){
		Boolean isSuccess = false;
		Map<String, Object> response = new HashMap<>();
		try {
			isSuccess = roleMasterService.deleteRoleMasterById(id);
			if (isSuccess) {
				response.put("msg", ConstantsPoc.ROLE_DELETE_SUCCESS);
				response.put("success", true);
			}else {
				response.put("msg", ConstantsPoc.ROLE_DELETE_FAIL);
				response.put("success", false);				
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
