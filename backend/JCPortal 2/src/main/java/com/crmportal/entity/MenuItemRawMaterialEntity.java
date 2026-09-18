package com.crmportal.entity;

import java.math.BigDecimal;
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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu_item_raw_material")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemRawMaterialEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menu_item_raw_material_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "raw_material_id")
	private RawMaterialMasterEntity rawMaterial;

	@Column(name = "weight")
	private BigDecimal weight = BigDecimal.ZERO;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unit_id")
	private UnitMasterEntity unit;

	@Column(name = "vanue", columnDefinition = "TEXT")
	private String venue;

	@Column(name = "rate", precision = 10, scale = 2)
	private BigDecimal rate = BigDecimal.ZERO;

	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;

	@Column(name = "is_published", nullable = false)
	private Boolean isPublished = true;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_item_id", nullable = false)
	private MenuItemMasterEntity menuItem;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_Id")
	private UserMasterEntity user;

	@Column(name = "uuid", nullable = false)
	private String uuid;
	
	@Column(name = "is_visible")
	private Boolean isVisible;
}
