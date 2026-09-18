package com.crmportal.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.*;

@Entity
@Table(name = "storeissuereturn")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreIssueReturnEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sir_id")
    private Long id;

    @Column(name = "sircode", unique = true, nullable = false, length = 30)
    private String sircode;

    // Reference to original Store PO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storepo_id")
    private PurchaseOrderStoreEntity storeIssue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id")
    private PartyMasterEntity party;

    @Column(name = "voucher", columnDefinition = "VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String voucher;

    @Column(name = "returndate")
    private LocalDate returndate;

    @Column(name = "invoicetype", columnDefinition = "VARCHAR(30) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String invoicetype;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_type_id")
    private StockTypeEntity stocktype;

    @Column(name = "remarks", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String remarks;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "isDelete", nullable = false)
    private Boolean isDelete = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserMasterEntity user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kitchen_type_id")
    private StockTypeEntity kitchentype;
    
    @Column(name = "event_id")
    private Long eventId;
}