package com.crmportal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.DecoreMainCategoryMasterRequestDto;
import com.crmportal.response.dto.DecoreMainCategoryMasterResponseDto;
import com.crmportal.service.DecoreMainCategoryMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/decoremaincategory" })
@CrossOrigin(origins = "*", maxAge = 3600L)
public class DecoreMainCategoryMasterController {

	@Autowired
	private DecoreMainCategoryMasterService decoreMainCategoryMasterService;

	@PostMapping(value = "/addorupdate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addDecoreMainCategory(
			@Valid @ModelAttribute DecoreMainCategoryMasterRequestDto request,
			@RequestParam(value = "imagePath", required = false) MultipartFile imagePath) {

		Map<String, Object> response = new HashMap<>();

		try {
			DecoreMainCategoryMasterResponseDto responseDto = decoreMainCategoryMasterService
					.addOrUpdateDecoreMainCategory(request, request.getId(), imagePath);

			if (responseDto != null) {
				response.put("msg", ConstantsPoc.DECORE_MAIN_CATEGORY_CREATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.DECORE_MAIN_CATEGORY_CREATE_FAIL);
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
	public ResponseEntity<Map<String, Object>> getAllDecoreMainCategoryByUserId(@RequestParam("userid") Long userId,
			@RequestParam(value = "categoryName", required = false) String categoryName,
			@RequestParam(value = "isActive", required = false) Boolean isActive) {

		Map<String, Object> response = new HashMap<>();

		try {
			List<DecoreMainCategoryMasterResponseDto> responseDto = decoreMainCategoryMasterService
					.getAllDecoreMainCategoryByUserId(userId, categoryName, isActive);

			if (responseDto.isEmpty()) {
				response.put("msg", ConstantsPoc.DECORE_MAIN_CATEGORY_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> categoryResp = new HashMap<>();
				categoryResp.put("Decore Main Category Details", responseDto);

				response.put("data", categoryResp);
				response.put("msg", ConstantsPoc.DECORE_MAIN_CATEGORY_FOUND);
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

	@GetMapping("/getid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getDecoreMainCategoryById(@RequestParam("id") Long id) {

		Map<String, Object> response = new HashMap<>();

		try {
			DecoreMainCategoryMasterResponseDto responseDto = decoreMainCategoryMasterService
					.getDecoreMainCategoryById(id);

			if (responseDto == null) {
				response.put("msg", ConstantsPoc.DECORE_MAIN_CATEGORY_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> categoryResp = new HashMap<>();
				List<DecoreMainCategoryMasterResponseDto> responseList = new ArrayList<>();

				responseList.add(responseDto);
				categoryResp.put("Decore Main Category Details", responseList);

				response.put("data", categoryResp);
				response.put("msg", ConstantsPoc.DECORE_MAIN_CATEGORY_FOUND);
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
	public ResponseEntity<Map<String, Object>> deleteDecoreMainCategoryById(@RequestParam("id") Long id) {

		Map<String, Object> response = new HashMap<>();

		try {
			boolean isSuccess = decoreMainCategoryMasterService.deleteDecoreMainCategoryById(id);

			if (!isSuccess) {
				response.put("msg", ConstantsPoc.DECORE_MAIN_CATEGORY_DELETED_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.DECORE_MAIN_CATEGORY_DELETED_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> updateDecoreMainCategoryStatus(@RequestParam("id") Long id,
			@RequestParam("isActive") Boolean isActive) {

		Map<String, Object> response = new HashMap<>();

		try {
			boolean updated = decoreMainCategoryMasterService.updateDecoreMainCategoryStatus(id, isActive);

			if (updated) {
				response.put("msg", ConstantsPoc.DECORE_MAIN_CATEGORY_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.DECORE_MAIN_CATEGORY_UPDATE_FAIL);
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