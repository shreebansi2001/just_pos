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
@Table(name = "event_generalfix")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventGeneralFixEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_generalfix_id")
	private Long id;

	@Column(name = "event_id")
	private Long eventId;

	@Column(name = "raw_cat_id")
	private Long rawCatId;

	@Column(name = "raw_id")
	private Long rawId;

	@Column(name = "unit_id")
	private Long unitId;

	@Column(name = "weight")
	private BigDecimal weight = BigDecimal.ZERO;

	@Column(name = "price")
	private BigDecimal price = BigDecimal.ZERO;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;
	
	@Column(name = "source_weight_per100_pax")
	private BigDecimal sourceWeightPer100Pax = BigDecimal.ZERO;
	
	@Column(name = "source_supplier_rate")
	private BigDecimal sourceSupplierRate = BigDecimal.ZERO;
	
	@Column(name = "source_unit_id")
	private Long sourceUnitId;
	
}
