package com.crmportal.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.dto.PosDto;
import com.crmportal.service.PosService;

@RestController
@RequestMapping("/v1/api/pos")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class PosKotController extends PosBaseController {

	@Autowired
	private PosService posService;

	@PostMapping("/orders/{id}/kot")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> sendKot(@PathVariable("id") Long id,
			@RequestParam(value = "userId", required = false) Long userId,
			@RequestBody List<PosDto.OrderItemUpdate> items) {
		return ok("KOT sent", posService.sendKot(id, items, userId));
	}

	@GetMapping("/kots")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getKots(@RequestParam(value = "userId", required = false) Long userId) {
		return ok("KOTs fetched", posService.getActiveKots(userId));
	}

	@PutMapping("/kots/{id}/status")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateKotStatus(@PathVariable("id") Long id,
			@RequestBody PosDto.KotStatusUpdate request) {
		return ok("KOT updated", posService.updateKotStatus(id, request.getStatus()));
	}
}
