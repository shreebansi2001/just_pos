package com.crmportal.response.dto;

import com.crmportal.entity.UserMasterEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawMaterialCategoryMasterResponseDto {

	private Long id;
	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private Integer sequence;
	private Boolean isDirect;
	private Boolean isActive;
	private String createdAt;
	private Long userId;
	private RawMaterialCategoryTypeMasterResponseDto rawMaterialCatType;
	private Boolean isRawMaterialDataSaved;
}
