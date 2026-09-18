package com.crmportal.controller;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.crmportal.request.dto.EventRawMaterialDisposableRequestDto;
import com.crmportal.request.dto.GenerateSotRequestDto;
import com.crmportal.service.EventRawMaterialDisposableService;

import java.io.File;
import java.nio.file.Files;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;

@RestController
@RequestMapping("/v1/api/event-rm-Disposable")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventRawMaterialDisposableController {

	@Autowired
	private EventRawMaterialDisposableService service;

	@Autowired
	private Environment environment;

	// GET — load event-wise if exists, else from master
	@GetMapping("/get")
	public ResponseEntity<Map<String, Object>> getOrLoad(@RequestParam("eventId") Long eventId,
			@RequestParam("userId") Long userId, @RequestParam("rawMatCatId") Long rawMatCatId) {

		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "Data fetched successfully");
			response.put("data", service.getOrLoad(eventId, userId, rawMatCatId, null, 1));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	// POST — save event-wise data
	@PostMapping("/save")
	public ResponseEntity<Map<String, Object>> save(@RequestBody EventRawMaterialDisposableRequestDto request) {

		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "Data saved successfully");
			response.put("data", service.save(request));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	@GetMapping("/pdf")
	public ResponseEntity<Map<String, Object>> generatePdf(@RequestParam("eventId") Long eventId,
			@RequestParam("userId") Long userId,
			@RequestParam(value = "isCompanyDetails", defaultValue = "1") Integer isCompanyDetails,
			@RequestParam(value = "isWithPrice", defaultValue = "1") Integer isWithPrice,
			@RequestParam(value = "lang", defaultValue = "1") Integer lang,
			@RequestParam(value = "isAllItems") Integer isAllItems,
			@RequestParam(value = "rawCategoryId") Long rawCategoryId,
			@RequestParam(value = "isWithImage", defaultValue = "1") Integer isWithImage,
			@RequestParam(value = "isTwoColumns", defaultValue = "0") Integer isTwoColumns,
			HttpServletRequest request) {

		Map<String, Object> response = new HashMap<>();
		try {
			byte[] pdf = service.generatePdfReport(eventId, userId, isCompanyDetails, isWithPrice, lang, isAllItems, rawCategoryId, isWithImage, isTwoColumns);

			String rootPath = request.getSession().getServletContext().getRealPath("/");
			String folderName = String.valueOf(userId);

			File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");
			if (!dir.exists())
				dir.mkdirs();

			String fileName = "disposable-" + System.currentTimeMillis() + ".pdf";
			Files.write(new File(dir, fileName).toPath(), pdf);

			String fileUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/" + folderName + "/"
					+ fileName;

			response.put("success", true);
			response.put("msg", "PDF generated successfully");
			response.put("fileUrl", fileUrl);
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	@PostMapping("/generate-sot")
	public ResponseEntity<Map<String, Object>> generateSot(
	        @RequestBody GenerateSotRequestDto request) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        // redirect to SOT service
	        response.put("success", true);
	        response.put("msg", "SOT generation triggered");
	        response.put("sotEndpoint", "/v1/api/sotcrockery/generate");
	        return ResponseEntity.ok(response);
	    } catch (RuntimeException e) {
	        response.put("success", false);
	        response.put("msg", e.getMessage());
	        return ResponseEntity.ok(response);
	    }
	}
}