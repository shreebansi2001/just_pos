package com.crmportal.entity;

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
@Table(name = "event_extra_rawmaterial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtraEventRawMaterialEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_extra_rawmaterial_id")
	private Long id;
	
	@Column(name = "extra_rawmaterial")
	private String extraRawmaterial;
	
	@Column(name = "event_id")
	private Long eventId;
	
	@Column(name = "event_function_id")
	private Long eventFunctionId;
	
	@Column(name = "rawmaterial_cat_id")
	private Long rawmaterialCatId;
	
	@Column(name = "party_id")
	private Long partyId;
	
	@Column(name = "unit_id")
	private Long unitId;
	
	@Column(name = "qty")
	private Double qty=0.0;
	
	@Column(name = "finalqty")
	private Double finalqty=0.0;
	
	@Column(name = "place")
	private String place;
	
	@Column(name = "totalprice")
	private Double totalprice=0.0;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
	
	
}
