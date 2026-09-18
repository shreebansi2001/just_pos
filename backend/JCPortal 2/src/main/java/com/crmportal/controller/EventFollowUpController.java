package com.crmportal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.EventFollowupRequestDto;
import com.crmportal.response.dto.EventFollowupResponseDto;
import com.crmportal.response.dto.EventMasterResponseDto;
import com.crmportal.service.EventFollowUpService;
import com.crmportal.utility.ConstantsPoc;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/api/eventfollowup")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class EventFollowUpController {

	private final EventFollowUpService eventFollowUpService;

	@PostMapping("/add-update")
	public ResponseEntity<Map<String, Object>> addOrUpdate(@RequestBody EventFollowupRequestDto dto) {
		Map<String, Object> response = new HashMap<>();

		try {
			EventFollowupResponseDto resp = eventFollowUpService.saveOrUpdate(dto);
			if (resp != null) {
				response.put("msg", "Followup Created Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Followup Created Failed");
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

	@GetMapping("/get")
	public ResponseEntity<?> getById(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventFollowupResponseDto responseDto = eventFollowUpService.getById(id);
			if (responseDto == null) {
				response.put("msg", ConstantsPoc.DATA_FETCH_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> eventResp = new HashMap<>();
				List<EventFollowupResponseDto> responseDtos = new ArrayList<>();
				responseDtos.add(responseDto);
				eventResp.put("Event Followup Details", responseDtos);
				response.put("data", eventResp);
				response.put("msg", ConstantsPoc.DATA_FETCH_SUCCESS);
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

	@GetMapping("/getall")
	public ResponseEntity<?> getAll(@RequestParam(required = false) Long userId,
			@RequestParam(required = false) Long eventId, @RequestParam(required = false) Long managerId,
			@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
			@RequestParam(required = false) Boolean isDone) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventFollowupResponseDto> datas = eventFollowUpService.getAll(userId, managerId, startDate, endDate,
					isDone, eventId);
			if (datas.isEmpty()) {
				response.put("msg", ConstantsPoc.DATA_FETCH_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> eventResp = new HashMap<>();
				eventResp.put("Event Followup Details", datas);
				response.put("data", eventResp);
				response.put("msg", ConstantsPoc.DATA_FETCH_SUCCESS);
				response.put("success", true);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (

		RuntimeException e) {
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

	@DeleteMapping("/delete")
	public ResponseEntity<?> delete(@PathVariable Long id) {

		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isDelete = eventFollowUpService.deleteById(id);
			if (isDelete) {
				response.put("msg", "Deleted Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Deleted Failed");
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (

		RuntimeException e) {
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