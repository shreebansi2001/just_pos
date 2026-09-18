package com.crmportal.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.response.dto.UserBasicFileResponseDto;
import com.crmportal.service.UserFileService;

@RestController
@RequestMapping("/v1/api/fileupload")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class FileUploadController {

	@Autowired
	UserFileService fileService;
	
	@PutMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> uploadFile(@Valid @ModelAttribute UserBasicFileResponseDto userBasicFile, @RequestParam("file") MultipartFile file){
		Map<String, Object> response = new HashMap<>();
		try {
			Map<String, Object> fileMap = fileService.storeFile(userBasicFile.getUserId(), userBasicFile.getModuleName(), userBasicFile.getModuleRecordId(), userBasicFile.getFileType(), file);
			response.put("success", fileMap.get("success"));
			response.put("msg", fileMap.get("msg"));
			response.put("fullPath", fileMap.get("fullPath"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK);
		
	}
}
