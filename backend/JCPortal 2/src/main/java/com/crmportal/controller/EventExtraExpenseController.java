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

import com.crmportal.request.dto.EventExtraExpenseRequestDto;
import com.crmportal.response.dto.EventExtraExpenseResponseDto;
import com.crmportal.service.EventExtraExpenseService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/extra-expense" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventExtraExpenseController {

	@Autowired
	EventExtraExpenseService eventExtraExpenseService;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addExtraExpenseMaster(
			@Valid @RequestBody EventExtraExpenseRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventExtraExpenseResponseDto responseDto = eventExtraExpenseService.addOrUpdateExtraService(request,
					Long.valueOf("-1"));
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.EVENT_EXTRA_EXPENSE_CREATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_EXTRA_EXPENSE_CREATE_FAIL);
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

	@PutMapping("/update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateExtraExpenseMaster(
			@Valid @RequestBody EventExtraExpenseRequestDto request, @RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventExtraExpenseResponseDto responseDto = eventExtraExpenseService.addOrUpdateExtraService(request,
					id);
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.EVENT_EXTRA_EXPENSE_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_EXTRA_EXPENSE_UPDATE_FAIL);
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

	@GetMapping("/getallbyeventId")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllEventExtraExpense(@RequestParam("eventId") Long eventId,@RequestParam(value = "eventFunctionId", required = true) Long eventFunctionId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventExtraExpenseResponseDto> responseDtos = eventExtraExpenseService
					.getAllEventExtraByEventId(eventId, eventFunctionId);
			if (responseDtos.isEmpty()) {
				response.put("msg", ConstantsPoc.EVENT_EXTRA_EXPENSE_FOUND_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> contactTypeRes = new HashMap<>();
				contactTypeRes.put("Contact Type Details", responseDtos);
				response.put("data", contactTypeRes);
				response.put("msg", ConstantsPoc.EVENT_EXTRA_EXPENSE_FOUND_SUCCESS);
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
	
	
	@GetMapping("/getbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getEventExtraExpenseById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventExtraExpenseResponseDto> responseDtos = new ArrayList<>();
			EventExtraExpenseResponseDto responseDto = eventExtraExpenseService
					.getEventExtraExpenseById(id);
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.EVENT_EXTRA_EXPENSE_FOUND_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> contactTypeRes = new HashMap<>();
				responseDtos.add(responseDto);
				contactTypeRes.put("Event Extra Expense Details", responseDtos);
				response.put("data", contactTypeRes);
				response.put("msg", ConstantsPoc.EVENT_EXTRA_EXPENSE_FOUND_SUCCESS);
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
	
	@DeleteMapping("/delete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteContactTypeById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = eventExtraExpenseService
					.deleteEventExtraById(id);
			if (isSuccess) {
				response.put("msg", ConstantsPoc.EVENT_EXTRA_EXPENSE_DELETE_SUCCESS);
				response.put("success", isSuccess);
			} else {
				response.put("msg", ConstantsPoc.EVENT_EXTRA_EXPENSE_DELETE_FAIL);
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
