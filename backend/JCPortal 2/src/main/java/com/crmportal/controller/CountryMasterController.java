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

import com.crmportal.request.dto.CountryMasterRequestDto;
import com.crmportal.response.dto.CountryMasterResponseDto;
import com.crmportal.service.CountryMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({"/v1/api/countrymaster"})
@CrossOrigin(origins = {"*"}, maxAge = 3600L)
public class CountryMasterController {

	@Autowired
	CountryMasterService countryMasterService;
	
	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addCountryMaster(@Valid @RequestBody CountryMasterRequestDto request){
		Map<String, Object> response = new HashMap<>();
		try {
			CountryMasterResponseDto responseDto = countryMasterService.addOrUpdateCountryMaster(request, Long.parseLong("-1"));

			if(responseDto == null) {
				response.put("msg", ConstantsPoc.COUNTRY_CREATE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.COUNTRY_CREATE_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> updateCountryMaster(@Valid @RequestBody CountryMasterRequestDto request,@RequestParam("id") Long id){
		Map<String, Object> response = new HashMap<>();
		try {
			CountryMasterResponseDto responseDto = countryMasterService.addOrUpdateCountryMaster(request, id);

			if(responseDto == null) {
				response.put("msg", ConstantsPoc.COUNTRY_UPDATE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.COUNTRY_UPDATE_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> getAllCountryMaster(@RequestParam(value = "countryName", required = false) String countryName){
		Map<String, Object> response = new HashMap<>();
		try {
			List<CountryMasterResponseDto> countryMasterResponseDtos = countryMasterService.getAllCountryMaster(countryName);
			if(countryMasterResponseDtos.isEmpty()) {
				response.put("msg", ConstantsPoc.COUNTRY_NOT_FOUND);
				response.put("success", false);
			}else {
				Map<String, Object> countryResp = new HashMap<>();
				countryResp.put("Country Details", countryMasterResponseDtos);
				response.put("data", countryResp);
				response.put("msg", ConstantsPoc.COUNTRY_FOUND_SUCCESS);
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
	
	
	@GetMapping("/getbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getCountryMasterById(@RequestParam("id") Long id){
		Map<String, Object> response = new HashMap<>();
		try {
			CountryMasterResponseDto countryMasterResponseDto = countryMasterService.getCountryMasterById(id);
			if(countryMasterResponseDto == null) {
				response.put("msg", ConstantsPoc.COUNTRY_NOT_FOUND);
				response.put("success", false);
			}else {
				List<CountryMasterResponseDto> countryMasterResponseDtos = new ArrayList<>();
				countryMasterResponseDtos.add(countryMasterResponseDto);
				Map<String, Object> countryResp = new HashMap<>();
				countryResp.put("Country Details", countryMasterResponseDtos);
				response.put("data", countryResp);
				response.put("msg", ConstantsPoc.COUNTRY_FOUND_SUCCESS);
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
	
//	@GetMapping("/getcountrywithsearch")
//	@ResponseBody
//	public ResponseEntity<Map<String, Object>> getCountryNameWithSearch(@RequestParam("countryName") String countryName){
//		Map<String, Object> response = new HashMap<>();
//		try {
//			List<CountryMasterResponseDto> countryMasterResponseDtos = countryMasterService.getCountryNameWithSearch(countryName);
//			if(countryMasterResponseDtos.isEmpty()) {
//				response.put("msg", ConstantsPoc.COUNTRY_NOT_FOUND);
//				response.put("success", false);
//			}else {
//				Map<String, Object> countryResp = new HashMap<>();
//				countryResp.put("Country Details", countryMasterResponseDtos);
//				response.put("data", countryResp);
//				response.put("msg", ConstantsPoc.COUNTRY_FOUND_SUCCESS);
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
	
	@DeleteMapping("/deletebyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteCountryMasterById(@RequestParam("id") Long id){
		Boolean isSuccess = false;
		Map<String, Object> response = new HashMap<>();
		try {
			isSuccess = countryMasterService.deleteCountryMasterById(id);
			if(isSuccess) {
				response.put("msg", ConstantsPoc.COUNTRY_DELETE_SUCCESS);
				response.put("success", true);
			}else {
				response.put("msg", ConstantsPoc.COUNTRY_DELETE_FAIL);
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
