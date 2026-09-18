package com.crmportal.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crmportal.response.dto.StockLedgerResponseDto;
import com.crmportal.service.StockLedgerService;
import java.io.File;
import java.nio.file.Files;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;

@RestController
@RequestMapping(value = "/v1/api/stockledger", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class StockLedgerController {

    @Autowired
    private StockLedgerService service;
    
    @Autowired
    private Environment environment;

    // GET /v1/api/stockledger/get?rawMaterialId=10&fromDate=25/03/2026&toDate=25/03/2026
    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> getStockLedger(
            @RequestParam("rawMaterialId") Long rawMaterialId,
            @RequestParam("fromDate") String fromDate,
            @RequestParam("toDate") String toDate,
            @RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();
        try {
            StockLedgerResponseDto data = service.getStockLedger(rawMaterialId, fromDate, toDate, userId);
            response.put("success", true);
            response.put("msg", "Stock ledger fetched successfully");
            response.put("data", data);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
    


    @GetMapping("/pdf")
    public ResponseEntity<Map<String, Object>> generatePdf(
            @RequestParam("rawMaterialId") Long rawMaterialId,
            @RequestParam("fromDate") String fromDate,
            @RequestParam("toDate") String toDate,
            @RequestParam("userId") Long userId,
            @RequestParam("isCompanyDetails") Integer isCompanyDetails,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {
            byte[] pdf = service.generatePdfReport(rawMaterialId, fromDate, toDate, userId, isCompanyDetails);

            String rootPath = request.getSession().getServletContext().getRealPath("/");
            String folderName = String.valueOf(userId);

            File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = "stock-ledger-" + System.currentTimeMillis() + ".pdf";
            File file = new File(dir, fileName);
            Files.write(file.toPath(), pdf);

            String fileUrl = environment.getProperty("ws_image_path")
                    + "/api/download/pdf/" + folderName + "/" + fileName;

            response.put("success", true);
            response.put("msg", "PDF generated successfully");
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