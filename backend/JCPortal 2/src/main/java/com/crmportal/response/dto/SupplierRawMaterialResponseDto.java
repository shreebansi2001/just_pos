package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierRawMaterialResponseDto {

	private Long supplireId;
	
	private String supplierName;
	
	private Long rawMaterialCatId;
	
	private String rawMaterialCatName;
	
	private Long rawMaterialId;
	
	private String rawMaterialName;
	
	private BigDecimal qty;
	
	private String unit;
	
	private String delieveryPlace;
	
	private String delieveryDateTime;
}
