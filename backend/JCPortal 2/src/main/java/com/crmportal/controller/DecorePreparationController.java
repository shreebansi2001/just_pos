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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.DecorePreparationRequestDto;
import com.crmportal.request.dto.EventFunctionDecorItemImagesRequestDto;
import com.crmportal.response.dto.DecorePreparationCombResponseDto;
import com.crmportal.response.dto.DecorePreparationResponseDto;
import com.crmportal.service.DecorePreparationService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/decorepreparation" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class DecorePreparationController {

	@Autowired
	DecorePreparationService decorePreparationService;

	@PostMapping("/addOrUpdate")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdateDecorePreparation(
			@Valid @RequestBody DecorePreparationRequestDto request) {

		Map<String, Object> response = new HashMap<>();

		try {
			Long id = (request.getId() != null && request.getId() > 0) ? request.getId() : 0L;

			DecorePreparationResponseDto responseDto = decorePreparationService.addOrUpdateDecorePreparation(request);

			if (responseDto == null) {
				response.put("success", false);
				response.put("msg", id == 0 ? ConstantsPoc.MENU_PALNNING_MASTER_ADD_FAILED
						: ConstantsPoc.MENU_PLANNING_MASTER_UPDATE_FAILED);
			} else {
				response.put("success", true);
				response.put("msg", id == 0 ? ConstantsPoc.MENU_PALNNING_MASTER_ADD_SUCCESS
						: ConstantsPoc.MENU_PLANNING_MASTER_UPDATE_SUCCESS);
				response.put("data", responseDto);
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

	@GetMapping("/getdecorepreparationitems")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getDecorePreparationItems(@RequestParam("pageNo") Integer pageNo,
			@RequestParam("totalRecord") Integer totalRecord, @RequestParam("decoreCategoryId") Long decoreCategoryId,
			@RequestParam("eventFunctionId") Long eventFunctionId,
			@RequestParam(value = "itemName", required = false) String itemName, @RequestParam("userId") Long userId) {

		Map<String, Object> response = new HashMap<>();

		try {

			DecorePreparationCombResponseDto responseDto = decorePreparationService.getDecorePreparationItems(pageNo,
					totalRecord, decoreCategoryId, eventFunctionId, itemName, userId);

			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", ConstantsPoc.MENU_PREPARATION_ITEM_FETCH_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.MENU_PREPARATION_ITEM_FETCH_FAIL);
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

	@DeleteMapping("/deletedecrepreparationitem")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteDecorePreparationItem(
			@RequestParam("decorePreparationId") Long decorePreparationId,
			@RequestParam("decoreCategoryId") Long decoreCategoryId, @RequestParam("itemId") Long itemId) {

		Map<String, Object> response = new HashMap<>();

		try {
			Boolean isSuccess = decorePreparationService.deleteDecorePreparationItem(decorePreparationId,
					decoreCategoryId, itemId);

			if (isSuccess) {
				response.put("msg", ConstantsPoc.MENU_PLANNING_MASTER_DELETE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.MENU_PLANNING_MASTER_DELETE_FAILED);
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

	@GetMapping("/copyeventfunctiondecore")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> copyEventFunctionDecore(
			@RequestParam("oldEventFunctionId") Long oldEventFunctionId,
			@RequestParam("activeEventFunctionId") Long activeEventFunctionId) {

		Map<String, Object> response = new HashMap<>();

		try {
			Boolean result = decorePreparationService.copyEventFunctionDecore(oldEventFunctionId,
					activeEventFunctionId);

			if (Boolean.TRUE.equals(result)) {
				response.put("msg", "Decore Copy Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Decore Copy Failed");
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

	@PostMapping("/decorimage")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> decorImages(
			@ModelAttribute EventFunctionDecorItemImagesRequestDto request) {
		Map<String, Object> response = new HashMap<>();

		try {
			List<String> images = decorePreparationService.uploadDecorImages(request);

			if (!images.isEmpty()) {
				response.put("msg", "Decore Item Images Successfully");
				response.put("success", true);
				response.put("data", images);
			} else {
				response.put("msg", "Decore Item Images Failed");
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