package com.crmportal.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.service.TransliterationService;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping({ "v1/api/transliterate", "v2/api/translate" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class TransliterationController {

	private final TransliterationService service;

	public TransliterationController(TransliterationService service) {
		this.service = service;
	}

	@GetMapping
	public Map<String, String> transliterate(@RequestParam("text") String text) {
		Map<String, String> result = new HashMap<>();

		result.put("english", text);
		result.put("hindi", service.transliterate(text, "hi"));
		result.put("gujarati", service.transliterate(text, "gu"));
		result.put("ta", service.transliterate(text, "ta"));
		result.put("te", service.transliterate(text, "te"));
		result.put("mr", service.transliterate(text, "mr"));
		result.put("ml", service.transliterate(text, "ml"));
		return result;
	}

	@PostMapping
	public ResponseEntity<?> translateObject(@RequestBody JsonNode inputNode) {
		try {
			Object result = service.translateAny(inputNode);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put("error", e.getMessage());
			return ResponseEntity.badRequest().body(errorMap);
		}
	}

	@PostMapping("/to-hindi")
	public ResponseEntity<?> translateToHindi(@RequestBody JsonNode inputNode) {
		try {
			Object result = service.translateAnyToLanguage(inputNode, "hi");
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put("error", e.getMessage());
			return ResponseEntity.badRequest().body(errorMap);
		}
	}

	@PostMapping("/to-gujarati")
	public ResponseEntity<?> translateToGujarati(@RequestBody JsonNode inputNode) {
		try {
			Object result = service.translateAnyToLanguage(inputNode, "gu");
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put("error", e.getMessage());
			return ResponseEntity.badRequest().body(errorMap);
		}
	}
}
