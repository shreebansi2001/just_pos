package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.enums.CategoryWisePackageType;
import com.crmportal.response.dto.CategoryWisePackageDto;
import com.crmportal.response.dto.CategoryWiseTypeItemResponseDto;
import com.crmportal.service.CategoryWisePackageService;

@RestController
@RequestMapping("/v1/api/category-wise-package")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class CategoryWisePackageController {

	@Autowired
	CategoryWisePackageService categoryWisePackageService;
	
	@PostMapping("/add-update")
	public ResponseEntity<?> addOrUpdateCategoryWisePackage(@RequestBody CategoryWisePackageDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			CategoryWisePackageDto data = categoryWisePackageService.addOrUpdateCategoryWisePackage(request);
			
			if(data == null) {
				response.put("msg", "Package added failed.");
				response.put("success", false);
			}else {
				response.put("msg", "Package added successfully.");
				response.put("success", true);
			}
			
			return new ResponseEntity<>(response , HttpStatus.OK);
		}catch (RuntimeException e) {
		    response.put("success", false);
		    response.put("msg", e.getMessage());
		    return new ResponseEntity<>(response, HttpStatus.OK);
		}  catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getall")
	public ResponseEntity<?> getAllCategoryWisePackage(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			CategoryWisePackageDto data = categoryWisePackageService.getAllCategoryWisePackage(userId);
			
			if(data == null) {
				response.put("msg", "Package found failed.");
				response.put("success", false);
			}else {
				response.put("msg", "Package found successfully.");
				response.put("success", true);
				response.put("data", data);
			}
			
			return new ResponseEntity<>(response , HttpStatus.OK);
		}catch (RuntimeException e) {
		    response.put("success", false);
		    response.put("msg", e.getMessage());
		    return new ResponseEntity<>(response, HttpStatus.OK);
		}  catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getbytype")
	public ResponseEntity<?> getCategoryWisePackageByType(
			@RequestParam("userId") Long userId, 
			@RequestParam("menuCategoryId") Long menuCategoryId,
			@RequestParam("type") CategoryWisePackageType type) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<CategoryWiseTypeItemResponseDto> data = categoryWisePackageService.getCategoryWisePackageItemByType(userId, menuCategoryId, type);
			
			if(data == null) {
				response.put("msg", "Package found failed.");
				response.put("success", false);
			}else {
				response.put("msg", "Package found successfully.");
				response.put("success", true);
				response.put("data", data);
			}
			
			return new ResponseEntity<>(response , HttpStatus.OK);
		}catch (RuntimeException e) {
		    response.put("success", false);
		    response.put("msg", e.getMessage());
		    return new ResponseEntity<>(response, HttpStatus.OK);
		}  catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
