package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class EventRawMaterialQueryResponse {

	Long eventId;
	Long supplierId;
	String supplierName;
	Long rawMaterialId;
	String rawMaterialNameEng;
	String rawMaterialNameGuj;
	String rawMaterialNameHin;
	Long unitId;
	String unitName;
	Double qty;
	Double finalQty;
	String place;
	Long functionId;
	Long eventFunctionId;
	String functionName;
	String itemName;
	String functiondatetime;
	Double totalPrice;
	Double rawMaterialPrice;
	Long menuItemId = 0l;
	Long rawMaterialCatId;
	Boolean isApplyCal;
	String remarksEnglish;
	String remarksHindi;
	String remarksGujarati;

	public EventRawMaterialQueryResponse(Long eventId, Long supplierId, String supplierName, Long rawMaterialId,
			String rawMaterialNameEng, String rawMaterialNameGuj, String rawMaterialNameHin, Long unitId,
			String unitName, Double qty, Double finalQty, String place, Long functionId, Long eventFunctionId,
			String functionName, String itemName, String functiondatetime, Double totalPrice, Double rawMaterialPrice,
			Long menuItemId, Long rawMaterialCatId, Boolean isApplyCal, String remarksEnglish, String remarksHindi,
			String remarksGujarati) {
		super();
		this.eventId = eventId;
		this.supplierId = supplierId;
		this.supplierName = supplierName;
		this.rawMaterialId = rawMaterialId;
		this.rawMaterialNameEng = rawMaterialNameEng;
		this.rawMaterialNameGuj = rawMaterialNameGuj;
		this.rawMaterialNameHin = rawMaterialNameHin;
		this.unitId = unitId;
		this.unitName = unitName;
		this.qty = qty;
		this.finalQty = finalQty;
		this.place = place;
		this.functionId = functionId;
		this.eventFunctionId = eventFunctionId;
		this.functionName = functionName;
		this.itemName = itemName;
		this.functiondatetime = functiondatetime;
		this.totalPrice = totalPrice;
		this.rawMaterialPrice = rawMaterialPrice;
		this.menuItemId = menuItemId;
		this.rawMaterialCatId = rawMaterialCatId;
		this.isApplyCal = isApplyCal;
		this.remarksEnglish = remarksEnglish;
		this.remarksHindi = remarksHindi;
		this.remarksGujarati = remarksGujarati;
	}

}
