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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.repository.TemplateModuleMasterRepository;
import com.crmportal.request.dto.TemplateModuleMasterRequestDto;
import com.crmportal.response.dto.TemplateModuleMasterResponseDto;
import com.crmportal.service.TemplateModuleMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({"/v1/api/templatemodulemaster"})
@CrossOrigin(
		   origins = {"*"},
		   maxAge = 3600L
		)
public class TemplateModuleMasterController {

	@Autowired
    TemplateModuleMasterRepository templateModuleMasterRepository;

	@Autowired
	TemplateModuleMasterService templateModuleMasterService;
	
	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<?> addTemplateModuleMaster(@RequestBody TemplateModuleMasterRequestDto request)  {
		Map<String, Object> response = new HashMap<>();
		try {
			TemplateModuleMasterResponseDto responseDto = templateModuleMasterService.addOrUpdateTemplateModuleMaster(request, Long.parseLong("-1"));
			
			if(responseDto == null) {
				response.put("msg", ConstantsPoc.TEMPLATE_MODULE_CREATE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.TEMPLATE_MODULE_CREATE_SUCCESS);
				response.put("success", true);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		}catch(RuntimeException re) {
			response.put("success", false);
			response.put("msg", re.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/update")
	@ResponseBody
	public ResponseEntity<?> updateTemplateModuleMaster(@RequestBody TemplateModuleMasterRequestDto request,@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			TemplateModuleMasterResponseDto responseDto = templateModuleMasterService.addOrUpdateTemplateModuleMaster(request, id);
			
			if(responseDto == null) {
				response.put("msg", ConstantsPoc.TEMPLATE_MODULE_UPDATE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.TEMPLATE_MODULE_UPDATE_SUCCESS);
				response.put("success", true);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		}catch(RuntimeException re) {
			response.put("success", false);
			response.put("msg", re.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getbyid")
	@ResponseBody
	public ResponseEntity<?> getTemplateModuleMasterById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			TemplateModuleMasterResponseDto responseDto = templateModuleMasterService.getTemplateModuleById(id);
			
			if(responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", ConstantsPoc.TEMPLATE_MODULE_FOUND_SUCCESS);
				response.put("success", true);
			}else {
				response.put("msg", ConstantsPoc.TEMPLATE_MODULE_FOUND_FAIL);
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		}catch(RuntimeException re) {
			response.put("success", false);
			response.put("msg", re.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getall")
	@ResponseBody
	public ResponseEntity<?> getAllTemplateModuleMaster() {
		Map<String, Object> response = new HashMap<>();
		try {
			List<TemplateModuleMasterResponseDto> responseDto = templateModuleMasterService.getAllTemplateModuleMaster();
			response.put("data", responseDto);
			response.put("msg", ConstantsPoc.TEMPLATE_MODULE_FOUND_SUCCESS);
			response.put("success", true);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}catch(RuntimeException re) {
			response.put("success", false);
			response.put("msg", re.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/deletebyid")
	@ResponseBody
	public ResponseEntity<?> deleteTemplateModuleById(@RequestParam("id") Long id) {
		Boolean isSuccess = false;
		Map<String, Object> response = new HashMap<>();
		try {
			isSuccess = templateModuleMasterService.deleteTemplateModuleById(id);
			if(isSuccess) {
				response.put("msg", ConstantsPoc.TEMPLATE_MODULE_DELETE_SUCCESS);
				response.put("success", true);
			}else {
				response.put("msg", ConstantsPoc.TEMPLATE_MODULE_DELETE_FAIL);
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
	
	@PutMapping("/updatestatus")
	@ResponseBody
	public ResponseEntity<?> updateTemplateModuleActiveStatusById(@RequestParam("id") Long id, @RequestParam("status") Boolean status) {
		Boolean isUpdated = false;
		Map<String, Object> response = new HashMap<>();
		try {
			isUpdated = templateModuleMasterService.updateTemplateModuleStatusById(id, status);
			
			if(isUpdated) {
				response.put("msg", ConstantsPoc.TEMPLATE_MODULE_UPDATE_SUCCESS);
				response.put("success", true);
			}else {
				response.put("msg", ConstantsPoc.TEMPLATE_MODULE_UPDATE_FAIL);
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
}
