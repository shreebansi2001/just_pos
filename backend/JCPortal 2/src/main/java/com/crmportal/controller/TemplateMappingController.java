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

import com.crmportal.request.dto.TemplateMappingRequestDto;
import com.crmportal.response.dto.TemplateMappingResponseDto;
import com.crmportal.service.TemplateMappingService;

@RestController
@RequestMapping({ "/v1/api/templatemapping" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class TemplateMappingController {

	@Autowired
	TemplateMappingService templateMappingService;
	
	@PostMapping("/addorupdate")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdateTemplateMapping(@RequestBody TemplateMappingRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			TemplateMappingResponseDto responseDto = templateMappingService.addOrUpdateTemplateMapping(request, request.getId());
			if(responseDto == null) {
				response.put("success", false);
				response.put("msg", "Template Mapping is not created/updated.");
			} else {
				response.put("success", true);
				response.put("msg", "Template Mapping created/updated successfully.");
				response.put("data", responseDto);
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
	public ResponseEntity<Map<String, Object>> deleteTemplateMappingById(@RequestParam Long id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = templateMappingService.deleteTemplateMappingById(id);
			if(!isSuccess) {
				response.put("success", false);
				response.put("msg", "Template Mapping is not deleted.");
			} else {
				response.put("success", true);
				response.put("msg", "Template Mapping deleted successfully.");
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
	public ResponseEntity<Map<String, Object>> getTemplateMappingById(@RequestParam Long id) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			TemplateMappingResponseDto responseDto = templateMappingService.getTemplateMappingById(id);
			if(responseDto == null) {
				response.put("success", false);
				response.put("msg", "Template Mapping is not found.");
			} else {
				response.put("success", true);
				response.put("msg", "Template Mapping found successfully.");
				response.put("data", responseDto);
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
	public ResponseEntity<Map<String, Object>> getAllTemplateMapping(@RequestParam(value = "template_module_id", required = false) Long templateModuleId) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			List<TemplateMappingResponseDto> responseDtos = templateMappingService.getAllTemplateMapping(templateModuleId);
			if(responseDtos == null || responseDtos.isEmpty()) {
				response.put("success", false);
				response.put("msg", "Template Mapping is not found.");
			} else {
				response.put("success", true);
				response.put("msg", "Template Mapping found successfully.");
				response.put("data", responseDtos);
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
