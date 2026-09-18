package com.crmportal.request.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FontMasterRequestDto {

	@NotNull(message = "Font name is required.")
	private String fontName;
	
	@NotNull(message = "Font is required.")
//	@NotEmpty(message = "Font is required.")
	private MultipartFile font;
	
}
