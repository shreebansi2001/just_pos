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

import com.crmportal.request.dto.ShiftRequestDto;
import com.crmportal.response.dto.ShiftResponseDto;
import com.crmportal.service.ShiftService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/shift" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class ShiftController {

	@Autowired
	ShiftService shiftService;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addShift(@Valid @RequestBody ShiftRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			ShiftResponseDto responseDto = shiftService.addOrUpdateShift(request,
					Long.parseLong("-1"));
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.SHIFT_CREATE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.SHIFT_CREATE_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> updateShift(@Valid @RequestBody ShiftRequestDto request , @RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			ShiftResponseDto responseDto = shiftService.addOrUpdateShift(request,id);
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.SHIFT_UPDATE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.SHIFT_CREATE_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> getAllShiftByUserId(
	        @RequestParam("userId") Long userId,
	        @RequestParam(value = "shiftName", required = false) String shiftName) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        // Call Service
	        List<ShiftResponseDto> responseDtos = shiftService.getAllShiftByUserId(userId, shiftName);

	        if (responseDtos.isEmpty()) {
	            response.put("msg", ConstantsPoc.SHIFT_NOT_FOUND);
	            response.put("success", false);
	        } else {
	            Map<String, Object> data = new HashMap<>();
	            data.put("Function Details", responseDtos);
	            response.put("data", data);
	            response.put("msg", ConstantsPoc.SHIFT_FOUND_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> getShiftById(
	        @RequestParam("id") Long id) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        
	    	ShiftResponseDto responseDtos = shiftService.getShiftById(id);

	        if (responseDtos == null) {
	            response.put("msg", ConstantsPoc.SHIFT_NOT_FOUND);
	            response.put("success", false);
	        } else {
	            Map<String, Object> data = new HashMap<>();
	            List<ShiftResponseDto> functionMasterResponseDtos = new ArrayList<>();
	            functionMasterResponseDtos.add(responseDtos);
	            data.put("Function Details", functionMasterResponseDtos);
	            response.put("data", data);
	            response.put("msg", ConstantsPoc.SHIFT_FOUND_SUCCESS);
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
		    	isSuccess = shiftService.deleteShiftById(id);
		       if(isSuccess) {
		            response.put("msg", ConstantsPoc.SHIFT_DELETED_SUCCESS);
		            response.put("success", true);
		        } else {
		            response.put("msg", ConstantsPoc.SHIFT_DELETED_FAIL);
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
