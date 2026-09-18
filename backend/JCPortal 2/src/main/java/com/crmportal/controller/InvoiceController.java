package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.InvoicePaymentHistoryRequestDto;
import com.crmportal.request.dto.InvoiceRequestDTO;
import com.crmportal.response.dto.AllInvoiceResponseDto;
import com.crmportal.response.dto.InvoicePaymentHistoryResponseDto;
import com.crmportal.response.dto.InvoiceWisePaymentHistoryResponseDto;
import com.crmportal.response.dto.SuperAdminInvoiceResponseDto;
import com.crmportal.service.InvoiceService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/invoice-operations" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class InvoiceController {
	
	@Autowired
	private InvoiceService invoiceService;

	@GetMapping("/health")
	@ResponseBody
	public ResponseEntity<String> healthCheck() {
		return new ResponseEntity<>("Server is running ", HttpStatus.OK);
	}

	@PostMapping(value = "/addInvoice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> addInvoice(@Valid @ModelAttribute InvoiceRequestDTO invoiceRequestDTO,
			BindingResult bindingResult) {
		Map<String, Object> response = new HashMap<>();
		try {
			if (bindingResult.hasErrors()) {
			    Map<String, String> errors = new HashMap<>();
			    bindingResult.getFieldErrors().forEach(error ->
			            errors.put(error.getField(), error.getDefaultMessage())
			    );
			    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
			}else {
				SuperAdminInvoiceResponseDto res = invoiceService.addOrUpdateInvoice(invoiceRequestDTO, -1L);
				
				if(res != null) {
					response.put("msg", ConstantsPoc.SUPERADMIN_INVOICE_CREATE_SUCCESS);
					response.put("success", true);
				}else {
					response.put("msg", ConstantsPoc.SUPERADMIN_INVOICE_CREATE_FAIL);
					response.put("success", false);
				}
				return ResponseEntity.status(HttpStatus.OK).body(response);
			}
		} catch (RuntimeException re) {
			re.printStackTrace();
			response.put("success", false);
			response.put("msg", re.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping(value = "/updateInvoice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateInvoice(@Valid @ModelAttribute InvoiceRequestDTO invoiceRequestDTO, 
			@RequestParam("invoiceId") Long invoiceId,
			BindingResult bindingResult) {
		Map<String, Object> response = new HashMap<>();
		try {
			if (bindingResult.hasErrors()) {
			    Map<String, String> errors = new HashMap<>();
			    bindingResult.getFieldErrors().forEach(error ->
			            errors.put(error.getField(), error.getDefaultMessage())
			    );
			    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}else {
				SuperAdminInvoiceResponseDto res = invoiceService.addOrUpdateInvoice(invoiceRequestDTO, invoiceId);
				
				if(res != null) {
					response.put("msg", ConstantsPoc.SUPERADMIN_INVOICE_UPDATE_SUCCESS);
					response.put("success", true);
				}else {
					response.put("msg", ConstantsPoc.SUPERADMIN_INVOICE_UPDATE_FAIL);
					response.put("success", false);
				}
				return ResponseEntity.status(HttpStatus.OK).body(response);
			}
		} catch (RuntimeException re) {
			response.put("success", false);
			response.put("msg", re.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAllAdminInvoice")
	public ResponseEntity<Map<String, Object>> getInvoice(
			@RequestParam(value = "startDate", required = false) String startDate, 
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "planId", required = false) Long planId, 
			@RequestParam(value = "customerId", required = false) Long customerId) {
		Map<String, Object> response = new HashMap<>();
		try {
			AllInvoiceResponseDto res = invoiceService.getAllAdminInvoice(startDate, endDate, planId, customerId);
			
			if (res == null) {
				response.put("msg", ConstantsPoc.SUPERADMIN_INVOICE_FOUND_FAIL);
				response.put("success", false);
			} else {
				response.put("data", res);
				response.put("msg", ConstantsPoc.SUPERADMIN_INVOICE_FOUND_SUCCESS);
				response.put("success", true);
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
	
	@GetMapping("/getadmininvoicebyid")
	public ResponseEntity<Map<String, Object>> getAdminInvoiceById(@RequestParam("invoiceId") Long invoiceId){
		Map<String, Object> response = new HashMap<>();
		try {
			SuperAdminInvoiceResponseDto invoiceResponseDtos = invoiceService.getAdminInvoiceById(invoiceId);
			
			if (invoiceResponseDtos == null) {
				response.put("msg", ConstantsPoc.SUPERADMIN_INVOICE_FOUND_FAIL);
				response.put("success", false);
			} else {
				response.put("data", invoiceResponseDtos);
				response.put("msg", ConstantsPoc.SUPERADMIN_INVOICE_FOUND_SUCCESS);
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
	
	@DeleteMapping("/deleteadmininvoicebyid")
	public ResponseEntity<?> deleteAdminInvoiceById(@RequestParam("invoiceId") Long invoiceId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isDelete = invoiceService.deleteInvoiceById(invoiceId);
			
			if (!isDelete) {
				response.put("msg", ConstantsPoc.SUPERADMIN_INVOICE_DELETE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.SUPERADMIN_INVOICE_DELETE_SUCCESS);
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
	
	@GetMapping("/generateInvoiceCode")
	public ResponseEntity<Map<String, Object>> generateInvoiceCode() {
		Map<String, Object> response = new HashMap<>();
		try {
			String invoiceCode = invoiceService.generateInvoiceCode();
			
			if (invoiceCode == null) {
				response.put("msg", ConstantsPoc.INVOICE_CODE_GENERATE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.INVOICE_CODE_GENERATE_SUCCESS);
				response.put("success", true);
				response.put("invoiceCode", invoiceCode);
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
	
	@PostMapping("/recordpayment")
	public ResponseEntity<?> recordPayment(@Valid @RequestBody InvoicePaymentHistoryRequestDto request, @RequestParam("invoicePaymentId") Long invoicePaymentHistoryId) {
		Map<String, Object> response = new HashMap<>();
		try {
			InvoicePaymentHistoryResponseDto paymentDetail = invoiceService.recordPayment(request, invoicePaymentHistoryId);
			
			if (paymentDetail == null) {
				response.put("msg", ConstantsPoc.RECORD_PAYMENT_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.RECORD_PAYMENT_SUCCESS);
				response.put("success", true);
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
	
	@GetMapping("/getPaymentHistoryByInvoiceId")
	public ResponseEntity<?> getPaymentHistoryByInvoiceId(@RequestParam("invoiceId") Long invoiceId) {
		Map<String, Object> response = new HashMap<>();
		try {
			InvoiceWisePaymentHistoryResponseDto paymentDetail = invoiceService.getPaymentHistoryByInvoiceId(invoiceId);
			
			if (paymentDetail == null) {
				response.put("msg", ConstantsPoc.PAYMENT_HISTORY_FOUND_FAIL);
				response.put("success", false);
			} else {
				response.put("data", paymentDetail);
				response.put("msg", ConstantsPoc.PAYMENT_HISTORY_FOUND_SUCCESS);
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
	
	@GetMapping("/getPaymentDetailByInvoicePaymentHistoryId")
	public ResponseEntity<?> getPaymentDetailByInvoicePaymentHistoryId(@RequestParam("invoicePaymentHistoryId") Long invoicePaymentHistoryId) {
		Map<String, Object> response = new HashMap<>();
		try {
			InvoicePaymentHistoryResponseDto paymentDetail = invoiceService.getPaymentDetailByInvoicePaymentHistoryId(invoicePaymentHistoryId);
			
			if (paymentDetail == null) {
				response.put("msg", ConstantsPoc.PAYMENT_HISTORY_FOUND_FAIL);
				response.put("success", false);
			} else {
				response.put("data", paymentDetail);
				response.put("msg", ConstantsPoc.PAYMENT_HISTORY_FOUND_SUCCESS);
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
	
	@DeleteMapping("/deletePaymentDetailByInvoicePaymentHistoryId")
	public ResponseEntity<?> deletePaymentDetailByInvoicePaymentHistoryId(
			@RequestParam("invoicePaymentHistoryId") Long invoicePaymentHistoryId,
			@RequestParam("invoiceId") Long invoiceId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean paymentDetail = invoiceService.deleteInvoicePaymentDetailsByHistoryId(invoicePaymentHistoryId, invoiceId);
			
			if (!paymentDetail) {
				response.put("msg", ConstantsPoc.PAYMENT_DETAILS_DELETE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.PAYMENT_DETAILS_DELETE_SUCCESS);
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