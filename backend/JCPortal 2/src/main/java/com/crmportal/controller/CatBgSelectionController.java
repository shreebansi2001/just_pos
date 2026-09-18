package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.CatBgSelectionRequestDto;
import com.crmportal.response.dto.CatBgSelectionResponseDto;
import com.crmportal.service.CatBgSelectionService;

@RestController
@RequestMapping("/v1/api/catbgselection")
@CrossOrigin(origins = "*", maxAge = 3600L)
public class CatBgSelectionController {

    @Autowired
    private CatBgSelectionService service;

 // ── Add / Update ──────────────────────────────────────────────────────────
    @PostMapping(value = "/add-update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> addOrUpdate(
            @RequestParam(value = "id",           required = false) Long id,
            @RequestParam(value = "categoryName")                   String categoryName,
            @RequestParam(value = "isCatImg")                       Boolean isCatImg,
            @RequestParam(value = "userId")                         Long userId,
            @RequestParam(value = "image",        required = false) MultipartFile image) {

        Map<String, Object> response = new HashMap<>();
        try {
            CatBgSelectionRequestDto request = new CatBgSelectionRequestDto();
            request.setId(id != null ? id : -1L);
            request.setCategoryName(categoryName);
            request.setIsCatImg(isCatImg);

            CatBgSelectionResponseDto dto = service.addOrUpdate(request, image, userId);

            response.put("success", true);
            response.put("msg",     "Saved successfully");
            response.put("data",    dto);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── Get All (with optional isCatImg filter) ───────────────────────────────
    // ?userId=1                      → all records for user
    // ?userId=1&isCatImg=true        → category images only
    // ?userId=1&isCatImg=false       → background images only
    @GetMapping("/getall")
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam("userId")                                 Long userId,
            @RequestParam(value = "isCatImg", required = false)     Boolean isCatImg) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<CatBgSelectionResponseDto> list = service.getAll(userId, isCatImg);
            response.put("success", true);
            response.put("msg",  "Data fetched successfully");
            response.put("data", list);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── Get by Type ───────────────────────────────────────────────────────────
    @GetMapping("/getbytype")
    public ResponseEntity<Map<String, Object>> getByType(
            @RequestParam("userId")                                 Long userId,
            @RequestParam(value = "isCatImg", required = false)     Boolean isCatImg) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<CatBgSelectionResponseDto> list = service.getByType(userId, isCatImg);
            response.put("success", true);
            response.put("msg",  "Data fetched successfully");
            response.put("data", list);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── Get by ID ─────────────────────────────────────────────────────────────
    @GetMapping("/getbyid/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            CatBgSelectionResponseDto dto = service.getById(id);
            response.put("success", true);
            response.put("msg",  "Data fetched successfully");
            response.put("data", dto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            service.delete(id);
            response.put("success", true);
            response.put("msg", "Deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}