package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.PurchaseRequestDto;
import com.crmportal.response.dto.PurchaseRequestDetailsResponseDto;
import com.crmportal.response.dto.PurchaseRequestResponseDto;
import com.crmportal.service.PurchaseApprovalService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/purchase-approval" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class PurchaseApprovalController {

	@Autowired
	PurchaseApprovalService purchaseApprovalService;

	@GetMapping("/generate-purchase-request-code")
	public ResponseEntity<?> generatePurchaseRequestCode(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {

			String code = purchaseApprovalService.generatePurchaseRequestCode(userId);

			if (code != null) {
				response.put("data", code);
				response.put("msg", ConstantsPoc.PURCHASE_REQUEST_CODE_GENERATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.PURCHASE_REQUEST_CODE_GENERATE_FAIL);
				response.put("success", false);
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

	@PostMapping("/add-update")
	public ResponseEntity<?> addUpdatePurchaseRequest(@RequestBody PurchaseRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {

			Boolean isSuccess = purchaseApprovalService.addUpdatePurchaseRequest(request);

			if (isSuccess) {
				response.put("msg", ConstantsPoc.PURCHASE_REQUEST_RAISE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.PURCHASE_REQUEST_RAISE_FAIL);
				response.put("success", false);
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

	@GetMapping("/getById")
	public ResponseEntity<?> getPurchaseRequestById(@RequestParam("purchaseRequestId") Long purchaseRequestId) {
		Map<String, Object> response = new HashMap<>();
		try {

			PurchaseRequestResponseDto responseDto = purchaseApprovalService.getPurchaseRequestById(purchaseRequestId);

			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", ConstantsPoc.PURCHASE_REQUEST_FOUND_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.PURCHASE_REQUEST_FOUND_FAIL);
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

	@GetMapping("/getAll")
	public ResponseEntity<?> getAllPurchaseRequest(@RequestParam("userId") Long userId,
			@RequestParam(value = "status", required = false) String status) {
		Map<String, Object> response = new HashMap<>();
		try {

			List<PurchaseRequestResponseDto> responseDto = purchaseApprovalService.getAllPurchaseRequests(userId, status);

			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", ConstantsPoc.PURCHASE_REQUEST_FOUND_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.PURCHASE_REQUEST_FOUND_FAIL);
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

	@GetMapping("/getRawMaterial")
	public ResponseEntity<?> getRawMaterial(@RequestParam("rawMaterialCatId") Long rawMaterialCatId,
			@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
			@RequestParam(value = "page", defaultValue = "0") int page,
	        @RequestParam(value = "size", defaultValue = "10") int size,
	        @RequestParam(value = "rawMaterialName", required = false) String rawMaterialName,
	        @RequestParam(value = "purchaseRequestId", required = false) Long purchaseRequestId,
	        @RequestParam("isAllData") Boolean isAllData) {
		Map<String, Object> response = new HashMap<>();
		try {

			Page<PurchaseRequestDetailsResponseDto> responseDto = purchaseApprovalService
					.getDetailsByRawMaterialId(rawMaterialCatId, startDate, endDate, page, size, rawMaterialName, purchaseRequestId, isAllData);

			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", ConstantsPoc.RAW_MATERIAL_FOUND_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_NOT_FOUND);
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

	@GetMapping("/getAllApprovedRequest")
	public ResponseEntity<?> getAllApprovedRequest(@RequestParam("userId") Long userId,
			@RequestParam(value = "page", defaultValue = "0") int page,
	        @RequestParam(value = "size", defaultValue = "10") int size) {
		Map<String, Object> response = new HashMap<>();
		try {

			Page<PurchaseRequestResponseDto> responseDto = purchaseApprovalService.getAllApprovedRequest(userId, page, size);

			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", ConstantsPoc.RAW_MATERIAL_FOUND_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_NOT_FOUND);
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
	
	@GetMapping("/purchase-approval-sheet-report")
	public ResponseEntity<?> generatePurchaseApprovalReport(@RequestParam("purchaseApprovalRequestId") Long purchaseApprovalRequestId) {
		Map<String, Object> response = new HashMap<>();
		try {

			String url = purchaseApprovalService.generatePurchaseApprovalSheetReport(purchaseApprovalRequestId);

//			if (responseDto != null) {
//				response.put("data", responseDto);
//				response.put("msg", ConstantsPoc.RAW_MATERIAL_FOUND_SUCCESS);
//				response.put("success", true);
//			} else {
//				response.put("msg", ConstantsPoc.RAW_MATERIAL_NOT_FOUND);
//				response.put("success", false);
//			}
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
