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

import com.crmportal.request.dto.StateMasterRequestDto;
import com.crmportal.response.dto.StateMasterResponseDto;
import com.crmportal.service.StateMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({"/v1/api/statemaster"})
@CrossOrigin(
		   origins = {"*"},
		   maxAge = 3600L
		)
public class StateMasterController {

	@Autowired
	StateMasterService stateMasterService;
	
	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addStateMaster(@Valid @RequestBody StateMasterRequestDto request){
		Map<String, Object> response = new HashMap<>();
		try {
			StateMasterResponseDto responseDto = stateMasterService.addOrUpdateStateMaster(request,Long.parseLong("-1"));

			if(responseDto == null) {
				response.put("msg", ConstantsPoc.STATE_CREATE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.STATE_CREATE_SUCCESS);
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
	
	
	@PutMapping("/update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateStateMaster(@Valid @RequestBody StateMasterRequestDto request,@RequestParam("id") Long id){
		Map<String, Object> response = new HashMap<>();
		try {
			StateMasterResponseDto responseDto = stateMasterService.addOrUpdateStateMaster(request,id);

			if(responseDto == null) {
				response.put("msg", ConstantsPoc.STATE_UPDATE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.STATE_UPDATE_SUCCESS);
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
	
//	@GetMapping("/getall")
//	@ResponseBody
//	public ResponseEntity<Map<String, Object>> getAllStateMaster(@RequestParam(value = "stateName", required = false) String stateName){
//		Map<String, Object> response = new HashMap<>();
//		try {
//			 List<StateMasterResponseDto> stateMasterResponseDtos = stateMasterService.getAllStateMaster(stateName);
//			if(stateMasterResponseDtos.isEmpty()) {
//				response.put("msg", ConstantsPoc.STATE_NOT_FOUND);
//				response.put("success", false);
//			}else {
//				Map<String, Object> stateResp = new HashMap<>();
//				stateResp.put("State Details", stateMasterResponseDtos);
//				response.put("data", stateResp);
//				response.put("msg", ConstantsPoc.STATE_FOUND_SUCCESS);
//				response.put("success", true);
//			}
//			return new ResponseEntity<>(response, HttpStatus.OK);
//		}catch (RuntimeException e) {
//		    response.put("success", false);
//		    response.put("msg", e.getMessage());
//		    return new ResponseEntity<>(response, HttpStatus.OK);
//		}  catch (Exception e) {
//			e.printStackTrace();
//			response.put("success", false);
//			response.put("msg", e.getMessage());
//			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
	
	
	@GetMapping("/getbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getStateMasterById(@RequestParam("id") Long id){
		Map<String, Object> response = new HashMap<>();
		try {
			StateMasterResponseDto stateMasterResponseDto = stateMasterService.getStateMasterById(id);
			if(stateMasterResponseDto == null) {
				response.put("msg", ConstantsPoc.STATE_NOT_FOUND);
				response.put("success", false);
			}else {
				List<StateMasterResponseDto> stateMasterResponseDtos = new ArrayList<>();
				stateMasterResponseDtos.add(stateMasterResponseDto);
				Map<String, Object> stateResp = new HashMap<>();
				stateResp.put("State Details", stateMasterResponseDtos);
				response.put("data", stateResp);
				response.put("msg", ConstantsPoc.STATE_FOUND_SUCCESS);
				response.put("success", true);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
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
	
	@GetMapping("/getbycountryid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getStateMasterByCountryId( @RequestParam(value = "countryId", required = false) Long countryId,
	        @RequestParam(value = "stateName", required = false) String stateName) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	    	List<StateMasterResponseDto> responseDtos = stateMasterService.getStateMasterByCountryId(countryId, stateName);
	    	if (responseDtos.isEmpty()) {
	            response.put("msg", ConstantsPoc.STATE_NOT_FOUND);
	            response.put("success", false);
	        } else {
	            Map<String, Object> stateResp = new HashMap<>();
	            stateResp.put("state Details", responseDtos);
	            response.put("data", stateResp);
	            response.put("msg", ConstantsPoc.STATE_FOUND_SUCCESS);
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

	
	
//	@GetMapping("/getstatewithsearch")
//	@ResponseBody
//	public ResponseEntity<Map<String, Object>> getStateNameWithSearch(@RequestParam("stateName") String stateName){
//		Map<String, Object> response = new HashMap<>();
//		try {
//			List<StateMasterResponseDto> stateMasterResponseDtos = stateMasterService.getStateNameWithSearch(stateName);
//			if(stateMasterResponseDtos.isEmpty()) {
//				response.put("msg", ConstantsPoc.STATE_NOT_FOUND);
//				response.put("success", false);
//			}else {
//				Map<String, Object> stateResp = new HashMap<>();
//				stateResp.put("State Details", stateMasterResponseDtos);
//				response.put("data", stateResp);
//				response.put("msg", ConstantsPoc.STATE_FOUND_SUCCESS);
//				response.put("success", true);
//			}
//			return new ResponseEntity<>(response, HttpStatus.OK);
//		}catch (RuntimeException e) {
//		    response.put("success", false);
//		    response.put("msg", e.getMessage());
//		    return new ResponseEntity<>(response, HttpStatus.OK);
//		}  catch (Exception e) {
//			e.printStackTrace();
//			response.put("success", false);
//			response.put("msg", e.getMessage());
//			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
	
	@DeleteMapping("deletebyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteStateMasterById(@RequestParam("id") Long id){
		Boolean isSuccess = false;
		Map<String, Object> response = new HashMap<>();
		try {
			isSuccess = stateMasterService.deleteStateMasterById(id);
			if(isSuccess) {
				response.put("msg", ConstantsPoc.STATE_DELETE_SUCCESS);
				response.put("success", true);
			}else {
				response.put("msg", ConstantsPoc.STATE_DELETE_FAIL);
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
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
