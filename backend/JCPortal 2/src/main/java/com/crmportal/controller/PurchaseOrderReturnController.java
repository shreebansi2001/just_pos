package com.crmportal.controller;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.crmportal.request.dto.PurchaseOrderReturnRequestDto;
import com.crmportal.response.dto.*;
import com.crmportal.service.PurchaseOrderReturnService;

@RestController
@RequestMapping("/v1/api/purchaseorderreturn")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
@RequiredArgsConstructor
public class PurchaseOrderReturnController {

    private final PurchaseOrderReturnService service;
    
    private final Environment environment;

    //--------Auto-fill: user types PO number and presses Enter--------
    @GetMapping("/getpodetails")
    public ResponseEntity<Map<String, Object>> getPoDetailsByPocode(
            @RequestParam("pocode") String pocode,
    		@RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();
        try {
            PurchaseOrderResponseDto po = service.getPoDetailsByPocode(pocode, userId);
            response.put("success", true);
            response.put("msg", "PO details fetched successfully");
            response.put("data", po);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
    
 // --------get all pocodes from purchase order--------
    @GetMapping("/getallpocodes")
    public ResponseEntity<Map<String, Object>> getAllPocodes(@RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg", "PO codes fetched successfully");
            response.put("data", service.getAllPocodes(userId));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // -------- Add / Update Return--------
    @PostMapping("/add-update")
    public ResponseEntity<Map<String, Object>> addOrUpdate(
            @Valid @RequestBody PurchaseOrderReturnRequestDto request) {

        Map<String, Object> response = new HashMap<>();
        try {
            service.addOrUpdate(request);
            response.put("success", true);
            response.put("msg", "Purchase Order Return saved successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // -------- Get by User--------
    @GetMapping("/getbyuser")
    public ResponseEntity<Map<String, Object>> getByUser(
            @RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg", "Purchase Order Returns fetched successfully");
            response.put("data", service.getByUser(userId));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // -------- Get by POR ID --------
    @GetMapping("/getbyporid")
    public ResponseEntity<Map<String, Object>> getByPorId(
            @RequestParam("porId") Long porId) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg", "Purchase Order Return fetched successfully");
            response.put("data", service.getByPorId(porId));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // -------- Get all returns for a specific original PO--------
    @GetMapping("/getbyoriginalpo")
    public ResponseEntity<Map<String, Object>> getByPoId(
            @RequestParam("poId") Long poId) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg", "Purchase Order Returns fetched successfully");
            response.put("data", service.getByPoId(poId));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // --------Delete--------
    @DeleteMapping("/delete/{porId}")
    public ResponseEntity<Map<String, Object>> deleteByPorId(
            @PathVariable("porId") Long porId) {

        Map<String, Object> response = new HashMap<>();
        try {
            service.deleteByPorId(porId);
            response.put("success", true);
            response.put("msg", "Purchase Order Return deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
    
    @GetMapping("/pdf")
    public ResponseEntity<Map<String, Object>> generatePdf(
            @RequestParam("porId")            Long porId,
            @RequestParam("userId")           Long userId,
            @RequestParam("isCompanyDetails") Integer isCompanyDetails,
            @RequestParam("isPrice") Integer isPrice,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        try {
            byte[] pdf = service.generatePdfReport(porId, userId, isCompanyDetails, isPrice);

            String rootPath   = request.getSession().getServletContext().getRealPath("/");
            String folderName = String.valueOf(userId);

            File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");
            if (!dir.exists()) dir.mkdirs();

            String fileName = "purchase-return-" + System.currentTimeMillis() + ".pdf";
            File file = new File(dir, fileName);
            Files.write(file.toPath(), pdf);

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
    
    @GetMapping("/excel")
    public ResponseEntity<Map<String, Object>> generateExcel(
            @RequestParam("porId")  Long porId,
            @RequestParam("userId") Long userId,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        try {
            byte[] excel = service.generateExcelReport(porId, userId);

            String rootPath   = request.getSession().getServletContext().getRealPath("/");
            String folderName = String.valueOf(userId);

            File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");
            if (!dir.exists()) dir.mkdirs();

            String fileName = "purchase-return-" + System.currentTimeMillis() + ".xlsx";
            Files.write(new File(dir, fileName).toPath(), excel);

            String fileUrl = environment.getProperty("ws_image_path")
                    + "/api/download/excel/" + folderName + "/" + fileName;

            response.put("success", true);
            response.put("msg",     "Excel generated successfully");
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