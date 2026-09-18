package com.crmportal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.crmportal.request.dto.EventFunctionQuotationRequestDto;
import com.crmportal.request.dto.EventQuotationSecurityDepositRequestDto;
import com.crmportal.response.dto.EventFunctionMasterResponseDto;
import com.crmportal.response.dto.EventFunctionQuotationItemsResponseDto;
import com.crmportal.response.dto.EventFunctionQuotationResponseDto;
import com.crmportal.response.dto.EventQuotationSecurityDepositResponseDto;
import com.crmportal.service.EventFunctionQuotationService;
import com.crmportal.service.EventQuotationSecurityDepositService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/quotation" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventFunctionQuotationController {

	@Autowired
	EventFunctionQuotationService eventFunctionQuotationService;

	@Autowired
	EventQuotationSecurityDepositService eventQuotationSecurityDepositService;
	
	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addEventFunctionQuotation(
			@Valid @RequestBody EventFunctionQuotationRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventFunctionQuotationResponseDto responseDto = eventFunctionQuotationService
					.addOrUpdateEventFunctoinQuotation(request, Long.parseLong("-1"));
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.EVENT_FUNCTION_QUOTATION_CREATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_FUNCTION_QUOTATION_CREATE_FAIL);
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
	public ResponseEntity<Map<String, Object>> updateEventFunctionQuotation(
			@Valid @RequestBody EventFunctionQuotationRequestDto request, @RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventFunctionQuotationResponseDto responseDto = eventFunctionQuotationService
					.addOrUpdateEventFunctoinQuotation(request, id);
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.EVENT_FUNCTION_QUOTATION_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_FUNCTION_QUOTATION_UPDATE_FAIL);
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

	@DeleteMapping("/deletebyquotationitemid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteByQuotationItemId(
			@RequestParam("quotationItemId") Long quotationItemId) {
		Boolean isSuccess = false;
		Map<String, Object> response = new HashMap<>();
		try {
			isSuccess = eventFunctionQuotationService.deleteByQuotationItemId(quotationItemId);
			if (isSuccess) {
				response.put("msg", ConstantsPoc.EVENT_FUNCTION_QUOTATION_DELETE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_FUNCTION_QUOTATION_DELETE_FAIL);
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
	public ResponseEntity<Map<String, Object>> getEventFunctionQuotationByEventId(@RequestParam("eventId") Long eventId,
			@RequestParam(value = "isCopyToInvoice", required = false) Integer isCopyToInvoice,
			@RequestParam(value = "isDecore", defaultValue = "false") Boolean isDecore) {
		Map<String, Object> response = new HashMap<>();
		List<EventFunctionQuotationResponseDto> responseDtos = new ArrayList<>();
		try {
			EventFunctionQuotationResponseDto responseDto = null;
			if (isCopyToInvoice == null) {
				isCopyToInvoice = 0;
			}
			responseDto = eventFunctionQuotationService.getEventFunctionQuotationByEventId(eventId, isCopyToInvoice,isDecore);

			if (responseDto != null) {
				Map<String, Object> quotationRes = new HashMap<>();
				responseDtos.add(responseDto);
				quotationRes.put("Event Functions Quotation Details", responseDtos);
				response.put("data", quotationRes);
				response.put("msg", ConstantsPoc.EVENT_FUNCTION_QUOTATION_FOUND_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_FUNCTION_QUOTATION_FOUND_FAIL);
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

	@GetMapping("/getalluserid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllEventFunctionQuotationByUserId(
			@RequestParam("userid") Long userid,@RequestParam(value = "isDecore", defaultValue = "false") Boolean isDecore) {
		Map<String, Object> response = new HashMap<>();
		List<EventFunctionQuotationResponseDto> responseDtos = new ArrayList<>();
		try {
			List<EventFunctionQuotationResponseDto> responseDto = eventFunctionQuotationService
					.getEventFunctionQuotationByUserId(userid,isDecore);
			if (responseDto != null) {
				Map<String, Object> quotationRes = new HashMap<>();
				quotationRes.put("Event Functions Quotation Details", responseDto);
				response.put("data", quotationRes);
				response.put("msg", ConstantsPoc.EVENT_FUNCTION_QUOTATION_FOUND_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_FUNCTION_QUOTATION_FOUND_FAIL);
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
	public ResponseEntity<Map<String, Object>> getAllEventFunctionQuotationByFilter(
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, @RequestParam("userid") Long userid,
			@RequestParam(value = "isVenue", required = false) Boolean isVenue,
			@RequestParam(value = "id", required = false) Long id,@RequestParam(value = "isDecore", defaultValue = "false") Boolean isDecore) {
		Map<String, Object> response = new HashMap<>();
		List<EventFunctionQuotationResponseDto> responseDtos = new ArrayList<>();
		try {
			List<EventFunctionQuotationResponseDto> responseDto = eventFunctionQuotationService
					.getEventFunctionQuotationByUserIdAndDateWise(userid, startDate, endDate, isVenue, id,isDecore);
			if (responseDto != null) {
				Map<String, Object> quotationRes = new HashMap<>();
				quotationRes.put("Event Functions Quotation Details", responseDto);
				response.put("data", quotationRes);
				response.put("msg", ConstantsPoc.EVENT_FUNCTION_QUOTATION_FOUND_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_FUNCTION_QUOTATION_FOUND_FAIL);
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

	@GetMapping("/quotationexcel")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> quotationExcel(
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, @RequestParam("userid") Long userid,
			HttpServletRequest request, @RequestParam(value = "isVenue", required = false) Boolean isVenue,
			@RequestParam(value = "id", required = false) Long id,@RequestParam(value = "isDecore", defaultValue = "false") Boolean isDecore) {
		Map<String, Object> response = new HashMap<>();
		try {
			String responseDto = eventFunctionQuotationService.quotationExcel(userid, startDate, endDate, request,
					isVenue, id,isDecore);
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

	@GetMapping("/getdefaultextrafunction")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getDefaultExtraFunction(@RequestParam("quotationId") Long quotationId,
			@RequestParam("isOn") Boolean isOn) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventFunctionQuotationItemsResponseDto> functionQuotationItems = eventFunctionQuotationService
					.getDefaultExtraFunction(quotationId, isOn);
			if (!functionQuotationItems.isEmpty()) {
				Map<String, Object> quotationRes = new HashMap<>();
				quotationRes.put("Default Functions Item Details", functionQuotationItems);
				response.put("data", quotationRes);
				response.put("msg", ConstantsPoc.EVENT_FUNCTION_QUOTATION_FOUND_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_FUNCTION_QUOTATION_FOUND_FAIL);
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

	@PutMapping("/lock-quotation")
	public ResponseEntity<?> lockQuotation(@RequestParam("quotationId") Long quotationId, @RequestParam("isLock") Boolean isLock) {
		Map<String, Object> response = new HashMap<>();

		try {
			Boolean isLocked = eventFunctionQuotationService.lockQuotation(quotationId, isLock);

			if (isLocked == null || !isLocked) {
				response.put("msg", ConstantsPoc.QUOTATION_ALREADY_LOCKED);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.QUOTATION_LOCKED_SUCCESS);
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

	@DeleteMapping("/deletequotationpayment")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteQuotationPayment(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isDelete = eventFunctionQuotationService.deleteQuotationPayment(id);
			if (isDelete) {
				response.put("msg", "Payment Deleted Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Payment Deleted Failed");
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

	@PostMapping("/add-security-deposit")
	public ResponseEntity<?> addSecurityDeposit(
			@Valid @RequestBody EventQuotationSecurityDepositRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {

			EventQuotationSecurityDepositResponseDto data = eventQuotationSecurityDepositService
					.addUpdateSecurityDeposit(request);

			if (data != null) {
				response.put("msg", "Security Depoit Saved Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Security Depoit Saved Failed");
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
	
	@GetMapping("/getAllSecurityDepositByEventId")
	public ResponseEntity<?> getAllSecurityDepositByEventId(@RequestParam("eventId") Long eventId) {
		Map<String, Object> response = new HashMap<>();
		try {

			List<EventQuotationSecurityDepositResponseDto> data = eventQuotationSecurityDepositService.getAllSecurityDepositByEventId(eventId);

			if (data != null) {
				response.put("msg", "Security Depoit Found Successfully");
				response.put("success", true);
				response.put("data", data);
			} else {
				response.put("msg", "Security Depoit Found Failed");
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
	
	@DeleteMapping("/delete-security-deposit")
	public ResponseEntity<?> deleteSecurityDeposit(@RequestParam("securityDepositId") Long securityDepositId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isDelete = eventQuotationSecurityDepositService.deleteSecurityDeposit(securityDepositId);
			
			if (isDelete) {
				response.put("msg", "Security Depoit Deleted Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Security Depoit Deleted Failed");
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
