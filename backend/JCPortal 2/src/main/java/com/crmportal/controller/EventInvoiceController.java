package com.crmportal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.EventInvoiceRequestDto;
import com.crmportal.response.dto.EventInvoiceResponseDto;
import com.crmportal.service.EventInvoiceService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/invoice" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventInvoiceController {

	@Autowired
	EventInvoiceService eventInvoiceService;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addEventInvoice(@Valid @RequestBody EventInvoiceRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventInvoiceResponseDto responseDto = eventInvoiceService.addOrUpdateEventInvoice(request,
					Long.parseLong("-1"));
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.EVENT_INVOICE_CREATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_INVOICE_CREATE_FAIL);
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

	@PutMapping("/update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateEventFunction(@Valid @RequestBody EventInvoiceRequestDto request,
			@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventInvoiceResponseDto responseDto = eventInvoiceService.addOrUpdateEventInvoice(request, id);
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.EVENT_INVOICE_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_INVOICE_UPDATE_FAIL);
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

	@DeleteMapping("/deletebyinvoiceitemid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteByInvoiceItemId(
			@RequestParam("invoiceItemId") Long invoiceItemId) {
		Boolean isSuccess = false;
		Map<String, Object> response = new HashMap<>();
		try {
			isSuccess = eventInvoiceService.deleteByInvoiceItemId(invoiceItemId);
			if (isSuccess) {
				response.put("msg", ConstantsPoc.EVENT_INVOICE_DELETE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_INVOICE_DELETE_FAIL);
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

	@GetMapping("/getbyeventid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getEventInvoiceByEventId(@RequestParam("eventId") Long eventId,
			@RequestParam(value = "isDecore", defaultValue = "false") Boolean isDecore) {
		Map<String, Object> response = new HashMap<>();
		List<EventInvoiceResponseDto> responseDtos = new ArrayList<>();
		try {
			EventInvoiceResponseDto responseDto = eventInvoiceService.getEventInvoiceByEventId(eventId, isDecore);
			if (responseDto != null) {
				Map<String, Object> invoiceRes = new HashMap<>();
				responseDtos.add(responseDto);
				invoiceRes.put("Event Invoice Details", responseDtos);
				response.put("data", invoiceRes);
				response.put("msg", ConstantsPoc.EVENT_INVOICE_FOUND_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_INVOICE_FOUND_FAIL);
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

	@GetMapping("/getalluserid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllEventInvoiceByUserId(@RequestParam("userid") Long userid) {
		Map<String, Object> response = new HashMap<>();
		List<EventInvoiceResponseDto> responseDtos = new ArrayList<>();
		try {
			List<EventInvoiceResponseDto> responseDto = eventInvoiceService.getEventInvoiceByUserId(userid, null, null);
			if (responseDto != null) {
				Map<String, Object> invoiceRes = new HashMap<>();
				invoiceRes.put("Event Invoice Details", responseDto);
				response.put("data", invoiceRes);
				response.put("msg", ConstantsPoc.EVENT_INVOICE_FOUND_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_INVOICE_FOUND_FAIL);
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

	@GetMapping("/getallbyfilter")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllEventInvoiceByFilter(
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, @RequestParam("userid") Long userid,
			@RequestParam(value = "isVenue", required = false) Boolean isVenue,
			@RequestParam(value = "id", required = false) Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventInvoiceResponseDto> responseDto = eventInvoiceService.getEventInvoiceByUserIdAndDateWise(userid,
					startDate, endDate, isVenue, id);
			if (responseDto != null) {
				Map<String, Object> invoiceRes = new HashMap<>();
				invoiceRes.put("Event Invoice Details", responseDto);
				response.put("data", invoiceRes);
				response.put("msg", ConstantsPoc.EVENT_INVOICE_FOUND_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_INVOICE_FOUND_FAIL);
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

	@GetMapping("/invoiceexcel")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> invoiceExcel(
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, @RequestParam("userid") Long userid,
			@RequestParam(value = "isVenue", required = false) Boolean isVenue,
			@RequestParam(value = "id", required = false) Long id, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		try {
			String responseDto = eventInvoiceService.invoiceExcel(userid, startDate, endDate, request, isVenue, id);
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

	@GetMapping("/getinvoicecode")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getInvoiceCode(
			@RequestParam(value = "userId", required = false) Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			String responseDto = eventInvoiceService.getInvoiceCode(userId);
			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", "Invoice Code Generated Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Invoice Code Generated Failed");
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
}
