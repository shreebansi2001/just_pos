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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.MenuCategoryMasterRequestDto;
import com.crmportal.response.dto.MenuCategoryMasterResponseDto;
import com.crmportal.service.MenuCategoryMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/menucategory" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class MenuCategoryMasterController {

	@Autowired
	MenuCategoryMasterService menuCategoryMasterService;

	@PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addMenuCategoryMaster(
			@Valid @ModelAttribute MenuCategoryMasterRequestDto request,@RequestParam(value = "file",required = false) MultipartFile file) {
		Map<String, Object> response = new HashMap<>();
		try {
			MenuCategoryMasterResponseDto responseDto = menuCategoryMasterService.addOrUpdateMenuCategory(request,
					Long.parseLong("-1"),file);
			if (responseDto != null) {
				response.put("ModuleId", responseDto.getId());
				response.put("ModuleName", "MenuCategory");
				response.put("FileType", "Img");
				response.put("msg", ConstantsPoc.MENU_CATEGORY_CREATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.MENU_CATEGORY_CREATE_FAIL);
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
	
	@PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateMenuCategoryMaster(
			@Valid @ModelAttribute MenuCategoryMasterRequestDto request,@RequestParam("id") Long id,@RequestParam(value = "file",required = false) MultipartFile file) {
		Map<String, Object> response = new HashMap<>();
		try {
			MenuCategoryMasterResponseDto responseDto = menuCategoryMasterService.addOrUpdateMenuCategory(request,
					id,file);
			if (responseDto != null) {
				response.put("ModuleId", responseDto.getId());
				response.put("ModuleName", "MenuCategory");
				response.put("FileType", "Img");
				response.put("msg", ConstantsPoc.MENU_CATEGORY_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.MENU_CATEGORY_UPDATE_FAIL);
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
	public ResponseEntity<Map<String, Object>> getAllMenuCategoryByUserId(@RequestParam("userid") Long userId,@RequestParam(value = "menuCategoryName", required = false) String menuCategoryName,@RequestParam(value = "isActive", required = false) Boolean isActive){
		Map<String, Object> response = new HashMap<>();
		try {
			List<MenuCategoryMasterResponseDto> responseDto = menuCategoryMasterService.getAllMenuCategoryByUserId(userId,menuCategoryName,isActive);
			if (responseDto.isEmpty()) {
				response.put("msg", ConstantsPoc.MENU_CATEGORY_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> menuCatResp = new HashMap<>();
				menuCatResp.put("Menu Category Details", responseDto);
				response.put("data", menuCatResp);
				response.put("msg", ConstantsPoc.MENU_CATEGORY_FOUND);
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
	public ResponseEntity<Map<String, Object>> getMenuCategoryById(@RequestParam("id") Long id){
		Map<String, Object> response = new HashMap<>();
		try {
			MenuCategoryMasterResponseDto responseDto = menuCategoryMasterService.getMenuCategoryById(id);
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.MENU_CATEGORY_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> menuCatResp = new HashMap<>();
				List<MenuCategoryMasterResponseDto> responseDt = new ArrayList<>();
				responseDt.add(responseDto);
				menuCatResp.put("Menu Category Details", responseDt);
				response.put("data", menuCatResp);
				response.put("msg", ConstantsPoc.MENU_CATEGORY_FOUND);
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
	public ResponseEntity<Map<String, Object>> deleteMenuCategoryById(@RequestParam("id") Long id){
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = menuCategoryMasterService.deleteMenuCategoryById(id);
			if (!isSuccess) {
				response.put("msg", ConstantsPoc.MENU_CATEGORY_DELETED_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.MENU_CATEGORY_DELETED_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> updateMenuCategoryStatus(
	        @RequestParam("id") Long id,
	        @RequestParam("isActive") Boolean isActive) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        boolean updated = menuCategoryMasterService.updateMenuCategoryStatus(id, isActive);
	        if (updated) {
	            response.put("msg", ConstantsPoc.MENU_CATEGORY_UPDATE_SUCCESS);
	            response.put("success", true);
	        } else {
	            response.put("msg", ConstantsPoc.MENU_CATEGORY_UPDATE_FAIL);
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
