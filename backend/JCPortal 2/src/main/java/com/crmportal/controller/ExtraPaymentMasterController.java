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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.ExtraPaymentRequestDto;
import com.crmportal.response.dto.ExtraPaymentMasterResponseDto;
import com.crmportal.service.ExtraPaymentMasterService;

@RestController
@RequestMapping({ "/v1/api/extrapayment" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class ExtraPaymentMasterController {

	@Autowired
	ExtraPaymentMasterService extraPaymentMasterService;

	@PostMapping("add-update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdate(@Valid ExtraPaymentRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = extraPaymentMasterService.addOrUpdate(request);
			if (isSuccess) {
				response.put("succes", isSuccess);
				response.put("msg", "Extra Payment Created Successfully");
			} else {
				response.put("succes", isSuccess);
				response.put("msg", "Extra Payment Created Failed");
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

	@GetMapping("/getall")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAll() {
		Map<String, Object> response = new HashMap<>();
		try {
			List<ExtraPaymentMasterResponseDto> dtos = extraPaymentMasterService.getAll();
			if (!dtos.isEmpty()) {
				Map<String, Object> extraPayResp = new HashMap<>();
				extraPayResp.put("Extra Payment Details", dtos);
				response.put("data", extraPayResp);
				response.put("msg", "Extra Payment Found Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Extra Payment Found Failed");
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

	@GetMapping("/getbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			ExtraPaymentMasterResponseDto dto = extraPaymentMasterService.getById(id);
			if (dto != null) {
				Map<String, Object> extraPayResp = new HashMap<>();
				extraPayResp.put("Extra Payment Details", dto);
				response.put("data", extraPayResp);
				response.put("msg", "Extra Payment Found Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Extra Payment Found Failed");
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

	@DeleteMapping("/deletebyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = extraPaymentMasterService.deleteById(id);
			if (isSuccess) {
				response.put("success", isSuccess);
				response.put("msg", "Extra Payment Deleted Successfully");
			} else {
				response.put("success", isSuccess);
				response.put("msg", "Extra Payment Deleted Failed");
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
