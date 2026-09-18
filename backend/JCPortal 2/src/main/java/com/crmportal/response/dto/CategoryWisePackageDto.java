package com.crmportal.response.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryWisePackageDto {

	private Long userId;
	
	private List<CategoryWiseTypeCatResponseDto> basicPackage;
	
	private List<CategoryWiseTypeCatResponseDto> premiumPackage;
}
