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

import com.crmportal.request.dto.MenuSubCategoryMasterRequestDto;
import com.crmportal.response.dto.MenuItemCategoryChangeResponseDto;
import com.crmportal.response.dto.MenuSubCategoryMasterResponseDto;
import com.crmportal.service.MenuSubCategoryMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/menusubcategory" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class MenuSubCategoryMasterController {

	@Autowired
	MenuSubCategoryMasterService menuSubCategoryMasterService;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addMenuSubCategoryMaster(
			@Valid @RequestBody MenuSubCategoryMasterRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			MenuSubCategoryMasterResponseDto responseDto = menuSubCategoryMasterService
					.addOrUpdateMenuSubCategoryMaster(request, Long.parseLong("-1"));
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.MENU_SUB_CATEGORY_CREATE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.MENU_SUB_CATEGORY_CREATE_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> updateMenuSubCategoryMaster(
			@Valid @RequestBody MenuSubCategoryMasterRequestDto request, @RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			MenuSubCategoryMasterResponseDto responseDto = menuSubCategoryMasterService
					.addOrUpdateMenuSubCategoryMaster(request, id);
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.MENU_SUB_CATEGORY_UPDATE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.MENU_SUB_CATEGORY_UPDATE_SUCCESS);
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

	@GetMapping("/getallbyuserid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllMenuSubCategoryByUserId(@RequestParam("userid") Long userId,
			@RequestParam(value = "menuSubCategoryName", required = false) String menuSubCategoryName,
			@RequestParam(value = "menuCategoryId", required = false) Long menuCategoryId,
			@RequestParam(value = "isActive", required = false) Boolean isActive) {

		Map<String, Object> response = new HashMap<>();
		try {
			List<MenuSubCategoryMasterResponseDto> responseDto = menuSubCategoryMasterService
					.getAllMenuSubCategoryByUserId(userId, menuSubCategoryName, isActive, menuCategoryId);

			if (responseDto.isEmpty()) {
				response.put("msg", ConstantsPoc.MENU_SUB_CATEGORY_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> menuSubCatResp = new HashMap<>();
				menuSubCatResp.put("Menu Sub Category Details", responseDto);
				response.put("data", menuSubCatResp);
				response.put("msg", ConstantsPoc.MENU_SUB_CATEGORY_FOUND);
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
	public ResponseEntity<Map<String, Object>> getMenuSubCategoryById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			MenuSubCategoryMasterResponseDto responseDto = menuSubCategoryMasterService.getMenuSubCategoryById(id);
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.MENU_SUB_CATEGORY_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> menuSubCatResp = new HashMap<>();
				List<MenuSubCategoryMasterResponseDto> responseDt = new ArrayList<>();
				responseDt.add(responseDto);
				menuSubCatResp.put("Menu Sub Category Details", responseDt);
				response.put("data", menuSubCatResp);
				response.put("msg", ConstantsPoc.MENU_SUB_CATEGORY_FOUND);
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
	public ResponseEntity<Map<String, Object>> deleteMenuSubCategoryById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			boolean deleted = menuSubCategoryMasterService.deleteMenuSubCategoryById(id);
			if (deleted) {
				response.put("msg", ConstantsPoc.MENU_SUB_CATEGORY_DELETED_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.MENU_SUB_CATEGORY_DELETED_FAIL);
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
	public ResponseEntity<Map<String, Object>> updateMenuSubCategoryStatus(@RequestParam("id") Long id,
			@RequestParam("isActive") Boolean isActive) {

		Map<String, Object> response = new HashMap<>();
		try {
			boolean updated = menuSubCategoryMasterService.updateMenuSubCategoryStatus(id, isActive);
			if (updated) {
				response.put("msg", ConstantsPoc.MENU_SUB_CATEGORY_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.MENU_SUB_CATEGORY_UPDATE_FAIL);
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
