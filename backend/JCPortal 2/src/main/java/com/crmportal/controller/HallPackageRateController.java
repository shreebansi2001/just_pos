package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.HallPackageRateRequestDto;
import com.crmportal.response.dto.HallPackagePriceResponseDto;
import com.crmportal.response.dto.HallPackageRateResponseDto;
import com.crmportal.service.HallPackageRateService;

@RestController
@RequestMapping({ "/v1/api/hallpackage" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class HallPackageRateController {

	@Autowired
	HallPackageRateService hallPackageRateService;

	@PostMapping("/addorupdate")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdate(@RequestBody HallPackageRateRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isCreated = hallPackageRateService.addOrUpdate(request);
			if (isCreated) {
				response.put("msg", "Banquet Package Created/Updated Successfully");
				response.put("success", isCreated);
			} else {
				response.put("msg", "Banquet Package Created/Updated Failed");
				response.put("success", isCreated);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getall")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAll(@RequestParam("userId") Long userId,
			@RequestParam(value = "isActive", required = false) Boolean isActive,
			@RequestParam(value = "hallId", required = false) Long hallId,
			@RequestParam(value = "packageId", required = false) Long packageId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<HallPackageRateResponseDto> dtos = hallPackageRateService.getAll(userId, isActive, hallId, packageId);
			if (dtos.isEmpty()) {
				response.put("msg", "Data Found Failed");
				response.put("success", false);
			} else {
				Map<String, Object> hallPckgResp = new HashMap<>();
				hallPckgResp.put("HallPackages", dtos);
				response.put("data", hallPckgResp);
				response.put("msg", "Data Found Successfully");
				response.put("success", true);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getprice")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getPrice(@RequestParam("hallId") Long hallId,
			@RequestParam("packageId") Long packageId, @RequestParam("functionPax") Integer functionPax) {

		Map<String, Object> response = new HashMap<>();
		try {
			HallPackagePriceResponseDto dto = hallPackageRateService.getPrice(hallId, packageId, functionPax);

			response.put("success", dto.getFound());
			response.put("msg", dto.getMessage());
			response.put("data", dto);

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/updatestatus")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateStatus(@RequestParam("id") Long id,
			@RequestParam("isActive") Boolean isActive) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean result = hallPackageRateService.updateStatus(id, isActive);
			response.put("success", result);
			response.put("msg", result ? "Status Updated Successfully" : "Status Update Failed");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/delete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> delete(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean result = hallPackageRateService.delete(id);
			response.put("success", result);
			response.put("msg", result ? "Deleted Successfully" : "Delete Failed");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
