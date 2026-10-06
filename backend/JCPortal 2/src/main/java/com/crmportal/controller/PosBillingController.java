package com.crmportal.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.dto.PosDto;
import com.crmportal.entity.PosTaxEntity;
import com.crmportal.service.PosService;

@RestController
@RequestMapping("/v1/api/pos")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class PosBillingController extends PosBaseController {

	@Autowired
	private PosService posService;

	// Taxes
	@GetMapping("/taxes")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getTaxes(@RequestParam(value = "userId", required = false) Long userId) {
		return ok("Taxes fetched", posService.getAllTaxes(userId));
	}

	@PostMapping("/taxes")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> saveTax(@RequestParam(value = "userId", required = false) Long userId,
			@RequestBody PosTaxEntity tax) {
		return ok("Tax saved", posService.saveTax(tax, userId));
	}

	@DeleteMapping("/taxes/{id}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteTax(@PathVariable("id") Long id) {
		posService.deleteTax(id);
		return ok("Tax deleted", null);
	}

	// Invoices
	@PostMapping("/orders/{id}/invoice")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> generateInvoice(@PathVariable("id") Long id,
			@RequestParam(value = "userId", required = false) Long userId) {
		return ok("Invoice generated", posService.generateInvoice(id, userId));
	}

	@PostMapping("/invoices/{id}/pay")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> payInvoice(@PathVariable("id") Long id,
			@RequestParam(value = "userId", required = false) Long userId,
			@RequestBody PosDto.InvoicePaymentRequest request) {
		return ok("Invoice settled", posService.payInvoice(id, request.getPaymentMode(), userId));
	}

	@GetMapping("/invoices")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getInvoices(
			@RequestParam(value = "userId", required = false) Long userId) {
		return ok("Invoices fetched", posService.getAllInvoices(userId));
	}
}
