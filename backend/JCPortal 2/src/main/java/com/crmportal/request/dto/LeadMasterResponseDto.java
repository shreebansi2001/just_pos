package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;

import org.hibernate.annotations.CreationTimestamp;

import com.crmportal.response.dto.FollowUpDetailsResponseDto;
import com.crmportal.response.dto.LeadSourceResponseDto;
import com.crmportal.response.dto.LeadStatusResponseDto;
import com.crmportal.response.dto.LeadSubSourceResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadMasterResponseDto {

	private Long id;
	
	private String leadCode;
	
	private String companyName;
	
	private Long planId;
	
	private String planName;
	
	private String clientName;
	
	private String emailId;
	
	private String contactNumber;
	
	private String alternateNumber;
	
	private Long cityId;
	
	private String cityName;
	
	private Long stateId;

	private String stateName;
	
	private String leadRemark;
	
	private LeadStatusResponseDto leadStatus;
	
	private String leadType;
	
	private String leadPriority;
	
	private String leadQuality;
	
	private LeadSourceResponseDto leadSource;
	
	private String medium;
	
	private String campaign;
	
	private Long leadAssignId;
	
	private String leadAssignName;
	
	private String dealValue;
	
	private String closeDate;
	
	private Long eventTypeId;
	private String eventTypeName;
	
	private String inquiryDate;
	
	private Integer minPax;

	private Integer maxPax;

	private Boolean anyFunctionWithUs;
	
	private Long userId;
	
	private List<String> tentEventDate;
	
	private List<FollowUpDetailsResponseDto> followUpDetails;
	
	private Boolean isDelete;

	private String createdAt;
	
	private Long leadStatusId;
	
	private String leadStatusName;
	
	private Long leadSourceId;
	
	private String leadSourceName;
	
	private String person;
	
	private String followUpDate;
	private String followUpMode;
	private String discussion;
	private String coordinatorName;
	/*
	 * 
	 * 
	 * private String leadTitle;
	 * 
	 * 
	 * 
	 * private LeadSubSourceResponseDto leadSubSource;
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * private Long pipelineId;
	 * 
	 * private String pipelineName;
	 * 
	 * private Long openStageId;
	 * 
	 * private String openStageName;
	 * 
	 * private Long closeStageId;
	 * 
	 * private String closeStageName;
	 * 
	 * private String selectPrefix;
	 * 
	 * 
	 * 
	 * private String pinCode;
	 * 
	 * private String overallRemark;
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * private Long functionId; private String functionName;
	 * 
	 * 
	 * 
	 * private String referralSource;
	 * 
	 * 
	 * 
	 * private BigDecimal estimateAmount;
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
}
