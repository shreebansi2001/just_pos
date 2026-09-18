package com.crmportal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.attribute.HashAttributeSet;
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

import com.crmportal.request.dto.UnitMasterRequestDto;
import com.crmportal.response.dto.UnitHierarchyDto;
import com.crmportal.response.dto.UnitMasterResponseDto;
import com.crmportal.service.UnitMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/unit" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class UnitMasterController {

	@Autowired
	UnitMasterService unitMasterService;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addUnitMaster(@Valid @RequestBody UnitMasterRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			UnitMasterResponseDto responseDto = unitMasterService.addOrUpdateUnitMaster(request, Long.parseLong("-1"));
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.UNIT_MASTER_CREATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.UNIT_MASTER_CREATE_FAIL);
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
	public ResponseEntity<Map<String, Object>> updateUnitMaster(@Valid @RequestBody UnitMasterRequestDto request,
			@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			UnitMasterResponseDto responseDto = unitMasterService.addOrUpdateUnitMaster(request, id);
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.UNIT_MASTER_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.UNIT_MASTER_UPDATE_FAIL);
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
	public ResponseEntity<Map<String, Object>> getAllByUserId(@RequestParam("userid") Long userid,
			@RequestParam(value = "unitName", required = false) String unitName,
			@RequestParam(value = "isActive", required = false) Boolean isActive) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<UnitMasterResponseDto> responseDtos = unitMasterService.getAllByUserId(userid, unitName, isActive);
			if (responseDtos.isEmpty()) {
				response.put("msg", ConstantsPoc.UNIT_MASTER_FOUND_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> unitResp = new HashMap<>();
				unitResp.put("Unit Details", responseDtos);
				response.put("data", unitResp);
				response.put("msg", ConstantsPoc.UNIT_MASTER_FOUND_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> getById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			UnitMasterResponseDto responseDto = unitMasterService.getById(id);
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.UNIT_MASTER_FOUND_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> unitResp = new HashMap<>();
				List<UnitMasterResponseDto> responseDtos = new ArrayList<>();
				responseDtos.add(responseDto);
				unitResp.put("Unit Details", responseDtos);
				response.put("data", unitResp);
				response.put("msg", ConstantsPoc.UNIT_MASTER_FOUND_SUCCESS);
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
		Boolean isSuccess = false;
		Map<String, Object> response = new HashMap<>();
		try {
			isSuccess = unitMasterService.deleteById(id);
			if (isSuccess) {
				response.put("msg", ConstantsPoc.UNIT_MASTER_DELETE_SUCCESS);
				response.put("success", isSuccess);
			} else {
				response.put("msg", ConstantsPoc.UNIT_MASTER_DELETE_FAIL);
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
	
	
	@PutMapping("/updatestatusbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateStatusById(@RequestParam("id") Long id,@RequestParam("isActive") Boolean isActive) {
		Boolean isSuccess = false;
		Map<String, Object> response = new HashMap<>();
		try {
			isSuccess = unitMasterService.updateStatusById(id,isActive);
			if (isSuccess) {
				response.put("msg", ConstantsPoc.UNIT_MASTER_UPDATE_SUCCESS);
				response.put("success", isSuccess);
			} else {
				response.put("msg", ConstantsPoc.UNIT_MASTER_UPDATE_FAIL);
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
	
	
	@GetMapping("/getparentwithchildren")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getParentUnitsWithChildren(@RequestParam("parentId") Long parentId) {
		Map<String, Object> response = new HashMap<>();
		try {
			UnitHierarchyDto responseDtos = unitMasterService.getParentUnitsWithChildren(parentId);
			if (responseDtos == null) {
				response.put("msg", ConstantsPoc.UNIT_MASTER_FOUND_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> unitResp = new HashMap<>();
				unitResp.put("Unit Details", responseDtos);
				response.put("data", unitResp);
				response.put("msg", ConstantsPoc.UNIT_MASTER_FOUND_SUCCESS);
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
