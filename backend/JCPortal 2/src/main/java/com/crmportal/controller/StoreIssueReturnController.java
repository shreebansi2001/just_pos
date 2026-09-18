package com.crmportal.controller;

import java.util.*;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.crmportal.request.dto.StoreIssueReturnRequestDto;
import com.crmportal.response.dto.*;
import com.crmportal.service.StoreIssueReturnService;

import java.io.File;
import java.nio.file.Files;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping(value = "/v1/api/storeissuereturn", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class StoreIssueReturnController {

    @Autowired
    private StoreIssueReturnService service;
    
    @Autowired
    private Environment environment;


    // ── All pocodes dropdown (space/enter trigger) ─────────────────────────────
    @GetMapping("/getallpocodes")
    public ResponseEntity<Map<String, Object>> getAllStorePocodes(@RequestParam("userId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("pocodes", service.getAllStorePocodes(userId));  // List<String>

            response.put("success", true);
            response.put("msg", "Store PO codes fetched successfully");
            response.put("data", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // ── Auto-fill: type pocode → get party, invoicetype, items ────────────────
    // GET /v1/api/storeissuereturn/getstoreissuedetails?pocode=SPO-24032026-001
    @GetMapping("/getstoreissuedetails")
    public ResponseEntity<Map<String, Object>> getStoreIssueDetailsByPocode(
            @RequestParam("pocode") String pocode,
            @RequestParam("userId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            PurchaseOrderStoreResponseDto dto = service.getStoreIssueDetailsByPocode(pocode, userId);
            response.put("success", true);
            response.put("msg", "Store Issue details fetched successfully");
            response.put("data", dto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // ── Add / Update ──────────────────────────────────────────────────────────
    @PostMapping("/add-update")
    public ResponseEntity<Map<String, Object>> addOrUpdate(
            @Valid @RequestBody StoreIssueReturnRequestDto request) {
        Map<String, Object> response = new HashMap<>();
        try {
            StoreIssueReturnResponseDto dto = service.addOrUpdate(request);
            response.put("success", true);
            response.put("msg", "Store Issue Return saved successfully");
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

    // ── Get by SIR ID (for Edit/View/Print) ───────────────────────────────────
    @GetMapping("/getbysir")
    public ResponseEntity<Map<String, Object>> getBySirId(
            @RequestParam("sirId") Long sirId) {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg", "Data fetched successfully");
            response.put("data", service.getBySirId(sirId));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // ── Get all returns for one original Store Issue ───────────────────────────
    @GetMapping("/getbystoreissue")
    public ResponseEntity<Map<String, Object>> getByStoreIssueId(
            @RequestParam("storeIssueId") Long storeIssueId,
            @RequestParam("userId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg", "Data fetched successfully");
            response.put("data", service.getByStoreIssueId(storeIssueId, userId));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    @DeleteMapping("/delete/{sirId}")
    public ResponseEntity<Map<String, Object>> deleteBySirId(
            @PathVariable("sirId") Long sirId) {
        Map<String, Object> response = new HashMap<>();
        try {
            service.deleteBySirId(sirId);
            response.put("success", true);
            response.put("msg", "Store Issue Return deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
    
 // Add fields:
    
    @GetMapping("/pdf")
    public ResponseEntity<Map<String, Object>> generatePdf(
            @RequestParam("sirId")            Long sirId,
            @RequestParam("userId")           Long userId,
            @RequestParam("isCompanyDetails") Integer isCompanyDetails,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        try {
            byte[] pdf = service.generatePdfReport(sirId, userId, isCompanyDetails);

            String rootPath   = request.getSession().getServletContext().getRealPath("/");
            String folderName = String.valueOf(userId);

            File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");
            if (!dir.exists()) dir.mkdirs();

            String fileName = "store-issue-return-" + System.currentTimeMillis() + ".pdf";
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
    
    @GetMapping("/pdf-by-storepo")
    public ResponseEntity<Map<String, Object>> generatePdfByStorePo(
            @RequestParam("storePoId")        Long storePoId,
            @RequestParam("userId")           Long userId,
            @RequestParam("isCompanyDetails") Integer isCompanyDetails,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        try {
            byte[] pdf = service.generatePdfReportByStorePoId(storePoId, userId, isCompanyDetails);

            String rootPath   = request.getSession().getServletContext().getRealPath("/");
            String folderName = String.valueOf(userId);

            File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");
            if (!dir.exists()) dir.mkdirs();

            String fileName = "store-issue-return-" + storePoId + "-" + System.currentTimeMillis() + ".pdf";
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
            response.put("msg", "Store Issue Return Not Generated");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/excel")
    public ResponseEntity<Map<String, Object>> generateExcel(
            @RequestParam("sirId") Long sirId,
            @RequestParam("userId") Long userId,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {

            byte[] excel = service.generateExcelReport(
                    sirId,
                    userId
            );

            String rootPath = request.getSession()
                    .getServletContext()
                    .getRealPath("/");

            String folderName = String.valueOf(userId);

            File dir = new File(
                    rootPath + "resources/tempDownload/" + folderName + "/"
            );

            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName =
                    "store-issue-return-" +
                    System.currentTimeMillis() +
                    ".xlsx";

            Files.write(new File(dir, fileName).toPath(), excel);

            String fileUrl =
                    environment.getProperty("ws_image_path")
                    + "/api/download/excel/"
                    + folderName
                    + "/"
                    + fileName;

            response.put("success", true);
            response.put("msg", "Excel generated successfully");
            response.put("fileUrl", fileUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put("success", false);
            response.put("msg", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}