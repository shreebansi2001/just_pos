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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.dto.GstReportResponseDTO;
import com.crmportal.service.GstReportPdfService;
import com.crmportal.service.GstReportService;

@RestController
@RequestMapping("/v1/api/gst-report")
@CrossOrigin(origins = "*")
public class GstReportController {

    @Autowired
    private GstReportService gstReportService;

    @Autowired
    private GstReportPdfService gstReportPdfService;

    @Autowired
    private Environment environment;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getGstReportData(
            @RequestParam("type")     String type,
            @RequestParam("fromDate") String fromDate,
            @RequestParam("toDate")   String toDate,
            @RequestParam("userId")   Long userId,
            @RequestParam(value = "gstType", required = false) String gstType) {

        Map<String, Object> response = new HashMap<>();
        try {
            LocalDate from = LocalDate.parse(fromDate, DATE_FMT);
            LocalDate to   = LocalDate.parse(toDate,   DATE_FMT);

            GstReportResponseDTO data;
            if ("SALES".equalsIgnoreCase(type)) {
                data = gstReportService.getSalesReport(from, to, userId, gstType);
            } else if ("PURCHASE".equalsIgnoreCase(type)) {
                data = gstReportService.getPurchaseReport(from, to, gstType);
            } else {
                response.put("success", false);
                response.put("msg", "Invalid type. Must be SALES or PURCHASE.");
                return ResponseEntity.badRequest().body(response);
            }

            response.put("success", true);
            response.put("msg", "Data fetched successfully");
            response.put("data", data);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/pdf")
    public ResponseEntity<Map<String, Object>> generateGstReportPdf(
            @RequestParam("type")     String type,
            @RequestParam("fromDate") String fromDate,
            @RequestParam("toDate")   String toDate,
            @RequestParam("userId")   Long userId,
            @RequestParam(value = "gstType", required = false) String gstType,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        try {
            LocalDate from = LocalDate.parse(fromDate, DATE_FMT);
            LocalDate to   = LocalDate.parse(toDate,   DATE_FMT);

            // ── 1. Build report data ──────────────────────────────────────────
            GstReportResponseDTO reportData;
            byte[] pdfBytes;
            
            if ("SALES".equalsIgnoreCase(type)) {
            	reportData = gstReportService.getSalesReport(from, to, userId, gstType);
            	pdfBytes   = gstReportPdfService.generateSalesPdf(reportData);
            } else if ("PURCHASE".equalsIgnoreCase(type)) {
            	reportData = gstReportService.getPurchaseReport(from, to, gstType);
            	pdfBytes   = gstReportPdfService.generatePurchasePdf(reportData);
            } else {
                response.put("success", false);
                response.put("msg", "Invalid type. Must be SALES or PURCHASE.");
                return ResponseEntity.badRequest().body(response);
            }

            // ── 2. Save to disk ───────────────────────────────────────────────
            String rootPath   = request.getSession().getServletContext().getRealPath("/");
            String folderName = String.valueOf(userId);

            File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");
            if (!dir.exists()) dir.mkdirs();

            String fileName = "gst-report-" + type.toLowerCase()
                    + "-" + System.currentTimeMillis() + ".pdf";
            File file = new File(dir, fileName);
            Files.write(file.toPath(), pdfBytes);

            // ── 3. Build download URL ─────────────────────────────────────────
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
}