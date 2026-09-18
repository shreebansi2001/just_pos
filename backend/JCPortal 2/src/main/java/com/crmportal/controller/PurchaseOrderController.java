package com.crmportal.controller;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import com.crmportal.request.dto.PurchaseOrderRequestDto;
import com.crmportal.response.dto.CashOpbResponseDto;
import com.crmportal.response.dto.PurchaseOrderRawMaterialDetailsResponseDto;
import com.crmportal.response.dto.PurchaseOrderResponseDto;
import com.crmportal.service.PurchaseOrderService;
import com.crmportal.utility.ConstantsPoc;

import java.io.File;
import java.nio.file.Files;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;

@RestController
@RequestMapping("/v1/api/purchaseorder")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService service;
    
    private final Environment environment;

    // ================= ADD / UPDATE =================

    @PostMapping("/add-update")
    public ResponseEntity<Map<String, Object>> addOrUpdate(
            @Valid @RequestBody PurchaseOrderRequestDto request) {

        Map<String, Object> response = new HashMap<>();

        try {
System.out.println("inn");
            service.addOrUpdate(request, 0);

            response.put("msg", "Purchase Order saved successfully");
            response.put("success", true);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
        	e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
    
    @PostMapping("/manual-add-update")
    public ResponseEntity<Map<String, Object>> manualAddOrUpdate(
            @Valid @RequestBody PurchaseOrderRequestDto request) {

        Map<String, Object> response = new HashMap<>();

        try {

            service.addOrUpdate(request, 1);

            response.put("msg", "Purchase Order saved successfully");
            response.put("success", true);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {

            response.put("success", false);
            response.put("msg", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // ================= GET BY USER =================

    @GetMapping("/getbyuser")
    public ResponseEntity<Map<String, Object>> getByUser(
    		@RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {

            List<PurchaseOrderResponseDto> list = service.getByUser(userId);

            response.put("msg", "Purchase Orders fetched successfully");
            response.put("success", true);
            response.put("data", list);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {

            response.put("success", false);
            response.put("msg", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
    
    @GetMapping("/getbyuserandtype")
    public ResponseEntity<Map<String, Object>> getbyuserandtype(@RequestParam("userId") Long userId, @RequestParam("potype") int potype) {

        Map<String, Object> response = new HashMap<>();

        try {

            List<PurchaseOrderResponseDto> list = service.getByUserAndPotype(userId, potype);

            response.put("msg", "Purchase Orders fetched successfully");
            response.put("success", true);
            response.put("data", list);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {

            response.put("success", false);
            response.put("msg", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
    
    @GetMapping("/getManualPO")
    public ResponseEntity<Map<String, Object>> getManualPO(@RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {

            List<PurchaseOrderResponseDto> list = service.getByUserAndPotype(userId, 1);

            response.put("msg", "Purchase Orders fetched successfully");
            response.put("success", true);
            response.put("data", list);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {

            response.put("success", false);
            response.put("msg", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // ================= GET BY PO ID =================

    @GetMapping("/getbypoid")
    public ResponseEntity<Map<String, Object>> getByPoId(
    		@RequestParam("poId") Long poId) {

        Map<String, Object> response = new HashMap<>();

        try {

            List<PurchaseOrderResponseDto> list = service.getByPoId(poId);

            response.put("msg", "Purchase Order fetched successfully");
            response.put("success", true);
            response.put("data", list);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {

            response.put("success", false);
            response.put("msg", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // ================= GET BY PO TYPE =================

    @GetMapping("/getbypotype")
    public ResponseEntity<Map<String, Object>> getByPoType(
    		@RequestParam("potype") int potype) {

        Map<String, Object> response = new HashMap<>();

        try {

            List<PurchaseOrderResponseDto> list = service.getByPoType(potype);

            response.put("msg", "Purchase Orders fetched successfully");
            response.put("success", true);
            response.put("data", list);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {

            response.put("success", false);
            response.put("msg", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
    
    // ================= DELETE BY ID =================
    
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
            @RequestParam("poId")   Long poId,
            @RequestParam("userId") Long userId,
            @RequestParam("isCompanyDetails") Integer isCompanyDetails,
            @RequestParam("isPrice") Integer isPrice,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {
        	 byte[] pdf = service.generatePdfReport(poId, userId, isCompanyDetails, isPrice);

            String rootPath  = request.getSession().getServletContext().getRealPath("/");
            String folderName = String.valueOf(userId);

            File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");
            if (!dir.exists()) dir.mkdirs();

            String fileName = "purchase-order-" + System.currentTimeMillis() + ".pdf";
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
            @RequestParam("poId") Long poId,
            @RequestParam("userId") Long userId,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {

            byte[] excel = service.generateExcelReport(
                    poId,
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
                    "purchase-order-" + System.currentTimeMillis() + ".xlsx";

            File file = new File(dir, fileName);

            Files.write(file.toPath(), excel);

            String fileUrl =
                    environment.getProperty("ws_image_path")
                    + "/api/download/excel/"
                    + folderName + "/"
                    + fileName;

            response.put("success", true);
            response.put("msg", "Excel generated successfully");
            response.put("fileUrl", fileUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("success", false);
            response.put("msg", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
    
    @GetMapping("/get-raw-material-price")
	public ResponseEntity<?> getRawMaterialPrice(@RequestParam("supplierId") Long supplierId,
			@RequestParam("rawMaterialId") Long rawMaterialId, @RequestParam("userId") Long userId) {
    	Map<String, Object> response = new HashMap<>();

        try {

            PurchaseOrderRawMaterialDetailsResponseDto data = service.getRawMaterialPrice(supplierId, rawMaterialId, userId);

            response.put("msg", "Purchase Orders fetched successfully");
            response.put("success", true);
            response.put("data", data);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {

            response.put("success", false);
            response.put("msg", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
    
    @GetMapping("/generateGrnNumber")
    public ResponseEntity<?> generateGrnNumber(@RequestParam("userId") Long userId) {
    	Map<String, Object> response = new HashMap<>();
        try {
        	String grnNumber = service.generatePoCode(userId, "GRN");
            
        	if (grnNumber != null) {
                response.put("msg", ConstantsPoc.GRN_NUMBER_CREATED_SUCCESS);
                response.put("success", true);
                response.put("grnNumber", grnNumber);
            } else {
                response.put("msg", ConstantsPoc.GRN_NUMBER_CREATED_FAIL);
                response.put("success", false);
            }

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
        	e.printStackTrace();
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