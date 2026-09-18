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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.StockTypeRequestDto;
import com.crmportal.response.dto.StockTypeResponseDto;
import com.crmportal.response.dto.StockTypeRightsDTO;
import com.crmportal.service.StockTypeService;

@RestController
@RequestMapping("/v1/api/stocktype")
@CrossOrigin(origins = "*", maxAge = 3600L)
public class StockTypeController {

	@Autowired
	private StockTypeService stockTypeService;

	@PostMapping("/add")
	public ResponseEntity<Map<String, Object>> addStockType(@Valid @RequestBody StockTypeRequestDto request) {

		Map<String, Object> response = new HashMap<>();

		try {
			StockTypeResponseDto dto = stockTypeService.addOrUpdateStockType(request, -1L);

			response.put("msg", "Stock type created successfully");
			response.put("success", true);

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@PutMapping("/update")
	public ResponseEntity<Map<String, Object>> updateStockType(@Valid @RequestBody StockTypeRequestDto request,
			@RequestParam("id") Long id) {

		Map<String, Object> response = new HashMap<>();

		try {
			stockTypeService.addOrUpdateStockType(request, id);

			response.put("msg", "Stock Type updated successfully");
			response.put("success", true);

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	}

	@GetMapping("/getallbyuserid")
	public ResponseEntity<Map<String, Object>> getAllStockType(@RequestParam("userId") Long userId,
			@RequestParam(value = "isActive", required = false) Boolean isActive,
			@RequestParam(value = "mainType", required = false) Integer mainType) {

		Map<String, Object> response = new HashMap<>();

		List<StockTypeResponseDto> list = stockTypeService.getAllStockTypeByUserId(userId, isActive, mainType);

		response.put("data", list);
		response.put("success", !list.isEmpty());
		response.put("msg", !list.isEmpty() ? "Stock Type found" : "Stock type not found");

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/getbyid")
	public ResponseEntity<Map<String, Object>> getById(@RequestParam("id") Long id) {

		Map<String, Object> response = new HashMap<>();

		StockTypeResponseDto dto = stockTypeService.getStockTypeById(id);

		response.put("data", dto);
		response.put("success", dto != null);
		response.put("msg", dto != null ? "Stock Type found" : "Stock type not found");

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Map<String, Object>> delete(@RequestParam("id") Long id) {

		Map<String, Object> response = new HashMap<>();

		Boolean deleted = stockTypeService.deleteStockTypeById(id);

		response.put("success", deleted);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/updatestatus")
	public ResponseEntity<Map<String, Object>> updateStatus(@RequestParam("id") Long id,
			@RequestParam("isActive") Boolean isActive) {

		Map<String, Object> response = new HashMap<>();

		boolean updated = stockTypeService.updateStockTypeStatus(id, isActive);

		response.put("success", updated);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/stocktyperights")
	public ResponseEntity<Map<String, Object>> stockTypeRights(@RequestParam("userId") Long userId,
			@ModelAttribute("stockTypeIds") List<Long> stockTypeIds) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = stockTypeService.stockTypeRights(userId, stockTypeIds);
			if (isSuccess) {
				response.put("msg", "Stock Type Rights Assigned Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "Stock Type Rights Assigned Failed");
				response.put("success", isSuccess);
			}
			return ResponseEntity.ok(response);

		} catch (RuntimeException e) {

			response.put("success", false);
			response.put("msg", e.getMessage());

			return ResponseEntity.ok(response);

		} catch (Exception e) {

			e.printStackTrace();

			response.put("success", false);
			response.put("msg", e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("getallstocktyperights")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllStockTypeRights(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<StockTypeRightsDTO> datas = stockTypeService.getAllStockTypeRights(userId);
			if (datas.isEmpty()) {
				response.put("msg", "Data Not Found");
				response.put("success", false);
			} else {
				response.put("data", datas);
				response.put("msg", "Data Found Successfully");
				response.put("success", true);
			}
			return ResponseEntity.ok(response);

		} catch (RuntimeException e) {

			response.put("success", false);
			response.put("msg", e.getMessage());

			return ResponseEntity.ok(response);

		} catch (Exception e) {

			e.printStackTrace();

			response.put("success", false);
			response.put("msg", e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}