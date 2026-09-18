package com.crmportal.response.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDocumentResponseDto {

	private Long id;
	private String kycType;
	private String kycNo;
	private String docPath;
}
