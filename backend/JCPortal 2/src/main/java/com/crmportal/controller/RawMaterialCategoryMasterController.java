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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.RawMaterialCategoryMasterRequestDto;
import com.crmportal.response.dto.RawMaterialCategoryMasterResponseDto;
import com.crmportal.response.dto.RawMaterialCategoryTypeMasterResponseDto;
import com.crmportal.service.RawMaterialCategoryMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/rawmaterialcategory" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class RawMaterialCategoryMasterController {

	@Autowired
	RawMaterialCategoryMasterService rawMaterialCategoryMasterService;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addRawMaterialCategory(
			@Valid @RequestBody RawMaterialCategoryMasterRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			RawMaterialCategoryMasterResponseDto responseDto = rawMaterialCategoryMasterService
					.addOrUpdateRawMaterialCategory(request, Long.valueOf("-1"));
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_CREATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_CREATE_FAIL);
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
	public ResponseEntity<Map<String, Object>> updateRawMaterialCategory(
			@Valid @RequestBody RawMaterialCategoryMasterRequestDto request, @RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			RawMaterialCategoryMasterResponseDto responseDto = rawMaterialCategoryMasterService
					.addOrUpdateRawMaterialCategory(request, id);
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_UPDATE_FAIL);
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
	public ResponseEntity<Map<String, Object>> getAllRawMaterialCategory(@RequestParam("userid") Long userId,@RequestParam("categoryTypeId") Long categoryTypeId,
			@RequestParam(value = "categoryName", required = false) String categoryName,
			@RequestParam(value = "isActive", required = false) Boolean isActive) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<RawMaterialCategoryMasterResponseDto> responseDtos = rawMaterialCategoryMasterService
					.getAllRawMaterialCategoryByUserId(userId,categoryTypeId, categoryName, isActive);
			if (responseDtos.isEmpty()) {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> rawMaterialCatResp = new HashMap<>();
				rawMaterialCatResp.put("Raw Material Category Details", responseDtos);
				response.put("data", rawMaterialCatResp);
				response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_FOUND);
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
	public ResponseEntity<Map<String, Object>> getRawMaterialCategoryById(@RequestParam("id") Long Id) {
		Map<String, Object> response = new HashMap<>();
		try {
			RawMaterialCategoryMasterResponseDto responseDto = rawMaterialCategoryMasterService
					.getRawMaterialCategoryById(Id);
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> rawMaterialCatResp = new HashMap<>();
				List<RawMaterialCategoryMasterResponseDto> responseDtos = new ArrayList<>();
				responseDtos.add(responseDto);
				rawMaterialCatResp.put("Raw Material Category Details", responseDtos);
				response.put("data", rawMaterialCatResp);
				response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_FOUND);
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

	
	@DeleteMapping("/delete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteRawMaterialCategoryById(@RequestParam("id") Long Id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = rawMaterialCategoryMasterService
					.deleteRawMaterialCategoryById(Id);
			if (!isSuccess) {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_DELETE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_DELETE_SUCCESSFULLY);
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
	    public ResponseEntity<Map<String, Object>> updateRawMaterialCategoryTypeStatus(
	            @RequestParam("id") Long id,
	            @RequestParam("isActive") Boolean isActive) {
	        Map<String, Object> response = new HashMap<>();
	        try {
	            boolean updated = rawMaterialCategoryMasterService.updateRawMaterialCategoryStatus(id, isActive);
	            if (updated) {
	                response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_UPDATE_SUCCESS);
	                response.put("success", true);
	            } else {
	                response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_UPDATE_FAIL);
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
	 
	 @GetMapping("/getbyeventid")
	 @ResponseBody
	 public ResponseEntity<Map<String, Object>> getRawmaterialCategoryByEventId(@RequestParam("eventId") Long eventId){
		 Map<String, Object> response = new HashMap<>();
			try {
				List<RawMaterialCategoryMasterResponseDto> responseDtos = rawMaterialCategoryMasterService
						.getRawmaterialCategoryByEventId(eventId);
				if (responseDtos.isEmpty()) {
					response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_NOT_FOUND);
					response.put("success", false);
				} else {
					Map<String, Object> rawMaterialCatResp = new HashMap<>();
					rawMaterialCatResp.put("Raw Material Category Details", responseDtos);
					response.put("data", rawMaterialCatResp);
					response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_FOUND);
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
	 
	 @GetMapping("/getbyrawmaterialcategorytypeid")
	 @ResponseBody
	 public ResponseEntity<Map<String, Object>> getRawmaterialCategoryByTypeId(@RequestParam("rawMaterialCategoryTypeId") Long rawMaterialCategoryTypeId,@RequestParam("userId") Long userId){
		 Map<String, Object> response = new HashMap<>();
			try {
				List<RawMaterialCategoryMasterResponseDto> responseDtos = rawMaterialCategoryMasterService
						.getRawmaterialCategoryByTypeId(rawMaterialCategoryTypeId,userId);
				if (responseDtos.isEmpty()) {
					response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_NOT_FOUND);
					response.put("success", false);
				} else {
					Map<String, Object> rawMaterialCatResp = new HashMap<>();
					rawMaterialCatResp.put("Raw Material Category Details", responseDtos);
					response.put("data", rawMaterialCatResp);
					response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_FOUND);
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
