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
@Table(name = "captain_receipe_raw_material")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptainReceipeRawMaterialItemEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "raw_item_id")
	private Long rawItemId;
	
	@Column(name = "captain_receipe_id")
	private Long captainReceipeId;

	@Column(name = "rate")
	private BigDecimal rate;
	
	@Column(name = "qty")
	private BigDecimal qty;
	
	@Column(name = "unit_id")
	private Long unitId;
	
	@Column(name = "unit_name")
	private String unitName;
	
	@Column(name = "is_delete")
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}
