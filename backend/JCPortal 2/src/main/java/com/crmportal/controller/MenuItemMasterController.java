package com.crmportal.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.aspectj.apache.bcel.classfile.Module.Require;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import com.crmportal.request.dto.MenuAllocationChangeRequestDto;
import com.crmportal.request.dto.MenuAllocationItemRequestDto;
import com.crmportal.request.dto.MenuItemMasterRequestDto;
import com.crmportal.request.dto.MenuRawMaterialIdsDto;
import com.crmportal.request.dto.UpdateItemRawMaterialWeightRequestDto;
import com.crmportal.request.dto.UpdateMisMatchedUnitsRequestDto;
import com.crmportal.response.dto.ExistingItemRawResponseDto;
import com.crmportal.response.dto.ItemRawMaterialsResponseDto;
import com.crmportal.response.dto.MenuItemCaptainReceipeResponseDto;
import com.crmportal.response.dto.MenuItemCategoryChangeResponseDto;
import com.crmportal.response.dto.MenuItemMasterResponseDto;
import com.crmportal.response.dto.MenuItemRawMaterialsResponseDto;
import com.crmportal.response.dto.RawMaterialMasterResponseDto;
import com.crmportal.response.dto.WrongItemRawMaterialResponseDto;
import com.crmportal.service.MenuItemMasterService;
import com.crmportal.service.MenuItemRawMaterialService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/menuitems" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class MenuItemMasterController {

	@Autowired
	MenuItemMasterService menuItemMasterService;

	@Autowired
	MenuItemRawMaterialService menuItemRawMaterialService;

	@PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addMenuItemMaster(
			@Valid @ModelAttribute MenuItemMasterRequestDto request,
			@RequestParam(value = "file", required = false) MultipartFile file) {
		Map<String, Object> response = new HashMap<>();
		try {
			MenuItemMasterResponseDto responseDto = menuItemMasterService.addOrUpdateMenuItemMaster(request,
					Long.parseLong("-1"), file);
			if (responseDto != null) {
				response.put("moduleId", responseDto.getId());
				response.put("moduleName", "MenuItem");
				response.put("FileType", "Img");
				response.put("msg", ConstantsPoc.MENU_ITEM_CREATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.MENU_ITEM_CREATE_FAIL);
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

	@PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateMenuItemMaster(
			@Valid @ModelAttribute MenuItemMasterRequestDto request, @RequestParam("id") Long id,
			@RequestParam(value = "file", required = false) MultipartFile file) {
		Map<String, Object> response = new HashMap<>();
		try {
			MenuItemMasterResponseDto responseDto = menuItemMasterService.addOrUpdateMenuItemMaster(request, id, file);
			if (responseDto != null) {
				response.put("msg", ConstantsPoc.MENU_ITEM_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.MENU_ITEM_UPDATE_FAIL);
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

	@PutMapping("/updatemenuallocation")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateMenuAllocation(
			@RequestBody List<MenuAllocationChangeRequestDto> request) {
		Map<String, Object> response = new HashMap<>();
		Boolean isDelete = false;

		try {
			isDelete = menuItemMasterService.updateMenuAllocationItemConfig(request);
			if (isDelete) {
				response.put("msg", "Menu Allocation Updated Successfully.");
				response.put("success", true);
			} else {
				response.put("msg", "Menu Allocation is not Updated.");
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

	@PutMapping("/updatemenuitemcategory")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateMenuItemCategory(
			@RequestParam("menu_item_ids") List<Long> menuItemIds, @RequestParam("new_cat_id") Long newCatId,
			@RequestParam("user_id") Long userId) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;

		try {

			isSuccess = menuItemMasterService.updateMenuItemCategory(menuItemIds, newCatId, userId);
			if (isSuccess) {
				response.put("success", isSuccess);
				response.put("msg", "Category updated Successfully.");
			} else {
				response.put("success", isSuccess);
				response.put("msg", "Category is not updated.");
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

	@PutMapping("/updatemenusubcategory")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateMenuItemSubcategory(
			@RequestParam("menu_item_ids") List<Long> menuItemIds,
			@RequestParam("new_menu_subcat_ids") Long newMenuSubCatId, @RequestParam("userId") Long userId) {

		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {

			isSuccess = menuItemMasterService.updateMenuItemSubCategory(menuItemIds, newMenuSubCatId, userId);

			if (!isSuccess) {
				response.put("msg", "Menu Item subcategory is not updated.");
				response.put("success", false);
			} else {
				response.put("msg", "Menu Item subcategory updated successfully.");
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

	@PutMapping("/updatemenuitemallocation")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateMenuItemAllocation(@RequestParam("to_type") String toType,
			@RequestBody List<MenuAllocationItemRequestDto> allocationItemRequestDtos,
			@RequestParam("user_id") Long userId) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {

			isSuccess = menuItemMasterService.updateMenuAllocation(toType, allocationItemRequestDtos, userId);

			if (!isSuccess) {
				response.put("msg", "Menu allocation is not updated.");
				response.put("success", false);
			} else {
				response.put("msg", "Menu allocation updated successfully.");
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

	@GetMapping("/getmenubycatorsubcat")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getMenuItemByCatOrSubcat(
			@RequestParam("menu_cat_ids") List<Long> menuCatIds,
			@RequestParam(value = "menu_subcat_ids", required = false) List<Long> menuSubcatIds,
			@RequestParam(value = "type", required = false) String type, @RequestParam("userId") Long userId) {

		Map<String, Object> response = new HashMap<>();
		try {

			List<MenuItemCategoryChangeResponseDto> responseDtos = menuItemMasterService
					.getAllMenuItemsByCategory(menuCatIds, menuSubcatIds, userId, type);

			if (responseDtos == null) {
				response.put("msg", "Menu Items not found.");
				response.put("success", false);
			} else {
				response.put("darta", responseDtos);
				response.put("msg", "Menu Items found successfully.");
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

	@GetMapping("/getallbyuserid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllMenuItem(@RequestParam("userId") Long userId,
			@RequestParam(value = "itemName", required = false) String itemName,
			@RequestParam(defaultValue = "1") int page, // <-- Start from 1
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(value = "menuCatId", required = false) Long menuCatId,
			@RequestParam(value = "menuSubCatId", required = false) Long menuSubCatId,
			@RequestParam(value = "isAcs", required = false) Boolean isAcs,
			@RequestParam(value = "isWithRecipe", required = false) Boolean isWithRecipe) {
		Map<String, Object> response = new HashMap<>();

		try {
			int pageIndex = page - 1;
			if (pageIndex < 0)
				pageIndex = 0;

			Pageable pageable = PageRequest.of(pageIndex, size);

			Page<MenuItemMasterResponseDto> pageResult = menuItemMasterService.getAllMenuItem(userId, itemName,
					pageable, menuCatId, menuSubCatId, isAcs, isWithRecipe);

			if (pageResult.isEmpty()) {
				response.put("msg", ConstantsPoc.MENU_ITEM_FOUND_FAIL);
				response.put("success", false);
			} else {

				Map<String, Object> menuItemResp = new HashMap<>();
				menuItemResp.put("items", pageResult.getContent());
				menuItemResp.put("currentPage", page);
				menuItemResp.put("totalItems", pageResult.getTotalElements());
				menuItemResp.put("totalPages", pageResult.getTotalPages());
				response.put("data", menuItemResp);
				response.put("msg", ConstantsPoc.MENU_ITEM_FOUND_SUCCESS);
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

	@GetMapping("/getbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getMenuItemById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			MenuItemMasterResponseDto itemMasterResponseDtos = menuItemMasterService.getMenuItemById(id);
			if (itemMasterResponseDtos == null) {
				response.put("msg", ConstantsPoc.MENU_ITEM_FOUND_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> menuItemResp = new HashMap<>();
				menuItemResp.put("Menu Item Details", itemMasterResponseDtos);
				response.put("data", menuItemResp);
				response.put("msg", ConstantsPoc.MENU_ITEM_FOUND_SUCCESS);
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

	@DeleteMapping("/deletebyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteMenuItemById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = menuItemMasterService.deleteMenuItemById(id);
			if (!isSuccess) {
				response.put("msg", ConstantsPoc.MENU_ITEM_DELETE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.MENU_ITEM_DELETE_SUCCESS);
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

	@PutMapping("/updatestatus")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateMenuItemStatus(@RequestParam("id") Long id,
			@RequestParam("isActive") Boolean isActive) {

		Map<String, Object> response = new HashMap<>();
		try {
			boolean updated = menuItemMasterService.updateMenuItemStatus(id, isActive);
			if (updated) {
				response.put("msg", ConstantsPoc.MENU_CATEGORY_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.MENU_CATEGORY_UPDATE_FAIL);
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

	@DeleteMapping("/deleteitemrawmaterialbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteMenuItemRawmaterialById(@RequestBody MenuRawMaterialIdsDto id) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = menuItemMasterService.deleteMenuItemRawmaterialById(id);
			if (!isSuccess) {
				response.put("msg", ConstantsPoc.MENU_ITEM_RAW_DELETE_FAIL);
				response.put("success", false);
			} else {
				response.put("msg", ConstantsPoc.MENU_ITEM_RAW_DELETE_SUCCESS);
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

	@GetMapping("/getmenuitemrawmaterialbymenuid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getMenuItemRawMaterialByMenuId(
			@RequestParam("menuItemId") Long menuItemId, @RequestParam("userId") Long userId,
			@RequestParam("isSync") Boolean isSync) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<MenuItemRawMaterialsResponseDto> dtos = menuItemRawMaterialService
					.getMenuItemRawMaterialByMenuId(menuItemId, userId, isSync);
			if (dtos.isEmpty()) {
				response.put("msg", ConstantsPoc.MENU_ITEM_FOUND_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> menuItemResp = new HashMap<>();
				menuItemResp.put("menuItemRawMaterials", dtos);
				response.put("data", menuItemResp);
				response.put("msg", ConstantsPoc.MENU_ITEM_FOUND_SUCCESS);
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

	@GetMapping("/getmenuitemcaptainreceipebymenuid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getMenuItemCaptainReceipeByMenuId(
			@RequestParam("menuItemId") Long menuItemId, @RequestParam("userId") Long userId,
			@RequestParam("isSync") Boolean isSync) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<MenuItemCaptainReceipeResponseDto> dtos = menuItemRawMaterialService
					.getMenuItemCaptainReceipeByMenuId(menuItemId, userId, isSync);

			if (dtos.isEmpty()) {
				response.put("msg", ConstantsPoc.MENU_ITEM_FOUND_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> menuItemResp = new HashMap<>();
				menuItemResp.put("menuItemRawMaterials", dtos);
				response.put("data", menuItemResp);
				response.put("msg", ConstantsPoc.MENU_ITEM_FOUND_SUCCESS);
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

	@GetMapping("/getmismatchedunitsbyuserid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getMismatchedUnitsByUserId(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<WrongItemRawMaterialResponseDto> dtos = menuItemRawMaterialService.getMismatchedUnitsByUserId(userId);
			if (dtos.isEmpty()) {
				response.put("msg", "Data Fetch Failed");
				response.put("success", false);
			} else {
				Map<String, Object> menuItemResp = new HashMap<>();
				menuItemResp.put("ItemRawMaterialUnit", dtos);
				response.put("data", menuItemResp);
				response.put("msg", "Data Fetch Successfully");
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

	@PutMapping("/updatemismatchedunits")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateMisMatchedUnits(
			@RequestBody List<UpdateMisMatchedUnitsRequestDto> request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = menuItemRawMaterialService.updateMisMatchedUnits(request);

			if (!isSuccess) {
				response.put("msg", "Menu Item Raw Updated Failed");
				response.put("success", false);
			} else {
				response.put("msg", "Menu Item Raw Updated Successfully");
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

	@GetMapping("/syncallitemrawmaterialrate")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> syncAllItemRawMaterialRate(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = menuItemRawMaterialService.syncAllItemRawMaterialRate(userId);
			if (!isSuccess) {
				response.put("msg", "Menu Item Raw Rate Updated Failed");
				response.put("success", false);
			} else {
				response.put("msg", "Menu Item Raw Rate Updated Successfully");
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

	@GetMapping("/syncallcaptainreceiperate")
	public ResponseEntity<?> syncAllCaptainReceipeRate(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = menuItemRawMaterialService.syncAllCaptainReceipeRate(userId);
			if (!isSuccess) {
				response.put("msg", "Menu Item Raw Rate Updated Failed");
				response.put("success", false);
			} else {
				response.put("msg", "Menu Item Raw Rate Updated Successfully");
				response.put("success", true);
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

	@GetMapping("/getallexistingrawitems")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllExistingItems(@RequestParam("userId") Long userId,
			@RequestParam("isCaptainRecipe") Boolean isCaptainRecipe) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<ExistingItemRawResponseDto> dtos = menuItemMasterService.getAllExistingItems(userId,isCaptainRecipe);
			if (dtos.isEmpty()) {
				response.put("msg", "Menu Item Not Found");
				response.put("success", false);
			} else {
				Map<String, Object> menuItemResp = new HashMap<>();
				menuItemResp.put("ItemDetails", dtos);
				response.put("data", menuItemResp);
				response.put("msg", "Menu Item Found");
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

	@GetMapping("getitemrawmaterialbyrawmaterial")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getItemRawMaterialByRawMaterial(
			@RequestParam("rawMaterialId") Long rawMaterialId, @RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<ItemRawMaterialsResponseDto> dtos = menuItemRawMaterialService
					.getItemRawMaterialByRawMaterialId(rawMaterialId, userId);
			if (dtos.isEmpty()) {
				response.put("msg", "RawMaterial Item Not Found");
				response.put("success", false);
			} else {
				Map<String, Object> menuItemResp = new HashMap<>();
				menuItemResp.put("rawMaterialDetails", dtos);
				response.put("data", menuItemResp);
				response.put("msg", "RawMaterial Item Found");
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

	@PutMapping("updateitemrawmaterialweight")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateItemRawMaterialWeight(
			@RequestBody List<UpdateItemRawMaterialWeightRequestDto> request) {

		Map<String, Object> response = new HashMap<>();

		try {
			Boolean isSuccess = menuItemRawMaterialService.updateItemRawMaterialWeight(request);
			if (!isSuccess) {
				response.put("msg", "Menu Item Raw Weight Updated Failed");
				response.put("success", false);
			} else {
				response.put("msg", "Menu Item Raw Weight Updated Successfully");
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
