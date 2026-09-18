package com.crmportal.request.dto;

import java.math.BigDecimal;

import com.crmportal.entity.RawMaterialCategoryMasterEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrockeryCutleryRequestDto {
	
	private Long id;
	
	private Long rawMaterialCategoryId;
	
	private Long rawMaterialId;

	private String rawMaterialNameEnglish;

	private String rawMaterialNameHindi;

	private String rawMaterialNameGujarati;
	
	private Long userId;
	
	private BigDecimal r_0_to_100;

	private BigDecimal r_101_to_200;

	private BigDecimal r_201_to_300;
	
	private BigDecimal r_301_to_400;

	private BigDecimal r_401_to_500;

	private BigDecimal r_501_to_600;

	private BigDecimal r_601_to_700;

	private BigDecimal r_701_to_800;

	private BigDecimal r_801_to_900;

	private BigDecimal r_901_to_1000;

	private BigDecimal r_1001_to_1100;

	private BigDecimal r_1101_to_1200;

	private BigDecimal r_1201_to_1300;

	private BigDecimal r_1301_to_1400;

	private BigDecimal r_1401_to_1500;

	private BigDecimal r_1501_to_1600;

	private BigDecimal r_1601_to_1700;

	private BigDecimal r_1701_to_1800;

	private BigDecimal r_1801_to_1900;

	private BigDecimal r_1901_to_2000;

}
