package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFunctionRawMaterialDto {
	private Long rawMaterialId;
	private String rawMaterialNameEnglish;
	private String rawMaterialNameHindi;
	private String rawMaterialNameGujarati;
}
