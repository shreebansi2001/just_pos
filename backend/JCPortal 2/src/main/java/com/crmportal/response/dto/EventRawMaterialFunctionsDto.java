package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventRawMaterialFunctionsDto {
	
	Long functionId;
	Long eventFunctionId;
	String functionName;
	Double qty;
	String itemName;
	Long supplierId;
	String supplierName;
	Long unitId;
	String unitName;
	String place;
	Double price=0.0;
	String functiondatetime;
	Boolean isExtraField;
	Double rawMaterialPrice;
	Long menuItemId = 0l;
	
	
	public EventRawMaterialFunctionsDto(Long functionId, Long eventFunctionId, String functionName, Double qty,
			String itemName, Long supplierId, String supplierName, Long unitId, String unitName, String place,
			Double price, String functiondatetime, Boolean isExtraField, Double rawMaterialPrice, Long menuItemId) {
		super();
		this.functionId = functionId;
		this.eventFunctionId = eventFunctionId;
		this.functionName = functionName;
		this.qty = qty;
		this.itemName = itemName;
		this.supplierId = supplierId;
		this.supplierName = supplierName;
		this.unitId = unitId;
		this.unitName = unitName;
		this.place = place;
		this.price = price;
		this.functiondatetime = functiondatetime;
		this.isExtraField = isExtraField;
		this.rawMaterialPrice = rawMaterialPrice;
		this.menuItemId = menuItemId;
	}
	UnitMasterResponseDto unit;
	UnitHierarchyDto unitHierarchyDto;
}
