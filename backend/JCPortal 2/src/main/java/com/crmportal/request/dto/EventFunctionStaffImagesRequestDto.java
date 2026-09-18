package com.crmportal.request.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionStaffImagesRequestDto {

	private Long uploadId;
	private Long assignmentId;
	private MultipartFile file;
}
