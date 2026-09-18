package com.crmportal.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "countries")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "country_id")
	private Long id;

	@Column(name = "name", nullable = false, length = 100, unique = true)
	private String name;

	@Column(name = "code", nullable = false, length = 10, unique = true)
	private String code;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
//	
//	 @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<StateMasterEntity> states;
//	 
//	 @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<UserBasicDetailsMasterEntity> userDetails;
}
