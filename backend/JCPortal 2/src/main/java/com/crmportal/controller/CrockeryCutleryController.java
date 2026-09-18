package com.crmportal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.CrockeryCutleryRequestDto;
import com.crmportal.request.dto.LeadMasterResponseDto;
import com.crmportal.response.dto.CrockeryCutleryResponseDto;
import com.crmportal.service.CrockeryCutleryService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/crockerycutlery" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class CrockeryCutleryController {

	@Autowired
	CrockeryCutleryService crockeryCutleryService;
	
	@PostMapping("/addupdatecrockerycutlery")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addUpdateCrockeryCutlery(@RequestBody List<CrockeryCutleryRequestDto> crockeryCutleries) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<CrockeryCutleryResponseDto> responseDtos = crockeryCutleryService.addUpdateCrockeryCutlery(crockeryCutleries);
			
			if(responseDtos == null) {
				response.put("success", false);
				response.put("msg", "Data is not updated.");
			} else {
				Map<String, Object> responseDto = new HashMap<>();
				response.put("data", responseDtos);
				response.put("success", true);
				response.put("msg", "Data is updated.");
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
	
	@GetMapping("/getByRawMaterialCat")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getByRawMaterialCatId(@RequestParam("rawMaterialCatId") Long rawMaterialCatId,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<CrockeryCutleryResponseDto> responseDtos = crockeryCutleryService.getByRawMaterialCatId(rawMaterialCatId, userId);
			
			if(responseDtos == null) {
				response.put("success", false);
				response.put("msg", "Data is not found.");
			} else {
				Map<String, Object> responseDto = new HashMap<>();
				response.put("data", responseDtos);
				response.put("success", true);
				response.put("msg", "Data is found.");
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
