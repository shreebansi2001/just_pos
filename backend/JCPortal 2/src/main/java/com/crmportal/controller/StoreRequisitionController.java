package com.crmportal.controller;

import java.util.*;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.crmportal.request.dto.StoreRequisitionRequestDto;
import com.crmportal.response.dto.StoreRequisitionResponseDto;
import com.crmportal.service.StoreRequisitionService;

import java.io.File;
import java.nio.file.Files;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping(value = "/v1/api/storerequisition", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class StoreRequisitionController {

    @Autowired
    private StoreRequisitionService service;
    
    @Autowired
    private Environment environment;

    // ── Add / Update ──────────────────────────────────────────────────────────
    @PostMapping("/add-update")
    public ResponseEntity<Map<String, Object>> addOrUpdate(
            @Valid @RequestBody StoreRequisitionRequestDto request) {

        Map<String, Object> response = new HashMap<>();
        try {
            StoreRequisitionResponseDto dto = service.addOrUpdate(request);
            response.put("success", true);
            response.put("msg", "Chef Requisition saved successfully");
            response.put("data", dto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // ── Update Status ─────────────────────────────────────────────────────────
    // PUT /v1/api/StoreRequisition/updatestatus/{crId}?status=COMPLETED
    @PutMapping("/updatestatus/{crId}")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable("crId") Long crId,
            @RequestParam("status") String status) {

        Map<String, Object> response = new HashMap<>();
        try {
            StoreRequisitionResponseDto dto = service.updateStatus(crId, status);
            response.put("success", true);
            response.put("msg", "Status updated successfully");
            response.put("data", dto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // ── Get by User ───────────────────────────────────────────────────────────
    @GetMapping("/getbyuser")
    public ResponseEntity<Map<String, Object>> getByUser(
            @RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg", "Data fetched successfully");
            response.put("data", service.getByUser(userId));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // ── Get by CR ID ──────────────────────────────────────────────────────────
    @GetMapping("/getbycrid")
    public ResponseEntity<Map<String, Object>> getByCrId(
            @RequestParam("crId") Long crId) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg", "Data fetched successfully");
            response.put("data", service.getBySrId(crId));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    @DeleteMapping("/delete/{crId}")
    public ResponseEntity<Map<String, Object>> deleteByCrId(
            @PathVariable("crId") Long crId) {

        Map<String, Object> response = new HashMap<>();
        try {
            service.deleteBySrId(crId);
            response.put("success", true);
            response.put("msg", "Chef Requisition deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
    
   

    @GetMapping("/pdf")
    public ResponseEntity<Map<String, Object>> generatePdf(
            @RequestParam("crId")             Long crId,
            @RequestParam("userId")           Long userId,
            @RequestParam("isCompanyDetails") Integer isCompanyDetails,
            @RequestParam("lang") Integer lang,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        try {
            byte[] pdf = service.generatePdfReport(crId, userId, isCompanyDetails, lang);

            String rootPath   = request.getSession().getServletContext().getRealPath("/");
            String folderName = String.valueOf(userId);

            File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");
            if (!dir.exists()) dir.mkdirs();

            String fileName = "chef-requisition-" + System.currentTimeMillis() + ".pdf";
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