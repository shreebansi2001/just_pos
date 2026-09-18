package com.crmportal.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crmportal.request.dto.SaveRoleReportRightsRequestDto;
import com.crmportal.service.RoleReportRightsService;

@RestController
@RequestMapping(value = "/v1/api/role-report-rights",
                produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = {"*"}, maxAge = 3600L)
public class RoleReportRightsController {

    @Autowired
    private RoleReportRightsService service;

    // ── GET report rights for a role (opens the modal) ────────────────────────
    @GetMapping("/{roleId}")
    public ResponseEntity<Map<String, Object>> getReportRights(
            @PathVariable Long roleId,
            @RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg",     "Report rights fetched successfully");
            response.put("data",    service.getReportRights(roleId, userId));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg",     e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── POST save report rights (Save permissions button) ─────────────────────
    // userId comes from request body only — no separate @RequestParam
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveReportRights(
            @RequestBody SaveRoleReportRightsRequestDto request) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg",     "Report rights saved successfully");
            response.put("data",    service.saveReportRights(request));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg",     e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}