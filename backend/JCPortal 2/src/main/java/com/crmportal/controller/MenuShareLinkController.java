package com.crmportal.controller;

import java.util.*;

import org.apache.hc.core5.reactor.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.crmportal.request.dto.GenerateLinkRequestDto;
import com.crmportal.request.dto.VerifyLinkRequestDto;
import com.crmportal.response.dto.MenuShareLinkResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.MenuShareLinkService;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/v1/api/menu-share")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class MenuShareLinkController {

	@Autowired
	private MenuShareLinkService service;

	@Autowired
	CommonService commonService;

	// ── Generate encrypted share link ─────────────────────────────────────────
	@PostMapping("/generate")
	public ResponseEntity<Map<String, Object>> generateLink(@RequestBody GenerateLinkRequestDto request) {

		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "Link generated successfully");
			MenuShareLinkResponseDto resp = service.generateLink(request);
			response.put("data", resp);

			List<String> set = new ArrayList<>(
					Arrays.asList(valueOrEmpty(resp.getUserName()), valueOrEmpty(resp.getEventNo()),
							valueOrEmpty(resp.getEventName()), valueOrEmpty(resp.getFunctionName()),
							valueOrEmpty(resp.getFunctionDate()), valueOrEmpty(resp.getPackageName()),
							valueOrEmpty(resp.getGuestName()), valueOrEmpty(resp.getGuestMobileNo()),
							valueOrEmpty(resp.getShareUrl()), valueOrEmpty(resp.getAccessCode())));

			try {
				commonService.sendWhatsappMsg("event_menu_link_access", resp.getUserMobileNo(), set, null, false,
						resp.getUserId());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	private static String valueOrEmpty(String value) {
		return value == null ? "NA" : value;
	}

	// ── Verify token + access code → return package menu data ─────────────────
	@PostMapping("/verify")
	public ResponseEntity<Map<String, Object>> verifyAndGetData(@RequestBody VerifyLinkRequestDto request) {

		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "Menu data fetched successfully");
			response.put("data", service.verifyAndGetData(request));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}
}