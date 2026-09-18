package com.crmportal.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.AssignDbRequest;
import com.crmportal.request.dto.DBExcelRequestDto;
import com.crmportal.service.DatabaseExcelParserService;
import com.crmportal.service.DatabasePlanningService;

@RestController
@RequestMapping({ "/v1/api/excel-parsing" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class DatabaseExcelParserController {

	@Autowired
	private DatabaseExcelParserService databaseExcelParserService;
	@Autowired
	private DatabasePlanningService databasePlanningService;

	@GetMapping("/health")
	@ResponseBody
	public ResponseEntity<String> healthCheck() {
		return new ResponseEntity<>("Server is running ", HttpStatus.OK);
	}

	@GetMapping("/getById")
	public ResponseEntity<Map<String, Object>> getById(@RequestParam(name = "db_planning_id") String dbPlanningId) {
		return ResponseEntity.ok(databasePlanningService.getByDbPlanningIdAndDbName(dbPlanningId));
	}
	
	@GetMapping("/getUserById")
	public ResponseEntity<Map<String, Object>> getUserById(@RequestParam(name = "parent_db_id") String parentDbId) {
		return ResponseEntity.ok(databasePlanningService.getByDbPlanningId(parentDbId));
	}

	@GetMapping("/getAll")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllDb() {
		return ResponseEntity.ok(databasePlanningService.getAllDatabasePlanningEntities());
	}

	@PostMapping(value = "/readExcel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> generatePdf(
			@RequestPart("json") @Valid DBExcelRequestDto dbExcelRequestDto, @RequestPart("file") MultipartFile file)
			throws IOException {
		System.err.println("inside readExcel endpoint");
		return ResponseEntity.ok(databaseExcelParserService.parseExcelToDb(file.getInputStream(), dbExcelRequestDto));
	}

	@PostMapping("/assignDb")
	public ResponseEntity<Map<String, Object>> assignDatabase(@Valid @RequestBody AssignDbRequest request) {
		return ResponseEntity.ok(databasePlanningService.assignDbToUser(request));
	}
}