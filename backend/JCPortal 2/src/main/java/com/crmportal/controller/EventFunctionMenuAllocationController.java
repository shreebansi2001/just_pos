package com.crmportal.controller;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.request.dto.EventFunctionMenuAllocationRequestDto;
import com.crmportal.request.dto.EventFunctionMenuItemRawMaterialRequestDto;
import com.crmportal.request.dto.ItemWeightRateCalRequestDto;
import com.crmportal.response.dto.EventFunctionMenuAllocationFullResponseDto;
import com.crmportal.response.dto.EventFunctionMenuAllocationFunctionFullResponseDto;
import com.crmportal.response.dto.MenuAllocationAgencyWithItemsResponseDto;
import com.crmportal.response.dto.MenuAllocationItemRawMaterialResponseDto;
import com.crmportal.service.EventFunctionMenuAllocationService;
import com.crmportal.service.MenuAllocationItemRawMaterialService;
import com.crmportal.utility.ConstantsPoc;

import springfox.documentation.spring.web.plugins.Docket;

@RestController
@RequestMapping({ "/v1/api/menuallocation" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventFunctionMenuAllocationController {

	private final Docket api;

	@Autowired
	EventFunctionMenuAllocationService eventFunctionMenuAllocationService;

	@Autowired
	EventFunctionMasterRepository eventFunctionMasterRepository;

	@Autowired
	MenuAllocationItemRawMaterialService menuAllocationItemRawMaterialService;

	EventFunctionMenuAllocationController(Docket api) {
		this.api = api;
	}

	@PostMapping("/add-update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addMenuAllocation(
			@Valid @RequestBody List<EventFunctionMenuAllocationRequestDto> request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = eventFunctionMenuAllocationService.addOrUpdatedMenuAllocation(request);
			if (isSuccess) {
				response.put("msg", ConstantsPoc.MENU_ALLOCATION_CREATE_SUCCESS);
				response.put("success", isSuccess);
			} else {
				response.put("msg", ConstantsPoc.MENU_ALLOCATION_CREATE_FAIL);
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

	@GetMapping("/getmenuallocation")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getMenuAllocation(@RequestParam("eventId") Long eventId,
			@RequestParam("eventFunctionId") Long eventFunctionId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventFunctionMenuAllocationFullResponseDto> dtos = eventFunctionMenuAllocationService
					.getMenuAllocation(eventId, eventFunctionId);
			if (dtos.isEmpty()) {
				response.put("msg", ConstantsPoc.MENU_ALLOCATION_FOUND_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> menuAllocationResp = new HashMap<>();
				menuAllocationResp.put("Menu Allocation Details", dtos);
				response.put("data", menuAllocationResp);
				response.put("msg", ConstantsPoc.MENU_ALLOCATION_FOUND_SUCCESS);
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

	@GetMapping("/getitembytype")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getItemByType(@RequestParam("eventId") Long eventId,
			@RequestParam("eventFunctionId") Long eventFunctionId, @RequestParam("type") String type) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventFunctionMenuAllocationFunctionFullResponseDto> dtos = eventFunctionMenuAllocationService
					.getMenuAllocation(eventId, eventFunctionId, type);
			if (dtos.isEmpty()) {
				response.put("msg", ConstantsPoc.MENU_ALLOCATION_FOUND_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> menuAllocationResp = new HashMap<>();
				menuAllocationResp.put("Menu Allocation Details", dtos);
				response.put("data", menuAllocationResp);
				response.put("msg", ConstantsPoc.MENU_ALLOCATION_FOUND_SUCCESS);
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

	@DeleteMapping("/deletemenuallocationorder")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteMenuAllocation(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = eventFunctionMenuAllocationService.deleteMenuAllocationOrder(id);
			if (isSuccess) {
				response.put("msg", ConstantsPoc.MENU_ALLOCATION_ORDER_DELETE_SUCCESS);
				response.put("success", isSuccess);
			} else {
				response.put("msg", ConstantsPoc.MENU_ALLOCATION_ORDER_DELETE_FAIL);
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

	@GetMapping("/getrawmaterialbyitem")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getRawMaterialByItem(@RequestParam("menuItemId") Long menuItemId,
			@RequestParam("eventFunctionId") Long eventFunctionId,
			@RequestParam(value = "oldPax", required = false) Integer oldPax,
			@RequestParam(value = "newPax", required = false) Integer newPax) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventFunctionMasterEntity entity = eventFunctionMasterRepository.findByIdAndIsDeleteFalse(eventFunctionId)
					.get();
			List<MenuAllocationItemRawMaterialResponseDto> dtos = eventFunctionMenuAllocationService
					.getAllRawMaterial(menuItemId, entity, null);
			if (dtos.isEmpty()) {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_ITEM_NOT_FOUND);
				response.put("success", false);
			} else {
				Map<String, Object> menuAllocationResp = new HashMap<>();
				menuAllocationResp.put("MenuItem RawMaterial Details", dtos);
				response.put("data", menuAllocationResp);
				response.put("msg", ConstantsPoc.RAW_MATERIAL_ITEM_FOUND);
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

	@PostMapping("/addorupdatemenuitemrawmat")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdateMenuItemRawMaterial(
			@Valid @RequestBody List<EventFunctionMenuItemRawMaterialRequestDto> requests) {
		Boolean isSuccess = false;
		Map<String, Object> response = new HashMap<>();
		try {
			isSuccess = menuAllocationItemRawMaterialService.addOrUpdateMenuItemRawMaterial(requests);
			if (isSuccess) {
				response.put("msg", ConstantsPoc.MENU_ALLOCATION_ITEM_RAWMAT_UPDATE_SUCCESS);
				response.put("isFromNewTable", true);
				response.put("success", isSuccess);
			} else {
				response.put("msg", ConstantsPoc.MENU_ALLOCATION_ITEM_RAWMAT_UPDATE_FAIL);
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

	@DeleteMapping("/deletemenuitemrawmaterial")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteMenuItemRawMaterialById(@RequestParam("id") Long id,
			@RequestParam("menuItemId") Long menuItemId, @RequestParam("eventId") Long eventId,
			@RequestParam("eventFunctionId") Long eventFunctionId) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = menuAllocationItemRawMaterialService.deleteMenuItemRawMaterialById(id, menuItemId, eventId,
					eventFunctionId);
			if (isSuccess) {
				response.put("msg", ConstantsPoc.MENU_ALLOCATION_ITEM_RAWMAT_DELETE_SUCCESS);
				response.put("success", isSuccess);
			} else {
				response.put("msg", ConstantsPoc.MENU_ALLOCATION_ITEM_RAWMAT_DELETE_FAIL);
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

	@GetMapping("/getagencywithitemsbytype")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAgencyWithItemType(@RequestParam("type") String type,
			@RequestParam("eventId") Long eventId, @RequestParam("eventFunctionId") Long eventFunctionId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<MenuAllocationAgencyWithItemsResponseDto> responseDtos = eventFunctionMenuAllocationService
					.getAgencyWithItemType(type, eventId, eventFunctionId, 0l);

			if (responseDtos == null) {
				response.put("msg", ConstantsPoc.MENU_ALLOCATION_FOUND_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> menuAllocationResp = new HashMap<>();
				menuAllocationResp.put("Menu Allocation Details", responseDtos);
				response.put("data", menuAllocationResp);
				response.put("msg", ConstantsPoc.MENU_ALLOCATION_FOUND_SUCCESS);
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

	@DeleteMapping("/syncrawmaterialitems")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> syncRawMaterialItem(
			@RequestParam("eventFunctionid") Long eventFunctionid, @RequestParam("eventId") Long eventId) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;

		try {
			isSuccess = eventFunctionMenuAllocationService.syncRawMaterialItemByEventFunctionId(eventFunctionid,
					eventId);
			if (!isSuccess) {
				response.put("msg", ConstantsPoc.MENU_ALLOCATION_ITEM_RAWMAT_SYNC_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.MENU_ALLOCATION_ITEM_RAWMAT_SYNC_SUCCESS);
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

	@DeleteMapping("/syncitemwiserawmaterial")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> syncItemWiseRawmaterial(
			@RequestParam("eventFunctionid") Long eventFunctionid, @RequestParam("eventId") Long eventId,
			@RequestParam("menuItemId") Long menuItemId) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;

		try {
			isSuccess = eventFunctionMenuAllocationService.syncItemWiseRawmaterialByMenuItemId(eventFunctionid,
					eventId, menuItemId);
			if (!isSuccess) {
				response.put("msg", ConstantsPoc.MENU_ALLOCATION_ITEM_RAWMAT_SYNC_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.MENU_ALLOCATION_ITEM_RAWMAT_SYNC_SUCCESS);
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

	@DeleteMapping("/syncagency")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> removeAgency(@RequestParam("eventFunctionId") Long eventFunctionId,
			@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;

		try {

			isSuccess = eventFunctionMenuAllocationService.removeAgencyByEventFunctionId(eventFunctionId, userId);
			if (!isSuccess) {
				response.put("msg", ConstantsPoc.AGENCY_REMOVE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.AGENCY_REMOVE_SUCCESS);
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

	@PutMapping("/calculations")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> calculateRate(@RequestBody ItemWeightRateCalRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			BigDecimal totalRate = eventFunctionMenuAllocationService.calculateRate(request);
			if (totalRate != null) {
				response.put("msg", "Calculate Successfully");
				response.put("data", totalRate);
				response.put("success", true);
			} else {
				response.put("msg", "Calculate Failed");
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
