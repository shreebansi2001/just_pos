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

import com.crmportal.request.dto.ContactCategoryMasterRequestDto;
import com.crmportal.response.dto.ContactCategoryMasterResponseDto;
import com.crmportal.service.ContactCategoryMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/contactcategory" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class ContactCategoryMasterController {

	@Autowired
	ContactCategoryMasterService categoryMasterService;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addContactCategory(
			@Valid @RequestBody ContactCategoryMasterRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			ContactCategoryMasterResponseDto responseDto = categoryMasterService
					.addOrUpdateContactCategoryMaster(request, Long.parseLong("-1"));
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.CONTACT_CATEGORY_CREATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.CONTACT_CATEGORY_CREATE_FAIL);
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
	public ResponseEntity<Map<String, Object>> upadteContactCategory(
			@Valid @RequestBody ContactCategoryMasterRequestDto request, @RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			ContactCategoryMasterResponseDto responseDto = categoryMasterService
					.addOrUpdateContactCategoryMaster(request, id);
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.CONTACT_CATEGORY_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.CONTACT_CATEGORY_UPDATE_FAIL);
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
	public ResponseEntity<Map<String, Object>> getAllContactCategoryByUserId(@RequestParam(value = "userId") Long userId, @RequestParam(value = "categoryName", required = false) String categoryName) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<ContactCategoryMasterResponseDto> responseDtos = categoryMasterService.getAllContactCategory(categoryName,userId);
			if (responseDtos.isEmpty()) {
				response.put("msg", ConstantsPoc.CONTACT_CATEGORY_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> contactResp = new HashMap<>();
				contactResp.put("Contact Category Details", responseDtos);
				response.put("data", contactResp);
				response.put("msg", ConstantsPoc.CONTACT_CATEGORY_FOUND);
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
	public ResponseEntity<Map<String, Object>> getContactCategoryById(@RequestParam("id") Long id) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	    	List<ContactCategoryMasterResponseDto> responseDtos = new ArrayList<>();
	        ContactCategoryMasterResponseDto dto = categoryMasterService.getContactCategoryById(id);

	        if (dto == null) {
	            response.put("msg", ConstantsPoc.CONTACT_CATEGORY_NOT_FOUND);
	            response.put("success", false);
	        } else {
	        	Map<String, Object> contactResp = new HashMap<>();
	        	responseDtos.add(dto);
	        	contactResp.put("Contact Category Details", responseDtos);
	            response.put("data", contactResp);
	            response.put("msg", ConstantsPoc.CONTACT_CATEGORY_FOUND);
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
	public ResponseEntity<Map<String, Object>> deleteContactCategoryById(@RequestParam("id") Long id){
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = categoryMasterService.deleteContactCategoryById(id);
			if(isSuccess) {
				response.put("msg", ConstantsPoc.CONTACT_CATEGORY_DELETE_SUCCESS);
				response.put("success", true);
			}else {
				response.put("msg", ConstantsPoc.CONTACT_CATEGORY_DELETE_FAIL);
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

//	@GetMapping("/getcontactcategorywithsearch")
//	@ResponseBody
//	public ResponseEntity<Map<String, Object>> getContactCategoryWithSearch(@RequestParam("categoryName") String categoryName) {
//	    Map<String, Object> response = new HashMap<>();
//	    try {
//	        List<ContactCategoryMasterResponseDto> responseDtos = categoryMasterService.getContactCategoryWithSearch(categoryName);
//
//	        if (responseDtos.isEmpty()) {
//	            response.put("msg", ConstantsPoc.CONTACT_CATEGORY_NOT_FOUND);
//	            response.put("success", false);
//	        } else {
//	            Map<String, Object> contactResp = new HashMap<>();
//	            contactResp.put("Contact Category Details", responseDtos);
//	            response.put("data", contactResp);
//	            response.put("msg", ConstantsPoc.CONTACT_CATEGORY_FOUND);
//	            response.put("success", true);
//	        }
//	        return new ResponseEntity<>(response, HttpStatus.OK);
//	    } catch (RuntimeException e) {
//	        response.put("success", false);
//	        response.put("msg", e.getMessage());
//	        return new ResponseEntity<>(response, HttpStatus.OK);
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        response.put("success", false);
//	        response.put("msg", e.getMessage());
//	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//	    }
//	}

	@GetMapping("/getallbycatid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllContactCategoryByCatAndUserId(@RequestParam(value = "userId") Long userId,@RequestParam(value = "conCatId") Long conCatId, @RequestParam(value = "categoryName", required = false) String categoryName) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<ContactCategoryMasterResponseDto> responseDtos = categoryMasterService.getAllContactCategoryByCatAndUserId(categoryName,userId,conCatId);
			if (responseDtos.isEmpty()) {
				response.put("msg", ConstantsPoc.CONTACT_CATEGORY_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> contactResp = new HashMap<>();
				contactResp.put("Contact Category Details", responseDtos);
				response.put("data", contactResp);
				response.put("msg", ConstantsPoc.CONTACT_CATEGORY_FOUND);
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
	public ResponseEntity<Map<String, Object>> updateContcatCategoryStatus(
	        @RequestParam("id") Long id,
	        @RequestParam("isActive") Boolean isActive) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        boolean updated = categoryMasterService.updateContcatCategoryStatus(id, isActive);
	        if (updated) {
	            response.put("msg", ConstantsPoc.CONTACT_CATEGORY_UPDATE_SUCCESS);
	            response.put("success", true);
	        } else {
	            response.put("msg", ConstantsPoc.CONTACT_CATEGORY_UPDATE_FAIL);
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
