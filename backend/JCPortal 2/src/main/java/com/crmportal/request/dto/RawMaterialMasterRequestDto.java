package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RawMaterialMasterRequestDto {
	
	private String nameEnglish;
	private String nameHindi;
	private String nameGujarati;
	private Long unitId;
	private BigDecimal supplierRate;
	private Boolean isGeneralFix;
	private BigDecimal weightPer100Pax;
	private Integer sequence = 1;
	private Long userId;
	private Long rawMaterialCatId;
	private MultipartFile file;
	private BigDecimal opbStock;
	private BigDecimal minStock;
	private BigDecimal maxStock = BigDecimal.ZERO;
	private BigDecimal leadTime  = BigDecimal.ZERO;
	private String expiryDate;
	private Boolean isApplyCal;
	private String dailyConsumption;
	private BigDecimal cgst;
	private BigDecimal sgst;
	private BigDecimal cess;
	private BigDecimal igst;
	private List<RawMaterialSupplierRequestDto> rawMaterialSupplierRequestDtos;
	
}
