package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.crmportal.entity.ExpenseItemEntity;
import com.crmportal.request.dto.ExpenseItemRequestDto;
import com.crmportal.response.dto.ExpenseItemResponseDto;
import com.crmportal.response.dto.ExpenseManagementResponseDto;
import com.crmportal.service.ExpenseItemService;

@RestController
@RequestMapping({ "/v1/api/expenseitem" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class ExpenseItemController {

	@Autowired
	ExpenseItemService expenseItemService;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdateExpenseItem(@RequestBody ExpenseItemRequestDto request) {
		Map<String, Object> response = new HashMap<>();

		try {
			ExpenseItemResponseDto expenseItemResponseDto = expenseItemService.addOrUpdateExpenseItem(request,
					request.getExpenseItemId());

			if (expenseItemResponseDto != null) {
				response.put("data", expenseItemResponseDto);
				response.put("msg", "Expense Item Added/Updated Successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Expense Item Add/Update Failed");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/delete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteExpenseItem(@RequestParam("expenseItemId") Long expenseItemId) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		
		try {
			isSuccess = expenseItemService.deleteExpenseItem(expenseItemId);

			if (isSuccess) {
				response.put("msg", "Expense Item Deleted Successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Expense Item Deletation Failed");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@GetMapping("/getbyexpenseandevent")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getExpenseItemByExpenseAndEvent(@RequestParam("expenseId") Long expenseId, @RequestParam("eventId") Long eventId) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			List<ExpenseItemResponseDto> responseDtos = expenseItemService.getExpenseItemByExpenseAndEvent(expenseId, eventId);

			if (responseDtos != null) {
				response.put("data", responseDtos);
				response.put("msg", "Expense Item Found Successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Expense Item doen not Found.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
