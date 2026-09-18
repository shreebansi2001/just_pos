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

import com.crmportal.request.dto.FunctionMasterRequestDto;
import com.crmportal.response.dto.FunctionMasterResponseDto;
import com.crmportal.service.FunctionMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/functionmaster" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class FunctionMasterController {

	@Autowired
	FunctionMasterService functionMasterService;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addFunctionMaster(@Valid @RequestBody FunctionMasterRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			FunctionMasterResponseDto responseDto = functionMasterService.addOrUpdateFunctionMaster(request,
					Long.parseLong("-1"));
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.FUNCTION_CREATE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.FUNCTION_CREATE_SUCCESS);
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
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateFunctionMaster(@Valid @RequestBody FunctionMasterRequestDto request , @RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			FunctionMasterResponseDto responseDto = functionMasterService.addOrUpdateFunctionMaster(request,
					id);
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.FUNCTION_UPDATE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.FUNCTION_CREATE_SUCCESS);
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
	
	@GetMapping("/getallbyuserid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllFunctionsByUserId(
	        @RequestParam("userId") Long userId,
	        @RequestParam(value = "functionName", required = false) String functionName) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        // Call Service
	        List<FunctionMasterResponseDto> responseDtos = functionMasterService.getAllFunctionsByUserId(userId, functionName);

	        if (responseDtos.isEmpty()) {
	            response.put("msg", ConstantsPoc.FUNCTION_NOT_FOUND);
	            response.put("success", false);
	        } else {
	            Map<String, Object> data = new HashMap<>();
	            data.put("Function Details", responseDtos);
	            response.put("data", data);
	            response.put("msg", ConstantsPoc.FUNCTION_FOUND_SUCCESS);
	            response.put("success", true);
	        }
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("msg", e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	
	@GetMapping("/getbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getFunctionsById(
	        @RequestParam("id") Long id) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        
	    	FunctionMasterResponseDto responseDtos = functionMasterService.getFunctionsById(id);

	        if (responseDtos == null) {
	            response.put("msg", ConstantsPoc.FUNCTION_NOT_FOUND);
	            response.put("success", false);
	        } else {
	            Map<String, Object> data = new HashMap<>();
	            List<FunctionMasterResponseDto> functionMasterResponseDtos = new ArrayList<>();
	            functionMasterResponseDtos.add(responseDtos);
	            data.put("Function Details", functionMasterResponseDtos);
	            response.put("data", data);
	            response.put("msg", ConstantsPoc.FUNCTION_FOUND_SUCCESS);
	            response.put("success", true);
	        }
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("msg", e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@DeleteMapping("/deletebyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>>  deleteFunctionById(@RequestParam("id") Long id){
		 Map<String, Object> response = new HashMap<>();
		 Boolean isSuccess = false;
		    try {
		    	isSuccess = functionMasterService.deleteFunctionById(id);
		       if(isSuccess) {
		            response.put("msg", ConstantsPoc.FUNCTION_DELETED_SUCCESS);
		            response.put("success", true);
		        } else {
		            response.put("msg", ConstantsPoc.FUNCTION_DELETED_FAIL);
		            response.put("success", false);
		        } 
		        return new ResponseEntity<>(response, HttpStatus.OK);
		    } catch (Exception e) {
		        response.put("success", false);
		        response.put("msg", e.getMessage());
		        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		    }
	}
}
