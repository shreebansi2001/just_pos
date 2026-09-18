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

import com.crmportal.request.dto.ReportConfigurationRequestDto;
import com.crmportal.response.dto.ReportConfigurationResponseDto;
import com.crmportal.service.ReportConfigurationService;


@RestController
@RequestMapping({ "v1/api/report/configuration" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class ReportConfigurationController {

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@PostMapping("addorupdate")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdateReportConfiguration(@RequestBody ReportConfigurationRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			ReportConfigurationResponseDto responseDto = reportConfigurationService.addOrUpdateReportConfiguration(request);
			if(responseDto != null) {
				response.put("success", true);
				response.put("msg", "Configuration created successfully.");
				response.put("data", responseDto);
			} else {
				response.put("success", false);
				response.put("msg", "Configuration is not created.");
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
	
	@DeleteMapping("delete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdateReportConfiguration(@RequestParam Long id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = reportConfigurationService.deleteReportConfiguration(id);
			if(isSuccess) {
				response.put("success", true);
				response.put("msg", "Configuration deleted successfully.");
			} else {
				response.put("success", false);
				response.put("msg", "Configuration is not deleted.");
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
	
	@GetMapping("get")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllReportConfiguration(
	        @RequestParam(value = "mappingId",      required = false) Long mappingId,
	        @RequestParam(value = "moduleId",       required = false) Long moduleId,
	        @RequestParam(value = "isExtraCharges", required = false) Integer isExtraCharges) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        List<ReportConfigurationResponseDto> responseDtos =
	                reportConfigurationService.getAllReportConfiguration(mappingId, moduleId, isExtraCharges);

	        if (responseDtos != null && !responseDtos.isEmpty()) {
	            response.put("success", true);
	            response.put("msg",     "Configuration found successfully.");
	            response.put("data",    responseDtos);
	        } else {
	            response.put("success", false);
	            response.put("msg",     "Configuration is not found.");
	        }
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (RuntimeException e) {
	        response.put("success", false);
	        response.put("msg", e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("msg", e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	@GetMapping("getbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getReportConfigurationById(
	        @RequestParam Long id,
	        @RequestParam(value = "isExtraCharges", required = false) Integer isExtraCharges) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        ReportConfigurationResponseDto responseDto =
	                reportConfigurationService.getReportConfigurationById(id, isExtraCharges);

	        if (responseDto != null) {
	            response.put("success", true);
	            response.put("msg",     "Configuration found successfully.");
	            response.put("data",    responseDto);
	        } else {
	            response.put("success", false);
	            response.put("msg",     "Configuration is not found.");
	        }
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (RuntimeException e) {
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