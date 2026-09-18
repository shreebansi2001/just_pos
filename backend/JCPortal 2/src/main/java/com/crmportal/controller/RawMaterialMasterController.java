package com.crmportal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import com.crmportal.request.dto.RawMaterialMasterRequestDto;
import com.crmportal.request.dto.UpdateRawMaterialRequestDto;
import com.crmportal.response.dto.RawMaterialCategoryChangeResponseDto;
import com.crmportal.response.dto.RawMaterialCategoryMasterResponseDto;
import com.crmportal.response.dto.RawMaterialMasterResponseDto;
import com.crmportal.service.RawMaterialMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/rawmaterial" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class RawMaterialMasterController {

	@Autowired
	RawMaterialMasterService rawMaterialMasterService;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addRawMaterial(@Valid @ModelAttribute RawMaterialMasterRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			RawMaterialMasterResponseDto responseDto = rawMaterialMasterService.addOrUpdateRawMaterial(request,
					Long.parseLong("-1"));
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_CREATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_CREATE_FAIL);
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
	public ResponseEntity<Map<String, Object>> updateRawMaterial(
			@Valid @ModelAttribute RawMaterialMasterRequestDto request, @RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			RawMaterialMasterResponseDto responseDto = rawMaterialMasterService.addOrUpdateRawMaterial(request, id);
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_UPDATE_FAIL);
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
	public ResponseEntity<Map<String, Object>> getAllRawMaterialByUserId(@RequestParam("userid") Long userid,
			@RequestParam("rawMateriaCatlId") Long rawMateriaCatlId, @RequestParam("unitid") Long unitid,
			@RequestParam(value = "rawMaterialName", required = false) String rawMaterialName,
			@RequestParam(value = "isActive", required = false) Boolean isActive,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "isAsc", required = false) Boolean isAsc,
			@RequestParam(value = "isPurchaseApprove", required = false) Boolean isPurchaseApprove,
			@RequestParam(value = "purchaseApproveId", required = false) Long purchaseApproveId) {

		Map<String, Object> response = new HashMap<>();
		try {

			Map<String, Object> serviceResponse = rawMaterialMasterService.getAllRawMaterialByUserId(userid,
					rawMateriaCatlId, rawMaterialName, isActive, unitid, pageNo, pageSize,isAsc, isPurchaseApprove, purchaseApproveId);

			return ResponseEntity.ok(serviceResponse);

		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/getbyuserid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllRawMaterialByUserId(@RequestParam("userid") Long userid,
			@RequestParam(value = "rawMaterialName", required = false) String rawMaterialName,
			@RequestParam(value = "isActive", required = false) Boolean isActive) {

		Map<String, Object> response = new HashMap<>();
		try {

			Map<String, Object> serviceResponse = rawMaterialMasterService.getAllRawMaterialByUserId(userid,
					rawMaterialName, isActive);

			return ResponseEntity.ok(serviceResponse);

		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	

	@GetMapping("/getbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getRawMaterialById(@RequestParam("id") Long Id) {
		Map<String, Object> response = new HashMap<>();
		try {
			RawMaterialMasterResponseDto responseDto = rawMaterialMasterService.getRawMaterialById(Id);
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> rawMaterialCatResp = new HashMap<>();
				List<RawMaterialMasterResponseDto> responseDtos = new ArrayList<>();
				responseDtos.add(responseDto);
				rawMaterialCatResp.put("Raw Material Details", responseDtos);
				response.put("data", rawMaterialCatResp);
				response.put("msg", ConstantsPoc.RAW_MATERIAL_FOUND_SUCCESS);
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
	
	@PutMapping("/updaterawmaterialitemcategory")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateRawMaterialItemCategory(@RequestParam("raw_material_id_list") List<Long> rawMaterialIds, 
			@RequestParam("new_cat_id") Long newCatId, @RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		
		try {
			isSuccess = rawMaterialMasterService.updateRawMaterialItemCategory(rawMaterialIds, newCatId, userId);
			if(isSuccess) {
				response.put("success", isSuccess);
				response.put("msg", "Category updated Successfully.");
			} else {
				response.put("success", isSuccess);
				response.put("msg", "Category is not updated.");
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
	
	@PutMapping("/updaterawmaterialsupplier")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateRawMaterialSupplier(@RequestParam("raw_material_id_list") List<Long> rawMaterialIds, @RequestParam("supplierId") Long newSupplierId, @RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = rawMaterialMasterService.updateRawMaterialSupplier(rawMaterialIds, newSupplierId, userId);
			if(isSuccess) {
				response.put("success", isSuccess);
				response.put("msg", "Supplier updated Successfully.");
			} else {
				response.put("success", isSuccess);
				response.put("msg", "Supplier is not updated.");
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
	
	@GetMapping("/getrawmaterialbycategory")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getRawMaterialItemByCategory(@RequestParam("cat_id_list") List<Long> catIds, 
			@RequestParam("user_id") Long userId,
			@RequestParam(value = "page", defaultValue = "0", required = false) int page,
			@RequestParam(value = "size", defaultValue = "10", required = false) int size) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			Pageable pageable = PageRequest.of(page, size);
			Page<RawMaterialCategoryChangeResponseDto> pageResult  = rawMaterialMasterService.getRawMaterialItemByCategory(catIds, userId, pageable);
			if(pageResult.hasContent()) {
				response.put("data", pageResult.getContent());
	            response.put("success", true);
	            response.put("msg", "Raw material item found successfully.");
	            response.put("totalRawMaterialItems", pageResult.getTotalElements());      
	            response.put("totalPages", pageResult.getTotalPages());            
	            response.put("currentPage", pageResult.getNumber());               
	            response.put("pageSize", pageResult.getSize());                    
	            response.put("hasNext", pageResult.hasNext());                     
	            response.put("hasPrevious", pageResult.hasPrevious());     
			} else {
				response.put("success", false);
				response.put("msg", "Raw material item does not found.");
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
	public ResponseEntity<Map<String, Object>> deleteRawMaterialById(@RequestParam("id") Long Id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = rawMaterialMasterService.deleteRawMaterialById(Id);
			if (!isSuccess) {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_DELETE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_DELETE_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> updateRawMaterialCategoryTypeStatus(@RequestParam("id") Long id,
			@RequestParam("isActive") Boolean isActive) {
		Map<String, Object> response = new HashMap<>();
		try {
			boolean updated = rawMaterialMasterService.updateRawMaterialStatus(id, isActive);
			if (updated) {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_UPDATE_FAIL);
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

	@PutMapping("/updatesequence")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateSequence(
			@Valid @RequestBody List<UpdateRawMaterialRequestDto> request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean responseDto = rawMaterialMasterService.updateSequence(request);

			if (responseDto) {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_UPDATE_FAIL);
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

	@GetMapping("/getallgeneralfix")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllGeneralFix(@RequestParam("isGeneralFix") Boolean isGeneralFix,
			@RequestParam("userId") Long userId,
			@RequestParam(value = "rawMaterialName", required = false) String rawMaterialName,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {

		Map<String, Object> response = new HashMap<>();
		try {
			// Convert to 0-indexed
			int pageIndex = page - 1;
			if (pageIndex < 0)
				pageIndex = 0;

			Map<String, Object> serviceResponse = rawMaterialMasterService.getAllGeneralFix(userId, rawMaterialName,
					isGeneralFix, pageIndex, size);

			return ResponseEntity.ok(serviceResponse);

		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

}
