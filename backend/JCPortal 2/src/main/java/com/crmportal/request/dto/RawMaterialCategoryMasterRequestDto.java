package com.crmportal.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawMaterialCategoryMasterRequestDto {

	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private Integer sequence = 1;
	private Boolean isDirect;
	private Long userId;
	private Long rawMaterialCatTypeId;
}

