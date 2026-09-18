package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.EventAdvancePaymentRequestDto;
import com.crmportal.response.dto.EventAdvancePaymentResponseDto;
import com.crmportal.service.EventAdvancePaymentService;
import com.crmportal.service.ReportService;

@RestController
@RequestMapping("/v1/api/event-adv-payment")
@CrossOrigin(origins = "*", maxAge = 3600L)
public class EventAdvancePaymentController {

	@Autowired
	EventAdvancePaymentService eventAdvancePaymentService;
	
	@Autowired
	ReportService reportService;
	
	@PostMapping("/add-update")
	public ResponseEntity<?> addOrUpdateEventAdvancePayment(@RequestBody EventAdvancePaymentRequestDto request) {
		Map<String, Object> response = new HashMap<>();

		try {
			
			EventAdvancePaymentResponseDto responseDto = eventAdvancePaymentService.addOrUpdateEventAdvancePayment(request);
			
			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", "Payment Done.");
				response.put("success", true);
			} else {
				response.put("msg", "Payment failed.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getall")
	public ResponseEntity<?> getAllEventAdvancePayment(@RequestParam("eventId") Long eventId) {
		Map<String, Object> response = new HashMap<>();

		try {
			List<EventAdvancePaymentResponseDto> responseDto = eventAdvancePaymentService.getAllEventAdvancePaymentList(eventId);
			
			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", "Payment Found Done.");
				response.put("success", true);
			} else {
				response.put("msg", "Payment Found failed.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getbyid")
	public ResponseEntity<?> getEventPaymentById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			EventAdvancePaymentResponseDto responseDto = eventAdvancePaymentService.getEventAdvancePaymentById(id);
			
			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", "Payment Found Done.");
				response.put("success", true);
			} else {
				response.put("msg", "Payment Found failed.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/deletebyid")
	public ResponseEntity<?> deleteEventPaymentById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			Boolean isSuccess = eventAdvancePaymentService.deleteEventAdvancePaymentById(id);
			
			if (isSuccess != null && isSuccess) {
				response.put("msg", "Payment Deleted Successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Payment Deleted failed.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getreport")
	public ResponseEntity<?> getAdvancePaymentReceipt(@RequestParam("eventId") Long eventId, @RequestParam("userId") Long userId, 
			@RequestParam("advancePaymentId") Long advancePaymentId,
			@RequestParam(value = "isTermsCond" , defaultValue = "false") Boolean isTermsCond,
			HttpServletRequest re,
			@RequestParam("eventFunctionId") Long eventFunctionId) {
		Map<String, Object> response = new HashMap<>();

		try {
			String url = reportService.generateAdvancePaymentReceipt(advancePaymentId, userId, eventId, re,isTermsCond, eventFunctionId);
			
			if (url != null) {
				response.put("data", url);
				response.put("msg", "Report Generated Successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Report Generated failed.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
