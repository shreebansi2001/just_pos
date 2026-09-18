package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

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

import com.crmportal.request.dto.EventFunctionFeedbackRequest;
import com.crmportal.response.dto.EventFunctionFeedbackResponse;
import com.crmportal.service.EventFunctionFeedbackService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/event-function-feedback" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventFunctionFeedbackController {

	@Autowired
	EventFunctionFeedbackService eventFunctionFeedbackService;

	@PostMapping("/add-update")
	public ResponseEntity<?> addOrUpdate(@RequestBody EventFunctionFeedbackRequest request) {

		Map<String, Object> response = new HashMap<>();

		try {

			EventFunctionFeedbackResponse res = eventFunctionFeedbackService.addOrUpdate(request);

			if (res != null) {

				response.put("data", res);
				response.put("msg", "Feedback Add/Updated Successfully");
				response.put("success", true);

			} else {

				response.put("msg", "Feedback Add/Updated Failed");
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

	@GetMapping("/getall")
	public ResponseEntity<?> getAll(

			@RequestParam(value = "name", required = false) String name,

			@RequestParam(value = "mobileno", required = false) String mobileno,

			@RequestParam(value = "eventId", required = false) Long eventId,

			@RequestParam(value = "eventFunctionId", required = false) Long eventFunctionId,

			@RequestParam(value = "userId", required = false) Long userId,

			@RequestParam(value = "memberId", required = false) Long memberId) {

		Map<String, Object> response = new HashMap<>();

		try {

			List<EventFunctionFeedbackResponse> res = eventFunctionFeedbackService.getAll(name, mobileno, eventId,
					eventFunctionId, userId, memberId);

			if (!res.isEmpty()) {

				response.put("data", res);
				response.put("msg", "Data Found Successfully");
				response.put("success", true);

			} else {

				response.put("msg", "Data Found Failed");
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

	@GetMapping("/getbyid")
	public ResponseEntity<?> getById(@RequestParam("feedbackId") Long feedbackId) {

		Map<String, Object> response = new HashMap<>();

		try {

			EventFunctionFeedbackResponse res = eventFunctionFeedbackService.get(feedbackId);

			if (res != null) {

				response.put("data", res);
				response.put("msg", "Data Found Successfully");
				response.put("success", true);

			} else {

				response.put("msg", "Data Found Failed");
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

	@DeleteMapping("/deletebyid")
	public ResponseEntity<?> deleteById(@RequestParam("feedbackId") Long feedbackId) {

		Map<String, Object> response = new HashMap<>();

		try {

			Boolean isDeleted = eventFunctionFeedbackService.deleteById(feedbackId);

			if (isDeleted) {

				response.put("data", isDeleted);
				response.put("msg", "Feedback Deleted Successfully");
				response.put("success", true);

			} else {

				response.put("msg", "Feedback Deleted Failed");
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