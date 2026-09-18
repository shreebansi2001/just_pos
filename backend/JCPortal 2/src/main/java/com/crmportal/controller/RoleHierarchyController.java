package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.RoleHierarchyRequestDto;
import com.crmportal.response.dto.RoleHierarchyResponseDto;
import com.crmportal.response.dto.RoleTreeResponseDto;
import com.crmportal.service.RoleHierarchyService;

@RestController
@RequestMapping({ "v1/api/role-hierarchy" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class RoleHierarchyController {

	@Autowired
	RoleHierarchyService roleHierarchyService;

	@PostMapping("/addorupdate")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdate(@RequestBody List<RoleHierarchyRequestDto> requests) {
		Map<String, Object> response = new HashMap<>();
		try {

			Boolean isSuccess = roleHierarchyService.addOrUpdate(requests);
			if (isSuccess) {
				response.put("msg", "Role Hierarchy Add/Updated Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "Role Hierarchy Add/Updated Failed");
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
	public ResponseEntity<Map<String, Object>> deleteById(@RequestParam("hierarchyId") Long hierarchyId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isDelete = roleHierarchyService.deleteById(hierarchyId);
			if (isDelete) {
				response.put("msg", "Role Hierarchy Deleted Successfully");
				response.put("success", isDelete);
			} else {
				response.put("msg", "Role Hierarchy Deleted Failed");
				response.put("success", isDelete);
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

	@GetMapping("/getchildren")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getChildren(@RequestParam("roleId") Long roleId,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<RoleHierarchyResponseDto> dtos = roleHierarchyService.getChildren(roleId, userId);
			if (dtos.isEmpty()) {
				response.put("msg", "Children Role Data Not Found");
				response.put("success", false);
			} else {
				Map<String, Object> childResp = new HashMap<>();
				childResp.put("childrenRole", dtos);
				response.put("data", childResp);
				response.put("msg", "Children Role Data Found Successfully");
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

	@GetMapping("getall")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAll(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {

			List<RoleHierarchyResponseDto> dtos = roleHierarchyService.getHierarchyList(userId);

			if (dtos.isEmpty()) {
				response.put("msg", "Children Role Data Not Found");
				response.put("success", false);
			} else {
				Map<String, Object> childResp = new HashMap<>();
				childResp.put("childrenRole", dtos);
				response.put("data", childResp);
				response.put("msg", "Children Role Data Found Successfully");
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

	@GetMapping("/gettree")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getTree(@RequestParam("roleId") Long roleId,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			RoleTreeResponseDto dto = roleHierarchyService.getTree(roleId, userId);
			if (dto == null) {
				response.put("msg", "Children Role Data Not Found");
				response.put("success", false);
			} else {
				Map<String, Object> childResp = new HashMap<>();
				childResp.put("childrenRole", dto);
				response.put("data", childResp);
				response.put("msg", "Children Role Data Found Successfully");
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
}
