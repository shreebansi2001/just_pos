package com.crmportal.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold.RangeType;
import org.hibernate.annotations.CreationTimestamp;

import com.crmportal.enums.ERangeType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "unit_ranges")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitRangeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "range_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "unit_id", nullable = false)
	private UnitMasterEntity unit;

	@Enumerated(EnumType.STRING)
    private ERangeType rangeType;

	@Column(name = "min_value")
    private Double minValue;
	
	@Column(name = "max_value")
    private Double maxValue;
	
	@Column(name = "round_off_value")
    private Double roundOffValue;
	
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
