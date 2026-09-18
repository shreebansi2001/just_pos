package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.response.dto.EventDishCostingResponseDto;
import com.crmportal.response.dto.RawMaterialCategoryRateResponseDto;
import com.crmportal.service.EventDishCostingService;
import com.crmportal.service.impl.MenuItemRawMaterialServiceImpl;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({"/v1/api/dish-costing"})
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventDishCostingController {

	@Autowired
	EventDishCostingService eventDishCostingService;
	
	@Autowired
	MenuItemRawMaterialServiceImpl menuItemRawMaterialServiceImpl;
	

	@GetMapping("/get")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> geDishCosting(@RequestParam("eventId") Long eventId, @RequestParam("eventFunctionId") Long eventFunctionId){
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isRawMaterialDone = menuItemRawMaterialServiceImpl.checkEventIdAndEventFunctionId(eventId,
					eventFunctionId);
			Boolean isMenuAllocationDone = menuItemRawMaterialServiceImpl.checkEventIdAndEventFunctionId2(eventId,
					eventFunctionId);
			
			EventDishCostingResponseDto responseDto = eventDishCostingService.geDishCosting(eventId, eventFunctionId,isRawMaterialDone,isMenuAllocationDone);
			if(responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", ConstantsPoc.EVENT_LABOR_FOUND_SUCCESS);
				response.put("success", true);
			}else {
				response.put("msg", ConstantsPoc.EVENT_LABOR_FOUND_FAIL);
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
	
	@GetMapping("/raw-material-category-wise")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getRawMaterialTotalCategoryWise(@RequestParam("eventId") Long eventId, @RequestParam("eventFunctionId") Long eventFunctionId){
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isRawMaterialDone = menuItemRawMaterialServiceImpl.checkEventIdAndEventFunctionId(eventId,
					eventFunctionId);
			Boolean isMenuAllocationDone = menuItemRawMaterialServiceImpl.checkEventIdAndEventFunctionId2(eventId,
					eventFunctionId);
			
			List<RawMaterialCategoryRateResponseDto> responseDto = eventDishCostingService.getRawMaterialTotalCategoryWise(eventId, eventFunctionId,isRawMaterialDone,isMenuAllocationDone);
			if(responseDto != null) {
				Map<String, Object> rawMatRes = new HashMap<>();
				rawMatRes.put("RawMaterialData", responseDto);
				response.put("data", rawMatRes);
				response.put("msg", "Event Raw Material Total Category Wise data found");
				response.put("success", true);
			}else {
				response.put("msg", "Event Raw Material Total Category Wise data found failed");
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
