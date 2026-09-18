package com.crmportal.request.dto;

import lombok.Data;

@Data
public class ReportConfigurationRequestDto {

	private Long id = 0L;

	private Integer isCategorySlogan = 0;

	private Integer isCategoryInstruction = 0;

	private Long isCategoryImage = 0L;

	private Integer isItemSlogan = 0;

	private Integer isItemInstruction = 0;

	private Integer isItemImage = 0;

	private Integer isCompanyLogo = 0;

	private Integer isCompanyDetails = 0;

	private Integer isPartyDetails = 0;

	private Integer isWithQty = 0;

	private Integer isAgency = 0;

	private Integer isStatus  = 0;

	private Integer isRawMaterialCat = 0;

	private String type = "";

	private Long templateMappingId = 0L;

	private Long templateModuleId = 0L;

	private String size1 = "";

	private String size2 = "";
	
	private String size3 = "";

	private Integer isWithPrice = 0;

	private Integer isItem = 0;

	private Integer isItemPage = 0;

	private Integer isItemColumn = 0;
	
	private Integer isCombo = 0;
	
	private Integer isQrCode = 0;
	
	private Integer isExtraCharges = 0;

	private Integer isTermsCond = 0;

	private Integer isAdvancedPay = 0;
	
	private Integer isDoc = 0;
	
	private Integer isHalfPax = 0;
	
	private Integer is3Column = 0;
	
	private Integer isFunctionNextPage = 0;
	
	private Integer isAddDecoration = 0;
	
	private Integer isOnePage = 0;
	
	private Boolean isDecore = Boolean.FALSE;
	
	private Integer isShowEventRemarks = 0;
	
	private Integer showAdditional = 0;
	
	private Integer isAgencyNextPage = 0;
	
	private Integer storeIssueWise = 0;
	
	private Integer isContactNoVisible = 0;
	
	private Integer isSignatureVisible = 0;
	
	private Integer isExcel = 0;
	private Integer is5Column = 0;
	private Integer isAddStoreIssue = 0;
	
	private Integer isAddMenu = 0;
	
	private Integer isAdvancePayment = 0;
	
	private Integer isNotes = 0;
	
	private Integer showLastPage = 0;
	
	private Integer showAddOnLabel = 0;

	private Integer withOutBg = 0;
	
	private Integer withVendor = 0;
	
	private Integer isAllItemTogether = 0;
	
	private Integer isStartDate = 0;
	
	private Integer isEndDate = 0;
}
