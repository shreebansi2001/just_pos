package com.crmportal.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "FollowUpDetailsMaster")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowUpDetailsEntity {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "follow_up_id")
	private Long id;
	
	@Column(name = "follow_up_date",columnDefinition = "DATETIME")
	private LocalDateTime followUpDate; 
	
	@Column(name = "follow_up_type")
	private String followUpType;
	
	@Column(name = "follow_up_status")
	private String followUpStatus;
	
	@Column(name = "client_remarks")
	private String clientRemarks;
	
	@Column(name = "employee_remarks")
	private String employeeRemarks;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lead_id")
	private LeadMasterEntity lead;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_assign_id")
	private UserMasterEntity followUpMember;
}
