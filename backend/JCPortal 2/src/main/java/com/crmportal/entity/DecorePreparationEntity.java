package com.crmportal.entity;

import java.math.BigDecimal;
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

import lombok.Data;

@Entity
@Table(name = "decore_preparation")
@Data
public class DecorePreparationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "decore_preparation_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "event_function_id", nullable = false)
	private EventFunctionMasterEntity eventFunction;

	@Column(name = "pax")
	private Integer pax;

	@Column(name = "sortorder")
	private Integer sortorder;

	@Column(name = "is_package")
	private Boolean isPackage;

	@ManyToOne
	@JoinColumn(name = "package_id", nullable = true)
	private DecorePackageEntity decorePackage;

	@Column(name = "package_name")
	private String packageName;

	@Column(name = "package_price")
	private BigDecimal packagePrice;

	@Column(name = "price", precision = 10, scale = 2)
	private BigDecimal price = BigDecimal.ZERO;

	@Column(name = "default_price", precision = 10, scale = 2)
	private BigDecimal defaultPrice;

	@Column(name = "is_delete", nullable = false)
	private Boolean isDelete = false;

	@Column(name = "is_update", nullable = false)
	private Boolean isUpdate = false;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}