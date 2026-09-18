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

@Entity
@Table(name = "lead_status_master")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadStatusEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lead_status_id")
	private Long leadStatusId;
	
	@Column(name = "status_name")
	private String statusName;
	
	@Column(name = "color_code")
	private String colorCode;
	
	@Column(name = "is_active")
	private Boolean isActive = true;
	
	@Column(name = "is_deleted")
	private Boolean isDeleted = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_status_type_id")
    private LeadStatusTypeEntity leadStatus;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_Id")
    private UserMasterEntity user;
}
