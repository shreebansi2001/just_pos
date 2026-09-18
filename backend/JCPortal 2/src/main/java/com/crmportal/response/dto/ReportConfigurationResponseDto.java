package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportConfigurationResponseDto {

	private Long id;

	private Integer isCompanyDetails;

	private Integer isCategorySlogan;

	private Integer isCategoryInstruction;

	private Long isCategoryImage;

	private Integer isItemSlogan;

	private Integer isItemInstruction;

	private Integer isItemImage;

	private Integer isPartyDetails;

	private Integer isWithQty;

	private Integer isAgency;

	private Integer isStatus;

	private Integer isRawMaterialCat;

	private String size1;

	private String size2;

	private String size3;

	private String type;

	private Long templateMappingId;

	private String mappingNameEnglish;

	private String mappingNameHindi;

	private String mappingNameGujarati;

	private Long templateModuleId;

	private String moduleNameEnglish;

	private String moduleNameHindi;

	private String moduleNameGujarati;

	private Integer isWithPrice;

	private Integer isItem;

	private Integer isItemPage;

	private Integer isItemColumn;

	private Integer isCombo;

	private Integer isQrCode;

	private Integer isExtraCharges;

	private Integer isTermsCond;

	private Integer isAdvancedPay;

	private Integer isDoc;

	private Integer isHalfPax;

	private Integer is3Column;

	private Integer isFunctionNextPage;

	private Integer isAddDecoration;

	private Integer isOnePage;

	private Boolean isDecore;

	private Integer isShowEventRemarks;

	private Integer showAdditional;

	private Integer isAgencyNextPage;

	private Integer storeIssueWise;

	private Integer isContactNoVisible;

	private Integer isSignatureVisible;

	private Integer isExcel;

	private Integer is5Column;

	private Integer isAddStoreIssue;
	
	private Integer isAddMenu;
	
	private Integer isAdvancePayment;
	
	private Integer isNotes;
	
	private Integer showLastPage;
	
	private Integer showAddOnLabel;
	
	private Integer withOutBg;
	
	private Integer withVendor;
	
	private Integer isAllItemTogether;
	
	private Integer isStartDate;
	
	private Integer isEndDate;
}
