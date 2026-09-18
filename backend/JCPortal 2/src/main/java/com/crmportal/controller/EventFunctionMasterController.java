package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import com.crmportal.request.dto.EventFunctionFilterRequestDto;
import com.crmportal.request.dto.EventFunctionMasterRequestDto;
import com.crmportal.response.dto.AllEventFunctionMasterResponseDto;
import com.crmportal.response.dto.EventFunctionFilterResponseDto;
import com.crmportal.response.dto.EventFunctionMasterResponseDto;
import com.crmportal.service.EventFunctionMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "v1/api/eventfunction" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventFunctionMasterController {

	@Autowired
	EventFunctionMasterService eventFunctionMasterService;

	@GetMapping("/getalleventfunctionByeventid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllEventFunctionByEventId(@RequestParam("eventId") Long eventId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventFunctionMasterResponseDto> responseDtos = eventFunctionMasterService
					.getAllEventFunctionByEventId(eventId);
			if (responseDtos.isEmpty()) {
				response.put("msg", ConstantsPoc.EVENT_FUNCTION_MASTER_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> eventFunctionResp = new HashMap<>();
				eventFunctionResp.put("Event Function Details", responseDtos);
				response.put("data", eventFunctionResp);
				response.put("msg", ConstantsPoc.EVENT_FUNCTION_MASTER_FOUND_SUCCESS);
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

	@DeleteMapping("/deleteeventfunction")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteEventFunctionById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = eventFunctionMasterService.deleteEventFunctionById(id);
			if (isSuccess) {
				response.put("msg", "Event Function Deleted Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "Event Function Deleted Failed");
				response.put("success", isSuccess);
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

	@GetMapping("/getalleventfunction")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllEventFunction(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search,@RequestParam("userId") Long userId) {

		Map<String, Object> response = new HashMap<>();

		Page<AllEventFunctionMasterResponseDto> pageResult = eventFunctionMasterService.getAllEventFunction(page, size,
				search,userId);

		if (pageResult.isEmpty()) {
			response.put("msg", ConstantsPoc.EVENT_FUNCTION_MASTER_NOT_FOUND);
			response.put("success", false);
		} else {
			Map<String, Object> eventFunctionResp = new HashMap<>();
			eventFunctionResp.put("EventFunctions", pageResult.getContent());
			eventFunctionResp.put("currentPage", page);
			eventFunctionResp.put("totalItems", pageResult.getTotalElements());
			eventFunctionResp.put("totalPages", pageResult.getTotalPages());

			response.put("data", eventFunctionResp);
			response.put("msg", ConstantsPoc.EVENT_FUNCTION_MASTER_FOUND_SUCCESS);
			response.put("success", true);
		}

		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/filtereventfunctions")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> filterEventFunctions(
	        @RequestBody EventFunctionFilterRequestDto request) {

	    Map<String, Object> response = new HashMap<>();

	    try {

	        List<EventFunctionFilterResponseDto> data =
	                eventFunctionMasterService
	                        .getFilteredEventFunctions(request);

	        if (data.isEmpty()) {
	            response.put("success", false);
	            response.put("msg", "No Event Functions Found");
	        } else {
	            response.put("success", true);
	            response.put("msg", "Event Functions Found");
	            response.put("data", data);
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

	        return new ResponseEntity<>(
	                response,
	                HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
}
