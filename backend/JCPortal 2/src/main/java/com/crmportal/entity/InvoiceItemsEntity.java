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
@Table(name = "invoice_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_item_id")
    private Long invoiceItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private InvoiceEntity invoice;

    @Column(name = "plan_history_id")
    private Long planHistoryId;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "qty")
    private BigDecimal qty;

    @Column(name = "rate")
    private BigDecimal rate;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "description")
    private String description;
    
    @Column(name = "hsn_code")
    private String hsnCode;
    
    @Column(name = "tax_percent")
    private BigDecimal taxPercent;
    
    @Column(name = "tax_amount")
    private BigDecimal taxAmount;
    
    @CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", nullable = false)
	private LocalDateTime createdAt;
    
    @Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
    
    @Column(name = "is_delete")
    private Boolean isDelete = false;
	
}