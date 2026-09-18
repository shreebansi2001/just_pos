package com.crmportal.entity;

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

import lombok.Data;

@Entity
@Table(name = "event_dish_costing")
@Data
public class EventDishCostingEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dish_costing_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	private EventMasterEntity event;
	
	@ManyToOne
    @JoinColumn(name = "event_function_id", nullable = true)
    private EventFunctionMasterEntity eventFunction;
	
	@Column(name = "pax")
	private Integer pax;
	
	@Column(name = "cheflaborcharge")
	private Double cheflaborcharge=0.0;
	
	@Column(name = "laborcharge")
	private Double laborcharge=0.0;
	
	@Column(name = "outsideagencycharge")
	private Double outsideagencycharge=0.0;
	
	@Column(name = "extraexpensecharge")
	private Double extraexpensecharge=0.0;
	
	@Column(name = "rawmaterialcharge")
	private Double rawmaterialcharge=0.0;
	
//	@Column(name = "total")
//	private Double total=0.0;
//	
//	@Column(name = "totalbyperplate")
//	private Double totalbyperplate=0.0;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}
