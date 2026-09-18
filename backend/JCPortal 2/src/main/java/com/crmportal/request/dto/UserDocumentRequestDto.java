package com.crmportal.request.dto;

import javax.persistence.Column;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDocumentRequestDto {

	private Long id;
	private String kycType;
	private String kycNo;
	private MultipartFile docPath;
	
}
