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
@Table(name = "purchase_request_details" )
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequestDetailsEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_request_id", nullable = false)
    private PurchaseRequestEntity purchaseRequest;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_id", nullable = false)
    private RawMaterialMasterEntity rawMaterial;
	
    @Column(name = "request_qty", precision = 10, scale = 2)
    private BigDecimal requestQty;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_qty_unit_id", nullable = false)
    private UnitMasterEntity requestQtyUnit;
    
    @Column(name = "approved_qty", precision = 10, scale = 2)
    private BigDecimal approvedQty;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_qty_unit_id")
    private UnitMasterEntity approvedQtyUnit;
    
    @Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
}
