package com.crmportal.request.dto;

import java.util.List;

import lombok.Data;

@Data
public class EventRawMaterialFunctionRequestDto {
	Long eventId;
	Long supplierId=0l;
	Long rawMaterialId;
	Long unitId;
	Double qty=0.0;
	Double finalQty=0.0;
	String place;
	Double totalprice=0.0;
	String extraItem;
	Long rawMaterialCatId;
	Long eventFunctionId;
	List<EventRawMaterialFunctionsDetailRequest> eventRawMatFunctions;
}
