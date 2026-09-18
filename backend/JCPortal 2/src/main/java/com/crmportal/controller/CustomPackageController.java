package com.crmportal.controller;

import java.util.ArrayList;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.entity.CustomPackageEntity;
import com.crmportal.request.dto.CustomPackageReportRequestDto;
import com.crmportal.request.dto.CustomPackageRequestDto;
import com.crmportal.response.dto.CustomPackageResponseDto;
import com.crmportal.service.CustomPackageService;
import com.crmportal.utility.ConstantsPoc;

import java.io.File;
import java.nio.file.Files;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;

@RestController
@RequestMapping({ "/v1/api/custompackage" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class CustomPackageController {

	@Autowired
	CustomPackageService customPackageService;
	
	@Autowired
	Environment environment;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addCustomPackage(@Valid @RequestBody CustomPackageRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			CustomPackageResponseDto responseDto = customPackageService.addOrUpdateCustomPackage(request,
					Long.valueOf(-1));
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.PACKAGES_CREATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.PACKAGES_CREATE_FAIL);
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

	@PutMapping("/update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateCustomPackage(@Valid @RequestBody CustomPackageRequestDto request,
			@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			CustomPackageResponseDto responseDto = customPackageService.addOrUpdateCustomPackage(request, id);
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.PACKAGES_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.PACKAGES_UPDATE_FAIL);
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

	@GetMapping("/getallbyuserid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllCustomPackageByUserId(@RequestParam("userid") Long userId,
			@RequestParam(value = "packageName", required = false) String packageName,
			@RequestParam(value = "isActive", required = false) Boolean isActive) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<CustomPackageResponseDto> responseDtos = customPackageService.getAllCustomPackageByUserId(userId,
					packageName, isActive);
			if (responseDtos.isEmpty()) {
				response.put("msg", ConstantsPoc.PACKAGES_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> customPackageResp = new HashMap<>();
				customPackageResp.put("Package Details", responseDtos);
				response.put("data", customPackageResp);
				response.put("msg", ConstantsPoc.PACKAGES_FOUND);
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

	@GetMapping("/getbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getCustomPackageById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			CustomPackageResponseDto responseDto = customPackageService.getCustomPackageById(id);
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.PACKAGES_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> customPackageResp = new HashMap<>();
				List<CustomPackageResponseDto> dtos = new ArrayList<>();
				dtos.add(responseDto);
				customPackageResp.put("Package Details", dtos);
				response.put("data", customPackageResp);
				response.put("msg", ConstantsPoc.PACKAGES_FOUND);
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
		Boolean isSuccess = false;
		try {
			isSuccess = customPackageService.deleteById(id);
			if (!isSuccess) {
				response.put("msg", ConstantsPoc.PACKAGES_DELETE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.PACKAGES_DELETE_SUCCESS);
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

	@PutMapping("/updatestatus")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateStatus(@RequestParam("id") Long id,
			@RequestParam("isActive") Boolean isActive) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = customPackageService.updateStatus(id, isActive);
			if (isSuccess) {
				response.put("msg", ConstantsPoc.PACKAGES_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.PACKAGES_UPDATE_FAIL);
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
	
	@GetMapping("/report/pdf")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> generatePackageReport(
	        @ModelAttribute CustomPackageReportRequestDto request,
	        HttpServletRequest re) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        String fileUrl = customPackageService.generateCustomPackageReport(request,re);

	        response.put("success", true);
	        response.put("msg", "Report generated successfully");
	        response.put("fileUrl", fileUrl);

	        return ResponseEntity.ok(response);

	    } catch (RuntimeException e) {
	        response.put("success", false);
	        response.put("msg", e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("success", false);
	        response.put("msg", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
}
