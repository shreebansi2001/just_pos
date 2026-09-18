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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.DefaultExtraQuotationFunctionRequestDto;
import com.crmportal.response.dto.DefaultExtraQuotationFunctionResponseDto;
import com.crmportal.service.DefaultExtraQuotationFunctionService;

@RestController
@RequestMapping(value = { "/v1/api/extraquotationfunction" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class DefaultExtraQuotationFunctionController {

	@Autowired
	DefaultExtraQuotationFunctionService defaultExtraQuotationFunctionService;

	@PostMapping("/addorupdate")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdate(
			@RequestBody DefaultExtraQuotationFunctionRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = defaultExtraQuotationFunctionService.addOrUpdate(request);
			if (isSuccess) {
				response.put("msg", "Extra Quotation Function Added/Updated Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "Extra Quotation Function Added/Updated Failed");
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

	@GetMapping("getall")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAll(@RequestParam("userId") Long userId,
			@RequestParam(value = "isActive", required = false) Boolean isActive) {
		Map<String, Object> response = new HashMap<>();
		try {

			List<DefaultExtraQuotationFunctionResponseDto> dtos = defaultExtraQuotationFunctionService.getAll(userId,
					isActive);

			if (dtos.isEmpty()) {
				response.put("msg", "Data Not Found");
				response.put("success", false);
			} else {
				Map<String, Object> resp = new HashMap<>();
				resp.put("ExtraQuotationFunctions", dtos);
				response.put("data", resp);
				response.put("msg", "Data Found Successfully");
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

	@DeleteMapping("/deletebyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = defaultExtraQuotationFunctionService.deleteById(id);
			if (isSuccess) {
				response.put("msg", "Extra Quotation Function Delete Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "Extra Quotation Function Delete Failed");
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
}
