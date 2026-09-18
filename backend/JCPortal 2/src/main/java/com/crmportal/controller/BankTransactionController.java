package com.crmportal.controller;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crmportal.response.dto.BankTransactionReportDTO;
import com.crmportal.service.BankTransactionReportService;

@RestController
@RequestMapping("/v1/api/bank-transaction")
@CrossOrigin(origins = "*", maxAge = 3600L)
public class BankTransactionController {

	// In your BankTransactionController — inject only one service
	@Autowired
	private BankTransactionReportService service;
	
	@Autowired
    private Environment environment;

	@GetMapping("/data")
	public ResponseEntity<Map<String, Object>> getReport(
	        @RequestParam("bankId")    Long   bankId,
	        @RequestParam("fromDate")  String fromDate,
	        @RequestParam("toDate")    String toDate,
	        @RequestParam("userId")    Long   userId) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        BankTransactionReportDTO data = service.getReport(
	                bankId,
	                LocalDate.parse(fromDate, fmt),
	                LocalDate.parse(toDate,   fmt));

	        response.put("success", true);
	        response.put("msg",  "Report fetched successfully");
	        response.put("data", data);
	        return ResponseEntity.ok(response);

	    } catch (RuntimeException e) {
	        response.put("success", false);
	        response.put("msg", e.getMessage());
	        return ResponseEntity.ok(response);
	    }
	}

	@GetMapping("/pdf")
	public ResponseEntity<Map<String, Object>> generatePdf(
	        @RequestParam("bankId")    Long   bankId,
	        @RequestParam("fromDate")  String fromDate,
	        @RequestParam("toDate")    String toDate,
	        @RequestParam("userId")    Long   userId,
	        HttpServletRequest request) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        byte[] pdf = service.generatePdf(
	                bankId,
	                LocalDate.parse(fromDate, fmt),
	                LocalDate.parse(toDate,   fmt));

	        String rootPath   = request.getSession()
	                .getServletContext().getRealPath("/");
	        String folderName = String.valueOf(userId);

	        File dir = new File(
	                rootPath + "resources/tempDownload/" + folderName + "/");
	        if (!dir.exists()) dir.mkdirs();

	        String fileName = "bank-transaction-"
	                + System.currentTimeMillis() + ".pdf";
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
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(response);
	    }
	}
}