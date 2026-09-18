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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.repository.IncomeExpenseTypeRepository;
import com.crmportal.request.dto.IncomeExpenseTypeRequestDto;
import com.crmportal.response.dto.IncomeExpenseTypeResponseDto;
import com.crmportal.service.IncomeExpenseTypeService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping(value = "/v1/api/income-expense-type")
@CrossOrigin(origins = { "*" }, maxAge = 3600l)
public class IncomeExpenseTypeController {

	@Autowired
	IncomeExpenseTypeService incomeExpenseTypeService;

	@PostMapping("/add-update")
	public ResponseEntity<?> addOrUpdateIncomeExpenseType(@Valid @RequestBody IncomeExpenseTypeRequestDto request) {
		Map<String, Object> response = new HashMap<>();

		try {
			IncomeExpenseTypeResponseDto res = incomeExpenseTypeService.addUpdateIncomeExpenseType(request);

			if (res == null) {
				response.put("msg", ConstantsPoc.INCOME_EXPENSE_TYPE_CREATE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.INCOME_EXPENSE_TYPE_CREATE_SUCCESS);
				response.put("success", true);
			}
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("msg", e.getLocalizedMessage());
			response.put("success", false);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("msg", e.getLocalizedMessage());
			response.put("success", false);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/getall")
	public ResponseEntity<?> getAllByUserId(@RequestParam("userId") Long userId, @RequestParam(value = "type", required = false) String type) {
		Map<String, Object> response = new HashMap<>();

		try {
			List<IncomeExpenseTypeResponseDto> res = incomeExpenseTypeService.getAllByUserId(userId, type);

			if (res == null) {
				response.put("msg", ConstantsPoc.INCOME_EXPENSE_TYPE_FOUND_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.INCOME_EXPENSE_TYPE_FOUND_SUCCESS);
				response.put("success", true);
				response.put("data", res);
			}
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("msg", e.getLocalizedMessage());
			response.put("success", false);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("msg", e.getLocalizedMessage());
			response.put("success", false);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	@GetMapping("/getbyid")
	public ResponseEntity<?> getById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			IncomeExpenseTypeResponseDto res = incomeExpenseTypeService.getById(id);

			if (res == null) {
				response.put("msg", ConstantsPoc.INCOME_EXPENSE_TYPE_FOUND_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.INCOME_EXPENSE_TYPE_FOUND_SUCCESS);
				response.put("success", true);
				response.put("data", res);
			}
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("msg", e.getLocalizedMessage());
			response.put("success", false);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("msg", e.getLocalizedMessage());
			response.put("success", false);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@DeleteMapping("/deletebyid")
	public ResponseEntity<?> deleteById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			Boolean isSuccess = incomeExpenseTypeService.deleteById(id);

			if (!isSuccess) {
				response.put("msg", ConstantsPoc.INCOME_EXPENSE_TYPE_DELETE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.INCOME_EXPENSE_TYPE_DELETE_SUCCESS);
				response.put("success", true);
			}
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("msg", e.getLocalizedMessage());
			response.put("success", false);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("msg", e.getLocalizedMessage());
			response.put("success", false);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

}
