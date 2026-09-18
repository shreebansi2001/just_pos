package com.crmportal.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crmportal.request.dto.ExtraChargesSaveRequestDto;
import com.crmportal.response.dto.ExtraChargesResponseDto;
import com.crmportal.service.ExtraChargesService;

@RestController
@RequestMapping("/v1/api/extracharges")
@CrossOrigin(origins = "*", maxAge = 3600L)
public class ExtraChargesController {

    @Autowired
    private ExtraChargesService extraChargesService;

    @PostMapping("/saveOrUpdate")
    public ResponseEntity<Map<String, Object>> saveOrUpdateExtraCharges(
            @Valid @RequestBody ExtraChargesSaveRequestDto request) {

        Map<String, Object> response = new HashMap<>();
        try {
            ExtraChargesResponseDto data = extraChargesService.saveOrUpdateExtraCharges(request);
            response.put("success", true);
            response.put("msg", "Extra Charges saved successfully");
            response.put("data", data);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> getExtraCharges(
            @RequestParam("eventId") Long eventId,
            @RequestParam("eventFunctionId") Long eventFunctionId,
            @RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();
        try {
            ExtraChargesResponseDto data = extraChargesService.getExtraCharges(eventId, eventFunctionId, userId);

            if (data.getHeadings() == null || data.getHeadings().isEmpty()) {
                response.put("success", false);
                response.put("msg", "No Extra Charges found");
            } else {
                response.put("success", true);
                response.put("msg", "Extra Charges fetched successfully");
                response.put("data", data);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteHeading")
    public ResponseEntity<Map<String, Object>> deleteHeading(
            @RequestParam("headingId") Long headingId) {

        Map<String, Object> response = new HashMap<>();
        try {
            Boolean success = extraChargesService.deleteHeading(headingId);
            response.put("success", success);
            response.put("msg", success ? "Heading deleted successfully" : "Heading deletion failed");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteRow")
    public ResponseEntity<Map<String, Object>> deleteRow(
            @RequestParam("rowId") Long rowId) {

        Map<String, Object> response = new HashMap<>();
        try {
            Boolean success = extraChargesService.deleteRow(rowId);
            response.put("success", success);
            response.put("msg", success ? "Row deleted successfully" : "Row deletion failed");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}