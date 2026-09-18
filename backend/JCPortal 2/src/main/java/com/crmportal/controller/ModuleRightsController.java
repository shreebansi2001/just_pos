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

import com.crmportal.request.dto.UserModuleRightsRequestDto;
import com.crmportal.response.dto.UserModuleRightsResponseDto;
import com.crmportal.service.UserModuleRightsService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping("/v1/api/modulerights")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class ModuleRightsController {

	@Autowired
	UserModuleRightsService userModuleRightsService;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addModuleRights(@Valid @RequestBody UserModuleRightsRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			UserModuleRightsResponseDto responseDto = userModuleRightsService.addOrUpdateModuleRights(request,
					Long.valueOf("-1"));
			if (responseDto == null) {
				response.put("msg", "User Module Rights Created Failed");
				response.put("success", false);
			} else {
				response.put("msg", "User Module Rights Created Successfully");
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
	public ResponseEntity<Map<String, Object>> updateModuleRights(
			@Valid @RequestBody UserModuleRightsRequestDto request, @RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			UserModuleRightsResponseDto responseDto = userModuleRightsService.addOrUpdateModuleRights(request, id);
			if (responseDto == null) {
				response.put("msg", "User Module Rights Updated Failed");
				response.put("success", false);
			} else {
				response.put("msg", "User Module Rights Updated Successfully");
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
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllModuleRights() {
		Map<String, Object> response = new HashMap<>();
		try {
			List<UserModuleRightsResponseDto> responseDto = userModuleRightsService.getAllModuleRights();
			if (responseDto.isEmpty()) {
				response.put("msg", "User Module Rights Ftech Failed");
				response.put("success", false);
			} else {
				response.put("data", responseDto);
				response.put("msg", "User Module Rights Fetch Successfully");
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
	public ResponseEntity<Map<String, Object>> getModuleRightsByid(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			UserModuleRightsResponseDto responseDto = userModuleRightsService.getModuleRightsById(id);
			if (responseDto == null) {
				response.put("msg", "User Module Rights Ftech Failed");
				response.put("success", false);
			} else {
				response.put("data", responseDto);
				response.put("msg", "User Module Rights Fetch Successfully");
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
	public ResponseEntity<Map<String, Object>> deleteModuleRightsById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			Boolean isSuccess = userModuleRightsService.deleteModuleRightsById(id);
			if (isSuccess) {
				response.put("msg", "Module Right Delete Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "Module Right Deleted Failed");
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
