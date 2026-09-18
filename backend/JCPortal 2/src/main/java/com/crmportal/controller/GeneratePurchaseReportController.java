package com.crmportal.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.service.PurchaseReportService;

@RestController
@RequestMapping("/v1/api/purchasereport")
@CrossOrigin(origins = {"*"}, maxAge = 3600L)
public class GeneratePurchaseReportController {

    @Autowired
    private PurchaseReportService purchaseReportService;

    /**
     * POST /v1/api/purchasereport/generate
     *
     * Params:
     *   poId   — Purchase Order ID
     *   lang   — 0 = English, 1 = Hindi, 2 = Gujarati
     *   userId — Logged-in user ID
     */
    @PostMapping("/generate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generatePurchaseReport(
            @RequestParam("poId")   Long poId,
            @RequestParam("lang")   int  lang,
            @RequestParam("userId") Long userId,
            HttpServletRequest re) {

        Map<String, Object> response = new HashMap<>();

        try {
            String reportPath = purchaseReportService.generatePurchaseOrderReport(poId, lang, userId, re);

            if (reportPath != null && !reportPath.trim().isEmpty()) {
                response.put("success",     true);
                response.put("report_path", reportPath);
                response.put("msg",         "Purchase Order report generated successfully");
            } else {
                response.put("success", false);
                response.put("msg",     "Purchase Order report generation failed");
            }

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg",     e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg",     e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/generate-datewise-puchase-report")
    public ResponseEntity<?> generateDatewisePurchaseReport(
    		@RequestParam("userId") Long userId,
    		@RequestParam("startDate") String startDate,
    		@RequestParam("endDate") String endDate,
    		@RequestParam("isCompanyDetails") Integer isCompanyDetails,
    		@RequestParam("isPrice") Integer isWithPrice,
    		HttpServletRequest re) {
    	Map<String, Object> response = new HashMap<>();

        try {
			String reportPath = purchaseReportService.generateDatewisePurchaseReport(userId, startDate, endDate,
					re, isCompanyDetails, isWithPrice);

            if (reportPath != null && !reportPath.trim().isEmpty()) {
                response.put("success",     true);
                response.put("report_path", reportPath);
                response.put("msg",         "Purchase Order report generated successfully");
            } else {
                response.put("success", false);
                response.put("msg",     "Purchase Order report generation failed");
            }

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg",     e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg",     e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}