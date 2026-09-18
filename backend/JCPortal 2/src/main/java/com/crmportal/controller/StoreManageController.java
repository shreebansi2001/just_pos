package com.crmportal.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crmportal.request.dto.StoreManageRequestDto;
import com.crmportal.response.dto.StoreManageCategoryDto;
import com.crmportal.service.StoreManageService;

@RestController
@RequestMapping("/v1/api/store-manage")
@CrossOrigin(origins = "*", maxAge = 3600L)
public class StoreManageController {

    @Autowired
    private StoreManageService service;

    // ── Load today's items (for MANAGE TODAY'S STOCK screen) ─────────────
    @GetMapping("/load-today")
    public ResponseEntity<Map<String, Object>> loadToday(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam("categoryId") Long categoryId, @RequestParam(value = "itemName", defaultValue = "")
            String itemName,@RequestParam(value = "stockTypeId", required = false) Long stockTypeId) {

        Map<String, Object> response = new HashMap<>();

        try {

            Pageable pageable = PageRequest.of(page, size);

            Page<StoreManageCategoryDto> result =
                    service.loadTodayItems(userId, pageable,categoryId,itemName,stockTypeId);

            response.put("success", true);
            response.put("msg", "Items loaded successfully");
            response.put("data", result.getContent());
            response.put("currentPage", result.getNumber());
            response.put("totalPages", result.getTotalPages());
            response.put("totalElements", result.getTotalElements());

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("success", false);
            response.put("msg", e.getMessage());

            return ResponseEntity.ok(response);
        }
    }

    // ── Save store manage ─────────────────────────────────────────────────
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> save(
            @RequestBody StoreManageRequestDto request) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg",  "Store manage saved successfully");
            response.put("data", service.save(request));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── Get all entries for a user ────────────────────────────────────────
    @GetMapping("/getall")
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg",  "Data fetched successfully");
            response.put("data", service.getAll(userId));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
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
            response.put("msg",  "Data fetched successfully");
            response.put("data", service.getById(id));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}