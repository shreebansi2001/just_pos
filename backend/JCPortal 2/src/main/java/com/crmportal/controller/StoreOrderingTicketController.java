package com.crmportal.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.crmportal.request.dto.*;
import com.crmportal.response.dto.*;
import com.crmportal.service.StoreOrderingTicketService;
import java.io.File;
import java.nio.file.Files;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping(value = "/v1/api/sot", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class StoreOrderingTicketController {

	@Autowired
	private StoreOrderingTicketService service;
	
	@Autowired
	private Environment environment;

	// ── Generate SOT from event raw material ──────────────────────────────────
	@PostMapping("/generate")
	public ResponseEntity<Map<String, Object>> generateSot(@RequestBody GenerateSotRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "SOT generated successfully");
			response.put("data", service.generateSot(request));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	// ── Accept SOT ────────────────────────────────────────────────────────────
	@PostMapping("/accept")
	public ResponseEntity<Map<String, Object>> acceptSot(@RequestBody AcceptSotRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "SOT accepted successfully");
			response.put("data", service.acceptSot(request));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	// ── Generate Manual PO agency wise ────────────────────────────────────────
	@PostMapping("/generate-manual-po/{sotId}")
	public ResponseEntity<Map<String, Object>> generateManualPo(@PathVariable Long sotId,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "Manual PO generated successfully");
			response.put("data", service.generateManualPo(sotId, userId));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	// ── Get Auto/Manual PO list by SOT ─────────────────────────────────────────────

	@GetMapping("/manual-po/")
	public ResponseEntity<Map<String, Object>> getManualPoBySot(

			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "Manual PO fetched successfully");
			response.put("data", service.getManualPoBySot(userId));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	// ── Get Manual PO by ID ───────────────────────────────────────────────────

	@GetMapping("/manual-po/detail/{sotPoId}")
	public ResponseEntity<Map<String, Object>> getManualPoById(

			@PathVariable Long sotPoId) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "Manual PO details fetched successfully");
			response.put("data", service.getManualPoById(sotPoId));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	// ── Generate Purchase Invoice ─────────────────────────────────────────────

	@PostMapping("/generate-invoice")
	public ResponseEntity<Map<String, Object>> generatePurchaseInvoice(

			@RequestBody GeneratePurchaseInvoiceRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			service.generatePurchaseInvoice(request);
			response.put("success", true);
			response.put("msg", "Purchase invoice generated successfully");
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	// ── Return SOT ────────────────────────────────────────────────────────────
	@PostMapping("/return")
	public ResponseEntity<Map<String, Object>> returnSot(@RequestBody ReturnSotRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "SOT returned successfully");
			response.put("data", service.returnSot(request));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	// ── Get All SOTs ──────────────────────────────────────────────────────────
	@GetMapping("/getall")
	public ResponseEntity<Map<String, Object>> getAll(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "SOTs fetched successfully");
			response.put("data", service.getAll(userId));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	// ── Get SOT by ID ─────────────────────────────────────────────────────────
	@GetMapping("/getbyid/{sotId}")
	public ResponseEntity<Map<String, Object>> getById(@PathVariable Long sotId) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "SOT fetched successfully");
			response.put("data", service.getById(sotId));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	// ── Get SOTs by Event ─────────────────────────────────────────────────────
	@GetMapping("/getbyevent/{eventId}")
	public ResponseEntity<Map<String, Object>> getByEvent(@PathVariable Long eventId) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "SOTs fetched successfully");
			response.put("data", service.getByEvent(eventId));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	// ── Store Report ──────────────────────────────────────────────────────────

	@GetMapping("/storereport/{sotId}")
	public ResponseEntity<Map<String, Object>> getStoreReport(

			@PathVariable Long sotId) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "Store report fetched successfully");
			response.put("data", service.getStoreReport(sotId));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	@PostMapping("/update-details")
	public ResponseEntity<Map<String, Object>> updateSotDetails(@RequestBody UpdateSotDetailsRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "SOT details updated successfully");
			response.put("data", service.updateSotDetails(request));
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	// ── Delete SOT ────────────────────────────────────────────────────────────
	@DeleteMapping("/delete/{sotId}")
	public ResponseEntity<Map<String, Object>> deleteSot(@PathVariable Long sotId) {
		Map<String, Object> response = new HashMap<>();
		try {
			service.deleteSot(sotId);
			response.put("success", true);
			response.put("msg", "SOT deleted successfully");
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	// ── Delete Manual PO ──────────────────────────────────────────────────────

	@DeleteMapping("/manual-po/delete/{sotPoId}")
	public ResponseEntity<Map<String, Object>> deleteManualPo(

			@PathVariable Long sotPoId) {
		Map<String, Object> response = new HashMap<>();
		try {
			service.deleteManualPo(sotPoId);
			response.put("success", true);
			response.put("msg", "Manual PO deleted successfully");
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}
	
	@GetMapping("/pdf/{sotId}")
	public ResponseEntity<Map<String, Object>> generatePdf(
	        @PathVariable Long sotId,
	        @RequestParam("userId")           Long userId,
	        @RequestParam("isCompanyDetails") Integer isCompanyDetails,
	        HttpServletRequest request) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        byte[] pdf = service.generatePdfReport(sotId, userId, isCompanyDetails);

	        String rootPath   = request.getSession().getServletContext().getRealPath("/");
	        String folderName = String.valueOf(userId);

	        File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");
	        if (!dir.exists()) dir.mkdirs();

	        String fileName = "sot-" + sotId + "-" + System.currentTimeMillis() + ".pdf";
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
	
	 @GetMapping("/manual-po/info/{sotPoId}")
	    public ResponseEntity<Map<String, Object>> getManualPoInfo(
	            @PathVariable Long sotPoId) {
	 
	        Map<String, Object> response = new HashMap<>();
	        try {
	            response.put("success", true);
	            response.put("msg",     "Manual PO info fetched successfully");
	            response.put("data",    service.getManualPoInfo(sotPoId));
	            return ResponseEntity.ok(response);
	        } catch (RuntimeException e) {
	            response.put("success", false);
	            response.put("msg",     e.getMessage());
	            return ResponseEntity.ok(response);
	        }
	    }
	 
	    // ── POST Save/Update Info for Manual PO (Save Changes in modal) ──────────
	    @PostMapping("/manual-po/info")
	    public ResponseEntity<Map<String, Object>> saveManualPoInfo(
	            @RequestBody SotPoInfoRequestDto request) {
	 
	        Map<String, Object> response = new HashMap<>();
	        try {
	            response.put("success", true);
	            response.put("msg",     "Manual PO info saved successfully");
	            response.put("data",    service.saveManualPoInfo(request));
	            return ResponseEntity.ok(response);
	        } catch (RuntimeException e) {
	            e.printStackTrace();
	            response.put("success", false);
	            response.put("msg",     e.getMessage());
	            return ResponseEntity.ok(response);
	        }
	    }
	 
	    // ── GET PDF for Manual PO (Print button) ─────────────────────────────────
	    @GetMapping("/manual-po/pdf/{sotPoId}")
	    public ResponseEntity<Map<String, Object>> generateManualPoPdf(
	            @PathVariable Long sotPoId,
	            @RequestParam("userId")           Long userId,
	            @RequestParam("isCompanyDetails") Integer isCompanyDetails,
	            HttpServletRequest request) {
	 
	        Map<String, Object> response = new HashMap<>();
	        try {
	            byte[] pdf = service.generateManualPoPdf(sotPoId, userId, isCompanyDetails);
	 
	            String rootPath   = request.getSession().getServletContext().getRealPath("/");
	            String folderName = String.valueOf(userId);
	 
	            File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");
	            if (!dir.exists()) dir.mkdirs();
	 
	            String fileName = "manual-po-" + sotPoId + "-" + System.currentTimeMillis() + ".pdf";
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
	            response.put("msg",     e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	        }
	    }
	    
	 // ── Add / Update Manual PO ────────────────────────────────────────────────
	    @PostMapping("/manual-po/add-update")
	    public ResponseEntity<Map<String, Object>> addOrUpdateManualPo(
	            @RequestBody SotManualPoRequestDto request) {

	        Map<String, Object> response = new HashMap<>();
	        try {
	            response.put("success", true);
	            response.put("msg",     "Manual PO saved successfully");
	            response.put("data",    service.addOrUpdateManualPo(request));
	            return ResponseEntity.ok(response);
	        } catch (RuntimeException e) {
	            e.printStackTrace();
	            response.put("success", false);
	            response.put("msg",     e.getMessage());
	            return ResponseEntity.ok(response);
	        }
	    }
	    
	    @GetMapping("/check-sot")
	    public ResponseEntity<Boolean> checkSot(
	            @RequestParam Long eventId,
	            @RequestParam Long userId) {

	        return ResponseEntity.ok(
	        		service.checkSotExists(eventId, userId));
	    }

	    @GetMapping("/get-multiple-sot-data")
	    public ResponseEntity<?> getMultipleSotData(@RequestParam("sotIds") List<Long> sotIds, @RequestParam("userId") Long userId) {
	    	Map<String, Object> response = new HashMap<>();
	    	try {

	    		List<MultipleSotResponseDto> data = service.getMultipleSotData(sotIds, userId);
	    		
	    		if(data != null) {
	    			response.put("data", data);
	    			response.put("msg", "Data found successfully.");
		    		response.put("success", true);
	    		}else {
	    			response.put("msg", "Data found failed.");
		    		response.put("success", false);
	    		}
	    		return ResponseEntity.status(HttpStatus.OK).body(response);
	    	} catch (RuntimeException re) {
	    		re.printStackTrace();
	    		response.put("msg", re.getLocalizedMessage());
	    		response.put("success", false);
	    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		response.put("msg", e.getLocalizedMessage());
	    		response.put("success", false);
	    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    	}
	    }
	    
	    @PostMapping("/add-update-multiple-sot-data")
	    public ResponseEntity<?> addUpdateMultipleSotData(
	            @RequestBody List<MultipleSotResponseDto> request) {
	        Map<String, Object> response = new HashMap<>();

	        try {
	            List<SotResponseDto> result = service.addUpdateMultipleSotData(request);

	            response.put("success", true);
	            response.put("msg", "Multiple SOTs accepted successfully.");
	            response.put("data", result);

	            return ResponseEntity.ok(response);

	        } catch (RuntimeException re) {
	            re.printStackTrace();
	            response.put("success", false);
	            response.put("msg", re.getLocalizedMessage());
	            return ResponseEntity.badRequest().body(response);

	        } catch (Exception e) {
	            e.printStackTrace();
	            response.put("success", false);
	            response.put("msg", e.getLocalizedMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	        }
	    }
	    
	    @DeleteMapping("/delete-sot-details")
	    public ResponseEntity<?> deleteSotDetails(@RequestParam("sotDetailIds") List<Long> sotDetailIds) {
	        Map<String, Object> response = new HashMap<>();

	        try {
	            Boolean isDeleted = service.deleteSotDetail(sotDetailIds);

	            if(isDeleted != null && isDeleted) {
		            response.put("success", true);
		            response.put("msg", "Raw Material Deleted Successfully.");
	            }else {
	            	response.put("success", false);
		            response.put("msg", "Raw Material Deleted failed.");
	            }
	            return ResponseEntity.ok(response);

	        } catch (RuntimeException re) {
	            re.printStackTrace();
	            response.put("success", false);
	            response.put("msg", re.getLocalizedMessage());
	            return ResponseEntity.badRequest().body(response);

	        } catch (Exception e) {
	            e.printStackTrace();
	            response.put("success", false);
	            response.put("msg", e.getLocalizedMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	        }	    	
	    }
}