package com.crmportal.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.crmportal.request.dto.*;
import com.crmportal.response.dto.*;
import com.crmportal.service.StoreOrderingTicketCrockeryService;
import com.crmportal.service.StoreOrderingTicketService;
import java.io.File;
import java.nio.file.Files;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;

@RestController
@RequestMapping(value = "/v1/api/sotcrockery", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class StoreOrderingTicketCrockeryController {

	@Autowired
	private StoreOrderingTicketCrockeryService service;
	
	@Autowired
	private Environment environment;

	// ── Generate SOT from event raw material ──────────────────────────────────
	@PostMapping("/generate")
	public ResponseEntity<Map<String, Object>> generateSot(@RequestBody GenerateSotRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "SOT generated successfully");
			response.put("data", service.generateSot(request));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	// ── Accept SOT ────────────────────────────────────────────────────────────
	@PostMapping("/accept")
	public ResponseEntity<Map<String, Object>> acceptSot(@RequestBody AcceptSotRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "SOT accepted successfully");
			response.put("data", service.acceptSot(request));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	
	// ── Return SOT ────────────────────────────────────────────────────────────
	@PostMapping("/return")
	public ResponseEntity<Map<String, Object>> returnSot(@RequestBody ReturnSotRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "SOT returned successfully");
			response.put("data", service.returnSot(request));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	// ── Get All SOTs ──────────────────────────────────────────────────────────
	@GetMapping("/getall")
	public ResponseEntity<Map<String, Object>> getAll(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "SOTs fetched successfully");
			response.put("data", service.getAll(userId));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	// ── Get SOT by ID ─────────────────────────────────────────────────────────
	@GetMapping("/getbyid/{sotId}")
	public ResponseEntity<Map<String, Object>> getById(@PathVariable Long sotId) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "SOT fetched successfully");
			response.put("data", service.getById(sotId));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	// ── Get SOTs by Event ─────────────────────────────────────────────────────
	@GetMapping("/getbyevent/{eventId}")
	public ResponseEntity<Map<String, Object>> getByEvent(@PathVariable Long eventId) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "SOTs fetched successfully");
			response.put("data", service.getByEvent(eventId));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	// ── Store Report ──────────────────────────────────────────────────────────

	@GetMapping("/storereport/{sotId}")
	public ResponseEntity<Map<String, Object>> getStoreReport(

			@PathVariable Long sotId) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "Store report fetched successfully");
			response.put("data", service.getStoreReport(sotId));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	@PostMapping("/update-details")
	public ResponseEntity<Map<String, Object>> updateSotDetails(@RequestBody UpdateSotDetailsRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "SOT details updated successfully");
			response.put("data", service.updateSotDetails(request));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	// ── Generate Manual PO agency wise ────────────────────────────────────────
		@PostMapping("/generate-manual-po/{sotId}")
		public ResponseEntity<Map<String, Object>> generateManualPo(@PathVariable Long sotId,
				@RequestParam("userId") Long userId) {
			Map<String, Object> response = new HashMap<>();
			try {
				response.put("success", true);
				response.put("msg", "Manual PO generated successfully");
				response.put("data", service.generateManualPo(sotId, userId));
				return ResponseEntity.ok(response);
			} catch (RuntimeException e) {
				e.printStackTrace();
				response.put("success", false);
				response.put("msg", e.getMessage());
				return ResponseEntity.ok(response);
			}
		}
	
	// ── Delete SOT ────────────────────────────────────────────────────────────
	@DeleteMapping("/delete/{sotId}")
	public ResponseEntity<Map<String, Object>> deleteSot(@PathVariable Long sotId) {
		Map<String, Object> response = new HashMap<>();
		try {
			service.deleteSot(sotId);
			response.put("success", true);
			response.put("msg", "SOT deleted successfully");
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	
	@GetMapping("/pdf/{sotId}")
	public ResponseEntity<Map<String, Object>> generatePdf(
	        @PathVariable Long sotId,
	        @RequestParam("userId")           Long userId,
	        @RequestParam("isCompanyDetails") Integer isCompanyDetails,
	        HttpServletRequest request) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        byte[] pdf = service.generatePdfReport(sotId, userId, isCompanyDetails);

	        String rootPath   = request.getSession().getServletContext().getRealPath("/");
	        String folderName = String.valueOf(userId);

	        File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");
	        if (!dir.exists()) dir.mkdirs();

	        String fileName = "sot-" + sotId + "-" + System.currentTimeMillis() + ".pdf";
	        Files.write(new File(dir, fileName).toPath(), pdf);

	        String fileUrl = environment.getProperty("ws_image_path")
	                + "/api/download/pdf/" + folderName + "/" + fileName;

	        response.put("success", true);
	        response.put("msg",     "PDF generated successfully");
	        response.put("fileUrl", fileUrl);
	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("success", false);
	        response.put("msg", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	
	    @GetMapping("/check-sot")
	    public ResponseEntity<Boolean> checkSot(
	            @RequestParam Long eventId,
	            @RequestParam Long userId) {

	        return ResponseEntity.ok(
	        		service.checkSotExists(eventId, userId));
	    }

}