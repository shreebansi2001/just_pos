package com.crmportal.request.dto;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import com.crmportal.entity.LeadMasterEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowUpDetailsRequestDto {

	private Long id;
	
	private String followUpDate; 
	
	private String followUpType;
	
	private String followUpStatus;
	
	private String clientRemarks;
	
	private String employeeRemarks;
	
	private Long leadId;
	
	private Long memberId;
	
}
