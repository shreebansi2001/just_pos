package com.crmportal.response.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.Data;

@Data
public class EventRawMaterialResponse {

	Long id = 0l;
	Long eventId;
	Long supplierId;
	String supplierName;
	Long rawMaterialId;
	String rawMaterialNameEng;
	String rawMaterialNameGuj;
	String rawMaterialNameHin;
	Long unitId;
	Double qty;
	Double finalQty;
	String place;
	Double totalprice = 0.0;
	String extraItemName;
	UnitHierarchyDto unitHierarchyDto;
	UnitMasterResponseDto units;
	Long rawMaterialCatId;
	Boolean isApplyCal;
	String date;
	String remarksEnglish;
	String remarksHindi;
	String remarksGujarati;

	public EventRawMaterialResponse(Long id, Long eventId, Long supplierId, String supplierName, Long rawMaterialId,
			String rawMaterialNameEng, String rawMaterialNameGuj, String rawMaterialNameHin, Long unitId, Double qty,
			Double finalQty, String place, Double totalprice,Long rawMaterialCatId,Boolean isApplyCal,LocalDateTime date,String remarksEnglish,String remarksHindi,String remarksGujarati) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.supplierId = supplierId;
		this.supplierName = supplierName;
		this.rawMaterialId = rawMaterialId;
		this.rawMaterialNameEng = rawMaterialNameEng;
		this.rawMaterialNameGuj = rawMaterialNameGuj;
		this.rawMaterialNameHin = rawMaterialNameHin;
		this.unitId = unitId;
		this.qty = qty;
		this.finalQty = finalQty;
		this.place = place;
		this.totalprice = totalprice;
		this.rawMaterialCatId = rawMaterialCatId;
		this.isApplyCal = isApplyCal;
		if (date != null) {
	        this.date = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	    }
		this.remarksEnglish = remarksEnglish;
		this.remarksHindi = remarksHindi;
		this.remarksGujarati = remarksGujarati;
	}

	public EventRawMaterialResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	List<EventRawMaterialFunctionsDto> eventRawMaterialFunctions;

}
