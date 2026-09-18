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
@Table(name = "menu_allocation_item_captain_receipe")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuAllocationItemCaptainReceipeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menuallocation_item_captain_receipe_id")
	private Long id;
	
	@Column(name = "weight")
	private BigDecimal weight = BigDecimal.ZERO;
	
	@Column(name = "rate")
	private BigDecimal rate = BigDecimal.ZERO;
	
	@Column(name = "capt_receipe_master_weight")
	private BigDecimal captainReceipeWeight = BigDecimal.ZERO;
	
	@Column(name = "capt_receipe_master_rate")
	private BigDecimal captainReceipeRate = BigDecimal.ZERO;
	
	@Column(name = "date_time",columnDefinition = "DATETIME")
	private LocalDateTime dateTime;
	
	@Column(name = "place")
	private String place;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = true)
    private PartyMasterEntity party;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = true)
    private UnitMasterEntity unit;
	
	@Column(name = "captain_receipe_raw_mat_unitid")
	private Long rawMaterialUnitId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "captain_receipe_id")
	private CaptainReceipeMasterEntity captainReceipe;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItemMasterEntity menuItem;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private EventMasterEntity event;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventfunction_id", nullable = false)
    private EventFunctionMasterEntity eventFunction;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "isActive", nullable = false)
	private Boolean isActive = true;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
}
