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
@Table(name = "extra_quotation_function")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultExtraQuotationFunctionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "extra_quotation_function_id")
	private Long id;
	
	@Column(name = "name",columnDefinition = "TEXT")
	private String name;
	
	@Column(name = "name_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameHindi;

	@Column(name = "name_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String nameGujarati;
	
	@Column(name = "is_active",nullable = false)
	private Boolean isActive = true;
	
	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "price",nullable = false)
	private BigDecimal price = BigDecimal.ZERO; 
	
	@Column(name = "user_id")
	private Long userId;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}
