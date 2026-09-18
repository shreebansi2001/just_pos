package com.crmportal.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.service.RefundDetailsService;

@RestController
@RequestMapping({"v1/api/refunddetails"})
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class RefundDetailsController {

	@Autowired
	RefundDetailsService refundDetailsService;
	
	@DeleteMapping("/deleterefunddetails")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteRefundDetails(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		
		try {
			isSuccess = refundDetailsService.deleteRefundDetails(id); 
			if(isSuccess) {
				response.put("success", false);
				response.put("msg", "Refund Details Deleted Successfully.");
			} else {
				response.put("success", false);
				response.put("msg", "Refund Details Deletation Faild.");
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
