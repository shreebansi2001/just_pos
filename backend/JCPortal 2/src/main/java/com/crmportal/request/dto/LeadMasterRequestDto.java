package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadMasterRequestDto {

	private String leadCode;

	private String companyName;
	
	private Long planId;
	
	private String clientName;

	private String contactNumber;
	
	private String alternateNumber;

	private String emailId;

	private Long cityId;

	private Long stateId;

	private String leadRemark;
	
	@NotNull(message = "Lead status id is required.")
	private Long leadStatusId;
	
	private String leadType;
	
	private String leadPriority;
	
	private String leadQuality;
	
	@NotNull(message = "Lead source id is required.")
	private Long leadSourceId;
	
	private String medium;
	
	private String campaign;
	
	private Long leadAssignId;
	
	private String dealValue;
	
	private String closeDate;
	
	private Long eventTypeId;
	
	private String inquiryDate;
	
	private Integer minPax;

	private Integer maxPax;

	private Boolean anyFunctionWithUs;
	
	private Long userId;

	private List<String> tentEventDate;
	private List<FollowUpDetailsRequestDto> followUpDetails;

	//private String leadTitle;

	//private String leadStatus;

	

	//@NotNull(message = "Lead sub source id is required.")
	//private Long leadSubSourceId;

	

	

	

	/*
	 * private BigDecimal estimateAmount;
	 * 
	 * private String leadFollowUpDate;
	 * 
	 * private Long pipelineId;
	 * 
	 * private Long closeStageId;
	 * 
	 * private Long openStageId;
	 * 
	 * private String selectPrefix;
	 * 
	 * 
	 * private String address;
	 * 
	 * 
	 * 
	 * 
	 * 
	 * private String pinCode;
	 * 
	 * private String overallRemark;
	 * 
	 * 
	 * 
	 * private Long functionId;
	 * 
	 * 
	 * 
	 * private String referralSource;
	 */

	

	
}
