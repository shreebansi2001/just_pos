package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.SpecialNotesRequestDto;
import com.crmportal.response.dto.SpecialNotesResponseDto;
import com.crmportal.service.SpecialNotesService;

@RestController
@RequestMapping({ "/v1/api/specialnotes" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class SpecialNotesController {

	@Autowired
	SpecialNotesService specialNotesService;

	@PostMapping("/add-update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpadate(@ModelAttribute SpecialNotesRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = specialNotesService.addOrUpdate(request);
			if (isSuccess) {
				response.put("msg", "Special Notes Added/Updated Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "Special Notes Added/Updated Failed");
				response.put("success", isSuccess);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (EntityNotFoundException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", "Something went wrong");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getspecialnotes")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getSpecialNotesByEventFunction(
			@RequestParam("eventFunctionId") Long eventFunctionId, @RequestParam("managerId") Long managerId,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<SpecialNotesResponseDto> dtos = specialNotesService.getSpecialNotesByEventFunction(eventFunctionId,
					managerId, userId);
			if (dtos.isEmpty()) {
				response.put("success", false);
				response.put("msg", "Data Not Found");
			} else {
				Map<String, Object> specialResp = new HashMap<>();
				specialResp.put("SpecialNotes", dtos);
				response.put("success", true);
				response.put("msg", "Data Found Successfully");
				response.put("data", specialResp);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (EntityNotFoundException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", "Something went wrong");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/deletespecialnotestaskimage")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteSpecialNotesImage(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isDelete = specialNotesService.deleteSpecialNotesImage(id);
			if (isDelete) {
				response.put("msg", "Special Notes Image Deleted Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Special Notes Image Deleted Failed");
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
