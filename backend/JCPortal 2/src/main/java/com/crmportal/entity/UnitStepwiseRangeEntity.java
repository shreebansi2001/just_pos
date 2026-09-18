package com.crmportal.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "unit_stepwise_ranges")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitStepwiseRangeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "step_id")
	private Long Id;

	@ManyToOne
	@JoinColumn(name = "unit_id", nullable = false)
	private UnitMasterEntity unit;

	@Column(name = "step_value")
	private Double stepValue;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;

	@Column(name = "isActive", nullable = false)
	private Boolean isActive = true;
	
	@Column(name = "isPublished", nullable = false)
	private Boolean isPublished = true;

	@Column(name = "uuid", nullable = false)
	private String uuid;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}
