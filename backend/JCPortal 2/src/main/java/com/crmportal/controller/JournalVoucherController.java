package com.crmportal.controller;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crmportal.request.dto.JournalVoucherRequestDto;
import com.crmportal.service.JournalVoucherService;

import java.io.File;
import java.nio.file.Files;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;

@RestController
@RequestMapping("/v1/api/journalvoucher")
@CrossOrigin(origins = "*", maxAge = 3600L)
public class JournalVoucherController {

    @Autowired
    private JournalVoucherService service;
    
    @Autowired
    private Environment environment;

    // ── Add or Update ─────────────────────────────────────────────────────
    @PostMapping("/add-update")
    public ResponseEntity<Map<String, Object>> addOrUpdate(
            @RequestBody JournalVoucherRequestDto request) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg",  "Voucher saved successfully");
            response.put("data", service.addOrUpdate(request));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── Get all by user ───────────────────────────────────────────────────
    @GetMapping("/getbyuser")
    public ResponseEntity<Map<String, Object>> getByUser(
            @RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<?> list = service.getByUser(userId);
            response.put("success", true);
            response.put("msg",  "Vouchers fetched successfully");
            response.put("data", list);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── Get by ID ─────────────────────────────────────────────────────────
    @GetMapping("/getbyid/{id}")
    public ResponseEntity<Map<String, Object>> getById(
            @PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg",  "Voucher fetched successfully");
            response.put("data", service.getById(id));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> delete(
            @PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();
        try {
            service.delete(id);
            response.put("success", true);
            response.put("msg", "Voucher deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    @GetMapping("/pdf")
    public ResponseEntity<Map<String, Object>> generatePdf(
            @RequestParam("voucherId")        Long    voucherId,
            @RequestParam("userId")           Long    userId,
            @RequestParam("isCompanyDetails") Integer isCompanyDetails,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        try {
            byte[] pdf = service.generatePdfReport(voucherId, userId, isCompanyDetails);

            String rootPath   = request.getSession().getServletContext().getRealPath("/");
            String folderName = String.valueOf(userId);

            File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");
            if (!dir.exists()) dir.mkdirs();

            String fileName = "journal-voucher-" + System.currentTimeMillis() + ".pdf";
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
}