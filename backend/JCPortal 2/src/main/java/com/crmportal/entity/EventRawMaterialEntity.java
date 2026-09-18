package com.crmportal.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_raw_material")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRawMaterialEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_raw_material_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	private EventMasterEntity event;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "party_id")
	private PartyMasterEntity supplier;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "raw_material_id")
	private RawMaterialMasterEntity rawMaterial;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "raw_material_cat_id")
	private RawMaterialCategoryMasterEntity rawMaterialCat;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unit_id")
	private UnitMasterEntity unit;
	
	@Column(name = "qty")
	private Double qty=0.0;
	
	@Column(name = "finalqty")
	private Double finalqty=0.0;
	
	@Column(name = "place")
	private String place;
	
	@Column(name = "remarks_english",columnDefinition = "TEXT")
	private String remarksEnglish;
	
	@Column(name = "remarks_hindi",columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String remarksHindi;
	
	@Column(name = "remarks_gujarati",columnDefinition = "TEXT CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String remarksGujarati;
	
	@Column(name = "totalprice")
	private Double totalprice=0.0;
	
	@Column(name = "delievery_date_time", columnDefinition = "DATETIME")
	private LocalDateTime delieveryDateTime;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

}
