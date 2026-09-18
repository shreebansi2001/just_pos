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

import com.crmportal.request.dto.EventInvoiceRequestDto;
import com.crmportal.request.dto.EventLaborDetailsRequestForAppDto;
import com.crmportal.request.dto.EventLaborRequestDto;
import com.crmportal.response.dto.EventInvoiceResponseDto;
import com.crmportal.response.dto.EventLaborResponseDto;
import com.crmportal.service.EventInvoiceService;
import com.crmportal.service.EventLaborService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({"/v1/api/labor"})
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventLaborController {

	@Autowired
	EventLaborService eventLaborService;
	
	@PostMapping("/add-update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addEventLabor(@Valid @RequestBody EventLaborRequestDto request){
		Map<String, Object> response = new HashMap<>();
		try {
			EventLaborResponseDto responseDto = eventLaborService.saveUpdateEventLabour(request);
			if(responseDto != null) {
				response.put("msg", ConstantsPoc.EVENT_LABOR_CREATE_SUCCESS);
				response.put("success", true);
			}else {
				response.put("msg", ConstantsPoc.EVENT_LABOR_CREATE_FAIL);
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
	
	@PostMapping("/add-updateForApp")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addEventLaborForApp(@Valid @RequestBody List<EventLaborDetailsRequestForAppDto> request){
		Map<String, Object> response = new HashMap<>();
		try {
			EventLaborResponseDto responseDto = eventLaborService.saveUpdateEventLabourForApp(request);
			if(responseDto != null) {
				response.put("msg", ConstantsPoc.EVENT_LABOR_CREATE_SUCCESS);
				response.put("success", true);
			}else {
				response.put("msg", ConstantsPoc.EVENT_LABOR_CREATE_FAIL);
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

	@GetMapping("/get")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getEventLabor(@RequestParam("eventId") Long eventId, @RequestParam("eventFunctionId") Long eventFunctionId){
		Map<String, Object> response = new HashMap<>();
		try {
			EventLaborResponseDto responseDto = eventLaborService.getEventLabor(eventId, eventFunctionId);
			if(responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", ConstantsPoc.EVENT_LABOR_FOUND_SUCCESS);
				response.put("success", true);
			}else {
				response.put("msg", ConstantsPoc.EVENT_LABOR_FOUND_FAIL);
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
	
	@GetMapping("/getBySupplier")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getEventLaborBySupplier(@RequestParam("eventId") Long eventId, 
			@RequestParam("eventFunctionId") Long eventFunctionId,
			@RequestParam("partyId") Long partyId){
		Map<String, Object> response = new HashMap<>();
		try {
			EventLaborResponseDto responseDto = eventLaborService.getEventLaborBySupplier(eventId, eventFunctionId, partyId);
			if(responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", ConstantsPoc.EVENT_LABOR_FOUND_SUCCESS);
				response.put("success", true);
			}else {
				response.put("msg", ConstantsPoc.EVENT_LABOR_FOUND_FAIL);
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
	
	@DeleteMapping("/delete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteEventLaborById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = eventLaborService.deleteEventLaborById(id);
			if (isSuccess) {
				response.put("msg", "Event Labor deleted successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "Event Labor deleted failed");
				response.put("success",isSuccess);
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
	
	
	
}
