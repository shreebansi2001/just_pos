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
public class UpdateUserMemberRequestDto {

	private String preFix;
	private String firstName;
	private String lastName;
	private String address;
	private Long cityId;
	private String contactNo;
	private Long reportingManagerId;
	private Long planId;
	private String planAmount;
	private String planBaseAmount;
	private String memberType;
	private String profile;
	private Long salesId;
	private Long managerId;
	private String dateOfBirth;
	private String salesReq;
	private String managerReq;
	private String overAllRemarks;
	private Long coupenId;
	private Long extraPayId;
	private String cgst;
	private String sgst;
	private BigDecimal cgstAmt;
	private BigDecimal sgstAmt;
	private BigDecimal finalTotal;
	private List<FileWithIdRequestDto> files;
	private List<UserDocumentRequestDto> userDocuments;
	private List<UserDownPaymentRequestDto> userDownPayments;
	private List<UserAmcRequestDto> userAmcs;
	private List<RefundDetailsRequestDto> refundDetails;
	private List<UserOfferRequestDto> userOffer;
	private String type;
	private String services;
	private String lang;
	private String themeColor;
	private String softType;
	private Integer followupDay;
}
