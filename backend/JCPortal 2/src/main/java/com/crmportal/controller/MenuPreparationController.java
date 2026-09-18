package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.MenuPreparationRequestDto;
import com.crmportal.response.dto.EventAndFunctionWisePartyResponseDto;
import com.crmportal.response.dto.EventFunctionRawMaterialPermissionResponseDto;
import com.crmportal.response.dto.MenuItemPartyMasterResponseDto;
import com.crmportal.response.dto.MenuPreparationCombResponseDto;
import com.crmportal.response.dto.MenuPreparationResponseDto;
import com.crmportal.response.dto.SavedMenuPreparationResponseDto;
import com.crmportal.service.MenuPreparationService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/menupreparation" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class MenuPreparationController {

	@Autowired
	MenuPreparationService menuPreparationService;

	@PostMapping("/addOrUpdate")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdateMenuPreparation(
			@Valid @RequestBody MenuPreparationRequestDto request) {

		Map<String, Object> response = new HashMap<>();
		try {
			Long preparationId = (request.getId() != null && request.getId() > 0) ? request.getId() : 0L;

			MenuPreparationResponseDto responseDto = menuPreparationService.addOrUpdateMenuPreparation(request);

			if (responseDto == null) {
				response.put("msg", preparationId == 0 ? ConstantsPoc.MENU_PALNNING_MASTER_ADD_FAILED
						: ConstantsPoc.MENU_PLANNING_MASTER_UPDATE_FAILED);
				response.put("success", false);
			} else {
				response.put("msg", preparationId == 0 ? ConstantsPoc.MENU_PALNNING_MASTER_ADD_SUCCESS
						: ConstantsPoc.MENU_PLANNING_MASTER_UPDATE_SUCCESS);
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

	@GetMapping("/getmenupreparationitems")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getMenuPreparationItems(@RequestParam("pageNo") Integer pageNo,
			@RequestParam("totalRecord") Integer totalRecord, @RequestParam("menuCategoryId") Long menuCategoryId,
			@RequestParam("eventFunctionId") Long eventFunctionId,
			@RequestParam(value = "itemName", required = false) String itemName, @RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			MenuPreparationCombResponseDto responseDto = menuPreparationService.getMenuPreparationItems(pageNo,
					totalRecord, menuCategoryId, eventFunctionId, itemName, userId);
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

	@DeleteMapping("/deletemenupreparationitem")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteMenuPreparationItem(
			@RequestParam("menuPreparationId") Long menuPreparationId,
			@RequestParam("menuCategoryId") Long menuCategoryId, @RequestParam("itemId") Long itemId) {
		Boolean isSuccess = false;
		Map<String, Object> response = new HashMap<>();
		try {
			isSuccess = menuPreparationService.deleteMenuPreparationItem(menuPreparationId, menuCategoryId, itemId);
			if (isSuccess) {
				response.put("msg", ConstantsPoc.MENU_PLANNING_MASTER_DELETE_SUCCESS);
				response.put("success", isSuccess);
			} else {
				response.put("msg", ConstantsPoc.MENU_PLANNING_MASTER_DELETE_FAILED);
				response.put("success", isSuccess);
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

	@GetMapping("/getselectedmenuitembyeventandeventfunctionid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getSelectedMenuItemByEventAndEventFunctionid(
			@RequestParam("eventId") Long eventId, @RequestParam("eventFunctionId") Long eventFunctionId,
			@RequestParam(value = "partyIds", required = false) List<Long> partyIds) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<MenuItemPartyMasterResponseDto> dto = menuPreparationService
					.getSelectedMenuItemByEventFunctionid(eventId, eventFunctionId, partyIds);
			if (dto.isEmpty()) {
				response.put("msg", ConstantsPoc.MENU_PREPARATION_ITEM_FETCH_FAIL);
				response.put("success", false);
			} else {
				response.put("data", dto);
				response.put("msg", ConstantsPoc.MENU_PREPARATION_ITEM_FETCH_SUCCESS);
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

	@GetMapping("/getagencybyeventandeventfunctionid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAgencyByEventAndEventFunctionid(@RequestParam("eventId") Long eventId,
			@RequestParam(value = "eventFunctionId", required = false) List<Long> eventFunctionIds,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "userId", required = false) Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventAndFunctionWisePartyResponseDto> dto = menuPreparationService
					.getAgencyByEventAndEventFunctionid(eventId, eventFunctionIds, type, userId);
			if (dto.isEmpty()) {
				response.put("msg", "No Agency Found Failed");
				response.put("success", false);
			} else {
				response.put("data", dto);
				response.put("msg", "No Agency Found Successfully");
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

	@GetMapping("/copyeventfunctionmenu")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> copyEventFunctionMenu(
			@RequestParam("oldEventFunctionId") Long oldEventFunctionId,
			@RequestParam("activeEventFunctionId") Long activeEventFunctionId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean responseDto = menuPreparationService.copyeventfunctionmenu(oldEventFunctionId,
					activeEventFunctionId);
			if (responseDto != null) {
				response.put("msg", "Menu Copy Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Menu Copy Failed");
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

	@PostMapping("/updatePreparationStatus")
	public ResponseEntity<?> updateMenuPreparationStatus(@RequestParam("eventId") Long eventId,
			@RequestParam("status") String status) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = menuPreparationService.updateEventMenuPreparationStatus(eventId, status);

			if (isSuccess != null && isSuccess) {
				response.put("msg", ConstantsPoc.STATUS_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.STATUS_UPDATE_FAIL);
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

	@GetMapping("/getPrepStatus")
	public ResponseEntity<?> getMenuPreparationStatus(@RequestParam("eventId") Long eventId) {
		Map<String, Object> response = new HashMap<>();
		try {
			String status = menuPreparationService.getMenuPreparationStatus(eventId);

			if (status != null) {
				response.put("msg", ConstantsPoc.STATUS_FOUND_SUCCESS);
				response.put("success", true);
				response.put("data", status);
			} else {
				response.put("msg", ConstantsPoc.STATUS_FOUND_FAIL);
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

	@GetMapping("/geteventfunctionpermissionrawmaterial")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getEventFunctionPermissionRawMaterial(
			@RequestParam("eventId") Long eventId, @RequestParam("eventFunctionId") Long eventFunctionId,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventFunctionRawMaterialPermissionResponseDto dto = menuPreparationService
					.getEventFunctionPermissionRawMaterial(eventId, eventFunctionId, userId);
			if (dto != null) {
				response.put("msg", ConstantsPoc.DATA_FETCH_SUCCESS);
				response.put("success", true);
				response.put("data", dto);
			} else {
				response.put("msg", ConstantsPoc.DATA_FETCH_FAIL);
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
	
	@GetMapping("/sync-slogan")
	public ResponseEntity<Map<String, Object>> getSyncItemSlogan(
			@RequestParam("menuItemId") Long menuItemId,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			String slogan = menuPreparationService.generateMenuItemSlogan(menuItemId, userId); 
			
			if (slogan != null) {
				response.put("msg", ConstantsPoc.DATA_FETCH_SUCCESS);
				response.put("success", true);
				response.put("data", slogan);
			} else {
				response.put("msg", ConstantsPoc.DATA_FETCH_FAIL);
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
	
	@PutMapping(value = "/update-item-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateItemImage(
			@RequestParam("menuItemId") Long menuItemId,
			@RequestParam("userId") Long userId,
			@RequestParam("image") MultipartFile image) {
		Map<String, Object> response = new HashMap<>();
		try {
			String path = menuPreparationService.updateMenuItemImage(menuItemId, userId, image); 
			
			if (path != null) {
				response.put("msg", ConstantsPoc.FILE_UPLOAD_SUCCESS);
				response.put("success", true);
				response.put("data", path);
			} else {
				response.put("msg", ConstantsPoc.FILE_UPLOAD_FAIL);
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
	
	@GetMapping("/getAllMenuPreparationItems")
	public ResponseEntity<Map<String, Object>> getAllMenuPreparationItems(@RequestParam("eventId") Long eventId, @RequestParam("eventFunctionId") Long eventFunctionId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<SavedMenuPreparationResponseDto> data = menuPreparationService.getAllMenuPreparationItems(eventId, eventFunctionId);
			
			if (data != null) {
				response.put("msg", ConstantsPoc.DATA_FETCH_SUCCESS);
				response.put("success", true);
				response.put("data", data);
			} else {
				response.put("msg", ConstantsPoc.DATA_FETCH_FAIL);
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
