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

import com.crmportal.request.dto.CityMasterRequestDto;
import com.crmportal.response.dto.CityMasterResponseDto;
import com.crmportal.response.dto.StateMasterResponseDto;
import com.crmportal.service.CityMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({"/v1/api/citymaster"})
@CrossOrigin(
		   origins = {"*"},
		   maxAge = 3600L
		)
public class CityMasterController {

	@Autowired
	CityMasterService cityMasterService;
	
	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addCityMaster(@Valid @RequestBody CityMasterRequestDto request){
		Map<String, Object> response = new HashMap<>();
		try {
			CityMasterResponseDto responseDto = cityMasterService.addOrUpdateCityMaster(request,Long.parseLong("-1"));
			if(responseDto == null) {
				response.put("msg", ConstantsPoc.CITY_CREATE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.CITY_CREATE_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> updateCityMaster(@Valid @RequestBody CityMasterRequestDto request,@RequestParam("id") Long id){
		Map<String, Object> response = new HashMap<>();
		try {
			CityMasterResponseDto responseDto = cityMasterService.addOrUpdateCityMaster(request,id);
			if(responseDto == null) {
				response.put("msg", ConstantsPoc.CITY_UPDATE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.CITY_UPDATE_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> getAllCityMaster(){
		Map<String, Object> response = new HashMap<>();
		try {
			List<CityMasterResponseDto> responseDtos = cityMasterService.getAllCityMaster();
			if(responseDtos.isEmpty()) {
				response.put("msg", ConstantsPoc.CITY_NOT_FOUND);
				response.put("success", false);
			}else {
				Map<String, Object> cityResp = new HashMap<>();
				cityResp.put("city Details", responseDtos);
				response.put("data", cityResp);
				response.put("msg", ConstantsPoc.CITY_FOUND_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> getCityMasterById(@RequestParam("id") Long id){
		Map<String, Object> response = new HashMap<>();
		try {
			CityMasterResponseDto responseDto = cityMasterService.getCityMasterById(id);
			if(responseDto == null) {
				response.put("msg", ConstantsPoc.CITY_NOT_FOUND);
				response.put("success", false);
			}else {
				List<CityMasterResponseDto> responseDtos = new ArrayList<>();
				responseDtos.add(responseDto);
				Map<String, Object> cityResp = new HashMap<>();
				cityResp.put("city Details", responseDtos);
				response.put("data", cityResp);
				response.put("msg", ConstantsPoc.CITY_FOUND_SUCCESS);
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
	
	
	@GetMapping("/getbystateid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getCityMasterByStateId(
	        @RequestParam(value = "stateId", required = false) Long stateId,
	        @RequestParam(value = "cityName", required = false) String cityName) {
	    
	    Map<String, Object> response = new HashMap<>();
	    try {
	        List<CityMasterResponseDto> cityMasterResponseDtos = cityMasterService.getCityMasterByStateId(stateId, cityName);

	        if (cityMasterResponseDtos.isEmpty()) {
	            response.put("msg", "City not found");
	            response.put("success", false);
	        } else {
	            Map<String, Object> cityResp = new HashMap<>();
	            cityResp.put("City Details", cityMasterResponseDtos);
	            response.put("data", cityResp);
	            response.put("msg", "City found successfully");
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
	
//	@GetMapping("/getcitywithsearch")
//	@ResponseBody
//	public ResponseEntity<Map<String, Object>> getCityWithSearch(@RequestParam("cityName") String cityName){
//		Map<String, Object> response = new HashMap<>();
//		try {
//			List<CityMasterResponseDto> cityMasterResponseDtos = cityMasterService.getCityNameWithSearch(cityName);
//			if(cityMasterResponseDtos.isEmpty()) {
//				response.put("msg", ConstantsPoc.CITY_NOT_FOUND);
//				response.put("success", false);
//			}else {
//				Map<String, Object> stateResp = new HashMap<>();
//				stateResp.put("City Details", cityMasterResponseDtos);
//				response.put("data", stateResp);
//				response.put("msg", ConstantsPoc.CITY_FOUND_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> deleteCityMasterById(@RequestParam("id") Long id){
		Boolean isSuccess = false;
		Map<String, Object> response = new HashMap<>();
		try {
			isSuccess = cityMasterService.deleteCityMasterById(id);
			if(isSuccess) {
				response.put("msg", ConstantsPoc.CITY_DELETE_SUCCESS);
				response.put("success", true);
			}else {
				response.put("msg", ConstantsPoc.CITY_DELETE_FAIL);
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
