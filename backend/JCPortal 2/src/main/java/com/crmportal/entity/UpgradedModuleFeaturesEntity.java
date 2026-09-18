package com.crmportal.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "upgradedmodule_features")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpgradedModuleFeaturesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "upgradedmodule_features_id")
	private Long id;
	
	@Column(name = "feature_text",columnDefinition = "TEXT")
	private String featureText;

	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@Column(name = "upgraded_module_id")
	private Long upgradedModuleId;
}
