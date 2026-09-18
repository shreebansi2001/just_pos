package com.crmportal.request.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class EventRawMaterialFunctionsDetailRequest {
	@NotBlank(message = ", is a mandatory field")
	Long functionId;
	@NotBlank(message = ", is a mandatory field")
	Long eventFunctionId;
	Double qty=0.0;
	String itemName;
	Long supplierId;
	Long unitId;
	String place;
	Double price=0.0;
	String functiondatetime;
	Boolean isExtraField;
	Double rawMaterialRate;
	Long menuItemId;
}
