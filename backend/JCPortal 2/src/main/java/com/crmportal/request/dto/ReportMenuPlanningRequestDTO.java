package com.crmportal.request.dto;

import java.util.List;

import lombok.Data;

@Data
public class ReportMenuPlanningRequestDTO {
	private Integer isCategorySlogan = 0;
	private Integer isCategoryInstruction = 0;
	private Long isCategoryImage;
	private Integer isItemSlogan = 0;
	private Integer isItemInstruction = 0;
	private Integer isItemImage = 0;
	private Integer isCompanyLogo = 0;
	private Integer isCompanyDetails = 0;
	private Integer isPartyDetails = 0;
	private Integer lang = 0;
	private Integer isWithQty = 0;
	private Integer isWithPrice = 0;
	private String type;
	private String startDate;
	private String endDate;
	private List<Long> agencyId;
	private List<Long> itemId;
	private List<Long> eventFunctionIds;
	private List<Long> rawMaterialCatIds;
	private List<Integer> eventStatus;
	private Integer isCombo = 0;
	private Long partyId;
	private List<Long> managerIds;
	private Long catFontId;
	private Long itemFontId;
	private Long sloganFontId;
	private Integer catFontSize = 0;
	private Integer itemFontSize = 0;
	private Integer sloganFontSize = 0;
	private Integer isExtraCharges = 0;
	private Integer isTermsCond = 0;
	private Long customPackageId;
	private Integer isDoc = 0;
	private Integer isHalfPax = 0;
	private Integer is3Column = 0;
	private Long statusId;
	private Long sourceId;
	private String priority;
	private List<Long> leadAssignId;
	private Integer isFunctionNextPage = 0;
	private Long advancePaymentId;
	private Integer isAddDecoration = 0;
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
}
