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
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.DecorePackageRequestDto;
import com.crmportal.response.dto.DecorePackageResponseDto;
import com.crmportal.service.DecorePackageService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping("/v1/api/decorepackage")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DecorePackageController {

	@Autowired
	private DecorePackageService decorePackageService;

	@PostMapping("/addorupdate")
	public ResponseEntity<Map<String, Object>> addOrUpdateDecorePackage(
			@Valid @RequestBody DecorePackageRequestDto request) {

		Map<String, Object> response = new HashMap<>();

		try {
			DecorePackageResponseDto responseDto = decorePackageService.addOrUpdateDecorePackage(request);

			if (responseDto != null) {
				response.put("success", true);
				response.put("msg",
						(request.getId() == null || request.getId() == 0 || request.getId() == -1)
								? ConstantsPoc.PACKAGES_CREATE_SUCCESS
								: ConstantsPoc.PACKAGES_UPDATE_SUCCESS);
			} else {
				response.put("success", false);
				response.put("msg",
						(request.getId() == null || request.getId() == 0 || request.getId() == -1)
								? ConstantsPoc.PACKAGES_CREATE_FAIL
								: ConstantsPoc.PACKAGES_UPDATE_FAIL);
			}

			return ResponseEntity.ok(response);

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

	@GetMapping("/getallbyuserid")
	public ResponseEntity<Map<String, Object>> getAllByUserId(@RequestParam("userid") Long userId,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "isActive", required = false) Boolean isActive) {

		Map<String, Object> response = new HashMap<>();

		try {
			List<DecorePackageResponseDto> list = decorePackageService.getAllByUserId(userId, name, isActive);

			if (list.isEmpty()) {
				response.put("success", false);
				response.put("msg", ConstantsPoc.PACKAGES_NOT_FOUND);
			} else {
				Map<String, Object> data = new HashMap<>();
				data.put("Decore Package Details", list);

				response.put("success", true);
				response.put("msg", ConstantsPoc.PACKAGES_FOUND);
				response.put("data", data);
			}

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/getbyid")
	public ResponseEntity<Map<String, Object>> getById(@RequestParam("id") Long id) {

		Map<String, Object> response = new HashMap<>();

		try {
			DecorePackageResponseDto dto = decorePackageService.getById(id);

			if (dto == null) {
				response.put("success", false);
				response.put("msg", ConstantsPoc.PACKAGES_NOT_FOUND);
			} else {
				List<DecorePackageResponseDto> list = new ArrayList<>();
				list.add(dto);

				Map<String, Object> data = new HashMap<>();
				data.put("Decore Package Details", list);

				response.put("success", true);
				response.put("msg", ConstantsPoc.PACKAGES_FOUND);
				response.put("data", data);
			}

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Map<String, Object>> delete(@RequestParam("id") Long id) {

		Map<String, Object> response = new HashMap<>();

		try {
			Boolean isDeleted = decorePackageService.deleteById(id);

			response.put("success", isDeleted);
			response.put("msg", isDeleted ? ConstantsPoc.PACKAGES_DELETE_SUCCESS : ConstantsPoc.PACKAGES_DELETE_FAIL);

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@PutMapping("/status")
	public ResponseEntity<Map<String, Object>> updateStatus(@RequestParam("id") Long id,
			@RequestParam("isActive") Boolean isActive) {

		Map<String, Object> response = new HashMap<>();

		try {
			Boolean updated = decorePackageService.updateStatus(id, isActive);

			response.put("success", updated);
			response.put("msg", updated ? ConstantsPoc.PACKAGES_UPDATE_SUCCESS : ConstantsPoc.PACKAGES_UPDATE_FAIL);

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

}