package com.crmportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.ConfigurationUtilDto;
import com.crmportal.service.UserConfigurationService;

import java.util.Map;

import javax.validation.Valid;

@RestController
@RequestMapping({ "/v1/api/user-config" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class UtilityController {
	
	@Autowired
	private UserConfigurationService userConfigurationService;

    @PostMapping(value = "/saveUserConfig")
    public ResponseEntity<Map<String, Object>> saveUserConfig(@Valid @ModelAttribute ConfigurationUtilDto dto) {
    	return ResponseEntity.ok(userConfigurationService.saveUserConfig(dto));
    }
    
	@GetMapping("/health")
	@ResponseBody
	public ResponseEntity<String> healthCheck() {
		return new ResponseEntity<>("UtilityController Server is running ", HttpStatus.OK);
	}
	
	@GetMapping("/getUserConfig/{user_id}")
	public ResponseEntity<Map<String, Object>> getUserConfig(@PathVariable("user_id") Long userId) {
		return ResponseEntity.ok(userConfigurationService.getUserConfig(userId));
	}
}