package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.FontMasterRequestDto;
import com.crmportal.response.dto.FontMasterResponseDto;
import com.crmportal.service.FontMasterService;
import com.crmportal.utility.ConstantsPoc;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping({ "/v1/api/font-master" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class FontsMasterController {

	@Autowired
	FontMasterService fontMasterService;
	
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	@PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> addFontMaster(@Valid @ModelAttribute FontMasterRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			
			FontMasterResponseDto res = fontMasterService.addOrUpdateFontMaster(request, Long.valueOf(-1)); 
			
			if(res == null) {
				response.put("msg", ConstantsPoc.FONT_ADDED_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.FONT_ADDED_SUCCESS);
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
	
	@PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> addFontMaster(@Valid @ModelAttribute FontMasterRequestDto request, @RequestParam("fontId") Long fontId) {
		Map<String, Object> response = new HashMap<>();
		try {
			
			FontMasterResponseDto res = fontMasterService.addOrUpdateFontMaster(request, fontId); 
			
			if(res == null) {
				response.put("msg", ConstantsPoc.FONT_UPDATE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.FONT_UPDATE_SUCCESS);
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
	
	@GetMapping("/getallactivefonts")
	public ResponseEntity<?> getAllActiveFonts() {
		Map<String, Object> response = new HashMap<>();
		try {
			
			List<FontMasterResponseDto> res = fontMasterService.getAllActiveFonts(); 
			
			if(res == null) {
				response.put("msg", ConstantsPoc.FONT_FOUND_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.FONT_FOUND_SUCCESS);
				response.put("success", true);
				response.put("data", res);
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
	
	@GetMapping("/getallfonts")
	public ResponseEntity<?> getAllFonts() {
		Map<String, Object> response = new HashMap<>();
		try {
			
			List<FontMasterResponseDto> res = fontMasterService.getAllFonts(); 
			
			if(res == null) {
				response.put("msg", ConstantsPoc.FONT_FOUND_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.FONT_FOUND_SUCCESS);
				response.put("success", true);
				response.put("data", res);
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
	public ResponseEntity<?> getFontById(@RequestParam("fontId") Long fontId) {
		Map<String, Object> response = new HashMap<>();
		try {
			
			FontMasterResponseDto res = fontMasterService.getFontById(fontId); 
			
			if(res == null) {
				response.put("msg", ConstantsPoc.FONT_FOUND_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.FONT_FOUND_SUCCESS);
				response.put("success", true);
				response.put("data", res);
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
	public ResponseEntity<?> updateFontStatusById(@RequestParam("fontId") Long fontId, @RequestParam("status") Boolean status) {
		Map<String, Object> response = new HashMap<>();
		try {
			
			Boolean res = fontMasterService.updateFontStatus(fontId, status); 
			
			if(res) {
				response.put("msg", ConstantsPoc.FONT_UPDATE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.FONT_UPDATE_SUCCESS);
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
	public ResponseEntity<?> deleteFontById(@RequestParam("fontId") Long fontId) {
		Map<String, Object> response = new HashMap<>();
		try {
			
			Boolean res = fontMasterService.deleteFontById(fontId); 
			
			if(!res) {
				response.put("msg", ConstantsPoc.FONT_DELETE_FAIL);
				response.put("success", false);
			}else {
				response.put("msg", ConstantsPoc.FONT_DELETE_SUCCESS);
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
