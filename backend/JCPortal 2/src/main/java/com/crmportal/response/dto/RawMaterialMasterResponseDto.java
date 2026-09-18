package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import com.crmportal.entity.UnitMasterEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawMaterialMasterResponseDto {

	private Long id;
	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private UnitMasterResponseDto unit;
	private BigDecimal supplierRate;
	private Boolean isGeneralFix;
	private BigDecimal weightPer100Pax;
	private Integer sequence;
	private Boolean isActive;
	private BigDecimal closingStock;
	private String createdAt;
	private Long userId;
	private RawMaterialCategoryMasterResponseDto rawMaterialCat;
	private List<RawMaterialSupplierResponseDto> rawMaterialSuppliers;
	private UnitHierarchyDto unitHierarchy;
	private String file;
	private BigDecimal opbStock;
	private BigDecimal minStock;
	private BigDecimal maxStock;
	private BigDecimal leadTime;
	private String expiryDate;
	private Boolean isApplyCal;
	private String dailyConsumption;
	private BigDecimal cgst;
	private BigDecimal sgst;
	private BigDecimal cess;
	private BigDecimal igst;
}
