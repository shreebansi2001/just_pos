package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.DecoreMainCategoryItemMasterRequestDto;
import com.crmportal.response.dto.DecoreMainCategoryItemMasterResponseDto;
import com.crmportal.service.DecoreMainCategoryItemMasterService;

@RestController
@RequestMapping({"/v1/api/decoreitem"})
@CrossOrigin(origins = "*", maxAge = 3600L)
public class DecoreMainCategoryItemMasterController {

	@Autowired
	private DecoreMainCategoryItemMasterService decoreItemService;

	@PostMapping(value = "/addorupdate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> saveDecoreItem(
			@Valid @ModelAttribute DecoreMainCategoryItemMasterRequestDto request) {

		Map<String, Object> response = new HashMap<>();

		try {

			DecoreMainCategoryItemMasterResponseDto responseDto = decoreItemService.saveDecoreItem(request);

			response.put("moduleId", responseDto.getId());
			response.put("moduleName", "DecoreItem");
			response.put("success", true);

			if (request.getId() == null) {
				response.put("msg", "Decore Item Created Successfully");
			} else {
				response.put("msg", "Decore Item Updated Successfully");
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

	@GetMapping("/getallbyuserid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllDecoreItems(@RequestParam("userId") Long userId,
			@RequestParam(value = "itemName", required = false) String itemName,
			@RequestParam(value = "categoryId", required = false) Long categoryId,
			@RequestParam(value = "isActive", required = false) Boolean isActive,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {

		Map<String, Object> response = new HashMap<>();

		try {

			int pageIndex = Math.max(page - 1, 0);

			Pageable pageable = PageRequest.of(pageIndex, size);

			Page<DecoreMainCategoryItemMasterResponseDto> pageResult = decoreItemService.getAllDecoreItems(userId,
					itemName, categoryId, isActive, pageable);

			Map<String, Object> data = new HashMap<>();

			data.put("items", pageResult.getContent());
			data.put("currentPage", page);
			data.put("totalItems", pageResult.getTotalElements());
			data.put("totalPages", pageResult.getTotalPages());

			response.put("data", data);
			response.put("success", true);
			response.put("msg", "Decore Items Found");

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

	/**
	 * Get By Id
	 */
	@GetMapping("/getbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getDecoreItemById(@RequestParam("id") Long id) {

		Map<String, Object> response = new HashMap<>();

		try {

			DecoreMainCategoryItemMasterResponseDto dto = decoreItemService.getDecoreItemById(id);

			response.put("data", dto);
			response.put("success", true);
			response.put("msg", "Decore Item Found");

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

	@DeleteMapping("/deletebyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteDecoreItemById(@RequestParam("id") Long id) {

		Map<String, Object> response = new HashMap<>();

		try {

			boolean deleted = decoreItemService.deleteDecoreItemById(id);

			response.put("success", deleted);

			if (deleted) {
				response.put("msg", "Decore Item Deleted Successfully");
			} else {
				response.put("msg", "Decore Item Delete Failed");
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

	@PutMapping("/updatestatus")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateStatus(@RequestParam("id") Long id,
			@RequestParam("isActive") Boolean isActive) {

		Map<String, Object> response = new HashMap<>();

		try {

			boolean updated = decoreItemService.updateDecoreItemStatus(id, isActive);

			response.put("success", updated);

			if (updated) {
				response.put("msg", "Status Updated Successfully");
			} else {
				response.put("msg", "Status Update Failed");
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