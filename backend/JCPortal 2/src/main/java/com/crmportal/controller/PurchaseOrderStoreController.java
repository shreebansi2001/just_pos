package com.crmportal.controller;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.PdfWithPriceRequestDto;
import com.crmportal.request.dto.PurchaseOrderStoreRequestDto;
import com.crmportal.response.dto.EventPartiesResponseDto;
import com.crmportal.response.dto.PurchaseOrderStoreResponseDto;
import com.crmportal.service.PurchaseOrderService;
import com.crmportal.service.PurchaseOrderStoreService;

@RestController
@RequestMapping("/v1/api/storepo")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class PurchaseOrderStoreController {

    @Autowired
    private PurchaseOrderStoreService service;
    
    @Autowired
    private Environment environment;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addOrUpdate(
            @Valid @RequestBody PurchaseOrderStoreRequestDto request) {

        Map<String, Object> response = new HashMap<>();

        try {
            PurchaseOrderStoreResponseDto dto = service.addOrUpdate(request);

            response.put("msg", "Purchase order saved successfully");
            response.put("success", true);
            response.put("data", dto);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getbyuser")
    public ResponseEntity<Map<String, Object>> getByUser(@RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<PurchaseOrderStoreResponseDto> list = service.getByUser(userId);

            response.put("success", true);
            response.put("msg", "Data fetched successfully");
            response.put("data", list);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getbypoid")
    public ResponseEntity<Map<String, Object>> getByPoId(@RequestParam("poId") Long poId) {

        Map<String, Object> response = new HashMap<>();

        try {
            PurchaseOrderStoreResponseDto dto = service.getByPoId(poId);

            response.put("success", true);
            response.put("msg", "Data fetched successfully");
            response.put("data", dto);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @DeleteMapping("/delete/{poId}")
    public ResponseEntity<Map<String, Object>> deleteByPoId(
            @PathVariable("poId") Long poId) {

        Map<String, Object> response = new HashMap<>();

        try {
            service.deleteByPoId(poId);

            response.put("msg", "Purchase Order deleted successfully");
            response.put("success", true);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {

            response.put("success", false);
            response.put("msg", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
    
    @GetMapping("/pdf")
    public ResponseEntity<Map<String, Object>> generatePdf(
            @RequestParam("poId")             Long poId,
            @RequestParam("userId")           Long userId,
            @RequestParam("isCompanyDetails") Integer isCompanyDetails,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        try {
            byte[] pdf = service.generatePdfReport(poId, userId, isCompanyDetails);

            String rootPath   = request.getSession().getServletContext().getRealPath("/");
            String folderName = String.valueOf(userId);

            File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");
            if (!dir.exists()) dir.mkdirs();

            String fileName = "store-issue-" + System.currentTimeMillis() + ".pdf";
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
    
    @PostMapping("/updatestatus/{poId}")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable Long poId,
            @RequestParam("status") String status) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg", "Status updated successfully");
            response.put("data", service.updateStatus(poId, status));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
 // GET all CR codes
    @GetMapping("/getallcrcodes")
    public ResponseEntity<Map<String, Object>> getAllCrcodes(
            @RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg", "CR codes fetched successfully");
            response.put("data", service.getAllCrcodes(userId));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // GET CR details by crcode
    @GetMapping("/getbycrcode")
    public ResponseEntity<Map<String, Object>> getByCrcode(
            @RequestParam("crcode") String crcode,
            @RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            response.put("success", true);
            response.put("msg", "Chef Requisition details fetched successfully");
            response.put("data", service.getByCrcode(crcode, userId));

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            response.put("success", false);
            response.put("msg", e.getMessage());

            return ResponseEntity.ok(response);
        }
    }
    
    @GetMapping("/excel")
    public ResponseEntity<Map<String, Object>> generateExcel(
            @RequestParam("poId") Long poId,
            @RequestParam("userId") Long userId,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {

            byte[] excel = service.generateExcelReport(poId, userId);

            String rootPath = request.getSession()
                    .getServletContext()
                    .getRealPath("/");

            String folderName = String.valueOf(userId);

            File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");

            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = "store-issue-" + System.currentTimeMillis() + ".xlsx";

            Files.write(new File(dir, fileName).toPath(), excel);

            String fileUrl = environment.getProperty("ws_image_path")
                    + "/api/download/excel/" + folderName + "/" + fileName;

            response.put("success", true);
            response.put("msg", "Excel generated successfully");
            response.put("fileUrl", fileUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put("success", false);
            response.put("msg", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
    
 // ── GET prices for a store PO ─────────────────────────────────────────────
    @GetMapping("/getprices")
    public ResponseEntity<Map<String, Object>> getPricesForPo(
            @RequestParam("poId") Long poId,
            @RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg", "Prices fetched successfully");
            response.put("data", service.getPricesForPo(poId, userId));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/pdfwithprice")
    public ResponseEntity<Map<String, Object>> generatePdfWithPrice(
            @RequestBody PdfWithPriceRequestDto request,
            @RequestParam("userId") Long userId,
            HttpServletRequest httpRequest) {

        Map<String, Object> response = new HashMap<>();

        try {

            byte[] pdf =
                    service.generatePdfReportWithPrice(request, userId);

            String rootPath =
                    httpRequest.getSession()
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
                    "store-issue-" + System.currentTimeMillis() + ".pdf";

            Files.write(new File(dir, fileName).toPath(), pdf);

            String fileUrl =
                    environment.getProperty("ws_image_path")
                            + "/api/download/pdf/"
                            + folderName
                            + "/"
                            + fileName;

            response.put("success", true);
            response.put("msg", "PDF generated successfully");
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
    
    @GetMapping("/getallparties")
    public ResponseEntity<?> getAllPartiesWithEvent(@RequestParam("userId") Long userId) {
    	Map<String, Object> response = new HashMap<>();

        try {
        	List<EventPartiesResponseDto> res = service.getAllPartiesWithEvent(userId);
        	
            response.put("success", true);
            response.put("msg", "Parties found successfully.");
            response.put("data", res);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();

            response.put("success", false);
            response.put("msg", e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
 // GET PO details by prcode
    @GetMapping("/getbypocode")
    public ResponseEntity<Map<String, Object>> getByPocode(
            @RequestParam("pocode") String pocode,
            @RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            response.put("success", true);
            response.put("msg", "Purchase details fetched successfully");
            response.put("data", service.getByPocode(pocode, userId));

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            response.put("success", false);
            response.put("msg", e.getMessage());

            return ResponseEntity.ok(response);
        }
    }
    
    @PostMapping("/datewiseStoreIssueReport")
    public ResponseEntity<Map<String, Object>> generateDatewiseStoreIssueReport(
    		@RequestParam("startDate") String startDate,
    		@RequestParam("endDate") String endDate,
    		@RequestParam("userId") Long userId,
    		@RequestParam("isCompanyDetails") Integer isCompanyDetails,
    		@RequestParam("isWithPrice") Integer isWithPrice,
    		@RequestParam("priceType") String priceType,
    		@RequestParam("kitchenTypeId") Long kitchenTypeId,
    		HttpServletRequest re) {
    	Map<String, Object> response = new HashMap<>();

        try {
			String reportPath = service.generateDatewiseStoreIssueReport(startDate, endDate, userId, isCompanyDetails, isWithPrice, priceType, re, kitchenTypeId);

            if (reportPath != null && !reportPath.trim().isEmpty()) {
                response.put("success",     true);
                response.put("report_path", reportPath);
                response.put("msg",         "Store Issue report generated successfully");
            } else {
                response.put("success", false);
                response.put("msg",     "Store Issue report generation failed");
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