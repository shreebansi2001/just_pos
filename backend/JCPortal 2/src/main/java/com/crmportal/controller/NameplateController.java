package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.NameplateRequestDto;
import com.crmportal.request.dto.EventFunctionNameplateRequestDto;
import com.crmportal.request.dto.NamePlateReportRequestDto;
import com.crmportal.request.dto.ReportMenuPlanningRequestDTO;
import com.crmportal.response.dto.EventFunctionNameplateResponseDto;
import com.crmportal.service.NameplateService;
import com.crmportal.service.ReportService;

@RestController
@RequestMapping({ "/v1/api/nameplate" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class NameplateController {

	@Autowired
	private NameplateService nameplateService;
	
	@PostMapping("/addorupdate")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addNameplateItems(@RequestBody EventFunctionNameplateRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			Map<String, Object> responseDto = nameplateService.addNameplateItems(request.getNamePlateRequests(), request.getItemFontSize(), 
					request.getCategoryFontSize(), request.getEventId(), request.getEventFunctionId(), 
					request.getUserId(), request.getIsCounterItem(), request.getIsStandyItem(), request.getIsTableMenuItem(),
					request.getHeaderNotesEnglish(), request.getHeaderNotesHindi(), request.getHeaderNotesGujarati(),
					request.getFooterNotesEnglish(), request.getFooterNotesHindi(), request.getFooterNotesGujarati());
			
			if(responseDto != null && !responseDto.isEmpty()) {
				response.put("data", responseDto);
				response.put("msg", "Item updated.");
				response.put("success", true);
			} else {
				response.put("msg", "item does not updated.");
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(RuntimeException e) { 
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/get")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getNameplateItems(@RequestParam Long userId, 
			@RequestParam Long eventId, 
			@RequestParam Long eventFunctionId, 
			@RequestParam Integer lang) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			Map<String, Object> responseDto = nameplateService.getNameplateItems(userId, eventId, 
					eventFunctionId, lang);
			
			if(responseDto != null && !responseDto.isEmpty()) {
				response.put("data", responseDto);
				response.put("msg", "Item found.");
				response.put("success", true);
			} else {
				response.put("msg", "item does not found.");
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(RuntimeException e) { 
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getbynameplatetype")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getNameplateItemsWithNamePlateType(@RequestParam Long userId, 
			@RequestParam Long eventId, 
			@RequestParam Long eventFunctionId, 
			@RequestParam Integer lang, 
			@RequestParam Integer isCounterItem, 
			@RequestParam Integer isStandyItem, 
			@RequestParam Integer isTableMenuItem) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			Map<String, Object> responseDto = nameplateService.getNameplateItemsWithNamePlateType(userId, eventId, 
					eventFunctionId, lang, isCounterItem, isStandyItem, isTableMenuItem);
			
			if(responseDto != null && !responseDto.isEmpty()) {
				response.put("data", responseDto);
				response.put("msg", "Item found.");
				response.put("success", true);
			} else {
				response.put("msg", "item does not found.");
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(RuntimeException e) { 
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
