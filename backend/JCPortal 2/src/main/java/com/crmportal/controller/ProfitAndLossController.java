package com.crmportal.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.response.dto.ProfitAndLossResponseDto;
import com.crmportal.service.ProfitAndLossService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping("/v1/api/profit-and-loss")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class ProfitAndLossController {

	@Autowired
	ProfitAndLossService profitAndLossService;
	
	@GetMapping("/get")
	 public ResponseEntity<?> getProfitAndLossData(@RequestParam(value = "startDate") String startDate, 
				@RequestParam(value = "endDate") String endDate,
				@RequestParam(value = "userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			ProfitAndLossResponseDto res = profitAndLossService.getProfitAndLossData(startDate, endDate, userId);
	
			if (res == null) {
				response.put("msg", ConstantsPoc.PNL_FOUND_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.PNL_FOUND_SUCCESS);
				response.put("success", true);
				response.put("data", res);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (RuntimeException e) {
			e.printStackTrace();
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
