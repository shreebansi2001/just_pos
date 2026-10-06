package com.crmportal.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.crmportal.entity.PosFloorEntity;
import com.crmportal.entity.PosTableEntity;
import com.crmportal.service.PosService;

@RestController
@RequestMapping("/v1/api/pos")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class PosTableController extends PosBaseController {

	@Autowired
	private PosService posService;

	// Floors
	@GetMapping("/floors")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getFloors(
			@RequestParam(value = "userId", required = false) Long userId) {
		return ok("Floors fetched", posService.getAllFloors(userId));
	}

	@PostMapping("/floors")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> saveFloor(@RequestParam(value = "userId", required = false) Long userId,
			@RequestBody PosFloorEntity floor) {
		return ok("Floor saved", posService.saveFloor(floor, userId));
	}

	@DeleteMapping("/floors/{id}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteFloor(@PathVariable("id") Long id) {
		posService.deleteFloor(id);
		return ok("Floor deleted", null);
	}

	// Tables
	@GetMapping("/tables")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getTables(
			@RequestParam(value = "userId", required = false) Long userId) {
		return ok("Tables fetched", posService.getAllTables(userId));
	}

	@PostMapping("/tables")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> saveTable(@RequestParam(value = "userId", required = false) Long userId,
			@RequestBody PosTableEntity table) {
		return ok("Table saved", posService.saveTable(table, userId));
	}

	@DeleteMapping("/tables/{id}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteTable(@PathVariable("id") Long id) {
		posService.deleteTable(id);
		return ok("Table deleted", null);
	}

	@PutMapping("/tables/{id}/status")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateTableStatus(@PathVariable("id") Long id,
			@RequestParam("status") String status) {
		posService.updateTableStatus(id, status);
		return ok("Table status updated", null);
	}
}
