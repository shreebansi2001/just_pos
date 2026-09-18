package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FontMasterResponseDto {

	private Long fontId;
	
	private String fontName;
	
	private String fontPath;
	
	private String createdDate;
	
	private String updatedDate;
	
	private Boolean isActive;
	
	private Boolean isDelete;
	
}
