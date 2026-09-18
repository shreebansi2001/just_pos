package com.crmportal.entity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "states")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StateMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "state_id")
	private Long id;
	
	@Column(name = "name",nullable = false)
	private String name;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

//	 @OneToMany(mappedBy = "state", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<CityMasterEntity> cities;
	 
	 @ManyToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "country_id", nullable = false)
	 private CountryMasterEntity country;
	 
//	 @OneToMany(mappedBy = "state", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<UserBasicDetailsMasterEntity> userDetails;
	
	 public StateMasterEntity(Long id) {
		super();
		this.id = id;
	}
	
	
	 
}
