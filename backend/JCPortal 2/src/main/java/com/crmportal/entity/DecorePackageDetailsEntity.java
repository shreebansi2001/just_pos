package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "decore_package_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecorePackageDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "decore_package_details_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "decore_package_id", nullable = false)
	private DecorePackageEntity decorePackage;

	@ManyToOne
	@JoinColumn(name = "decore_main_category_id", nullable = false)
	private DecoreMainCategoryMasterEntity decoreMainCategory;

	@ManyToOne
	@JoinColumn(name = "decore_item_id", nullable = true)
	private DecoreMainCategoryItemMasterEntity decoreItem;

	@Column(name = "menu_sortorder")
	private Integer menuSortOrder;

	@Column(name = "item_sortorder")
	private Integer itemSortOrder;

	@Column(name = "price", precision = 10, scale = 2)
	private BigDecimal price = BigDecimal.ZERO;

	@Column(name = "sequence")
	private Integer sequence;

	@Column(name = "is_active")
	private Boolean isActive = true;

	@Column(name = "is_delete")
	private Boolean isDelete = false;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserMasterEntity user;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}