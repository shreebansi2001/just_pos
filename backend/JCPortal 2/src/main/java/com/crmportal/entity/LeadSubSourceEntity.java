package com.crmportal.entity;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
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
@Table(name = "lead_subsource")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadSubSourceEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lead_subsource_id")
	private Long leadSubSourceId;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "lead_source_id")
	private LeadSourceEntity leadSource;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "date_time", columnDefinition = "DATETIME")
	private LocalDateTime dateTime;
	
	@Column(name = "description")
	private String description;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@Column(name = "is_delete")
	private Boolean isDelete = false;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_Id")
    private UserMasterEntity user;
}
