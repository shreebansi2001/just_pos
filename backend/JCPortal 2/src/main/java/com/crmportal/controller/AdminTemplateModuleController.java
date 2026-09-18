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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.AdminTemplateModuleFontAndFontSizeRequestDto;
import com.crmportal.request.dto.AdminTemplateModuleRequestDTO;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;
import com.crmportal.service.AdminTemplateModuleService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/admintemplatemodule" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class AdminTemplateModuleController {

	@Autowired
	AdminTemplateModuleService adminTemplateModuleService;
	
	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<?> addAdminTemplateModule(@Valid @RequestBody List<AdminTemplateModuleRequestDTO> request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean res = adminTemplateModuleService.addOrUpdateAdminTemplateModule(request, Long.parseLong("-1"));
			
			if(res == null) {
				response.put("msg", ConstantsPoc.ADMIN_TEMPLATE_MODULE_CREATE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.ADMIN_TEMPLATE_MODULE_CREATE_SUCCESS);
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
	
	@PutMapping("/update")
	@ResponseBody
	public ResponseEntity<?> updateAdminTemplateModule(@Valid @RequestBody List<AdminTemplateModuleRequestDTO> request, @RequestParam("adminTemplateid") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean res = adminTemplateModuleService.addOrUpdateAdminTemplateModule(request, id);
			
			if(res == null) {
				response.put("msg", ConstantsPoc.ADMIN_TEMPLATE_MODULE_UPDATE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.ADMIN_TEMPLATE_MODULE_UPDATE_SUCCESS);
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
	
	@GetMapping("/getall")
	public ResponseEntity<?> getAllAdminTemplateModule(@RequestParam(value = "userId", required = true) Long userId,
			@RequestParam(value = "templateModuleId", required = false) Long templateModuleId,
			@RequestParam(value = "isExclusive", required = false) Boolean isExclusive) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<AdminTemplateModuleResponseDto> res = adminTemplateModuleService.getAllAdminTemplateModule(userId,templateModuleId, isExclusive);
			
			if(res == null) {
				response.put("msg", ConstantsPoc.ADMIN_TEMPLATE_MODULE_FOUND_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.ADMIN_TEMPLATE_MODULE_FOUND_SUCCESS);
				response.put("success", true);
				response.put("data", res);
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
	public ResponseEntity<?> getAdminTemplateModuleById(@RequestParam("adminTemplateModuleid") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			AdminTemplateModuleResponseDto res = adminTemplateModuleService.getAdminTemplateModuleById(id);
			
			if(res == null) {
				response.put("msg", ConstantsPoc.ADMIN_TEMPLATE_MODULE_FOUND_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.ADMIN_TEMPLATE_MODULE_FOUND_SUCCESS);
				response.put("success", true);
				response.put("data", res);
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
	
	@PutMapping("/updatestatusbyid")
	public ResponseEntity<?> updateAdminTemplateModuleStatusById(@RequestParam("adminTemplateModuleid") Long id, @RequestParam("status") Boolean status) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = adminTemplateModuleService.updateAdminTemplateModuleStatusById(id, status);
			
			if(!isSuccess) {
				response.put("msg", ConstantsPoc.ADMIN_TEMPLATE_MODULE_UPDATE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.ADMIN_TEMPLATE_MODULE_UPDATE_SUCCESS);
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
	
	@PutMapping("/updatefontandfontsizebyid")
	public ResponseEntity<?> updateAdminTemplateModuleFontAndFontSizeById(@Valid @RequestBody List<AdminTemplateModuleFontAndFontSizeRequestDto> request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = adminTemplateModuleService.updateAdminTemplateModuleFontAndFontSizeById(request);
			
			if(!isSuccess) {
				response.put("msg", ConstantsPoc.ADMIN_TEMPLATE_MODULE_UPDATE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.ADMIN_TEMPLATE_MODULE_UPDATE_SUCCESS);
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
	public ResponseEntity<?> deleteAdminTemplateModuleById(@RequestParam("adminTemplateModuleid") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = adminTemplateModuleService.deleteAdminTemplateModuleById(id);
			
			if(!isSuccess) {
				response.put("msg", ConstantsPoc.ADMIN_TEMPLATE_MODULE_DELETE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.ADMIN_TEMPLATE_MODULE_DELETE_SUCCESS);
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
	
}
