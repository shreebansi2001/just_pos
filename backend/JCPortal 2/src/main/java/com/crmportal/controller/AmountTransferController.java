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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.AmountTransferRequestDto;
import com.crmportal.response.dto.AmountTransferResponseDto;
import com.crmportal.service.AmountTransferService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping(value = "/v1/api/amount-transfer")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class AmountTransferController {

	@Autowired
	AmountTransferService amountTransferService;

	@PostMapping("/add-update")
	public ResponseEntity<?> addUpdateTransferAmount(@RequestBody AmountTransferRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {

			AmountTransferResponseDto res = amountTransferService.addUpdateAmountTransfer(request);

			if (res == null) {
				response.put("success", false);
				response.put("msg", ConstantsPoc.AMOUNT_TRANSFER_FAIL);
			} else {
				response.put("success", true);
				response.put("msg", ConstantsPoc.AMOUNT_TRANSFER_SUCCESS);
			}

			return ResponseEntity.status(HttpStatus.OK).body(response);
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

	@GetMapping("/getall")
	public ResponseEntity<?> getAllTransfer(@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {

			List<AmountTransferResponseDto> res = amountTransferService.getAllTransfer(startDate, endDate, userId);

			if (res == null) {
				response.put("success", false);
				response.put("msg", ConstantsPoc.TRANSFER_RECORD_FOUND_FAIL);
			} else {
				response.put("data", res);
				response.put("success", true);
				response.put("msg", ConstantsPoc.TRANSFER_RECORD_FOUND_SUCCESS);
			}

			return ResponseEntity.status(HttpStatus.OK).body(response);
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
	public ResponseEntity<?> getTransferById(@RequestParam("id") Long id, @RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {

			AmountTransferResponseDto res = amountTransferService.getTransferById(id, userId);

			if (res == null) {
				response.put("success", false);
				response.put("msg", ConstantsPoc.TRANSFER_RECORD_FOUND_FAIL);
			} else {
				response.put("data", res);
				response.put("success", true);
				response.put("msg", ConstantsPoc.TRANSFER_RECORD_FOUND_SUCCESS);
			}

			return ResponseEntity.status(HttpStatus.OK).body(response);
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
	
	@DeleteMapping("/deletebyid")
	public ResponseEntity<?> deleteTransferById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {

			Boolean isSuccess = amountTransferService.deleteTransferById(id);

			if (!isSuccess) {
				response.put("success", false);
				response.put("msg", ConstantsPoc.TRANSFER_RECORD_DELETE_FAIL);
			} else {
				response.put("success", true);
				response.put("msg", ConstantsPoc.TRANSFER_RECORD_DELETE_SUCCESS);
			}

			return ResponseEntity.status(HttpStatus.OK).body(response);
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
