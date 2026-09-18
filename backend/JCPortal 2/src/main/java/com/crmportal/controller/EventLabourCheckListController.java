package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.EventLabourCheckListMainRequestDto;
import com.crmportal.request.dto.EventLabourCheckListRequestDto;
import com.crmportal.response.dto.EventLabourCheckListResponseDto;
import com.crmportal.service.EventLabourChecklistService;

@RestController
@RequestMapping({ "/v1/api/eventlabourchecklist" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventLabourCheckListController {

	@Autowired
	EventLabourChecklistService eventLabourChecklistService;

	@PostMapping(value = "/addorupdate",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdate(@ModelAttribute EventLabourCheckListMainRequestDto request,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = eventLabourChecklistService.addOrUpdate(request, userId);
			if (isSuccess) {
				response.put("success", isSuccess);
				response.put("msg", "CheckList Add/Updated Successfully");
			} else {
				response.put("success", isSuccess);
				response.put("msg", "CheckList Add/Updated Fail");
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

	@GetMapping("/getallchecklist")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllCheckList(@RequestParam("eventId") Long eventId,
			@RequestParam("eventFunctionId") Long eventFunctionId,@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventLabourCheckListResponseDto> dtos = eventLabourChecklistService.getAllChecklist(eventId, eventFunctionId,userId);
			if(dtos.isEmpty()) {
				response.put("msg", "Data not Found");
				response.put("success", false);
			}else {
				Map<String, Object> resp = new HashMap<>();
				resp.put("checkLists", dtos);
				response.put("data", resp);
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
