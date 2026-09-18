package com.crmportal.request.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NamePlateImagesRequestDto {

	private Long templateMasterId;
	
	private List<MultipartFile> images;
	
}
