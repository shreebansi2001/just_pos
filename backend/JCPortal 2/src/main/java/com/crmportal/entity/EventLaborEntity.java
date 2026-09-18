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

import lombok.Data;

@Entity
@Table(name = "event_labor")
@Data
public class EventLaborEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "labor_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	private EventMasterEntity event;
	
	@ManyToOne
    @JoinColumn(name = "event_function_id", nullable = false)
    private EventFunctionMasterEntity eventFunction;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "party_id")
	private PartyMasterEntity contact;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contact_category_id")
	private ContactCategoryMasterEntity contactCategory;
	
	@Column(name = "laborshift")
	private String laborshift;
	
	@Column(name = "labordatetime",columnDefinition = "DATETIME")
	private LocalDateTime labordatetime;
	
	@Column(name = "shift_trans_price")
	private BigDecimal shiftTransPrice = BigDecimal.ZERO;
	
	@Column(name = "price")
	private Double price=0.0;
	
	@Column(name = "qty")
	private Double qty=0.0;
	
	@Column(name = "totalprice")
	private Double totalprice=0.0;
	
	@Column(name = "place")
	private String place;
	
	@Column(name = "sort_order",nullable = false)
	private Integer sortOrder = 0;
	
	@Column(name = "notes_english", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String notesEnglish;

	@Column(name = "notes_hindi", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String notesHindi;

	@Column(name = "notes_gujarati", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
	private String notesGujarati;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;
}
