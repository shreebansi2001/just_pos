package com.crmportal.response.dto;

import java.util.List;

import org.apache.catalina.startup.UserDatabase;

import com.crmportal.request.dto.FileWithIdRequestDto;
import com.crmportal.request.dto.UserOfferRequestDto;
import com.crmportal.request.dto.UserOfferResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAdminResponseDto {

	private Long id;
	private Long clientId;
	private String cityName;
	private Long stateId;
	private String stateName;
	
	private String contactNo;
	private String createdAt;
	private String email;
	private String firstName;
	private String lastName;
	private Boolean isActive;
	private Boolean isApprove;
	private String userCode;
	
	private String companyName;
	private String countryCode;
	private String companyEmail;
	private String officeNo;
	private String address;
	private Long reportingManagerId;
	private String reportingManagerName;
	private String memberType;
	private String profile;
	private Long salesId;
	private String salesName;
	private Long managerId;
	private String managerName;
	private String dateOfBirth;
	private String salesReq;
	private String managerReq;
	private String overAllRemarks;
	private String type;
	private String services;
	private String lang;
	private DataBaseResponse database;
	private List<UserAssignedAllModuleWiseThemeResponseDto> userThemes;
	private List<FileWithIdResponseDto> files;
	private UserPlansHistoryResponseDto userPlan;
	private List<UserDocumentResponseDto> userDocument;
	private List<UserDownPaymentResponseDto> downPayment;
	private List<UserAmcResponseDto> userAmc;
	private List<RefundDetailsResponseDto> refundDetails;
	private List<UserOfferResponseDto> userOffers;
	private Boolean isBlock;
	private String softType;
	private String uniqueCode;
	private Integer followUpDay;
}
