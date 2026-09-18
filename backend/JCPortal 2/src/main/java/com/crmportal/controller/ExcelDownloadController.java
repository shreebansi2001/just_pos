package com.crmportal.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/download/excel")
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class ExcelDownloadController {

	@GetMapping({ "/{folder}/{fileName}", "/{fileName}" })
	public ResponseEntity<Resource> downloadExcel(@PathVariable(value = "folder", required = false) String folder,
			@PathVariable(value = "fileName") String fileName, HttpServletRequest request) throws IOException {

		String rootPath = request.getSession().getServletContext().getRealPath("/");
		File file;

		if (folder != null && !folder.trim().isEmpty() && !"null".equalsIgnoreCase(folder)) {
			file = new File(rootPath + "resources/tempDownload/" + folder + "/" + fileName);
		} else {
			// fallback FIXED
			file = new File(rootPath + "resources/tempDownload/" + fileName);
		}

		if (!file.exists()) {
			return ResponseEntity.notFound().build();
		}

		Resource resource = new FileSystemResource(file);

		return ResponseEntity.ok()
				.contentType(
						MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName).body(resource);
	}
}