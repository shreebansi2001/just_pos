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

@Data
@Entity
@Table(name = "menuitem_rawmaterial_rate_dishcosting")
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemRawMaterialRateDishCostingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menuitem_rawmaterial_rate_dishcosting_id")
	private Long id;
	
	@ManyToOne
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItemMasterEntity menuItem;
	
	@Column(name = "total_rate", precision = 10, scale = 2)
	private BigDecimal totalRate;
	
	@Column(name = "dish_costing", precision = 10, scale = 2)
	private BigDecimal dishCosting;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "isActive", nullable = false)
	private Boolean isActive = true;
	
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
	
	@Column(name = "isPublished", nullable = false)
	private Boolean isPublished = true;
}
