package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.EventMasterRequestDto;
import com.crmportal.request.dto.EventRawMaterialAppRequest;
import com.crmportal.request.dto.EventRawMaterialFunctionRequestDto;
import com.crmportal.request.dto.EventRawMaterialRequest;
import com.crmportal.request.dto.GenerateSotRequestDto;
import com.crmportal.response.dto.EventMasterResponseDto;
import com.crmportal.response.dto.EventRawMaterialFunctionsDto;
import com.crmportal.response.dto.EventRawMaterialResponse;
import com.crmportal.response.dto.RawMaterialMasterResponseDto;
import com.crmportal.service.EventRawMaterialService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/event-raw-material" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventRawMaterialController {
	
	@Autowired
	EventRawMaterialService eventRawMaterialService;
	
	@GetMapping("/getbyevent")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getEventRawMaterial(
			@RequestParam("rawMateriaCatlId") Long rawMateriaCatlId, 
			@RequestParam("eventId") Long eventId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventRawMaterialResponse> responseDtos = eventRawMaterialService.getEventRawMaterial(eventId,rawMateriaCatlId,0l);
			if(responseDtos.isEmpty()) {
				response.put("msg", ConstantsPoc.EVENT_RAW_MATERIAL_FOUND_FAIL);
				response.put("success", false);
			}else {
				Map<String, Object> rawMaterialResp = new HashMap<>();
				rawMaterialResp.put("Event_RAW_MATERIAL_ALLOCATION", responseDtos);
				response.put("data", rawMaterialResp);
				response.put("msg", ConstantsPoc.EVENT_RAW_MATERIAL_FOUND_SUCCESS);
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
	
	
	@PostMapping("/add-update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addEventRawMaterial(@Valid @RequestBody EventRawMaterialRequest request) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventRawMaterialResponse> eventRawMatResponseDto = eventRawMaterialService.addOrUpdateEventRawMaterial(request);
			if (eventRawMatResponseDto != null) {
				response.put("msg", ConstantsPoc.EVENT_RAW_MATERIAL_CREATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_RAW_MATERIAL_CREATE_FAIL);
				response.put("success", false);
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
	
	//Below APIs for APP only,
	
	@GetMapping("/getbyrawmaterialid-app")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getEventRawMaterialByEventRawMaterialId(
			@RequestParam("rawMateriaId") Long rawMateriaId, 
			@RequestParam("eventId") Long eventId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventRawMaterialFunctionsDto> responseDtos = eventRawMaterialService.getEventRawMaterialByEventRawMaterialId(eventId,rawMateriaId);
			if(responseDtos == null) {
				response.put("msg", ConstantsPoc.EVENT_RAW_MATERIAL_FOUND_FAIL);
				response.put("success", false);
			}else {
				Map<String, Object> rawMaterialResp = new HashMap<>();
				rawMaterialResp.put("EVENT_RAW_MATERIAL_FUNCTION_DATA", responseDtos);
				response.put("data", rawMaterialResp);
				response.put("msg", ConstantsPoc.EVENT_RAW_MATERIAL_FOUND_SUCCESS);
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
	
	
	@PostMapping("/add-update-event-rawmaterial-functions-app")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addEventRawMaterialFunctions(@Valid @RequestBody EventRawMaterialFunctionRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			boolean isSuccess = eventRawMaterialService.addEventRawMaterialFunctions(request);
			if (isSuccess) {
				response.put("msg", "Event Raw Material function Updated Successfully");
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_RAW_MATERIAL_CREATE_FAIL);
				response.put("success", false);
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
	
	@PostMapping("/add-update-for-app")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addEventRawMaterialForApp(@Valid @RequestBody EventRawMaterialAppRequest request) {
		Map<String, Object> response = new HashMap<>();
		try {
			boolean eventRawMatResponseDto = eventRawMaterialService.addEventRawMaterialForApp(request);
			if (eventRawMatResponseDto) {
				response.put("msg", "Event Raw Material Updated Successfully");
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_RAW_MATERIAL_CREATE_FAIL);
				response.put("success", false);
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
	
	@PostMapping("/generate-sot")
	public ResponseEntity<Map<String, Object>> generateSot(
	        @RequestBody GenerateSotRequestDto request) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        // redirect to SOT service
	        response.put("success", true);
	        response.put("msg", "SOT generation triggered");
	        response.put("sotEndpoint", "/v1/api/sot/generate");
	        return ResponseEntity.ok(response);
	    } catch (RuntimeException e) {
	        response.put("success", false);
	        response.put("msg", e.getMessage());
	        return ResponseEntity.ok(response);
	    }
	}
	
	
	
}
