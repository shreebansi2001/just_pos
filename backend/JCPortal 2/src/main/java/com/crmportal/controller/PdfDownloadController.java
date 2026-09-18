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
@RequestMapping("/api/download/pdf")
@CrossOrigin(
		   origins = {"*"},
		   maxAge = 3600L
		)
public class PdfDownloadController {

    @GetMapping({"/{eventNo}/{fileName}", "/{fileName}"})
    public ResponseEntity<Resource> downloadPdf(
            @PathVariable(value = "eventNo", required = false) String eventNo,
            @PathVariable(value = "fileName") String fileName,
            HttpServletRequest request) throws IOException {

        String rootPath = request.getSession().getServletContext().getRealPath("/");
        File file;
        if(eventNo != null && !eventNo.isEmpty()) {
            file = new File(rootPath + "resources/tempDownload/" + eventNo + "/" + fileName);
        } else {        	
        	file = new File(rootPath + "resources/tempDownload/eventReports/" + fileName);
        }

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName)
                .body(resource);
    }
}