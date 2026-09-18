package com.crmportal.request.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class DecoreCatItemImagesRequestDto {

	private MultipartFile imagePath;

}
