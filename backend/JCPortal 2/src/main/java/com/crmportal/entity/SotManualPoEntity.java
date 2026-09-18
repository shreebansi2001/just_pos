package com.crmportal.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.*;

@Entity
@Table(name = "sot_manual_po")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SotManualPoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sot_po_id")
    private Long id;

    @Column(name = "voucher_no", length = 20)
    private String voucherNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sot_id")
    private StoreOrderingTicketEntity sot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id")
    private PartyMasterEntity party;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private EventMasterEntity event;

    // Status: PENDING, INVOICE_GENERATED
    @Column(name = "status", length = 20)
    private String status = "PENDING";

    @Column(name = "isDelete", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isDelete = false;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserMasterEntity user;
    
    @Column(name = "challan_no", length = 100)
    private String challanNo;
 
    @Column(name = "event_name", length = 255)
    private String eventName;
 
    @Column(name = "delivery_venue", length = 255)
    private String deliveryVenue;
 
    @Column(name = "delivery_time", length = 100)
    private String deliveryTime;
 
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
    
 // Add these fields to SotManualPoEntity.java

    @Column(name = "voucher_date")
    private LocalDate voucherDate;

    @Column(name = "billno", columnDefinition = "VARCHAR(30) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String billno;

    @Column(name = "invoicetype", columnDefinition = "VARCHAR(30) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String invoicetype;


    @Column(name = "subamount")
    private float subamount = 0.0f;

    @Column(name = "discountper")
    private float discountper = 0.0f;

    @Column(name = "discountval")
    private float discountval = 0.0f;

    @Column(name = "adjustamount")
    private float adjustamount = 0.0f;

    @Column(name = "finalamount")
    private float finalamount = 0.0f;
    
    @Column(name = "is_purchase_approve")
    private Boolean isPurchaseApprove;
    
    @Column(name = "purchase_approve_id")
    private Long purchaseApproveRequestId;
}