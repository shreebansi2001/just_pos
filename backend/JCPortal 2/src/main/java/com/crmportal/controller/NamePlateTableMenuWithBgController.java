package com.crmportal.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.NamePlateTableMenuRequestDto;
import com.crmportal.service.NamePlateTableMenuWithBgService;

@RestController
@RequestMapping({ "/v1/api/nameplate/tablemenuwithbg/" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class NamePlateTableMenuWithBgController {

	@Autowired
	NamePlateTableMenuWithBgService namePlateTableMenuWithBgService;
	
	@PostMapping("/addorupdate")
	public ResponseEntity<Map<String, Object>> addNameplateItems(@RequestBody NamePlateTableMenuRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			Map<String, Object> responseDto = namePlateTableMenuWithBgService.addOrUpdateNamePlate(request.getItems(), request.getEventId(), request.getEventFunctionId(),
					request.getUserId(), request.getCategoryFontSize(), request.getItemFontSize(),
					request.getHeaderNotesEnglish(), request.getHeaderNotesHindi(), request.getHeaderNotesGujarati(),
					request.getFooterNotesEnglish(), request.getFooterNotesHindi(), request.getFooterNotesGujarati());
			
			if(responseDto != null) {
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
	public ResponseEntity<Map<String, Object>> getNameplateItems(@RequestParam Long userId, 
			@RequestParam Long eventId, 
			@RequestParam Long eventFunctionId, 
			@RequestParam Integer lang) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			Map<String, Object> responseDto = namePlateTableMenuWithBgService.getNameplateItems(userId, eventId, eventFunctionId, lang);
			
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
