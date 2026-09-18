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
@Table(name = "eventfunction_menuallocation_order")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuAllocationOrdersEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menuallocation_order_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = true)
    private PartyMasterEntity party;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_allocation_id", nullable = false)
	private EventFunctionMenuAllocationEntity menuAllocation;

	// Outside Order fields
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;
    
    @Column(name = "quantity", precision = 10, scale = 2)
    private BigDecimal quantity  = BigDecimal.ZERO;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = true)
    private UnitMasterEntity unit;
    
    @Column(name = "number")
    private String number;
    
    @Column(name = "remarks")
    private String remarks;
    
    @Column(name = "pax")
    private Integer pax;
    
    // Agency Order fields
    @Column(name = "service_type", length = 100)
    private String serviceType;
    
    @Column(name = "counter_quantity")
    private Integer counterQuantity;
    
    @Column(name = "helper_quantity")
    private Integer helperQuantity;
    
    @Column(name = "counter_price", precision = 10, scale = 2)
    private BigDecimal counterPrice;
    
    @Column(name = "helper_price", precision = 10, scale = 2)
    private BigDecimal helperPrice;

    @Column(name = "shift_trans_price", precision = 10, scale = 2)
    private BigDecimal shiftTransPrice;
    
    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;
    
    @Column(name = "is_outside")
    private Boolean isOutside;
    
    @Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "isActive", nullable = false)
	private Boolean isActive = true;
	
	@Column(name = "reporting_time",nullable =  true)
	private String reportingTime;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}
