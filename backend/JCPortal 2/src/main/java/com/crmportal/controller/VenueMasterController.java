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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.VenueMasterRequestDto;
import com.crmportal.response.dto.VenueMasterResponseDto;
import com.crmportal.service.VenueMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "v1/api/venuemaster" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class VenueMasterController {

	@Autowired
	VenueMasterService venueMasterService;

	@PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addVenue(
	        @Valid @ModelAttribute VenueMasterRequestDto request,
	        @RequestParam(value = "venueImg", required = false) MultipartFile venueImg) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        VenueMasterResponseDto responseDto = venueMasterService.addOrUpdateVenue(request, venueImg, request.getId());
	        if (responseDto != null) {
	            response.put("msg", ConstantsPoc.VENUE_CREATE_SUCCESS);
	            response.put("success", true);
	        } else {
	            response.put("msg", ConstantsPoc.VENUE_CREATE_FAIL);
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
	public ResponseEntity<Map<String, Object>> updateVenue(
	        @Valid @ModelAttribute VenueMasterRequestDto request,
	        @RequestParam(value = "venueImg", required = false) MultipartFile venueImg) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        VenueMasterResponseDto responseDto = venueMasterService.addOrUpdateVenue(request, venueImg, request.getId());
	        if (responseDto != null) {
	            response.put("msg", ConstantsPoc.VENUE_UPDATE_SUCCESS);
	            response.put("success", true);
	        } else {
	            response.put("msg", ConstantsPoc.VENUE_UPDATE_FAIL);
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
	
	
	@GetMapping("/getallbyuser")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllVenueByUser(@RequestParam("userId") Long userId,
			@RequestParam(value = "venueName", required = false) String venueName,@RequestParam(value = "isActive", required = false) Boolean isActive) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<VenueMasterResponseDto> dtos = venueMasterService.getAllVenueByUser(userId, venueName,isActive);
			if (dtos.isEmpty()) {
				response.put("msg", ConstantsPoc.DATA_FETCH_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> venueResp = new HashMap<>();
				venueResp.put("Venue Details", dtos);
				response.put("data", venueResp);
				response.put("msg", ConstantsPoc.DATA_FETCH_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> getVenueById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			VenueMasterResponseDto dto = venueMasterService.getVenueById(id);
			if (dto == null) {
				response.put("msg", ConstantsPoc.DATA_FETCH_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> venueResp = new HashMap<>();
				List<VenueMasterResponseDto> dtos = new ArrayList<>();
				dtos.add(dto);
				venueResp.put("Venue Details", dtos);
				response.put("data", venueResp);
				response.put("msg", ConstantsPoc.DATA_FETCH_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> updateStatus(@RequestParam("id") Long id,@RequestParam("isActive") Boolean isActive){
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = venueMasterService.updateStatus(id,isActive);
			if (isSuccess) {
				response.put("msg", ConstantsPoc.VENUE_UPDATE_SUCCESS);
				response.put("success", isSuccess);
			} else {
				response.put("msg", ConstantsPoc.VENUE_UPDATE_FAIL);
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
	
	@DeleteMapping("/deletebyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteVenue(@RequestParam("id") Long id){
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = venueMasterService.deleteVenue(id);
			if (isSuccess) {
				response.put("msg", ConstantsPoc.VENUE_DELETE_SUCCESS);
				response.put("success", isSuccess);
			} else {
				response.put("msg", ConstantsPoc.VENUE_DELETE_FAIL);
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
