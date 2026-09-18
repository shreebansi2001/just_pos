package com.crmportal.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/download/docx")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class DocDownloadController {
	@GetMapping({ "/{eventNo}/{fileName}", "/{fileName}" })
	public ResponseEntity<Resource> downloadDocx(@PathVariable(required = false) String eventNo,
			@PathVariable String fileName, HttpServletRequest request) throws IOException {

		if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
			return ResponseEntity.badRequest().build();
		}

		String rootPath = request.getSession().getServletContext().getRealPath("/");

		File file;

		if (eventNo != null && !eventNo.isEmpty()) {
			file = new File(rootPath + "resources/tempDownload/" + eventNo + "/" + fileName);
		} else {
			file = new File(rootPath + "resources/tempDownload/eventReports/" + fileName);
		}

		if (!file.exists()) {
			return ResponseEntity.notFound().build();
		}

		Resource resource = new FileSystemResource(file);

		return ResponseEntity.ok()
				.contentType(MediaType
						.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
				.contentLength(file.length()).body(resource);
	}
}
