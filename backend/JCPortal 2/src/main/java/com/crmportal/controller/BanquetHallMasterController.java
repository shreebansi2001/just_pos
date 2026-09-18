package com.crmportal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.BanquetHallRequestDto;
import com.crmportal.service.BanquetHallMasterService;

@RestController
@RequestMapping("/v1/api/banquethall")
@CrossOrigin(origins = "*", maxAge = 3600L)
public class BanquetHallMasterController {

    @Autowired
    private BanquetHallMasterService service;

    // ── Add / Update ──────────────────────────────────────────────────────────
    @PostMapping(value = "/add-update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> addOrUpdate(
            @RequestParam(value = "id",               required = false) Long    id,
            @RequestParam(value = "hallName")                           String  hallName,
            @RequestParam(value = "capacity",         required = false) Integer capacity,
            @RequestParam(value = "morningPrice",     required = false) Float   morningPrice,
            @RequestParam(value = "eveningPrice",     required = false) Float   eveningPrice,
            @RequestParam(value = "fullDayPrice",     required = false) Float   fullDayPrice,
            @RequestParam(value = "exhibitionPrice",  required = false) Float   exhibitionPrice,
            @RequestParam(value = "corporatePrice",   required = false) Float   corporatePrice,
            @RequestParam(value = "extraChargesPerHr",required = false) Float   extraChargesPerHr,
            @RequestParam(value = "password",         required = false) String  password,
            @RequestParam(value = "userId")                             Long    userId,
            @RequestParam(value = "isActive", required = false, defaultValue = "true") Boolean isActive,
            @RequestParam(value = "images",           required = false)
                    List<MultipartFile> images) {

        Map<String, Object> response = new HashMap<>();
        try {
            BanquetHallRequestDto request = new BanquetHallRequestDto();
            request.setId(id != null ? id : -1L);
            request.setHallName(hallName);
            request.setCapacity(capacity);
            request.setMorningPrice(morningPrice);
            request.setEveningPrice(eveningPrice);
            request.setFullDayPrice(fullDayPrice);
            request.setExhibitionPrice(exhibitionPrice);
            request.setCorporatePrice(corporatePrice);
            request.setExtraChargesPerHr(extraChargesPerHr);
            request.setPassword(password);
            request.setUserId(userId);
            request.setIsActive(isActive);

            response.put("success", true);
            response.put("msg",     "Saved successfully");
            response.put("data",    service.addOrUpdate(request, images));
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── Get All ───────────────────────────────────────────────────────────────
    @GetMapping("/getall")
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg",     "Data fetched successfully");
            response.put("data",    service.getAll(userId));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── Get by ID ─────────────────────────────────────────────────────────────
    @GetMapping("/getbyid/{id}")
    public ResponseEntity<Map<String, Object>> getById(
            @PathVariable Long id,
            @RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg",     "Data fetched successfully");
            response.put("data",    service.getById(id, userId));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── Delete Hall ───────────────────────────────────────────────────────────
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> delete(
            @PathVariable Long id,
            @RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();
        try {
            service.delete(id, userId);
            response.put("success", true);
            response.put("msg",     "Deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── Delete Single Image ───────────────────────────────────────────────────
    @DeleteMapping("/deleteimage/{imageId}")
    public ResponseEntity<Map<String, Object>> deleteImage(
            @PathVariable Long imageId) {

        Map<String, Object> response = new HashMap<>();
        try {
            service.deleteImage(imageId);
            response.put("success", true);
            response.put("msg",     "Image deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── Verify Password ───────────────────────────────────────────────────────
    @PostMapping("/verify-password")
    public ResponseEntity<Map<String, Object>> verifyPassword(
            @RequestParam("hallName")   String   hallName,
            @RequestParam("password") String password) {

        Map<String, Object> response = new HashMap<>();
        try {
            boolean isValid = service.verifyPassword(hallName, password);
            response.put("success", true);
            response.put("valid",   isValid);
            response.put("msg",     isValid ? "Password matched" : "Invalid password");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
 // ── Toggle Active Status ──────────────────────────────────────────────────
    @PostMapping("/toggle-status/{id}")
    public ResponseEntity<Map<String, Object>> toggleStatus(
            @PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg",     "Status updated successfully");
            response.put("data",    service.toggleStatus(id));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
 // ── Get hall names for event dropdown (includes static ODC option) ────────
    @GetMapping("/gethallwithodc")
    public ResponseEntity<Map<String, Object>> getHallNameWithOdc(
            @RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> list = new ArrayList<>();

            // ── Static ODC option first ───────────────────────────────────────
            Map<String, Object> odc = new HashMap<>();
            odc.put("id",       null);
            odc.put("hallName", "ODC");
            list.add(odc);

            // ── Actual halls ──────────────────────────────────────────────────
            service.getAll(userId).forEach(hall -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id",       hall.getId());
                item.put("hallName", hall.getHallName());
                list.add(item);
            });

            response.put("success", true);
            response.put("msg",     "Hall list fetched successfully");
            response.put("data",    list);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}