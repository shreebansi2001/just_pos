package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crmportal.request.dto.BanquetRightsRequestDto;
import com.crmportal.service.BanquetRightsService;

@RestController
@RequestMapping("/v1/api/banquet-rights")
@CrossOrigin(origins = "*")
public class BanquetRightsController {

	@Autowired
	private BanquetRightsService service;

	@PostMapping("/add")
	public ResponseEntity<Map<String, Object>> add(@RequestBody List<BanquetRightsRequestDto> request,
			@RequestParam("userId") Long memberId) {

		Map<String, Object> response = new HashMap<>();

		try {

			response.put("success", true);

			response.put("msg", "Rights added successfully");

			response.put("data", service.add(request, memberId));

		} catch (RuntimeException e) {

			response.put("success", false);

			response.put("msg", e.getMessage());
		}

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getbyuser")
	public ResponseEntity<Map<String, Object>> getByUser(@RequestParam Long userId, @RequestParam Long memberId) {

		Map<String, Object> response = new HashMap<>();

		response.put("success", true);

		response.put("data", service.getByUser(userId, memberId));

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {

		Map<String, Object> response = new HashMap<>();

		service.delete(id);

		response.put("success", true);

		response.put("msg", "Deleted successfully");

		return ResponseEntity.ok(response);
	}
}