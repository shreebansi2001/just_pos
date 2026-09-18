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

import com.crmportal.request.dto.PlansRequestDto;
import com.crmportal.response.dto.PlansResponseDto;
import com.crmportal.service.PlansService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({"/v1/api/plans"})
@CrossOrigin(
		   origins = {"*"},
		   maxAge = 3600L
		)
public class PlansController {

	@Autowired
	PlansService plansService;
	
	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addPlans(@Valid @RequestBody PlansRequestDto request){
		Map<String, Object> response = new HashMap<>();
		try {
			PlansResponseDto responseDto = plansService.addOrUpdatePlans(request , Long.parseLong("-1"));
			if(responseDto == null) {
				response.put("msg", ConstantsPoc.PLAN_CREATE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.PLAN_CREATE_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> updatePlans(@Valid @RequestBody PlansRequestDto request,@RequestParam("id") Long id){
		Map<String, Object> response = new HashMap<>();
		try {
			PlansResponseDto responseDto = plansService.addOrUpdatePlans(request , id);
			if(responseDto == null) {
				response.put("msg", ConstantsPoc.PLAN_UPDATE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.PLAN_UPDATE_SUCCESS);
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
	
	@GetMapping("/getall")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllPlans(){
		Map<String, Object> response = new HashMap<>();
		try {
			List<PlansResponseDto> responseDto = plansService.getAllPlans();
			if(responseDto.isEmpty()) {
				response.put("msg", ConstantsPoc.PLAN_NOT_FOUND);
				response.put("success", false);
			}else {
				Map<String, Object> planResp = new HashMap<>();
				planResp.put("Plan Details", responseDto);
				response.put("data", planResp);
				response.put("msg", ConstantsPoc.PLAN_FOUND_SUCCESS);
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
	
	@GetMapping("/getallbybillingcycle")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllPlansByBillingCycle(@RequestParam("billingCycle") String billingCycle){
		Map<String, Object> response = new HashMap<>();
		try {
			List<PlansResponseDto> responseDto = plansService.getAllPlansByBillingCycle(billingCycle);
			if(responseDto.isEmpty()) {
				response.put("msg", ConstantsPoc.PLAN_NOT_FOUND);
				response.put("success", false);
			}else {
				Map<String, Object> planResp = new HashMap<>();
				planResp.put("Plan Details", responseDto);
				response.put("data", planResp);
				response.put("msg", ConstantsPoc.PLAN_FOUND_SUCCESS);
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
	
	@GetMapping("/getbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getPlansById(@RequestParam("id") Long id){
		Map<String, Object> response = new HashMap<>();
		try {
			PlansResponseDto responseDto = plansService.getPlansById(id);
			if(responseDto == null) {
				response.put("msg", ConstantsPoc.PLAN_NOT_FOUND);
				response.put("success", false);
			}else {
				Map<String, Object> planResp = new HashMap<>();
				List<PlansResponseDto> responseDtos = new ArrayList<>();
				responseDtos.add(responseDto);
				planResp.put("Plan Details", responseDtos);
				response.put("data", planResp);
				response.put("msg", ConstantsPoc.PLAN_FOUND_SUCCESS);
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

	@DeleteMapping("/deletebyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deletePlanById(@RequestParam("id") Long id){
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = plansService.deletePlanById(id);
			if(isSuccess) {
				response.put("msg", ConstantsPoc.PLAN_DELETE_SUCCESS);
				response.put("success", true);
			}else {
				response.put("msg", ConstantsPoc.PLAN_DELETE_FAIL);
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
