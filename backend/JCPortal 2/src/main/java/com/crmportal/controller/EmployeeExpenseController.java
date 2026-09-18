package com.crmportal.controller;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.ExpenseRequestDto;
import com.crmportal.request.dto.OfficeExpensePayoutRequestDto;
import com.crmportal.request.dto.OfficeExpenseRequestDto;
import com.crmportal.request.dto.TripExpensePayoutRequestDto;
import com.crmportal.response.dto.AllExpensesResponseDto;
import com.crmportal.response.dto.ExpenseResponseDto;
import com.crmportal.response.dto.MemberResponseDto;
import com.crmportal.response.dto.OfficeExpensePayoutResponseDto;
import com.crmportal.response.dto.OfficeExpenseResponseDto;
import com.crmportal.response.dto.TripExpensePayoutHistoryResponseDto;
import com.crmportal.service.ExpenseService;

@RestController
@RequestMapping({ "/v1/api/employeeexpense" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EmployeeExpenseController {

	@Autowired
	ExpenseService expenseService;

	@GetMapping("/getallmemberdata")
	public ResponseEntity<?> getAllMemberData(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();

		try {
			List<MemberResponseDto> data = expenseService.getAllMembers(userId);

			if (data != null) {
				response.put("data", data);
				response.put("msg", "Expense Added successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Expense is not Added.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/addOrUpdateTripExpense")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdateExpense(@ModelAttribute ExpenseRequestDto request) {
		Map<String, Object> response = new HashMap<>();

		try {
			ExpenseResponseDto expenseResponseDto = expenseService.addOrUpdateExpense(request);

			if (expenseResponseDto != null) {
				response.put("data", expenseResponseDto);
				response.put("msg", "Expense Added successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Expense is not Added.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/addOrUpdateOfficeExpense")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdateOfficeExpense(@ModelAttribute OfficeExpenseRequestDto request) {
		Map<String, Object> response = new HashMap<>();

		try {
			OfficeExpenseResponseDto officeExpenseResponseDto = expenseService.addOrUpdateOfficeExpense(request);

			if (officeExpenseResponseDto != null) {
				response.put("data", officeExpenseResponseDto);
				response.put("msg", "Expense Added successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Expense is not Added.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@DeleteMapping("/deleteTripExpense")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteExpense(@RequestParam Long expenseId) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		
		try {
			isSuccess = expenseService.deleteExpense(expenseId);

			if (isSuccess != null) {
				response.put("msg", "Expense is deleted.");
				response.put("success", true);
			} else {
				response.put("msg", "Expense is not deleted.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/deleteOfficeExpense")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteOfficeExpense(@RequestParam Long expenseId) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		
		try {
			isSuccess = expenseService.deleteOfficeExpense(expenseId);

			if (isSuccess != null) {
				response.put("msg", "Expense is deleted.");
				response.put("success", true);
			} else {
				response.put("msg", "Expense is not deleted.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/payoutTripExpense")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> payoutExpense(@RequestBody TripExpensePayoutRequestDto payout) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			TripExpensePayoutHistoryResponseDto data = expenseService.payoutExpense(payout);

			if (data != null) {
				response.put("data", data);
				response.put("msg", "Payout done.");
				response.put("success", true);
			} else {
				response.put("msg", "Payout failed.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/deletetrippayout")
	public ResponseEntity<?> deleteTripPayout(@RequestParam("payoutId") Long payoutId) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			Boolean isSuccess = expenseService.deleteTripPayout(payoutId);

			if (isSuccess) {
				response.put("msg", "Payout deleted done.");
				response.put("success", true);
			} else {
				response.put("msg", "Payout deleted failed.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getalltrippayouthistorybyexpenseid")
	public ResponseEntity<?> getAllTripPayoutHistoryByExpenseId(@RequestParam("expenseId") Long expenseId) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			List<TripExpensePayoutHistoryResponseDto> data = expenseService.getAllTripPayoutByExpenseId(expenseId);

			if (data != null) {
				response.put("msg", "Payout done.");
				response.put("success", true);
				response.put("data", data);
			} else {
				response.put("msg", "Payout failed.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/gettrippayoutbypayoutid")
	public ResponseEntity<?> getTripPayoutByPayoutId(@RequestParam("payoutId") Long payoutId) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			TripExpensePayoutHistoryResponseDto data = expenseService.getTripPayoutByPayoutId(payoutId);

			if (data != null) {
				response.put("msg", "Payout done.");
				response.put("success", true);
				response.put("data", data);
			} else {
				response.put("msg", "Payout failed.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/payoutOfficeExpense")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> payoutOfficeExpense(@Valid @RequestBody OfficeExpensePayoutRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			OfficeExpensePayoutResponseDto  res = expenseService.payoutOfficeExpense(request);

			if (res != null) {
				response.put("msg", "Payout done.");
				response.put("success", true);
			} else {
				response.put("msg", "Payout failed.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getallofficepayouthistorybyexpenseid")
	public ResponseEntity<?> getAllOfficePayoutHistoryByExpenseId(@RequestParam("expenseId") Long expenseId) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			List<OfficeExpensePayoutResponseDto> data = expenseService.getAllOfficePayoutByExpenseId(expenseId);

			if (data != null) {
				response.put("msg", "Payout done.");
				response.put("success", true);
				response.put("data", data);
			} else {
				response.put("msg", "Payout failed.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getofficepayoutbypayoutid")
	public ResponseEntity<?> getOfficePayoutByPayoutId(@RequestParam("payoutId") Long payoutId) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			OfficeExpensePayoutResponseDto data = expenseService.getOfficePayoutByPayoutId(payoutId);

			if (data != null) {
				response.put("msg", "Payout done.");
				response.put("success", true);
				response.put("data", data);
			} else {
				response.put("msg", "Payout failed.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/deleteOfficepayout")
	public ResponseEntity<?> deleteOfficePayout(@RequestParam("payoutId") Long payoutId) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			Boolean isSuccess = expenseService.deleteOfficePayout(payoutId);

			if (isSuccess) {
				response.put("msg", "Payout deleted done.");
				response.put("success", true);
			} else {
				response.put("msg", "Payout deleted failed.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getAllTripExpense")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllTripExpense(@RequestParam Long userId) {
		Map<String, Object> response = new HashMap<>();

		try {
			List<ExpenseResponseDto> expenseResponseDto = expenseService.getAllTripExpense(userId);

			if (expenseResponseDto != null) {
				response.put("data", expenseResponseDto);
				response.put("msg", "Expense get successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Expense is not get.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getAllOfficeExpense")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllOfficeExpense(@RequestParam Long userId) {
		Map<String, Object> response = new HashMap<>();

		try {
			List<OfficeExpenseResponseDto> officeExpenseResponseDtos = expenseService.getAllOfficeExpense(userId);

			if (officeExpenseResponseDtos != null) {
				response.put("data", officeExpenseResponseDtos);
				response.put("msg", "Expense get successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Expense is not get.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getTripExpenseByExpenseId")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getByExpenseId(@RequestParam Long expenseId) {
		Map<String, Object> response = new HashMap<>();

		try {
			ExpenseResponseDto expenseResponseDto = expenseService.getByExpenseId(expenseId);

			if (expenseResponseDto != null) {
				response.put("data", expenseResponseDto);
				response.put("msg", "Expense get successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Expense is not get.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getOfficeExpenseByExpenseId")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getOfficeExpenseByExpenseId(@RequestParam Long expenseId) {
		Map<String, Object> response = new HashMap<>();

		try {
			OfficeExpenseResponseDto officeExpenseResponseDto = expenseService.getOfficeExpenseByExpenseId(expenseId);

			if (officeExpenseResponseDto != null) {
				response.put("data", officeExpenseResponseDto);
				response.put("msg", "Expense get successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Expense is not get.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getTripExpenseByExpenseType")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getTripExpenseByExpenseType(@RequestParam String expenseType, 
			@RequestParam Long userId, @RequestParam String startDate, @RequestParam String endDate,
			@RequestParam Long accountContactId) {
		Map<String, Object> response = new HashMap<>();

		try {
			Map<String, Object> expenseResponseDtos = expenseService.getTripExpenseByExpenseType(expenseType, userId,
					startDate, endDate, accountContactId);

			if (expenseResponseDtos != null) {
				response.put("data", expenseResponseDtos);
				response.put("msg", "Expense get successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Expense is not get.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getOfficeExpenseByExpenseType")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getOfficeExpenseByExpenseType(
			@RequestParam("incomeExpenseTypeId") Long incomeExpenseTypeId,
			@RequestParam Long userId, 
			@RequestParam String startDate, 
			@RequestParam String endDate,
			@RequestParam("accountContactId") Long accountContactId) {
		Map<String, Object> response = new HashMap<>();

		try {
			Map<String, Object> officeExpenseResponseDtos = expenseService
					.getOfficeExpenseByExpenseType(incomeExpenseTypeId, userId, startDate, endDate, accountContactId);

			if (officeExpenseResponseDtos != null) {
				response.put("data", officeExpenseResponseDtos);
				response.put("msg", "Expense get successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Expense is not get.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/addCloseDate")
	public ResponseEntity<?> addCloseDate(
			@RequestParam("startDate") String startDate, 
			@RequestParam("closeDate") String closeDate,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();

		try {
			Map<String, Object> res = expenseService.addOrUpdateCloseDate(startDate, closeDate, userId);

			if (res != null) {
				response.put("msg", "Date Added Successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Failed to add date.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping
	public ResponseEntity<?> getCloseDate(
			@RequestParam("month") Integer month, 
			@RequestParam("year") Integer year,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();

		try {
			Map<String, Object> date = expenseService.getCloseDate(month, year, userId);

			if (date != null) {
				response.put("closeDate", date);
				response.put("msg", "Date Found Successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Date Found Failed.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getAllExpenses")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllExpenses(
	        @RequestParam Long userId,
	        @RequestParam String startDate,
	        @RequestParam String endDate,
	        @RequestParam Long accountContactId) {

	    Map<String, Object> response = new HashMap<>();

	    try {
	        AllExpensesResponseDto dto =
	                expenseService.getAllExpenses(userId, startDate, endDate, accountContactId);

	        response.put("data", dto);
	        response.put("msg", "All expenses fetched successfully.");
	        response.put("success", true);

	        return new ResponseEntity<>(response, HttpStatus.OK);

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("success", false);
	        response.put("msg", e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@DeleteMapping("/deletefile")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteOfficeExpenseFile(@RequestParam("id") Long id){
		Map<String, Object> response = new HashMap<>();

		try {
			Boolean isDelete = expenseService.deleteOfficeExpenseFile(id);

			if (isDelete) {
				response.put("msg", "File delete successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "File delete failed.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			e.printStackTrace();
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