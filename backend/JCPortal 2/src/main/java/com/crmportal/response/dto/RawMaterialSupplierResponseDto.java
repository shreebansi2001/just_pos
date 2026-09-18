package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawMaterialSupplierResponseDto {

	private Long id;
	private Long rawMaterialId;
	private Long userId;
	private PartyMasterResponseDto party;
	private String createdAt;
	private Boolean isDefault;
	
}
