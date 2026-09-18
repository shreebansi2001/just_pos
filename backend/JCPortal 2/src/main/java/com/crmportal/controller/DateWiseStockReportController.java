package com.crmportal.controller;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.crmportal.response.dto.DateWiseStockReportResponseDto;
import com.crmportal.service.DateWiseStockReportService;

@RestController
@RequestMapping(value = "/v1/api/datewisestockreport", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*", maxAge = 3600L)
public class DateWiseStockReportController {

	@Autowired
	private DateWiseStockReportService service;

	@Autowired
	private Environment environment;

    // ===================== PDF GENERATE =====================
    @GetMapping("/pdf")
    public ResponseEntity<Map<String, Object>> generatePdf(
    		@RequestParam("isCompanyDetails") Integer isCompanyDetails,
            @RequestParam(value = "categoryId", defaultValue = "0") Long categoryId,
            @RequestParam(value = "stockTypeId", defaultValue = "0") Long stockTypeId,
            @RequestParam(value = "kitchenTypeId", defaultValue = "0") Long kitchenTypeId,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "itemName", defaultValue = "") String search,
            HttpServletRequest request) {

		Map<String, Object> response = new HashMap<>();

        try {
            byte[] pdf = service.generatePdfReport(
                    categoryId, stockTypeId, fromDate, toDate, userId, isCompanyDetails, search);

			String rootPath = request.getSession().getServletContext().getRealPath("/");

			String folderName = String.valueOf(userId);

			File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");

			if (!dir.exists()) {
				dir.mkdirs();
			}

			String fileName = "stock-report-" + System.currentTimeMillis() + ".pdf";

			File file = new File(dir, fileName);

			Files.write(file.toPath(), pdf);

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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// ===================== EXCEL GENERATE =====================

	@GetMapping("/excel")
	public ResponseEntity<Map<String, Object>> generateExcel(

			@RequestParam(value = "categoryId", defaultValue = "0") Long categoryId,

			@RequestParam(value = "stockTypeId", defaultValue = "0") Long stockTypeId,
			
			@RequestParam(value = "kitchenTypeId", defaultValue = "0") Long kitchenTypeId,

			@RequestParam(value = "fromDate", required = false) String fromDate,

			@RequestParam(value = "toDate", required = false) String toDate,

			@RequestParam("userId") Long userId,
			@RequestParam(value = "itemName", defaultValue = "") String search,HttpServletRequest request) {

		Map<String, Object> response = new HashMap<>();

		try {
			byte[] excel = service.generateExcelReport(categoryId, stockTypeId, fromDate, toDate, userId, search);

			String rootPath = request.getSession().getServletContext().getRealPath("/");

			String folderName = String.valueOf(userId);

			File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");

			if (!dir.exists()) {
				dir.mkdirs();
			}

			String fileName = "stock-report-" + System.currentTimeMillis() + ".xlsx";

			File file = new File(dir, fileName);

			Files.write(file.toPath(), excel);

			String fileUrl = environment.getProperty("ws_image_path") + "/api/download/excel/" + folderName + "/"
					+ fileName;

			response.put("success", true);
			response.put("msg", "Excel generated successfully");
			response.put("fileUrl", fileUrl);

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// ===================== GET DATA =====================
	@GetMapping("/get")
	public ResponseEntity<Map<String, Object>> getReport(
			@RequestParam(value = "categoryId", defaultValue = "0") Long categoryId,
			@RequestParam(value = "stockTypeId", defaultValue = "0") Long stockTypeId,
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate, @RequestParam("userId") Long userId,
			@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "itemName",      defaultValue = "")  String search) {

		Map<String, Object> response = new HashMap<>();

		try {
			DateWiseStockReportResponseDto data = service.getReport(categoryId, stockTypeId, fromDate, toDate, userId,
					pageNo, pageSize, search);

			response.put("success", true);
			response.put("msg", "Stock report fetched successfully");
			response.put("data", data);

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}
}