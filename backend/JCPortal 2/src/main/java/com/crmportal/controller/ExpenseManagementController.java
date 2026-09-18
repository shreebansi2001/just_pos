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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.entity.ExpenseManagementEntity;
import com.crmportal.repository.ExpenseManagementRepository;
import com.crmportal.request.dto.ExpenseManagementRequestDto;
import com.crmportal.request.dto.LeadMasterResponseDto;
import com.crmportal.response.dto.ExpenseManagementResponseDto;
import com.crmportal.service.ExpenseManagementService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/expensemanagement" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class ExpenseManagementController {

	@Autowired
	ExpenseManagementService expenseManagementService;
	
	@Autowired
	ExpenseManagementRepository excepenseManagementRepository;
	
	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdateExpense(@ModelAttribute ExpenseManagementRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			ExpenseManagementResponseDto excepenseManagementResponseDto = expenseManagementService.addOrUpdateExpenseMaster(request);
			
			if(excepenseManagementResponseDto != null) {
				response.put("data", excepenseManagementResponseDto);
				response.put("msg", "Expense Added/Updated Successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Expense Is Not Added/Updated.");
				response.put("success", false);
			}
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(RuntimeException e) { 
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getallexpenses")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllExpenses(@RequestParam("eventId") Long eventId, @RequestParam("userId") Long userId ) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			List<ExpenseManagementResponseDto> excepenseManagementResponseDtos = expenseManagementService.getAllExpenses(eventId, userId);
			
			if(excepenseManagementResponseDtos != null) {
				response.put("data", excepenseManagementResponseDtos);
				response.put("msg", "Expenses Fetched Successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Expenses Fetched Failed");
				response.put("success", false);
			}
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(RuntimeException e) { 
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getexpensebyusertype")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getExpensesByUsertype(@RequestParam("userType") String userType, 
			@RequestParam("eventId") Long eventId, @RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			Map<String, Object> responseData = expenseManagementService.getExpensesUserType(userType, eventId, userId);
			
			if(responseData != null) {
				response.put("data", responseData);
				response.put("msg", "Expenses Fetched Successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Expenses Fetched Failed");
				response.put("success", false);
			}
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(RuntimeException e) { 
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getExpenseById(@RequestParam("expenseId") Long expenseId) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			ExpenseManagementResponseDto entity = expenseManagementService.getExpensesById(expenseId);
			
			if(entity != null) {
				response.put("data", entity);
				response.put("msg", "Expense Fetched Successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Expense Fetched Failed");
				response.put("success", false);
			}
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(RuntimeException e) { 
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/deletebyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteExpenseById(@RequestParam("expenseId") Long expenseId) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		
		try {
			isSuccess = expenseManagementService.deleteExpensesById(expenseId);
			
			if(isSuccess) {
				response.put("msg", "Expenses Deleted Successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Expenses Deletation Failed");
				response.put("success", false);
			}
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(RuntimeException e) { 
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
