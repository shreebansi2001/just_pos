package com.crmportal.entity;

import java.math.BigDecimal;
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
@Table(name = "captain_receipe_master")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptainReceipeMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "weight")
	private BigDecimal weight;
	
	@Column(name = "rate")
	private BigDecimal rate;
	
	@Column(name = "unit_id")
	private Long unitId;
	
	@Column(name = "is_active")
	private Boolean isActive;
	
	@Column(name = "is_delete")
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
}
