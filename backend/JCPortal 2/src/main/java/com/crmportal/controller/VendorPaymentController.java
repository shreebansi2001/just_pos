package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.UpdateVendorPaymentRequestDto;
import com.crmportal.request.dto.VendorPaymentRequestDto;
import com.crmportal.response.dto.AccountLadgerPartyResponseDto;
import com.crmportal.response.dto.AccountLadgerResponseDto;
import com.crmportal.response.dto.AccountLedgerFinalResponseDto;
import com.crmportal.response.dto.VendorPartyWiseEventResponseDto;
import com.crmportal.response.dto.VendorPartyWiseFinalEventResponseDto;
import com.crmportal.response.dto.VendorPaymentInvoiceResponseDto;
import com.crmportal.response.dto.VendorPaymentResponseDto;
import com.crmportal.service.VendorPaymentService;

import io.swagger.models.Response;

@RestController
@RequestMapping({ "v1/api/vendorpayment" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class VendorPaymentController {

	@Autowired
	VendorPaymentService vendorPaymentService;

	@GetMapping("/getallvendorpaymentbyeventid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllVendorPaymentByEventId(@RequestParam("eventId") Long eventId,
			@RequestParam("isLabour") Boolean isLabour) {
		Map<String, Object> response = new HashMap<>();
		try {
			VendorPaymentResponseDto responseDto = vendorPaymentService.getAllVendorPaymentByEventId(eventId, isLabour);
			if (responseDto != null) {
				Map<String, Object> vendorResp = new HashMap<>();
				vendorResp.put("Vendor Payments Details", responseDto);
				response.put("msg", "Vendor Payment Found Successfully");
				response.put("data", vendorResp);
				response.put("success", true);
			} else {
				response.put("msg", "Vendor Payment Found Failed");
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

	@GetMapping("/getvendorpaymentbyeventidandvendorid")
	public ResponseEntity<Map<String, Object>> getVendorPaymentByEventIdAndVendorId(
			@RequestParam Long eventId,
			@RequestParam Long vendorId, 
			@RequestParam Long userId, 
			@RequestParam(required = false) Boolean isPayable,
			@RequestParam(value = "vendorName",required = false) String vendorName) {

		Map<String, Object> response = new HashMap<>();

		try {

			Map<String, Object> vendorResponse = vendorPaymentService.getVendorPaymentByEventIdAndVendorId(eventId,
					vendorId, userId, isPayable, vendorName);

			if (vendorResponse.isEmpty()) {
				response.put("success", false);
				response.put("msg", "Vendor Payment Invoice Not Found");
			} else {
				response.put("success", true);
				response.put("msg", "Vendor Payment Invoice Found");
				response.put("data", vendorResponse);
			}

			return ResponseEntity.ok(response);

		} catch (RuntimeException e) {

			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);

		} catch (Exception e) {

			e.printStackTrace();
			response.put("success", false);
			response.put("msg", "Something went wrong");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@PostMapping("/addorupdatevendorpaymentinvoice")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdateVendorPaymentInvoice(
			@RequestBody VendorPaymentRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = vendorPaymentService.addOrUpdateVendorPaymentInvoice(request);
			if (isSuccess) {
				response.put("msg", "Vendor Payment Invoice Added Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "Vendor Payment Invoice Added Failed");
				response.put("success", isSuccess);
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

	@PostMapping("/updatevendorpaymentinvoice")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateVendorPaymentInvoice(
			@RequestBody UpdateVendorPaymentRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = vendorPaymentService.updateVendorPaymentInvoice(request);
			if (isSuccess) {
				response.put("msg", "Vendor Payment Invoice Updated Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "Vendor Payment Invoice Updated Failed");
				response.put("success", isSuccess);
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

	@DeleteMapping("/deletevendorpaymentinvoicebyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteEventVendorPaymentInvoiceById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = vendorPaymentService.deleteEventVendorPaymentInvoiceById(id);

			if (isSuccess) {
				response.put("msg", "Vendor Payment Invoice Added/Updated Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "Vendor Payment Invoice Added/Updated Failed");
				response.put("success", isSuccess);
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

	@GetMapping("/getaccountladger")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAccountLadger(
			@RequestParam(value = "partyId", required = true) Long partyId,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, @RequestParam("userId") Long userId,
			@RequestParam("vendorCat") String vendorCat,@RequestParam(value = "type" , defaultValue = "both") String type) {

		Map<String, Object> response = new HashMap<>();

		try {

			AccountLedgerFinalResponseDto result = vendorPaymentService.getAccountLadger(partyId, startDate, endDate,
					userId, vendorCat,type);

			// ✅ Check list inside DTO
			if (result == null || result.getLedgerList() == null || result.getLedgerList().isEmpty()) {
				response.put("msg", "Data Not Found");
				response.put("success", false);
				response.put("data", result); // optional
			} else {
				response.put("msg", "Data Found Successfully");
				response.put("data", result);
				response.put("success", true);
			}

			return ResponseEntity.ok(response);

		} catch (RuntimeException e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", "Something went wrong");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/account-ledger-pdf")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> accountLedgerPdf(
			@RequestParam(value = "partyId", required = true) Long partyId,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, @RequestParam("userId") Long userId,
			@RequestParam("vendorCat") String vendorCat, HttpServletRequest request,@RequestParam(value = "type" , defaultValue = "both") String type) {
		Map<String, Object> response = new HashMap<>();
		try {
			String responseDto = vendorPaymentService.accountLedgerPdf(partyId, startDate, endDate, userId, vendorCat,
					request,type);
			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", "PDF Generated Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "PDF Generated Failed");
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

	@GetMapping("/account-ledger-excel")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> accountLedgerExcel(
			@RequestParam(value = "partyId", required = true) Long partyId,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, @RequestParam("userId") Long userId,
			@RequestParam("vendorCat") String vendorCat, HttpServletRequest request,@RequestParam(value = "type" , defaultValue = "both") String type) {
		Map<String, Object> response = new HashMap<>();
		try {
			String responseDto = vendorPaymentService.accountLedgerExcel(partyId, startDate, endDate, userId, vendorCat,
					request,type);
			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", "Excel Generated Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Excel Generated Failed");
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

	@GetMapping("/getallaccountladgerparty")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllAccountLadgerParty(@RequestParam("userId") Long userid,
			@RequestParam(value = "isBookingParty", required = false) Boolean isBookingParty,
			@RequestParam(value = "isAllStatus") Boolean isAllStatus) {
		Map<String, Object> response = new HashMap<>();

		try {

			List<AccountLadgerPartyResponseDto> dtos = vendorPaymentService.getAllParty(userid, isBookingParty,
					isAllStatus);
			if (dtos.isEmpty()) {
				response.put("msg", "Data Not Found");
				response.put("success", false);
			} else {
				response.put("data", dtos);
				response.put("msg", "Data Found Successfully");
				response.put("success", true);
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

	@GetMapping("/getalleventbyparty")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllEventByParty(@RequestParam("partyId") Long partyId,
			@RequestParam("userId") Long userId) {

		Map<String, Object> response = new HashMap<>();
		try {
			VendorPartyWiseFinalEventResponseDto dtos = vendorPaymentService.getAllEventByParty(partyId, userId);

			if (dtos == null) {
				response.put("msg", "Data Not Found");
				response.put("success", false);
			} else {
				response.put("data", dtos);
				response.put("msg", "Data Found Successfully");
				response.put("success", true);
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

	@PostMapping("/generate-vendor-payment-report")
	public ResponseEntity<?> generateVendorPaymentReport(@RequestParam("isCompanyDetails") Integer isCompanyDetails,
			@RequestParam("isPayable") Boolean isPayable, HttpServletRequest request,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			String file = vendorPaymentService.generateVendorPaymentReport(isCompanyDetails, isPayable, request,
					userId);

			if (file == null) {
				response.put("msg", "Data Not Found");
				response.put("success", false);
			} else {
				response.put("url", file);
				response.put("msg", "Data Found Successfully");
				response.put("success", true);
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

	@PostMapping("/generate-payment-receipt")
	public ResponseEntity<?> generatePaymentReceipt(@RequestParam("isCompanyDetails") Integer isCompanyDetails,
			HttpServletRequest request, @RequestParam("vendorPayId") Long vendorPayId,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			String file = vendorPaymentService.generatePaymentReceipt(isCompanyDetails, request, vendorPayId,userId);

			if (file == null) {
				response.put("msg", "Data Not Found");
				response.put("success", false);
			} else {
				response.put("url", file);
				response.put("msg", "Data Found Successfully");
				response.put("success", true);
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
}
