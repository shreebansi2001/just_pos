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

import lombok.Data;

@Entity
@Table(name = "event_labor_helper")
@Data
public class EventLaborHelperEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_labor_helper_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "party_id")
	private PartyMasterEntity contact;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "labor_helper_id")
	private LaborHelperEntity laborhelper;
	
	@Column(name = "event_id")
	private Long event_id;
	
	@Column(name = "event_function_id")
	private Long event_function_id;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
}
