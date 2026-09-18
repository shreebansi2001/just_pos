package com.crmportal.response.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateModuleMasterResponseDto {

	Long id;
	String nameEnglish;
	String nameHindi;
	String nameGujarati;
	Boolean isDelete;
	String createdAt;
	Boolean isActive;
}
