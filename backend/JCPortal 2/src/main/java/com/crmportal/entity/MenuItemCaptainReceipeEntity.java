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
@Table(name = "menu_item_captain_receipe")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemCaptainReceipeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menu_item_captain_receipe_id")
	private Long id;
	
	@Column(name = "weight")
	private BigDecimal weight;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unit_id")
	private UnitMasterEntity unit;
	
	@Column(name = "rate", precision = 10, scale = 2)
	private BigDecimal rate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "captain_receipe_id")
	private CaptainReceipeMasterEntity captainReceipe;
	
	@Column(name = "venue")
	private String venue;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_item_id", nullable = false)
	private MenuItemMasterEntity menuItem;
	
	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;

	@Column(name = "is_active", nullable = false)
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
}
