package com.crmportal.response.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMasterResponseDto {

	private Long id;
	private Long clientId;
	private String contactNo;
	private String createdAt;
	private String email;
	private String firstName;
	private String lastName;
	private Boolean isActive;
	private Boolean isApprove;
	private String userCode;
	private Boolean isFirstTime;
	private String remarks;
	private String logo;
	private String lang;
	private PlansResponseDto plan;
	private UserBasicDetailsMasterResponseDto userBasicDetails;
	private UserPlansHistoryResponseDto userPlan;
	private List<UserRightsPageWithModuleResponseDto> userRights;
	private String token;
	private String tokenType = "Bearer";
	private Long expiresIn;
	private DataBaseResponse database;
	private Map<String, Long> leadStatus;
	private Map<String, Long> leadType;
	private Double efficiencyBreakdown;
	private Double serviceQualityRation;
	private Boolean isBlock;
	private List<UserUpgradedModuleResponseDto> userUpgradedModule;
	private RoleReportRightsResponseDto roleReportRights;
	private List<BanquetRightsResponseDto> banquetRights;
	private List<StockTypeRightsDTO> stockTypeRights;
	private Boolean ischilduser;
	private Boolean isVisible;
	private String themeColor;
	private String softType;
	private String uniqueCode;
	private String gstNumber;
	private String panNumber;
	private String fdaLincense;
	private String cinNumber;
	private String hsnNumber;
	private String fssaiNumber;
	private Boolean isInquiryVisible;
	private Integer followupDay;
}
