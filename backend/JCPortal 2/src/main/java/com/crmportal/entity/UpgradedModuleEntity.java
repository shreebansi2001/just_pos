package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "upgrade_module")
@AllArgsConstructor
@NoArgsConstructor
public class UpgradedModuleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "upgrade_module_id")
	private Long id;
	
	@Column(name = "price")
	private BigDecimal price = BigDecimal.ZERO;
	
	@Column(name = "module_name")
	private String moduleName;
	
	@Column(name = "billing_cycle")
	private String billingCycle;
	
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;
	
	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;

	@Column(name = "is_config", nullable = false)
	private Boolean isConfig = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;
	
}
