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
@Table(name = "labor_helper")
@Data
public class LaborHelperEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "labor_hepler_id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "party_id")
	private PartyMasterEntity contact;
	
	@ManyToOne
	@JoinColumn(name = "contact_category_id")
	private ContactCategoryMasterEntity contactCategory;
	
	@Column(name = "name", length=100)
	private String name;
	
	@Column(name = "phonenumber", length=30)
	private String phonenumber;
	
	@Column(name = "aadharcard", length=30)
	private String aadharcard;
	
	@Column(name = "pancard", length=20)
	private String pancard;
	
	@Column(name = "aadharcarddocpathfront", length=300)
	private String aadharcarddocpathfront;
	
	@Column(name = "aadharcarddocpathback", length=300)
	private String aadharcarddocpathback;
	
	@Column(name = "pancarddocpath", length=300)
	private String pancarddocpath;
	
	@Column(name = "photo", length=300)
	private String photo;
	
	@Column(name = "drivinglicense", length=300)
	private String drivinglicense;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
}
