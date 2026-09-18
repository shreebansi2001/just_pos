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

import com.crmportal.request.dto.ContactTypeMasterRequestDto;
import com.crmportal.response.dto.ContactTypeMasterResponseDto;
import com.crmportal.service.ContactTypeMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/contacttype" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class ContactTypeMasterController {

	@Autowired
	ContactTypeMasterService contactTypeMasterService;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addContactTypeMaster(
			@Valid @RequestBody ContactTypeMasterRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			ContactTypeMasterResponseDto responseDto = contactTypeMasterService.addOrUpdateContactTypeMaster(request,
					Long.valueOf("-1"));
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.CONTACT_TYPE_MASTER_CREATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.CONTACT_TYPE_MASTER_CREATE_FAIL);
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
	public ResponseEntity<Map<String, Object>> updateContactTypeMaster(
			@Valid @RequestBody ContactTypeMasterRequestDto request, @RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			ContactTypeMasterResponseDto responseDto = contactTypeMasterService.addOrUpdateContactTypeMaster(request,
					id);
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.CONTACT_TYPE_MASTER_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.CONTACT_TYPE_MASTER_UPDATE_FAIL);
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
	public ResponseEntity<Map<String, Object>> getAllContactTypeByUserId(@RequestParam("userId") Long userId,@RequestParam(value = "contactTypeName", required = false) String contactTypeName,@RequestParam(value = "isActive", required = false) Boolean isActive) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<ContactTypeMasterResponseDto> responseDtos = contactTypeMasterService
					.getAllContactTypeByUserId(userId,contactTypeName,isActive);
			if (responseDtos.isEmpty()) {
				response.put("msg", ConstantsPoc.CONTACT_TYPE_MASTER_FOUND_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> contactTypeRes = new HashMap<>();
				contactTypeRes.put("Contact Type Details", responseDtos);
				response.put("data", contactTypeRes);
				response.put("msg", ConstantsPoc.CONTACT_TYPE_MASTER_FOUND_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> getContactTypeById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<ContactTypeMasterResponseDto> responseDtos = new ArrayList<>();
			ContactTypeMasterResponseDto responseDto = contactTypeMasterService
					.getContactTypeById(id);
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.CONTACT_TYPE_MASTER_FOUND_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> contactTypeRes = new HashMap<>();
				responseDtos.add(responseDto);
				contactTypeRes.put("Contact Type Details", responseDtos);
				response.put("data", contactTypeRes);
				response.put("msg", ConstantsPoc.CONTACT_TYPE_MASTER_FOUND_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> deleteContactTypeById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = contactTypeMasterService
					.deleteContactTypeById(id);
			if (isSuccess) {
				response.put("msg", ConstantsPoc.CONTACT_TYPE_MASTER_DELETE_SUCCESS);
				response.put("success", isSuccess);
			} else {
				response.put("msg", ConstantsPoc.CONTACT_TYPE_MASTER_DELETE_FAIL);
				response.put("success",isSuccess);
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
	public ResponseEntity<Map<String, Object>> updateContcatTypeStatus(
	        @RequestParam("id") Long id,
	        @RequestParam("isActive") Boolean isActive) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        boolean updated = contactTypeMasterService.updateContcatTypeStatus(id, isActive);
	        if (updated) {
	            response.put("msg", ConstantsPoc.CONTACT_TYPE_MASTER_UPDATE_SUCCESS);
	            response.put("success", true);
	        } else {
	            response.put("msg", ConstantsPoc.CONTACT_TYPE_MASTER_UPDATE_FAIL);
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
