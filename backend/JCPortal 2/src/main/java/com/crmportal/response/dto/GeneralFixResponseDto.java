package com.crmportal.response.dto;

import java.math.BigDecimal;

import com.crmportal.entity.UnitMasterEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralFixResponseDto {
	private Long eventId;
	private Long eventFunctionId;
	private Long menuPreparationId;
	private Long menuItemId;
	private Long rawMaterialId;
	private Long rawMaterialCatId;
	private String functionName;
	private String functionVenue;
	private String rawMaterialCatName;
	private Integer person;
	private String rawMaterialName;
	private BigDecimal weight;
	private Double finalQty;
	private Long unitId;
	private String unitName;
	private String eventNo;
	private UnitMasterEntity unit;
}
